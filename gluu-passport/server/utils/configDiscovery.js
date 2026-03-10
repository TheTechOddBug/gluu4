const misc = require('./misc')
const logger = require('./logging')
const uma = require('./uma')
const oauth = require('./oauth')
const { default: got } = require('got')

/**
 * Validates and parses data fetched from config endpoint
 * @param data : Object containing configuration data fetched from endpoint
 * @returns {*}: Object containing validated data
 */
function validate (data) {
  // Perform a shallow validation on configuration data gathered
  const paths = [
    ['conf', 'logging', 'level'],
    ['conf', 'serverWebPort']
  ]

  if (misc.pathsHaveData(paths, data) && Array.isArray(data.providers)) {
    logger.log2('info', 'Configuration data has been parsed')
    return data
  } else {
    throw new Error('Received data not in the expected format')
  }
}

/**
 * Retrieves the full configuration from passport configuration endpoint
 * @param cfgEndpoint : configuration endpoint got from basic config file
 * @returns {*}
 */
function retrieve (cfgEndpoint) {
  const options = {
    uri: cfgEndpoint,
    throwHttpErrors: false
  }
  return got.get(cfgEndpoint, options).then((response) => {
    const { body, statusCode, headers } = response
    let prfn = null

    switch (statusCode) {
      case 401:
        // Infer protection mode based on presence of header
        if (headers['www-authenticate']) {
          prfn = uma.request
          logger.log2('info', 'Found www-authenticate header, inferring UMA protection mode')
        } else {
          prfn = oauth.request
          logger.log2('info', 'No www-authenticate header found, inferring OAuth protection mode')
        }
        break
      case 200:
        logger.log2('info', 'Skip security check and successfully retrieved configuration data')
        prfn = (_) => Promise.resolve(JSON.parse(body))
        break
      default:
        throw new Error(
          `Received unexpected HTTP status code ${statusCode} from configuration endpoint`
        )
    }
    return misc.pipePromise(prfn, validate)(options)
  })
}

module.exports = {
  retrieve
}
