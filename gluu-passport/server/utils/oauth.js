const got = require('got')
const { v4: uuidv4 } = require('uuid')
const misc = require('./misc')
const logger = require('./logging')

// Cache for token endpoints (keyed by IDP host)
const endpointCache = {}

// Cache for access tokens (keyed by IDP host)
// Structure: { token: String, expiresAt: Number }
const tokenCache = {}

/**
 * Extract IDP host from URI
 * @param uri : String - configuration endpoint URI
 * @returns {String} - IDP host (protocol + host)
 */
function extractIdpHost (uri) {
  try {
    const url = new URL(uri)
    return `${url.protocol}//${url.host}`
  } catch (error) {
    logger.log2('error', 'extractIdpHost. Failed to parse URI')
    throw new Error(`Invalid URI: ${uri}`)
  }
}

/**
 * Log error details from got error object
 * @param {*} context: String - context of the error for logging
 * @param {*} err: Error object from got request
 */
function logGotError (context, err) {
  const responseBody = err.response && err.response.body

  logger.log2('error', context)
  logger.log2('error', {
    message: err.message,
    code: err.code,
    statusCode: err.response && err.response.statusCode,
    error: responseBody && responseBody.error,
    error_description: responseBody && responseBody.error_description
  })
}

/**
 * Fetch token endpoint from well-known OpenID configuration
 * @param idpHost : String - IDP host URL
 * @returns {Promise<String>} - token endpoint URL
 */
function getTokenEndpointFromWellKnown (idpHost) {
  const wellKnownUrl = `${idpHost}/.well-known/openid-configuration`
  logger.log2('debug', `getTokenEndpointFromWellKnown. Fetching from ${wellKnownUrl}`)

  return got
    .get(wellKnownUrl, { responseType: 'json', timeout: { request: 10000 } })
    .then((response) => {
      const config = response.body
      if (!config.token_endpoint) {
        throw new Error('token_endpoint not found in well-known configuration')
      }
      logger.log2('info', `getTokenEndpointFromWellKnown. Token endpoint: ${config.token_endpoint}`)
      return config.token_endpoint
    })
    .catch((err) => {
      logGotError('getTokenEndpointFromWellKnown. Failed to fetch well-known configuration', err)
      throw err
    })
}

/**
 * Create JWT token for private_key_jwt client authentication
 * @param clientId : String - client ID
 * @param tokenEndpoint : String - token endpoint URL
 * @returns {String} - JWT token
 */
function makeClientAssertionJWTToken (clientId, tokenEndpoint) {
  const now = new Date().getTime()
  const nowInSeconds = Math.floor(now / 1000)
  return misc.getRpJWT({
    iss: clientId,
    sub: clientId,
    aud: tokenEndpoint,
    jti: uuidv4(),
    exp: nowInSeconds + 30,
    iat: nowInSeconds
  })
}

/**
 * Get OAuth token using private_key_jwt authentication
 * @param tokenEndpoint : String - token endpoint URL
 * @param scope : String - OAuth scope required
 * @returns {Promise<String>} - access_token
 */
function getOAuthToken (tokenEndpoint, scope) {
  logger.log2('verbose', 'getOAuthToken called for ' + tokenEndpoint)

  const clientId = global.basicConfig.clientId
  const token = makeClientAssertionJWTToken(clientId, tokenEndpoint)

  const options = {
    responseType: 'json',
    form: {
      grant_type: 'client_credentials',
      client_assertion_type:
        'urn:ietf:params:oauth:client-assertion-type:jwt-bearer',
      client_assertion: token,
      scope
    }
  }

  logger.log2(
    'debug',
    `getOAuthToken request to ${tokenEndpoint} with scope: ${scope}`
  )

  return got
    .post(tokenEndpoint, { ...options, timeout: { request: 10000 } })
    .then((response) => {
      const tokenDetails = response.body
      if (!tokenDetails || typeof tokenDetails.access_token !== 'string') {
        throw new Error('Token endpoint response did not include access_token')
      }
      logger.log2('info', 'getOAuthToken. OAuth token received')

      return {
        accessToken: tokenDetails.access_token,
        expiresIn: tokenDetails.expires_in || 3600
      }
    })
    .catch((err) => {
      logGotError('getOAuthToken. Failed to get OAuth token', err)
      throw err
    })
}

/**
 * Make protected request with OAuth token
 * @param requestOptions : Object containing request options
 * @param accessToken : String - OAuth access token
 * @returns {Promise<Object>} - response body
 */
function doRequest (requestOptions, accessToken) {
  const { uri, ...options } = requestOptions

  options.headers = {
    ...(options.headers || {}),
    authorization: `Bearer ${accessToken}`
  }

  logger.log2('debug', `doRequest. Making request to ${uri}`)

  return got
    .get(uri, { ...options, responseType: 'json', timeout: { request: 10000 } })
    .then((response) => {
      const { body, statusCode } = response
      if (statusCode < 200 || statusCode >= 300) {
        throw new Error(`Config request failed with status ${statusCode}`)
      }
      logger.log2('info', 'doRequest. Response received')
      logger.log2('debug', `doRequest. Passport configs are: ${JSON.stringify(body)} status: ${statusCode}`)

      return body
    })
    .catch((err) => {
      logGotError('doRequest. Request failed', err)
      throw err
    })
}

/**
 * Request config endpoint with OAuth protection
 * @param options : Object containing request options (uri, headers, etc.)
 * @param scope : String - OAuth scope required (default: 'https://gluu.org/passport/config.read')
 * @returns {Promise<Object>} - config response
 */
function request (
  options,
  scope = 'https://gluu.org/passport/config.read'
) {
  logger.log2('verbose', 'request called with OAuth protection')

  try {
    // Extract IDP host from options.uri
    const idpHost = extractIdpHost(options.uri)

    // Check if token endpoint is cached
    let getEndpointPromise
    if (endpointCache[idpHost]) {
      logger.log2('debug', `request. Using cached token endpoint for ${idpHost}`)
      getEndpointPromise = Promise.resolve(endpointCache[idpHost])
    } else {
      logger.log2('debug', `request. Fetching token endpoint for ${idpHost}`)
      getEndpointPromise = getTokenEndpointFromWellKnown(idpHost)
        .then((tokenEndpoint) => {
          endpointCache[idpHost] = tokenEndpoint
          return tokenEndpoint
        })
    }

    // Get token endpoint, then check if access token is cached and valid
    return getEndpointPromise
      .then((tokenEndpoint) => {
        const now = Date.now()
        const cachedToken = tokenCache[idpHost]

        // Check if cached token exists and is not expired
        // Add a 30-second buffer to avoid race conditions
        if (cachedToken && cachedToken.expiresAt > (now + 30000)) {
          logger.log2('debug', `request. Using cached access token for ${idpHost}`)
          return Promise.resolve(cachedToken.token)
        }

        // Token is missing or expired, fetch a new one
        logger.log2('debug', `request. Fetching new access token for ${idpHost}`)
        return getOAuthToken(tokenEndpoint, scope)
          .then(({ accessToken, expiresIn }) => {
            tokenCache[idpHost] = {
              token: accessToken,
              expiresAt: now + (expiresIn * 1000)
            }
            return accessToken
          })
      })
      .then((accessToken) => {
        logger.log2('debug', 'request. OAuth token obtained, making request')
        return doRequest(options, accessToken)
      })
      .catch((err) => {
        logger.log2('error', 'request. OAuth protected request failed')
        throw err
      })
  } catch (error) {
    logger.log2('error', 'request. Failed to process request')
    return Promise.reject(error)
  }
}

module.exports = {
  request
}
