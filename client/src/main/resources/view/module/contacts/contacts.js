Contacts = {
    data: {
        individuals: [],
        chatRooms: [],
        filterKey: '', // 搜索关键字
        searchResult: [], // 搜索结果
        contactTab: 'contact',
        loading: false
    },
    methods: {
        // 导出联系人txt
        saveContactsAsTxt() {
            var urlObject = window.URL || window.webkitURL || window;
            var _outStr = "************ 联系人 ************\n";
            Contacts.data.individuals.forEach(e => {
                _outStr = _outStr + wxbot.rmEmoji(e.NickName) + "\n";
            });
            _outStr = _outStr + "************ 群聊 ************\n";
            Contacts.data.chatRooms.forEach(e => {
                _outStr = _outStr + wxbot.rmEmoji(e.NickName) + "\n";
            });
            var export_blob = new Blob([_outStr]);
            var save_link = document.createElementNS("http://www.w3.org/1999/xhtml", "a")
            save_link.href = urlObject.createObjectURL(export_blob);
            save_link.download = "联系人.txt";
            var ev = document.createEvent("MouseEvents");
            ev.initMouseEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            save_link.dispatchEvent(ev);
        },
        // 通过昵称，群名称关键字可搜索
        searchContacts() {
            Contacts.data.searchResult = [];
            let _searchResult = this.$refs.searchResult;
            let _not_found = _searchResult.firstChild;
            let _result = _searchResult.querySelector(".ivu-select-dropdown-list");
            let _loading = _searchResult.lastChild;
            // 初始化搜索结果div 设置display：none 隐藏
            _searchResult.style['display'] = "none";
            _not_found.style['display'] = "none";
            _result.style['display'] = "none";
            _loading.style['display'] = "none";
            // 搜索关键字
            const filterKey = Contacts.data.filterKey;
            if (filterKey && filterKey.length > 0) {
                _searchResult.style['display'] = "block";
                _loading.style['display'] = "block";
                setTimeout(() => {
                    let me = this;
                    if (Contacts.data.contactTab == "chatroom") {
                        Contacts.data.searchResult = me.filterAll(Contacts.data.chatRooms.map(e => {
                            return {
                                seq: e.seq,
                                UserName: e.UserName,
                                HeadImgUrl: e.HeadImgUrl,
                                NickName: Contacts.methods.emojiFormatter(e.NickName)
                            };
                        }), {
                                NickName: filterKey
                        });
                    } else if (Contacts.data.contactTab == "contact") {
                        Contacts.data.searchResult = me.filterAll(Contacts.data.individuals.map(e => {
                            return {
                                seq: e.seq,
                                UserName: e.UserName,
                                HeadImgUrl: e.HeadImgUrl,
                                NickName: Contacts.methods.emojiFormatter(e.NickName)
                            };
                        }), {
                                NickName: filterKey
                        });
                    } else {
                        // app.$Message.error("操作失败！"); // 没有匹配到tab name
                    }

                    _loading.style['display'] = "none";
                    // 未匹配任何结果
                    if (Contacts.data.searchResult.length == 0) {
                        _not_found.style['display'] = "block";
                    } else {
                        _result.style['display'] = "block";
                    }
                }, 500);
            }
        },
        // 初始化联系人列表
        loadContacts() {
            console.log(wxbot.getIndividuals());
            Contacts.data.chatRooms = wxbot.getChatRooms();
            Contacts.data.individuals = wxbot.getIndividuals();
            //console.log($('#avatar_683740735').html());
            // this.$nextTick(() => {
            //     //Contacts.methods.syncContacts();

            // });
        },
        // 重新加载联系人列表
        reloadContacts() {
            let me = this;
            Contacts.data.loading = true;
            setTimeout(() => {
                this.loadContacts();
                this.$nextTick(() => {
                    Contacts.data.loading = false;
                    me.$Message.success("刷新联系人列表成功！");
                });
            }, 1000);
        },
        // 处理好友，群成员变动
        execContactsChanged(msg) {
            Contacts.methods.loadContacts();
            app.$nextTick(() => {
                app.$Message.success(String(msg));
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
        // 处理emoji表情
        emojiFormatter(userName) {
            return wxbot.getEmoji(userName);
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
            Contacts.data.filterKey = "";
            this.$refs.searchResult.style['display'] = "none";
        },
        // 初始化群聊窗口、群成员列表
        startGroupChat(seq, nickName, userName) {
            Chat.data.seq = seq;
            Chat.data.title = nickName;
            Chat.data.userName = userName;
            Chat.data.ownerHeadImg = wxbot.getOwnerHeadImgUrl();
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            Info.data.members = wxbot.getChatRoomMembers(userName);
            Info.data.chatRoomUserName = userName;
            $('#avatar_' + seq + " sup.ivu-badge-count").remove();
            Contacts.data.filterKey = "";
            this.$refs.searchResult.style['display'] = "none";
            //this.loadKeyMap(seq);
            //this.loadMsgs(seq);
        }
    }
}