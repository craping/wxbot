$script("lib/vue/vue.js", "vue", () => {
    $script([
    "lib/vue-virtual-scroller/intersection-observer.js", "lib/vue-virtual-scroller/vue-observe-visibility.min.js", 
    "lib/vue-virtual-scroller/vue-virtual-scroller.min.js", 
    "lib/iview-3/iview.min.js"], "vue-plugs");
});
$script('lib/jquery/jquery-3.3.1.min.js', 'jquery', () => {
    // $script(['lib/bootstrap-4.2.1-dist/js/popper.min.js', 'lib/bootstrap-4.2.1-dist/js/bootstrap.min.js'], 'bootstrap');
});
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
$script.ready(["vue-plugs", "jquery", "crypto", "common"], () => {
    $("#header").load("module/header/header.html", {}, () => {
        $script("module/header/header.js", "header");
    });
    $("#contacts").load("module/contacts/contacts.html", {}, () => {
        $script("module/contacts/contacts.js", "contacts");
    });
    $("#chat").load("module/chat/chat.html", {}, () => {
        $script("module/chat/chat.js", "chat");
    });
    $("#keyword").load("module/keyword/keyword.html", {},() => {
        $script("module/keyword/keyword.js", "keyword");
    });
    $("#timer").load("module/timer/timer.html", {}, () => {
        $script("module/timer/timer.js", "timer");
    });
    $("#info").load("module/info/info.html", {}, () => {
        $script("module/info/info.js", "info");
    });
})
var app;
$script.ready(["header", "contacts", "chat", "keyword", "timer", "info"], () => {
    Web.user = wxbot.getUserInfo();
    Web.serverURL = wxbot.getDomain()+":9527/";
    Web.root = wxbot.getRootPath();
    Web.wxHost = wxbot.getHostUrl();
    Web.owner = wxbot.getOwner();
    Chat.data.ownerHeadImg = Web.wxHost + Web.owner.HeadImgUrl;

    let methods = Object.assign({
        filterAll(data, argumentObj) {
            return data.filter(d => {
                for (let argu in argumentObj) {
                    if (d[argu] != null && d[argu].toUpperCase().includes(argumentObj[argu].toUpperCase()))
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
                        return d[argu].includes(argumentObj[argu]);
                    });
                    dataClone = res;
                }
            }
            return res;
        },
        exportFile(content, fileName) {

            let aEle = document.createElement("a"), blob = new Blob([new String(content, "utf-8")]);

            aEle.download = fileName;

            aEle.href = URL.createObjectURL(blob);

            aEle.click();
        },
        syncSeq(seqMap){
            this.contacts.individuals.forEach(e => {
                const newSeq = seqMap[e.seq];
                if(newSeq)
                    e.seq = newSeq;
            });
            this.contacts.chatRooms.forEach(e => {
                const newSeq = seqMap[e.seq];
                if(newSeq)
                    e.seq = newSeq;
            });
            const newSeq = seqMap[this.chat.seq];
            if(newSeq)
                this.chat.seq = newSeq;

            console.log("syncSeq:"+seqMap);
            Web.ajax("contact/syncSeq", {
                data:{
                    seqMap:JSON.stringify(seqMap)
                },
                success: function (data) {
                    console.log("seq同步成功")
                },
                fail: function (data) {
                    console.log("seq同步失败")
                },
                error: function(){
                    console.log("seq同步失败")
                }
            });
        },
        syncSetting(){
            const me = this;
            Web.ajax("setting/getSetting", {
                success: function (data) {
                    wxbot.syncSetting(data.info);
                },
                fail: function (data) {
                }
            });
        },
        global_click(event){
            if(this.$refs.recordDatePicker && !this.$refs.recordDatePicker.$el.contains(event.target))
                this.chat.datePickerOpen = false;
            console.log("click");
        }
    }, Header.methods, Contacts.methods, Chat.methods, Keyword.methods, Timer.methods, Info.methods);

    let computed = Object.assign({}, Header.computed, Contacts.computed, Chat.computed, Keyword.computed, Timer.computed, Info.computed);
    app = new Vue({
        el: "#app",
        data: {
            skin: "dark",
            rightTab:"info",
            header: Header.data,
            contacts: Contacts.data,
            chat: Chat.data,
            keyword: Keyword.data,
            timer: Timer.data,
            info: Info.data
        },
        computed:computed,
        mounted() {
            this.syncSetting();
            this.syncKeywords();
            this.syncTimers();
        },
        updated: function () {
        },
        methods: methods
    });
})

function text() {
    return { a: 1, b: 2 };
}