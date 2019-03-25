import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { ImagePreview,Toast } from 'vant';
export default {
    components:{topNav,footerNav},
    data() {
        return {
            tipType: '',
            tipsTxt: null,
            tipFile: {},
            tipFileName: '',
            tips: {
                chatRoomFoundTip: {},
                memberJoinTip: {},
                memberLeftTip: {}
            },
            radio: '1',
            imageVal: null,
            loading: false,
            token: ''
        }
    },
    watch: {
        '$router' : 'getParams'
    },
    created(){
        this.getParams();
    },
    mounted() { 
        this.loadTips();
    },
    methods: {
        initTips() {
            this.tips = Object.assign(this.tips, this.$config.setting.tips);
            const _obj = this.tips[''+this.tipType+''];
            if (Object.keys(_obj).length) {
                this.tipsTxt = _obj.content;
                if (_obj.type != 1) this.radio="2";
                if (_obj.type == 2) this.imageVal = _obj.content;
                if (this.radio == "2") this.tipFileName = _obj.content;
            }
        },
        loadTips() {
            if (Object.keys(this.$config.setting).length) {
                this.initTips();
                return;
            } 
            this.$http.post("setting/getSetting?format=json", {token: this.token}).then(response => {
                const data = response.data;
                if (!data.result) {
                    this.$config.setting = data.data.info;
                    this.initTips();
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        },
        showImg() {
            ImagePreview(['' + this.imageVal + '']);
        },
        onRead(event) {
            // 通过DOM取文件数据
            let inputDOM = this.$refs.uploader;
            let file = inputDOM.files[0];
            if (file == undefined) return;
            this.imageVal = null;   
            let size = Math.floor(file.size / 1024);
            if (size > 80*1024) {
                Toast('请选择80M以内的文件！');
                return false
            }

            this.tipFile = file;
            this.tipsTxt = file.name;
            this.tipFileName = file.name;
            // 转化base64预览
            if (file.type.search("image/") != -1) {
                const _this = this;
                if (!event || !window.FileReader) return  
                let reader = new FileReader()
                reader.readAsDataURL(file) 
                reader.onloadend = function () {
                    _this.imageVal = this.result;
                }
            } 
        },
        getParams() {
            // 取到路由带过来的参数
            this.tipType = this.$route.query.tipType;
            this.$route.meta.title = this.$route.query.title;
            this.token = this.$route.query.token;
        },
        setTpis() {
            if (this.tipsTxt == this.tips[''+this.tipType+''].content){
                Toast("操作成功！");
                setTimeout(() => {
                    Object.assign(this.$data, this.$options.data())
                    this.$router.go(-1);
                }, 1000);
                return;
            }
            
            this.loading = true;
            let param = new FormData();
            param.append('token', this.token);
            param.append('tipType', this.tipType);
            param.append('content', this.radio=="1" ? this.tipsTxt : this.tipFile);
            this.$http.post("setting/setTips?format=json",  param) .then(response => {
                const data = response.data;
                this.loading = false;
                if (!data.result) {
                    this.$config.setting.tips = data.data.info;
                    Toast(data.msg);
                    setTimeout(() => {
                        Object.assign(this.$data, this.$options.data());
                        this.$router.go(-1);
                    }, 1000);
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                this.loading = false;
                console.log(error);
            });
        }
    }
}