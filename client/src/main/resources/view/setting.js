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
    let chatRooms = [];
    for (let i = 0; i < 50; i++) {
        chatRooms.push({
            seq:"65535"+i,
            NickName:"上帝群 明天香港采购🇭🇰",
            status:1
        },{
            seq:"65536"+i,
            NickName:"华强电脑 古城便民服务（一）群",
            status:1
        });
    }
    let data = Object.assign({
        header:{},
        setting:{
            switchs:{
                autoAcceptFriend:true,
                globalKeyword:true,
                globalTimer:true
            },
            forwards:[]
        },
        chatRooms:Object.freeze(chatRooms)
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
        syncSetting(){
            const me = this;
            Web.ajax("setting/getSetting", {
                success: function (data) {
                    me.setting = data.info;
                },
                fail: function (data) {
                }
            });
        },
        setTips(tips){
            const me = this;
            Web.ajax("setting/setTips", {
                data:tips,
                success: function (data) {
                    me.setting = data.info;
                },
                fail: function (data) {
                }
            });
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
            // this.syncSetting();
            this.loadMsgs();
            this.loadKeyMap();
            this.generalReset();
        },
        methods: methods
    });
})