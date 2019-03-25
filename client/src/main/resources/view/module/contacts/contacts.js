Contacts = {
    data: {
        individuals: [],
        chatRooms: [],
        filterKey: '', // 搜索关键字
        searchResult: [], // 搜索结果
        contactTab: 'contact',
        loading: true
    },
    computed:{
        filterIndividuals(){
            let me = this;
            return me.filterAll(me.contacts.individuals, {
                RemarkName: me.contacts.filterKey,
                RemarkPYInitial: me.contacts.filterKey,
                RemarkPYQuanPin: me.contacts.filterKey,
                NickName: me.contacts.filterKey,
                PYInitial: me.contacts.filterKey,
                PYQuanPin: me.contacts.filterKey
            });
        },
        filterChatRooms(){
            let me = this;
            return me.filterAll(me.contacts.chatRooms, {
                RemarkName: me.contacts.filterKey,
                RemarkPYInitial: me.contacts.filterKey,
                RemarkPYQuanPin: me.contacts.filterKey,
                NickName: me.contacts.filterKey,
                PYInitial: me.contacts.filterKey,
                PYQuanPin: me.contacts.filterKey
            });
        }
    },
    methods: {
        // 导出联系人txt
        saveContactsAsTxt() {
            var urlObject = window.URL || window.webkitURL || window;
            var content = "************ 联系人 ************\n";
            this.contacts.individuals.forEach(e => {
                content += wxbot.rmEmoji(e.NickName) + "\n";
            });
            content += "************ 群聊 ************\n";
            this.contacts.chatRooms.forEach(e => {
                content += wxbot.rmEmoji(e.NickName) + "\n";
            });
            this.exportFile(content ,"联系人.txt");
        },
        // 加载通讯录
        loadContacts() {
            this.loadIndividuals();
            this.loadChatRooms();
        },
        //加载联系人
        loadIndividuals(){
            wxbot.getIndividuals(data => {
                this.contacts.individuals = data.sort((e, t) => {
                    e.count = 0;
                    t.count = 0;
                    e.HeadImgUrl += "r="+Date.now();
                    t.HeadImgUrl += "r="+Date.now();
                    e.MMOrderSymbol = this.getContactOrderSymbol(e);
                    t.MMOrderSymbol = this.getContactOrderSymbol(t);
                    return e.MMOrderSymbol > t.MMOrderSymbol ? 1 : -1
                });
                this.contacts.loading = false;
            })
        },
        //加载群聊
        loadChatRooms(){
            wxbot.getChatRooms(data => {
                this.contacts.chatRooms = data.sort((e, t) => {
                    e.count = 0;
                    t.count = 0;
                    e.HeadImgUrl += "r="+Date.now();
                    t.HeadImgUrl += "r="+Date.now();
                    e.MMOrderSymbol = this.getContactOrderSymbol(e);
                    t.MMOrderSymbol = this.getContactOrderSymbol(t);
                    return e.MMOrderSymbol > t.MMOrderSymbol ? 1 : -1
                });
                this.syncChatRooms();
            })
        },
        addContact(contacts){
            contacts.forEach(e => {
                e.count = 0;
                if(e.UserName.includes("@@"))
                    this.contacts.chatRooms.push(e);
                else
                    this.contacts.individuals.push(e);
            });
            this.syncChatRooms();
        },
        modContact(contacts){
            contacts.forEach(e => {
                const contacts = e.UserName.includes("@@")?this.contacts.chatRooms:this.contacts.individuals;
                let index = -1;
                let contact = contacts.find((c, i) => {
                    index = i;
                    return c.UserName == e.UserName;
                });
                if(contact){
                    e.count = contact.count;
                    contacts.splice(index, 1, e);
                }
            });
            this.syncChatRooms();
        },
        delContact(contacts){
            contacts.forEach(e => {
                const contacts = userName.includes("@@")?this.contacts.chatRooms:this.contacts.individuals;
                const index = contacts.findIndex(c => c.UserName == e.UserName);
                if(index != -1)
                    contacts.splice(index, 1);
            });
            this.syncChatRooms();
        },
        //联系人置顶
        setContactToTop(userName){
            const contacts = userName.includes("@@")?this.contacts.chatRooms:this.contacts.individuals;
            let index = -1;
            let contact = contacts.find((e, i) => {
                index = i;
                return e.UserName == userName;
            });
            if (contact) {
                contacts.splice(index, 1);
                contacts.splice(0, 0, contact);
            }
        },
        setCount(userName, count){
            const contacts = userName.includes("@@")?this.contacts.chatRooms:this.contacts.individuals;
            let contact = contacts.find(e => e.UserName == userName);
            if(contact && contact.count){
                if(count != undefined)
                    contact.count = count;
                else
                    contact.count ++;
            } else {
                if(count != undefined)
                    contact.count = count;
                else
                    contact.count = 1;
            }

        },
        reloadContacts() {
            this.contacts.loading = true;
            this.loadContacts();
            this.$nextTick(() => {
                this.contacts.loading = false;
                this.$Message.success("刷新联系人列表成功！");
            });
        },
        // 图片 base64
        getBase64Image(imgSrc) {
            var image = new Image();
            image.crossOrigin = '';
            image.src = imgSrc;
            var deferred = $.Deferred();
            image.onload = function () {
                var canvas = document.createElement("canvas");
                canvas.width = 35;
                canvas.height = 35;
                var ctx = canvas.getContext("2d");
                ctx.drawImage(image, 0, 0, canvas.width, canvas.height);
                deferred.resolve(canvas.toDataURL("image/jpeg"));//将base64传给done上传处理
            }
            return deferred.promise();//问题要让onload完成后再return;
        },
        // 同步群聊到服务端
        syncChatRooms() {
            let crs = [];  // 群聊
            let promises = [];

            this.contacts.chatRooms.forEach(e => {
                promises.push(Promise.resolve(e).then(c => {
                    return Contacts.methods.getBase64Image(Web.wxHost + e.HeadImgUrl).then(base64 => {
                        crs.push({
                            seq: c.seq,
                            headImgUrl: base64,
                            nickName: c.NickName
                        });
                    })
                }));
            });
            
            Promise.all(promises).then(() => {
                Web.ajax("contact/syncContacts", {
                    data: {
                        crs: crs
                    },
                    success: function (data) {
                        console.log("联系人同步成功!");
                    },
                    fail: function (data) {
                        console.error("联系人同步失败：" + data.msg);
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        console.error("联系人同步失败:" + textStatus);
                    }
                });
            });
        },
        // 初始化聊天窗口
        startChat(item) {
            if(this.chat.userName != item.UserName) {
                this.chat.seq = item.seq;
                this.chat.title = item.RemarkName || item.NickName;
                this.chat.userName = item.UserName;
                this.chat.userHeadImg = Web.wxHost + item.HeadImgUrl;
                this.chat.recordDate = new Date().format("yyyy-MM-dd");
                this.info.user = item;
                this.info.members = [];
                item.count = 0;
                if(this.contacts.contactTab == "chatroom"){
                    this.getKeyMap(item.seq);
                    this.getMsgs(item.seq);
                    this.rightTab = "keyword";
                } else {
                    this.rightTab = "info";
                }
                this.resetChatRecord(item.seq);
            }
            this.contacts.filterKey = "";
        },
        getContactOrderSymbol: function(e) {
            if (!e)
                return "";
            var t = "";
            return t = this.clearHtmlStr(e.RemarkPYQuanPin || e.PYQuanPin || e.NickName || "").toLocaleUpperCase().replace(/\W/gi, ""),
            t.charAt(0) < "A" && (t = "~"),
            t
        },
        clearHtmlStr: function(e) {
            return e ? e.replace(/<[^>]*>/g, "") : e
        }
    }
}