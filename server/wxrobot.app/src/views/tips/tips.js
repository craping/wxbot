import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { ImagePreview,Toast } from 'vant';
export default {
    components:{topNav,footerNav},
    data() {
        return {
            tipType: '',
            tipsTxt: '',
            tipFile: null,
            tipFileName: '',
            tip: {},
            radio: '1',
            imageVal: null,
            loading: false,
            cloading: false,
            isShow: false,
            token: '',
            seq: ''
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
            if (this.tip != undefined && Object.keys(this.tip).length) {
                this.tipsTxt = this.tip.content;
                if (this.tip.type != 1) this.radio="2";
                if (this.tip.type == 2) this.imageVal = this.tip.content;
                if (this.radio == "2") this.tipFileName = this.tip.content;
            }
        },
        loadTips() {
            const data = { seq: this.seq, token: this.token};
            this.$http.post("tip/getTips?format=json", data).then(response => {
                const data = response.data;
                console.log(data);
                if (!data.result) {
                    if (Object.keys(data.data).length > 0) {
                        this.tip = data.data.info[this.seq][this.tipType];
                        this.initTips();
                    }
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        },
        beforeClose(action, done) {
            if (action == "cancel")
                done();
            if (action == "confirm") {
                if (this.tip == undefined) {
                    done();
                    Toast("操作成功！");
                    setTimeout(() => {
                        this.$router.go(-1);
                    }, 1000);
                    return;
                }
                const params = {
                    token: this.token,
                    seq: this.seq,
                    type: this.tipType
                }
                this.$http.post("tip/del?format=json", params).then(response => {
                    const data = response.data;
                    done();
                    if (!data.result) {
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
                    console.log(error);
                });
            }
        },
        showImg() {
            ImagePreview(['' + this.imageVal + '']);
        },
        onRead(event) {
            // 通过DOM取文件数据
            const maxSize = this.$config.maxFileSize;
            let inputDOM = this.$refs.uploader;
            let file = inputDOM.files[0];
            if (file == undefined) return;
            this.imageVal = null;   
            let size = Math.floor(file.size / 1024);
            if (size > maxSize*1024) {
                Toast('请选择'+ maxSize +'M以内的文件！');
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
            this.seq = this.$route.query.seq;
        },
        setTpis() {
            // if (this.tipsTxt == this.tip.content){
            //     Toast("操作成功！");
            //     setTimeout(() => {
            //         Object.assign(this.$data, this.$options.data())
            //         this.$router.go(-1);
            //     }, 1000);
            //     return;
            // }
            const content = this.radio=="1" ? this.tipsTxt : this.tipFile;
            if (!content || content == null || content == "") {
                Toast('信息不完整');
                return false
            }
            
            this.loading = true;
            let param = new FormData();
            param.append('token', this.token + "_m");
            param.append('seq', this.seq);
            param.append('type', this.tipType);
            param.append('content', content);
            this.$http.post("tip/set?format=json",  param) .then(response => {
                const data = response.data;
                this.loading = false;
                if (!data.result) {
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