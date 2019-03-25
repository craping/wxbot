$script("lib/vue/vue.js", "vue", () => {
    $script([
    "lib/vue-virtual-scroller/intersection-observer.js", "lib/vue-virtual-scroller/vue-observe-visibility.min.js", 
    "lib/vue-virtual-scroller/vue-virtual-scroller.min.js", 
    "lib/iview-3/iview.min.js"], "vue-plugs");
});
$script('lib/jquery/jquery-3.3.1.min.js', 'jquery', function (){
    $script(['lib/bootstrap-4.2.1-dist/js/popper.min.js', 'lib/bootstrap-4.2.1-dist/js/bootstrap.min.js'], 'bootstrap');
});
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
$script.ready(["vue-plugs", "bootstrap", "crypto", "common"], function () {
    $("#user").load("settingModule/user/user.html", {}, function () {
        $script("settingModule/user/user.js", "user");
    });
    $("#general").load("settingModule/general/general.html", {}, function () {
        $script("settingModule/general/general.js", "general");
    });
    $("#seqs").load("settingModule/seqs/seqs.html", {}, function () {
        $script("settingModule/seqs/seqs.js", "seqs");
    });
    $("#globalTimer").load("settingModule/globalTimer/globalTimer.html", {}, function () {
        $script("settingModule/globalTimer/globalTimer.js", "globalTimer");
    });
    $("#globalKeyword").load("settingModule/globalKeyword/globalKeyword.html", {}, function () {
        $script("settingModule/globalKeyword/globalKeyword.js", "globalKeyword");
    });
    $("#tips").load("settingModule/tips/tips.html", {}, function () {
        $script("settingModule/tips/tips.js", "tips");
    });
})
var app;
const constant = {
    header: {
        user: {
            icon: "fa-user",
            title: "个人中心"
        },
        general: {
            icon: "fa-cog",
            title: "通用"
        },
        seqs: {
            icon: "fa-toggle-on",
            title: "分群开关"
        },
        globalTimer: {
            icon: "fa-clock",
            title: "全群定时"
        },
        globalKeyword: {
            icon: "fa-comment-dots",
            title: "全群回复"
        },
        tips: {
            icon: "fa-info-circle",
            title: "群提示语"
        }
    },
    typeIcon:["", "fa-comment-dots", "fa-image", "fa-laugh", "fa-file-video", "fa-file"]
};
$script.ready(["user", "general", "seqs", "globalTimer", "globalKeyword", "tips"], function () {
    let data = Object.assign({
        header:{},
        setting:{},
        chatRooms:[],
        switchTimer:{},
        switchKeyword:{}
    }, {user:User.data}, {general:General.data}, {seqs:Seqs.data}, {globalTimer:GlobalTimer.data}, {globalKeyword:GlobalKeyword.data}, {tips:Tips.data});
    let methods = Object.assign({
        filterAll(data, argumentObj) {
            return data.filter(d => {
                for (let argu in argumentObj) {
                    if (d[argu].toUpperCase().indexOf(argumentObj[argu].toUpperCase()) > -1)
                        return true;
                }
                return false;
            });
        },
        filter(data, argumentObj) {
            let res = data;
            let dataClone = data;
            for (let argu in argumentObj) {
                if (argumentObj[argu].length > 0) {
                    res = dataClone.filter(d => {
                        return d[argu].indexOf(argumentObj[argu]) > -1;
                    });
                    dataClone = res;
                }
            }
            return res;
        },
        syncSetting(menu){
            this.header = constant.header[menu];
            $("a[name='"+menu+"']").tab("show");
            this.setting = wxbot.getSetting();
            wxbot.getChatRooms(data => {
                this.chatRooms = data;
            });
            // this.generalReset();
            // this.getMsgs();
            // this.getKeyMap();
            // this.tipsReset();
            this.switchTimer = {
                seq: "global",
                module:"timers",
                state: this.setting.timers.includes("global"),
                result: this.setting.timers.includes("global"),
                loading: false,
            };
            this.switchKeyword = {
                seq: "global",
                module:"keywords",
                state: this.setting.keywords.includes("global"),
                result: this.setting.keywords.includes("global"),
                loading: false,
            };
        },
        onMembersSeqChanged(seqMap){
            app.modKeywords(seqMap);
        }
    }, User.methods, General.methods, Seqs.methods, GlobalTimer.methods, GlobalKeyword.methods, Tips.methods);
    let computed = Object.assign({}, User.computed, General.computed, Seqs.computed, GlobalTimer.computed, GlobalKeyword.computed, Tips.computed);
    app = new Vue({
        el: "#app",
        data: data,
        computed:computed,
        mounted() {
            this.syncSetting("user");
        },
        methods: methods
    });
})