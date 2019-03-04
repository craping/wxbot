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
    let data = Object.assign({
        header:{}
    }, {user:User.data}, {general:General.data}, {forward:Forward.data}, {timer:Timer.data}, {keyword:Keyword.data}, {tips:Tips.data});
    let methods = Object.assign({}, User.methods, General.methods, Forward.methods, Timer.methods, Keyword.methods, Tips.methods);
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