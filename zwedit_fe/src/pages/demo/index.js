import Vue from 'vue';
import iView from 'iview';
import VueRouter from 'vue-router';
import VueI18n from 'vue-i18n';
import Util from '@/libs/util';
import router from '@/router/demo';
import App from './App.vue';
import '@/themes/index.less';

import zhLocale from 'iview/dist/locale/zh-CN';
import enLocale from 'iview/dist/locale/en-US';
import jaLocale from 'iview/dist/locale/ja-JP';
import ptLocale from 'iview/dist/locale/pt-BR';

Vue.config.productionTip = false;
Vue.config.debug = true;

Vue.use(VueRouter);
Vue.use(VueI18n);

const messages = {
    'zh-CN': zhLocale,
    'en-US': enLocale,
    'ja-JP': jaLocale,
    'pt-BR': ptLocale,
};

const i18n = new VueI18n({
    locale: Util.languageCode(),
    fallbackLocale: 'zh-CN',
    messages,
});

Vue.use(iView, {
    i18n: function (path, options) {
        let value = i18n.t(path, options);
        if (value !== null && value !== undefined) {
            return value;
        }
        return '';
    },
});

router.beforeEach((to, from, next) => {
    iView.LoadingBar.start();
    Util.title(to.meta.title);
    next();
});

router.afterEach((to, from, next) => {
    iView.LoadingBar.finish();
    window.scrollTo(0, 0);
});

new Vue({
    el: '#app',
    router,
    i18n,
    template: '<App/>',
    components: { App },
});
