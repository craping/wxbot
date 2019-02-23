$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script("lib/jquery/jquery-3.3.1.min.js", "jquery");
$script("lib/common.js", "common");
$script.ready(["vue", "iview", "jquery", "common"], function () {
    $("#setting").load("module/setting/setting.html", {}, function () {
        $script("module/setting/setting.js", "setting");
    });
    $("#contacts").load("module/contacts/contacts.html", {}, function () {
        $script("module/contacts/contacts.js", "contacts");
    });
    $("#chat").load("module/chat/chat.html", {}, function () {
        $script("module/chat/chat.js", "chat");
    });
    $("#keyword").load("module/keyword/keyword.html", {}, function () {
        $script("module/keyword/keyword.js", "keyword");
    });
})

$script.ready(["setting", "contacts", "chat", "keyword"], function () {
    let methods = Object.assign({}, Setting.methods, Contacts.methods, Chat.methods, Keyword.methods);
    new Vue({
        el: "#app",
        data: {
            skin: "dark",
            setting: Setting.data,
            contacts: Contacts.data,
            chat: Chat.data,
            keyword: Keyword.data
        },
        mounted() {
            methods.loadIndividuals(); // 加载联系人列表
        },
        updated: function () {
            methods.scrollToBottom();
        },
        methods: methods
    });
})

function text() {
    return { a: 1, b: 2 };
}