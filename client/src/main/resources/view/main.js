$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script("lib/jquery/jquery-3.3.1.min.js", "jquery");
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
$script.ready(["vue", "iview", "jquery", "crypto", "common"], function () {
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
var app;
$script.ready(["setting", "contacts", "chat", "keyword"], function () {
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
    }, Setting.methods, Contacts.methods, Chat.methods, Keyword.methods);

    let computed = Object.assign({}, Setting.computed, Contacts.computed, Chat.computed, Keyword.computed);
    app = new Vue({
        el: "#app",
        data: {
            skin: "dark",
            setting: Setting.data,
            contacts: Contacts.data,
            chat: Chat.data,
            keyword: Keyword.data
        },
        computed:computed,
        mounted() {
            this.syncKeywords();
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