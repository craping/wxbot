import topNav from '@/components/nav/top-nav'
import footerNav from '@/components/nav/footer-nav'
import { Toast,ImagePreview } from 'vant'
import { getContentTypeDesc,getParams } from '@/libs/util'
let mm = ["*"]; // 月
let dd = ["*"]; // 日
let h = ["*"]; // 时
let m = ["*"]; // 分
let s = ["*"]; // 秒
for (var i = 1; i <= 12; i++) {
    mm.push(i);
}
for (var i = 1; i <= 31; i++) {
    dd.push(i);
}
for (var i = 0; i < 24; i++) {
    h.push(i);
}
for (var i = 0; i < 60; i++) {
    m.push(i);
}
for (var i = 0; i < 60; i++) {
    s.push(i);
}

const citys = { 月: mm }
let day = { 日: dd }
const hr = { 时: h }
const min = { 分: m }
const sec  = { 秒: s }

export default {
    components: {
        topNav,
        footerNav
    },
    data() {
        return {
            token: '',
            seq: '',
            uuid: '',
            isShow: false,
            addPopup: false,
            loading: false,
            finished: true,
            msgs: [],
            columns: [
                { values: citys['月'] },
                { values: day["日"] },
                { values: hr['时'] },
                { values: min['分'] },
                { values: sec['秒'] }
            ],
            nowMM: "*",
            schedule:{
                MM:null,
                dd:null,
                HH:null,
                mm:null,
                ss: ''
            },
            show: false,
            timeRadio: "1",
            pickerPopup: false,
            // 内容
            contentRadio: "1",
            content: null,      // 文本
            contentFile: {},    // 文件
            contentFileName: '',
            imageVal: null,
            loading: false,
        }
    },
    watch: {
        timeRadio(val) {
            if (val == 2) this.pickerPopup = true;
            else this.pickerPopup = false;
        }
    },
    mounted() {
        this.seq = getParams(window.location.href).seq;
        this.token = getParams(window.location.href).token;
        this.loadTimer();
    },
    methods: {
        showContent(e) {
            let msg = "时间类型：" + this.getTimerTypeDesc(e.schedule) + "\n" +
                "时间设定：" + this.formatTime(e.schedule) + "\n" + 
                "内容类型：" + this.getContentTypeDesc(e.type) + "\n" + 
                "内容详情：" + e.content;
            Toast(msg);
        },
        onChange(picker, values, index) {
            this.schedule.MM = values[0];
            this.schedule.dd = values[1];
            this.schedule.HH = values[2];
            this.schedule.mm = values[3];
            this.schedule.ss = values[4];
            if (index == 0) {
                const mmVal = values[0];
                if (mmVal == this.nowMM) return;
                this.nowMM = mmVal;
                let dd2 = ["*"];
                if(mmVal == "*") {
                    for (var i = 1; i <= 31; i++) {
                        dd2.push(i);
                    }
                } else {
                    const dayCount = new Date(2019, parseInt(mmVal), 0).getDate();
                    for (var i = 1; i <= dayCount; i++) {
                        dd2.push(i);
                    }
                }
                day = { 日: dd2 }
                const columns2 = [
                    { values: citys['月'] },
                    { values: day["日"] },
                    { values: hr['时'] },
                    { values: min['分'] },
                    { values: sec['秒'] }
                ];
                this.columns = columns2;
            }
        },
        onInput(value) {
            this.schedule.ss = this.schedule.ss + "" + value;
        },
        onDelete() {
            this.schedule.ss = this.schedule.ss.substr(0,this.schedule.ss.length-1);
        },
        goback() {
            this.$router.go(-1);
        },
        getContentTypeDesc(type) {
            return getContentTypeDesc(type);
        },
        formatTime(schedule) {
            const _time = schedule.split("|");
            const type = _time[0];
            if (type == "1") {
                const cron = _time[1].split(",");
                return cron[0] + "月" + cron[1] + "日" + cron[2] + "点" + cron[3] + "分" + cron[4] + "秒"
            } else {
                return parseInt(parseInt(_time[1]) / 60 / 60 % 60) + "小时" + parseInt(parseInt(_time[1]) / 60 % 60) + "分钟" +
                    parseInt(parseInt(_time[1]) % 60) + "秒"
            }
        },
        getTimerTypeDesc(schedule) {
            const _time = schedule.split("|");
            const type = _time[0];
            if (type == "1") return "定时"
            else return "间隔"
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
            
            this.contentFile = file;
            this.content = file.name;
            this.contentFileName = file.name;

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
        loadTimer() {
            const data = { seq: this.seq, token: this.token};
            this.$http.post("timer/getTimers?format=json", data).then(response => {
                const data = response.data;
                console.log(data);
                if (!data.result) {
                    if (Object.keys(data.data).length > 0) {
                        this.msgs = data.data.info[this.seq];
                    }
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        },
        delDialog(uuid) {
            this.uuid = uuid;
            this.isShow = true;
        },
        beforeClose(action, done) {
            if (action == "cancel")
                done();
            if (action == "confirm") {
                const param = { seq: this.seq, uuid: this.uuid ,token: this.token}
                this.$http.post("timer/delMsg?format=json",  param).then(response => {
                    done();
                    Toast(response.data.msg);
                    this.msgs = this.msgs.filter(e => e.uuid != this.uuid);
                })
                .catch(error => {
                    console.log(error);
                });
            }
        },
        addTimerMsg() {
            var param = new FormData();
            param.append('token', this.token + "_m");
            param.append("seq", this.seq);
            param.append("content", this.contentRadio=="1" ? this.content : this.contentFile);
            let schedule = [];
            if(this.timeRadio == '2'){
                Object.keys(this.schedule).forEach(key => {
                    schedule.push(this.schedule[key]);
                });
                schedule = 1+"|"+schedule.join(",");
                if(schedule.indexOf(",,") != -1){
                    Toast("时间信息不完整!");
                    return;
                }
            } else {
                if(this.schedule.ss == ''){
                    Toast("时间信息不完整");
                    return;
                }
                schedule = 2+"|"+this.schedule.ss;
            }
            param.append("schedule", schedule);
            this.loading = true;
            this.$http.post("timer/addMsg?format=json",  param).then(response => {
                const data = response.data;
                if (!data.result) {
                    this.msgs.push(data.data.info);
                    this.loading = false;
                    Toast.success(data.msg);
                    setTimeout(() => {
                        this.addPopup = false;
                    }, 500);
                } else {
                    this.loading = false;
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