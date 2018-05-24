// see http://vuejs-templates.github.io/webpack for documentation.
var devHost = '180.76.236.226';
var frontendPort = '9701';
var backendPort = '9700';
var basePath = 'http://' + devHost + ':' + frontendPort;
var baseApiPath = 'http://' + devHost + ':' + backendPort;

module.exports = {
    devHost,
    frontendPort,
    backendPort,
    basePath,
    baseApiPath,
};
