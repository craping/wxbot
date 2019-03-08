$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script("lib/jquery/jquery-3.3.1.min.js", "jquery");
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
$script.ready(["vue", "iview", "jquery", "crypto", "common"], function () {
    $("#header").load("module/header/header.html", {}, function () {
        $script("module/header/header.js", "header");
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
    $("#timer").load("module/timer/timer.html", {}, function () {
        $script("module/timer/timer.js", "timer");
    });
    $("#info").load("module/info/info.html", {}, function () {
        $script("module/info/info.js", "info");
    });
})
var app;
$script.ready(["header", "contacts", "chat", "keyword", "timer", "info"], function () {
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
        onMembersSeqChanged(seqMap){
            app.modKeywords(seqMap);
        }
    }, Header.methods, Contacts.methods, Chat.methods, Keyword.methods, Timer.methods, Info.methods);

    let computed = Object.assign({}, Header.computed, Contacts.computed, Chat.computed, Keyword.computed, Timer.computed, Info.computed);
    app = new Vue({
        el: "#app",
        data: {
            skin: "dark",
            header: Header.data,
            contacts: Contacts.data,
            chat: Chat.data,
            keyword: Keyword.data,
            timer: Timer.data,
            info: Info.data
        },
        computed:computed,
        mounted() {
            this.syncKeywords();
            this.syncTimers();
            this.loadContacts();
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