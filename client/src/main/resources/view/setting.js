$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script('lib/jquery/jquery-3.3.1.min.js', 'jquery', function (){
    $script(['lib/bootstrap-4.2.1-dist/js/popper.min.js', 'lib/bootstrap-4.2.1-dist/js/bootstrap.min.js'], 'bootstrap');
});
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
$script.ready(["vue", "iview", "jquery", "crypto", "common"], function () {
    $("#user").load("settingModule/user/user.html", {}, function () {
        $script("settingModule/user/user.js", "user");
    });
    $("#general").load("settingModule/general/general.html", {}, function () {
        $script("settingModule/general/general.js", "general");
    });
    $("#forward").load("settingModule/forward/forward.html", {}, function () {
        $script("settingModule/forward/forward.js", "forward");
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
        forward: {
            icon: "fa-paper-plane",
            title: "全群转发"
        },
        globalTimer: {
            icon: "fa-clock",
            title: "全群定时"
        },
        globalKeyword: {
            icon: "fa-book",
            title: "全群回复"
        },
        tips: {
            icon: "fa-info-circle",
            title: "群提示语"
        }
    }
};
$script.ready(["user", "general", "forward", "globalTimer", "globalKeyword", "tips"], function () {
    Web.user = wxbot.getUserInfo();
    Web.serverURL = wxbot.getDomain()+":9527/";
    Web.wxHost = wxbot.getHostUrl();
    let data = Object.assign({
        header:{},
        setting:{},
        chatRooms:[]
    }, {user:User.data}, {general:General.data}, {forward:Forward.data}, {globalTimer:GlobalTimer.data}, {globalKeyword:GlobalKeyword.data}, {tips:Tips.data});
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
            this.chatRooms = Object.freeze(wxbot.getChatRooms())
            this.generalReset();
            this.getMsgs();
            this.getKeyMap();
            this.tipsReset();
        },
        onMembersSeqChanged(seqMap){
            app.modKeywords(seqMap);
        }
    }, User.methods, General.methods, Forward.methods, GlobalTimer.methods, GlobalKeyword.methods, Tips.methods);
    let computed = Object.assign({}, User.computed, General.computed, Forward.computed, GlobalTimer.computed, GlobalKeyword.computed, Tips.computed);
    app = new Vue({
        el: "#app",
        data: data,
        computed:computed,
        mounted() {
            this.syncSetting();
        },
        methods: methods
    });
})