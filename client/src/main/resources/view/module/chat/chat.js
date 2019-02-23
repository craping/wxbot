Chat = {
    data: {
        userHeadImg: "", //接受者头像
        ownerHeadImg: "", // 当前用户头像
        userName: "",   // 接受者
        seq: "",        // 用户唯一值
        title: " ",     // 窗口标题
        text: "",       // 聊天文本
        chatRecord: [{ }],
    },
    methods: {
        sendApp() {
            wxbot.sendApp(null);
        },
        // 发送文本消息
        sendText() {
            if (this.chat.text == "")
                return false;

            wxbot.sendText(this.chat.seq, this.chat.title, this.chat.userName, "chat", this.chat.text);
            Chat.data.text = "";
            Chat.methods.reloadChat(this.chat.seq);
        },
        // 重新加载聊天信息窗口
        reloadChat(seq) {
            console.log(seq);
            Chat.data.chatRecord = wxbot.chatRecord(seq);
        },
        // 渲染 新消息
        newMessage(seq) {
            console.log(seq);
            if (seq == Chat.data.seq) {
                Chat.methods.reloadChat(String(seq));
            } else {
                var _html = $('#avatar_' + seq).html();
                _html = _html + "<sup class='ivu-badge-dot'></sup>";
                $('#avatar_' + seq).html(_html);
            }
        },
        // 获取图片高、宽
        imgHeightOrWidth(path, type) {
            return wxbot.getImgHeightOrWidth(path, type);
        },
        // 播放视频
        mediaPlay(path) {
            wxbot.mediaPlay(path);
        },
        // 获取文件绝对路径
        realUrl(path) {
            return wxbot.getRealUrl(path);
        },
        // 聊天窗口滚动条自动底部
        scrollToBottom: function () {
            var container = document.getElementById('chatcontheight');
            container.scrollTop = container.scrollHeight;
        },
    },
}