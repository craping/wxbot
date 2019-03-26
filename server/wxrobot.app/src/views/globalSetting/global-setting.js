import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { getParams } from '@/libs/util'
import { Toast } from 'vant'
export default {
    components:{topNav,footerNav},
    data() {
        return {
            aafLoading: false,
            gkLoading: false,
            gtLoading: false,
            switchs:{
                autoAcceptFriend: false,
                globalKeyword: false,
                globalTimer: false,
            },
            token: ''
        }
    },
    mounted() { 
        this.token = getParams(window.location.href).token;
        this.loadSetting();
    },
    methods: {
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
        goTips(tipType) {
            const title = this.$refs[tipType].innerText;
            this.$config.active = null;
            this.$router.push({
                path: '/tips',
                query: { 
                    tipType: tipType, 
                    title: title,
                    token: this.token
                }
            })
        },
        goGlobalTimer() {
            this.$config.active = null;
            this.$router.push({
                path: '/globalTimer',
                query: {
                    token: this.token
                }
            })
        },
        goGlobalKeyword() {
            this.$config.active = null;
            this.$router.push({
                path: '/globalKeyword',
                query: {
                    token: this.token
                }
            })
        },
        loadSetting() {
            if (Object.keys(this.$config.setting).length) {
                this.switchs = this.$config.setting.switchs;
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