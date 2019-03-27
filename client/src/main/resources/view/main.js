$script("lib/vue/vue.js", "vue", () => {
    $script([
    "lib/vue-virtual-scroller/intersection-observer.js", "lib/vue-virtual-scroller/vue-observe-visibility.min.js", 
    "lib/vue-virtual-scroller/vue-virtual-scroller.min.js", 
    "lib/iview-3/iview.min.js"], "vue-plugs");
});
$script('lib/jquery/jquery-3.3.1.min.js', 'jquery', () => {
    $script(["lib/qrcode.min.js"], "jquery-plugs");
    // $script(['lib/bootstrap-4.2.1-dist/js/popper.min.js', 'lib/bootstrap-4.2.1-dist/js/bootstrap.min.js'], 'bootstrap');
});
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
$script.ready(["vue-plugs", "jquery-plugs", "crypto", "common"], () => {
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
                    if(data.info){
                        wxbot.syncSetting(data.info);
                        if(data.info.permissions)
                            me.permissions = data.info.permissions;
                    }
                },
                fail: function (data) {
                }
            });
        },
        sync(){
            var deferred = $.Deferred();
            const me = this;
            Web.ajax("api/sync", {
                timeout:35000,
                success: function (data) {
                    deferred.resolve(data);
                },
                fail: function (data) {
                    deferred.reject();
                    me.sync();
                },
                error: function(){
                    deferred.reject();
                    me.sync();
                }
            }, "sync");
            return deferred.promise();
        },
        handling(){
            const me = this;
            this.sync().then(events => {
                events.forEach(msg => {
                    console.log(msg);
                    const data = msg.data;
                    switch (msg.biz) {
                        case "SETTING":
                        case "SWITCHS":
                            me.syncSetting();
                            break;
                        case "PERMISSIONS":
                            me.permissions = data;
                            wxbot.syncPermissions(data);
                            break;
                        case "KEYWORD":
                            switch (msg.action) {
                                case "DEL":
                                    wxbot.delKeyMap(data.seq, data.keyList[0]);
                                    break;
                                default:
                                    wxbot.setKeyMap(data.seq, data.key, data.msg.type, data.msg.content);
                                    break;
                            }
                            if(data.seq == me.keyword.form.seq){
                                me.getKeyMap(data.seq);
                            }
                            break;
                        case "TIMER":
                            switch (msg.action) {
                                case "DEL":
                                    wxbot.delMsg(data.seq, data.uuid);
                                    break;
                                default:
                                    wxbot.addMsg(data.se, data.timer);
                                    break;
                            }
                            if(data.seq == me.timer.form.seq){
                                me.getMsgs(data.seq);
                            }
                            break;
                        case "NOTICE":
                            data.read = false;
                            me.notify(data);
                            wxbot.noticeForward(data.content);
                            break;
                        default:
                            break;
                    }
                })
                this.handling();
            },() => {
                console.log("reject");
                this.handling();
            });
        },
        global_click(event){
            if(this.$refs.recordDatePicker && !this.$refs.recordDatePicker.$el.contains(event.target))
                this.chat.datePickerOpen = false;
        }
    }, Header.methods, Contacts.methods, Chat.methods, Keyword.methods, Timer.methods, Info.methods);

    let computed = Object.assign({}, Header.computed, Contacts.computed, Chat.computed, Keyword.computed, Timer.computed, Info.computed);
    app = new Vue({
        el: "#app",
        data: {
            skin: "dark",
            rightTab:"info",
            permissions:{},
            header: Header.data,
            contacts: Contacts.data,
            chat: Chat.data,
            keyword: Keyword.data,
            timer: Timer.data,
            info: Info.data
        },
        computed:computed,
        updated: function () {
        },
        mounted() {
            this.syncSetting();
            // this.loadContacts();
            this.syncKeywords();
            this.syncTimers();
            this.handling();
            this.noticeList();
            this.syncTuringKey();
            new QRCode(document.getElementById("wapSite"), this.header.wapSite.url);
        },
        methods: methods
    });
})