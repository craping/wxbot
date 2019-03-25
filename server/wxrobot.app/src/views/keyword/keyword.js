import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { Toast,SwipeCell,Popup,ImagePreview,Dialog } from 'vant'
import { getContentTypeDesc,filterAll,getParams } from '@/libs/util'
export default {
    components:{topNav,footerNav,SwipeCell,Popup,ImagePreview,Dialog},
    data() {
        return {
            seq: '',
            keyword: '',
            chatRoomKeyMap: {},
            loading: false,
            finished: true,
            popup: false,
            popupItem: {
                keyword: '',
                barTitle: '新增',    //标题
                content: null,      // 文本
                contentFile: {},    // 文件
                contentFileName: '',
                imageVal: null,
                txtDisabled: false,
            },
            key: '',
            radio: '1',
            isShow: false,
            token: ''
        }
    },
    computed: {
        keyMap() {
            return filterAll(Object.keys(this.chatRoomKeyMap).map(key => {
                const msg = this.chatRoomKeyMap[key];
                return {
                    key: key,
                    type:msg.type,
                    content: msg.content
                };
            }), {
                key: this.keyword,
                content: this.keyword
            });
        }
    },
    mounted() {
        this.token = getParams(window.location.href).token;
        this.seq = getParams(window.location.href).seq;
        this.loadKeyword();
    },
    methods: {
        goback() {
            this.$router.go(-1);
        },
        getContentTypeDesc(type) {
            return getContentTypeDesc(type);
        },
        closePopup() {
            Object.assign(this.$data.popupItem, this.$options.data().popupItem)
            this.popup = false;
        },
        showContent(key) {
            const val = this.chatRoomKeyMap[key];
            let msg = "关键字：" + key + "\n" +
                "回复类型：" + this.getContentTypeDesc(val.type) + "\n" + 
                "回复内容：" + val.content;
            Toast(msg);
        },
        loadKeyword() {
            const data = {seq: this.seq, token: this.token}
            this.$http.post("keyword/getKeywords?format=json", data).then(response => {
                const data = response.data;
                console.log(data);
                if (!data.result) {
                    if (Object.keys(data.data).length > 0) {
                        this.chatRoomKeyMap = data.data.info[this.seq];
                    }
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        },
        delDialog(key) {
            this.key = key;
            this.isShow = true;
        },
        updPopup(key) {
            const val = this.chatRoomKeyMap[key];
            this.popupItem.barTitle = '修改';
            this.popupItem.txtDisabled = true;
            this.popupItem.keyword = key;
            this.popupItem.content = val.content;
            if (val.type != 1) this.radio="2";
            if (val.type == 2) this.popupItem.imageVal = val.content;
            if (this.radio == "2") this.popupItem.contentFileName = val.content;
            this.popup = true;
        },
        oprSwipe(clickPosition, instance) {
            if (clickPosition == 'right') {
            } else {
                instance.close();
            }
        },
        beforeClose(action, done) {
            if (action == "cancel")
                done();
            if (action == "confirm") {
                const param = { seq: this.seq, keyList:[this.key],token: this.token }
                this.$http.post("keyword/del?format=json",  param).then(response => {
                    const data = response.data;
                    done();
                    this.$delete(this.chatRoomKeyMap, this.key);
                    setTimeout(() => {
                        Toast(data.msg);
                    }, 500);
                })
                .catch(error => {
                    console.log(error);
                });
            }
        },
        addKeyword() {
            if (this.popupItem.keyword.trim() == ''){
                Toast('请输入关键字');
                return;
            }
            var param = new FormData();
            param.append('token', this.token);
            param.append("seq", this.seq);
            param.append("key", this.popupItem.keyword);
            param.append("content", this.radio=="1" ? this.popupItem.content : this.popupItem.contentFile);
            this.$http.post("keyword/set?format=json",  param).then(response => {
                const data = response.data;
                if (!data.result) {
                    this.$set(this.chatRoomKeyMap, this.popupItem.keyword, data.data.info);
                    Toast.success(data.msg);
                    this.$nextTick(() => {
                        this.closePopup();
                    });
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        },
        showImg() {
            ImagePreview(['' + this.popupItem.imageVal + '']);
        },
        onRead(event) {
            // 通过DOM取文件数据
            let inputDOM = this.$refs.uploader;
            let file = inputDOM.files[0];
            if (file == undefined) return;
            this.popupItem.imageVal = null;   
            let size = Math.floor(file.size / 1024);
            if (size > 80*1024) {
                Toast('请选择80M以内的文件！');
                return false
            }
            
            this.popupItem.contentFile = file;
            this.popupItem.content = file.name;
            this.popupItem.contentFileName = file.name;

            // 转化base64预览
            if (file.type.search("image/") != -1) {
                const _this = this;
                if (!event || !window.FileReader) return  
                let reader = new FileReader()
                reader.readAsDataURL(file) 
                reader.onloadend = function () {
                    _this.popupItem.imageVal = this.result;
                }
            }
        }
    }
}