import Vue from 'vue'
import App from './App.vue'
import Vant from 'vant';
import { router } from './router';
import http from '@/libs/axios'
import config from '@/config'

import 'vant/lib/index.css';

Vue.use(Vant);

Vue.config.productionTip = false
Vue.prototype.$http = http
Vue.prototype.$config = config

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
