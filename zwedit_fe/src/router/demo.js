import Vue from 'vue';
import Router from 'vue-router';
import Index from '@/pages/Index';
import lostpage from '@/pages/404';
import whiteboard from '@/pages/whiteboard';
Vue.use(Router);

export default new Router({
    routes: [
        {
            path: '/',
            name: 'index',
            component: Index,
        },
	{
	    path: '/whiteboard/:id',
	    name: 'whiteboard',
	    component: whiteboard,
	},
	{
	    path: '/*',
	    name: 'error-404',
	    meta: {
		title: '404-页面不存在',
	    },
	    component: lostpage,
	},
    ],
});
