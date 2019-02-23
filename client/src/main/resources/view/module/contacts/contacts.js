Contacts = {
    data: {
        temp: [{}]
    },
    methods: {
        loadIndividuals() {
            console.log(wxbot.getIndividuals());
            Contacts.data.temp = wxbot.getIndividuals();
            //console.log(Contacts.data.temp[0].NickName);
        },
        hostUrl() {
            // 拼接头像url
            //console.log(wxbot.getHostUrl());
            //console.log(param);
            // return "";
            return wxbot.getHostUrl();
        },
        startChat(seq, nickName, userName, headImgUrl) {
            Chat.data.seq = seq;
            Chat.data.title = nickName;
            Chat.data.to = userName;
            Chat.data.userHeadImg = wxbot.getHostUrl() + headImgUrl;
            Chat.data.ownerHeadImg = wxbot.getOwnerHeadImgUrl();
            Chat.data.chatRecord = wxbot.chatRecord(seq);
            console.log(Chat.data.ownerHeadImg);
            Chat.methods.scrollBottom();
        }
    }
}