Chat = {
    data: {
        userHeadImg: "", //接受者头像
        ownerHeadImg: "", // 当前用户头像
        userName: "",   // 接受者
        seq: "",        // 用户唯一值
        title: " ",     // 窗口标题
        text: "",       // 聊天文本
        chatRecord: [{}]
    },
    methods: {
        sendApp() {
            wxbot.sendApp(null);
        },
        sendText() {
            console.log(this.chat.text);
            //Chat.data.text = "";
            console.log(this.chat.to);
            wxbot.sendText(this.chat.seq, this.chat.title, this.chat.userName, "chat", this.chat.text);
        },
        scrollBottom() {

        }
    }
}