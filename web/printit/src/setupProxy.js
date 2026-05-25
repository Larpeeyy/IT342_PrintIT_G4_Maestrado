const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function setupProxy(app) {
  const backendProxy = createProxyMiddleware({
    target: "http://localhost:8080",
    changeOrigin: false,
    xfwd: true,
  });

  app.use("/api", backendProxy);
  app.use("/oauth2", backendProxy);
  app.use("/login/oauth2", backendProxy);
};
