Contacts = {
    data: {
        individuals: [],
        chatRooms: []
    },
    methods: {
        // 处理emoji表情
        emojiFormatter(userName) {
            return wxbot.getEmoji(userName);
        },
        // 初始化联系人列表
        loadContacts() {
            Contacts.data.chatRooms = wxbot.getChatRooms();
            Contacts.data.individuals = wxbot.getIndividuals();
            console.log(Contacts.data.individuals);
            //console.log($('#avatar_683740735').html());
            this.$nextTick(() => {
                Contacts.methods.syncContacts();
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
        // 同步联系人、群聊到服务端
        syncContacts() {
            let idis = []; // 联系人
            let crs = [];  // 群聊
            let promises = [];
            Contacts.data.individuals.forEach(e => {
                promises.push(Promise.resolve(e).then(c => {
                    const imgSrc = $("#avatar_" + c.seq + " img").attr("src");
                    return Contacts.methods.getBase64Image(imgSrc).then(base64 => {
                        idis.push({
                            seq: c.seq,
                            headImgUrl: base64,
                            nickName: c.NickName
                        });
                    })
                }));
            });

            Contacts.data.chatRooms.forEach(e => {
                promises.push(Promise.resolve(e).then(c => {
                    const imgSrc = $("#avatar_" + c.seq + " img").attr("src");
                    return Contacts.methods.getBase64Image(imgSrc).then(base64 => {
                        crs.push({
                            seq: c.seq,
                            headImgUrl: base64,
                            nickName: c.NickName
                        });
                    })
                }));
            });

            // Promise.all(promises).then(() => {
            //     Web.ajax("contact/syncContacts", {
            //         data: {
            //             idis: idis,
            //             crs: crs
            //         },
            //         success: function (data) {
            //             console.log(data);
            //         },
            //         fail: function (data) {
            //         }
            //     });
            // });
        },
        // 获取host url
        hostUrl() {
            return wxbot.getHostUrl();
        },
        // 初始化聊天窗口
        startChat(seq, nickName, userName, headImgUrl) {
            Chat.data.seq = seq;
            Chat.data.title = nickName;
            Chat.data.userName = userName;
            Chat.data.userHeadImg = wxbot.getHostUrl() + headImgUrl;
            Chat.data.ownerHeadImg = wxbot.getOwnerHeadImgUrl();
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            $('#avatar_' + seq + " sup.ivu-badge-count").remove();
        },
        // 初始化群聊窗口、群成员列表
        startGroupChat(seq, nickName, userName) {
            Chat.data.seq = seq;
            Chat.data.title = nickName;
            Chat.data.userName = userName;
            Chat.data.ownerHeadImg = wxbot.getOwnerHeadImgUrl();
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            Info.data.members = wxbot.getChatRoomMembers(userName);
            $('#avatar_' + seq + " sup.ivu-badge-count").remove();
            this.loadKeyMap(seq);
            this.loadMsgs(seq);
        }
    }
}