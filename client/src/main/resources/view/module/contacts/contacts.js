Contacts = {
    data: {
        temp: [{}],
        chatRooms: [{}]
    },
    methods: {
        // 初始化联系人列表
        loadIndividuals() {
            //console.log(wxbot.getIndividuals());
            console.log(wxbot.getUserInfo());
            Contacts.data.temp = wxbot.getIndividuals();
            //console.log(Contacts.data.temp[0].NickName);
        },
        // 初始化群聊列表
        loadChatRooms() {
            console.log(wxbot.getChatRooms());
            Contacts.data.chatRooms = wxbot.getChatRooms();
        },
        // 获取host url
        hostUrl() {
            // 拼接头像url
            //console.log(wxbot.getHostUrl());
            //console.log(param);
            return "";
            // return wxbot.getHostUrl();
        },
        // 初始化聊天窗口
        startChat(seq, nickName, userName, headImgUrl) {
            Chat.data.seq = seq;
            Chat.data.title = nickName;
            Chat.data.userName = userName;
            Chat.data.userHeadImg = wxbot.getHostUrl() + headImgUrl;
            Chat.data.ownerHeadImg = wxbot.getOwnerHeadImgUrl();
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            $('#avatar_' + seq + " sup.ivu-badge-dot").remove();
        }
    }
}