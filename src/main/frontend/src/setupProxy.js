const { createProxyMiddleware } = require('http-proxy-middleware');

// For proxy configuration options seE:
// https://github.com/chimurai/http-proxy-middleware#http-proxy-options

module.exports = function (app) {
  app.use(
    '^\/action',
    createProxyMiddleware({
      target: process.env.REACT_APP_ROXY_SERVER,
      changeOrigin: true,
      cookieDomainRewrite: '',
    })
  );

  app.use(
    '^\/actuator',
    createProxyMiddleware({
      target: process.env.REACT_APP_ROXY_SERVER,
      changeOrigin: true,
      cookieDomainRewrite: '',
    })
  );

  app.use(
    '^\/login|\/logged-in|\/logout|\/logged-out$',
    createProxyMiddleware({
      target: process.env.REACT_APP_ROXY_SERVER,
      changeOrigin: true,
      cookieDomainRewrite: '',
      /*
       * Rewrites the location host/ port on (301 / 302 / 307 / 308) redirects based on requested host/ port.Default: false. 
       * Required for authentication logged-in redirect.
       */
      autoRewrite: true,
    })
  );

};