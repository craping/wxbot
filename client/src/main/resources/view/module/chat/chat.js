Chat = {
    data: {
        userHeadImg: "", //接受者头像
        ownerHeadImg: "", // 当前用户头像
        userName: "",   // 接受者
        seq: "",        // 用户唯一值
        title: " ",     // 窗口标题
        text: "",       // 聊天文本
        chatRecord: [{

        }],
    },
    methods: {
        // 获取群成员头像
        memberHeadImg(chatRoomName, memberUserName) {
            return wxbot.getChatRoomMemberHeadImgUrl(chatRoomName, memberUserName);
        },
        // 消息提醒
        alertUtil(type, msg) {
            if (type == "SUCCESS") {
                app.$Message.success(msg);
                return false;
            } else if (type == "WARNING") {
                app.$Message.warning(msg);
                return false;
            } else if (type == "ERROR") {
                app.$Message.error(msg);
                return false;
            } else {
                app.$Message.error('操作错误');
                return false;
            }
        },
        // 发送文件
        sendApp() {
            if (this.chat.title ==' ') {
                this.$Message.error('请选择一个聊天好友');
                return false;
            }
            wxbot.openAppFile(this.chat.seq, this.chat.title, this.chat.userName);
        },
        // 发送文本消息
        sendText() {
            if (this.chat.text == ""){
                this.$Message.warning('不能发送空消息');
                return false;
            }
            wxbot.sendText(this.chat.seq, this.chat.title, this.chat.userName, this.chat.text);
            Chat.data.text = "";
            Chat.methods.reloadChat(this.chat.seq);
        },
        // 重新加载聊天信息窗口
        reloadChat(seq) {
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            Chat.methods.scrollToBottom();
        },
        // 渲染 新消息
        newMessage(seq) {
            if (seq == Chat.data.seq) {
                Chat.methods.reloadChat(String(seq));
            } else {
                var _html = $('#avatar_' + seq).html();
                _html = _html + "<sup class='ivu-badge-dot'></sup>";
                $('#avatar_' + seq).html(_html);
            }
        },
        // 渲染新语音消息
        newVoiceMessage(param) {
            var _html = $('#popover_' + param).html();
            _html = _html + "<sup class='ivu-badge-dot'></sup>";
            $('#popover_' + param).html(_html);
        },
        // 获取图片高、宽
        imgHeightOrWidth(path, type) {
            return wxbot.getImgHeightOrWidth(path, type);
        },
        // 播放视频
        mediaPlay(path) {
            wxbot.mediaPlay(path);
        },
        //播放语音
        voicePlay(path, param) {
            wxbot.voicePlay(path);
            $('#popover_'+param+" sup.ivu-badge-dot").remove();
        },
        // 获取文件绝对路径
        realUrl(path) {
            return wxbot.getRealUrl(path);
        },
        // 聊天窗口滚动条自动底部
        scrollToBottom: function () {
            var container = document.getElementById('chatcontheight');
            if (container != null){
                //console.log(container);
                container.scrollTop = container.scrollHeight;
            }
        },
    },
}