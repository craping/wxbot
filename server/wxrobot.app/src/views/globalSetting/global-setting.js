import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { getParams } from '@/libs/util'
import { Toast } from 'vant'
export default {
    components:{topNav,footerNav},
    data() {
        return {
            globalKeywords: false,
            globalTimers: false,
            aafLoading: false,
            gkLoading: false,
            gtLoading: false,
            switchs:{
                autoAcceptFriend: false,
                globalKeyword: false,
                globalTimer: false,
            },
            token: '',
            user:''
        }
    },
    mounted() { 
        this.token = getParams(window.location.href).token;
        this.user = getParams(window.location.href).user;
        this.loadSetting();
    },
    methods: {
        initSwitchs() {
            this.globalTimers = this.$config.setting.timers.includes('global');
            this.globalKeywords = this.$config.setting.keywords.includes('global');
        },
        updSwitchs(loding) {
            this[''+loding+''] = true;
            const data = { switchs: JSON.stringify(this.switchs),token: this.token }
            this.$http.post("setting/setSwitchs?format=json", data).then(response => {
                const data = response.data;
                if (!data.result) {
                    this[''+loding+''] = false;
                    this.$config.setting.switchs = this.switchs;
                } else {
                    this[''+loding+''] = false;
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                this[''+loding+''] = false;
                console.log(error);
            });
        },
        updGlobalSetting($event, moduleName) {
            let url = "setting/disableSeq?format=json";
            if ($event) {
                url = "setting/enableSeq?format=json";
            }
            const data = {
                seq: 'global', 
                module: moduleName,
                token: this.token
            }
            this.$http.post(url, data).then(response => {
                const data = response.data;
                if (!data.result) {
                    if ($event) {
                        this.$config.setting[moduleName].push('global');
                    } else {
                        this.$config.setting[moduleName].splice(this.$config.setting[moduleName].indexOf('global'), 1);
                    }
                    setTimeout(() => {
                        Toast('操作成功');
                    }, 500);
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            }); 
        },
        goGlobalTimer() {
            this.$config.active = null;
            this.$router.push({
                path: '/globalTimer',
                query: {
                    token: this.token,
                    user: this.user
                }
            })
        },
        goGlobalKeyword() {
            this.$config.active = null;
            this.$router.push({
                path: '/globalKeyword',
                query: {
                    token: this.token,
                    user: this.user
                }
            })
        },
        loadSetting() {
            if (Object.keys(this.$config.setting).length) {
                this.switchs = this.$config.setting.switchs;
                this.initSwitchs();
                return;
            } 
            Toast.loading({
                forbidClick: true,
                duration: 0,
                message: '正在请求...'
            });
            this.$http.post("setting/getSetting?format=json", {token: this.token}).then(response => {
                const data = response.data;
                console.log(data);
                if (!data.result) {
                    this.$config.setting = data.data.info;
                    this.initSwitchs();
                    this.switchs = this.$config.setting.switchs;
                    setTimeout(() => {
                        Toast('初始化用户全局配置成功');
                    }, 500);
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        }
    }
}