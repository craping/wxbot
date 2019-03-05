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
    $("#timer").load("settingModule/timer/timer.html", {}, function () {
        $script("settingModule/timer/timer.js", "timer");
    });
    $("#keyword").load("settingModule/keyword/keyword.html", {}, function () {
        $script("settingModule/keyword/keyword.js", "keyword");
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
            title: "群转发"
        },
        timer: {
            icon: "fa-clock",
            title: "定时群发"
        },
        keyword: {
            icon: "fa-book",
            title: "群关键词"
        },
        tips: {
            icon: "fa-info-circle",
            title: "群提示语"
        }
    }
};
$script.ready(["user", "general", "forward", "timer", "keyword", "tips"], function () {
    let chatRooms = [];
    for (let i = 0; i < 250; i++) {
        chatRooms.push({
            seq:"65535",
            NickName:"上帝群 明天香港采购🇭🇰",
            status:1
        },{
            seq:"65536",
            NickName:"华强电脑 古城便民服务（一）群",
            status:1
        });
    }
    let data = Object.assign({
        header:{},
        chatRooms:Object.freeze(chatRooms)
    }, {user:User.data}, {general:General.data}, {forward:Forward.data}, {timer:Timer.data}, {keyword:Keyword.data}, {tips:Tips.data});
    let methods = Object.assign({
        filterAll(data, argumentObj) {
            return data.filter(d => {
                for (let argu in argumentObj) {
                    if (d[argu].indexOf(argumentObj[argu]) > -1)
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
        onMembersSeqChanged(seqMap){
            app.modKeywords(seqMap);
        }
    }, User.methods, General.methods, Forward.methods, Timer.methods, Keyword.methods, Tips.methods);
    let computed = Object.assign({}, User.computed, General.computed, Forward.computed, Timer.computed, Keyword.computed, Tips.computed);
    app = new Vue({
        el: "#app",
        data: data,
        computed:computed,
        mounted() {
            
        },
        methods: methods
    });
})