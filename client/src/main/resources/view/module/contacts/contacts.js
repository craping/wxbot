Contacts = {
    data: {
        temp: [{}],
        chatRooms: [{}]
    },
    methods: {
        // 处理emoji表情
        emojiFormatter(userName) {
            return wxbot.getEmoji(userName);
        },
        // 初始化联系人列表
        loadIndividuals() {
            Contacts.data.temp = wxbot.getIndividuals();
        },
        // 初始化群聊列表
        loadChatRooms() {
            Contacts.data.chatRooms = wxbot.getChatRooms();
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
            $('#avatar_' + seq + " sup.ivu-badge-dot").remove();
        },
        // 初始化群聊窗口、群成员列表
        startGroupChat(seq, nickName, userName) {
            Chat.data.seq = seq;
            Chat.data.title = nickName;
            Chat.data.userName = userName;
            Chat.data.ownerHeadImg = wxbot.getOwnerHeadImgUrl();
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            Info.data.members = wxbot.getChatRoomMembers(userName);
            //console.log(Info.data.members);
            $('#avatar_' + seq + " sup.ivu-badge-dot").remove();
            this.loadKeyMap(seq);
            this.loadMsgs(seq);
        }
    }
}