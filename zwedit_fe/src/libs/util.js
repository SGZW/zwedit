let util = {

};
util.title = function (title) {
    title = title ? title + ' - Home' : 'ZWEDIT';
    window.document.title = title;
};

util.languageCode = function () {
    return navigator.language || navigator.browserLanguage;
};

util.localName = function () {
    return '';
};

export default util;
