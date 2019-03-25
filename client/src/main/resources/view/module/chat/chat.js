Chat = {
    data: {
        userHeadImg: "", //接受者头像
        ownerHeadImg: "", // 当前用户头像
        userName: "",   // 接受者
        seq: "",        // 用户唯一值
        title: " ",     // 窗口标题
        text: "",       // 聊天文本
        chatRecord: [],
        recordDate:new Date().format("yyyy-MM-dd"),
        hasMore:false,
        loading:false,
        datePickerOpen:false,
        options:{
            disabledDate(date){
                return date && date.getTime() > Date.now();
            }
        }
    },
    methods: {
        // 获取群成员头像
        memberHeadImg(chatRoomName, memberUserName) {
            return wxbot.getChatRoomMemberHeadImgUrl(chatRoomName, memberUserName);
        },
        // 发送文件
        sendApp() {
            if (this.chat.title ==' ') {
                this.$Message.error('请选择一个聊天好友');
                return false;
            }
            setTimeout(() => {
                wxbot.openAppFile(this.chat.userName);
            }, 5);
        },
        // 发送文本消息
        sendText() {
            const text = $("#chatText").val();
            if (text == ""){
                this.$Message.warning('不能发送空消息');
                return false;
            }
            wxbot.sendText(this.chat.seq, this.chat.title, this.chat.userName, text);
            $("#chatText").val("");
            this.chat.chatRecord.push({
                "timestamp": Date.now()+"",
                "to": this.chat.title,
                "from": Web.owner.NickName,
                "avatar": null,
                "chatType": 2,
                "direction": 1,
                "msgType": 1,
                "body": {
                    "content": text
                }
            });
            this.scrollToBottom();
        },
        moreChatRecord(){
            this.chat.loading = true;
            this.loadChatRecord(this.chat.seq);
        },
        // 加载聊天信息窗口
        loadChatRecord(seq) {
            wxbot.chatRecord(seq, this.chat.recordDate, data =>{
                if(data.length == 30)
                    this.chat.hasMore = true;
                else
                    this.chat.hasMore = false;
                this.chat.chatRecord.unshift(...data);
                this.chat.loading = false;
            });
        },
        resetChatRecord(seq) {
            wxbot.resetChatRecord(seq, this.chat.recordDate, data =>{
                if(data.length == 30)
                    this.chat.hasMore = true;
                else
                    this.chat.hasMore = false;
                this.chat.chatRecord = data;
                this.chat.loading = false;
                this.scrollToBottom();
            });
        },
        // 渲染 新消息
        newMessage(userName, msg) {
            if (userName == this.chat.userName) {
                // 渲染新语音消息
                if(msg.msgType == 34)
                    msg.played = false;
                this.chat.chatRecord.push(msg);
                this.scrollToBottom(true);
            } else {
                this.setCount(userName);
                $("#msgAudio")[0].play();
            }
            this.setContactToTop(userName);
        },
        charRecordPicker(date){
            this.chat.recordDate = date;
            this.chat.datePickerOpen = false;
            this.resetChatRecord(this.chat.seq);
        },
        // 获取图片高、宽
        imgHeightOrWidth(path, type) {
            return wxbot.getImgHeightOrWidth(path, type);
        },
        // 多媒体播放
        mediaPlay(item) {
            wxbot.mediaPlay(item);
            item.played = true;
            this.$forceUpdate();
        },
        // 获取文件绝对路径
        realUrl(path) {
            return wxbot.getRealUrl(path);
        },
        // 聊天窗口滚动条自动底部
        scrollToBottom: function (scrolling) {
            this.$nextTick(() => {
                if(scrolling){
                    if((this.$refs.chatScroller.$el.scrollTop + 410) >= (this.$refs.chatScroller.$el.scrollHeight - 410))
                        this.$refs.chatScroller.$el.scrollTop = this.$refs.chatScroller.$el.scrollHeight;
                }else{
                    this.$refs.chatScroller.$el.scrollTop = this.$refs.chatScroller.$el.scrollHeight;
                }
                // this.$refs.chatScroller.scrollToBottom();
            })
        },
    },
}