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
    $("#keywords").load("module/keywords/keywords.html", {}, function () {
        $script("module/keywords/keywords.js", "keywords");
    });
})

$script.ready(["setting", "contacts", "chat", "keywords"], function () {
    new Vue({
        el: "#app",
        data: {
            skin: "dark",
            Setting: Setting.data,
            Contacts: Contacts.data,
            Chat: Chat.data,
            Keywords: Keywords.data
        },
        methods: {
            Setting: Setting.methods,
            Contacts: Contacts.methods,
            Chat: Chat.methods,
            Keywords: Keywords.methods
        }
    });
})