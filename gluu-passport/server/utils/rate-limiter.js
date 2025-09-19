const rateLimit = require('express-rate-limit')
const config = require('config')

const windowMs = config.get('rateLimitWindowMs')
const max = config.get('rateLimitMaxRequestAllow')
const whitelist = config.get('rateLimitWhitelistIP')
  ? config.get('rateLimitWhitelistIP').split(',').map(ip => ip.trim())
  : []

const rateLimiter = rateLimit({
  windowMs,
  max,
  message: `You have exceeded the ${max} requests in ${windowMs} milliseconds limit!`,
  headers: true,
  skip: (req, res) => {
    const clientIp = req.ip || req.connection.remoteAddress
    return whitelist.includes(clientIp)
  }
})

module.exports = {
  rateLimiter
}
