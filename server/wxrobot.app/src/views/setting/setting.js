import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { Toast } from 'vant';
import { getParams } from '@/libs/util'
export default {
    components: {
        topNav,
        footerNav,
        Toast
    },
    data() {
        return {
            seq: '',
            turing: false,
            keywords: false,
            timers: false,
            forwards: false,
            setting: {},
            token: '',
            user: ''
        }
    },
    mounted() {
        this.token = getParams(window.location.href).token;
        this.user = getParams(window.location.href).user;
        this.seq = getParams(window.location.href).seq;
        this.loadSetting();
    },
    methods: {
        goSetting(mode, seq) {
            this.$config.active = null;
            this.$router.push({
                path: '/'+ mode,
                query: {
                    seq: seq,
                    token: this.token,
                    user: this.user
                }
            })
        },
        goTips(tipType) {
            const title = this.$refs[tipType].innerText;
            this.$config.active = null;
            this.$router.push({
                path: '/tips',
                query: { 
                    tipType: tipType, 
                    title: title,
                    token: this.token,
                    seq: this.seq
                }
            })
        },
        updSwitchs($event, moduleName) {
            let url = "setting/disableSeq?format=json";
            if ($event) {
                url = "setting/enableSeq?format=json";
            }
            const data = {
                seq: this.seq, 
                module: moduleName,
                token: this.token
            }
            this.$http.post(url, data).then(response => {
                const data = response.data;
                if (!data.result) {
                    if ($event) {
                        this.setting[moduleName].push(this.seq);
                    } else {
                        this.setting[moduleName].splice(this.setting[moduleName].indexOf(this.seq), 1);
                    }
                    setTimeout(() => {
                        Toast('操作成功');
                    }, 500);
                    this.$config.setting = this.setting;
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            }); 
        },
        initSwitchs() {
            this.turing = this.setting.turing.includes(this.seq);
            this.keywords = this.setting.keywords.includes(this.seq);
            this.timers = this.setting.timers.includes(this.seq);
            this.forwards = this.setting.forwards.includes(this.seq);
        },
        loadSetting() {
            if (Object.keys(this.$config.setting).length) {
                this.setting = this.$config.setting;
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
                    this.setting = this.$config.setting = data.data.info;
                    setTimeout(() => {
                        Toast('初始化用户配置成功');
                    }, 500);
                    this.initSwitchs();
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