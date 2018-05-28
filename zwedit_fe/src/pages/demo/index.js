import Vue from 'vue';
import iView from 'iview';
import VueRouter from 'vue-router';
import Util from '@/libs/util';
import router from '@/router/demo';
import App from './App.vue';
import '@/themes/index.less';

Vue.config.productionTip = false;
Vue.config.debug = true;

Vue.use(VueRouter);

Vue.use(iView);

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
    template: '<App/>',
    components: { App },
});
