import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

const routes = [{
    path: '*',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('./views/home'),
    meta: { title: '首页' }
  },
  {
    path: '/setting',
    name: 'setting',
    component: () => import('./views/setting'),
    meta: { title: '分群设置' }
  },
  {
    path: '/globalSetting',
    name: 'globalSetting',
    component: () => import('./views/globalSetting'),
    meta: { title: '全群设置' }
  },
  {
    path: '/tips',
    name: 'tips',
    component: () => import('./views/tips'),
    meta: { title: '提示语设置' }
  },
  {
    path: '/globalKeyword',
    name: 'globalKeyword',
    component: () => import('./views/globalKeyword'),
    meta: { title: '全域关键词设置' }
  },
  {
    path: '/globalTimer',
    name: 'globalTimer',
    component: () => import('./views/globalTimer'),
    meta: { title: '全域定时群发设置' }
  },
  {
    path: '/keyword',
    name: 'keyword',
    component: () => import('./views/keyword'),
    meta: { title: '分群关键字' }
  },
  {
    path: '/timer',
    name: 'timer',
    component: () => import('./views/timer'),
    meta: { title: '分群定时' }
  },
];

// add route path
routes.forEach(route => {
  route.path = route.path || '/' + (route.name || '');
});

const router = new Router({
  routes
});

router.beforeEach((to, from, next) => {
  const title = to.meta && to.meta.title;
  if (title) {
    document.title = title;
  }
  next();
});

export { router };