Contacts = {
    data:{
        text:"1-2",
        temp:[{}]
    },
    methods:{
        loadIndividuals() {
            //console.log(wxbot.getIndividuals());
            Contacts.data.temp = wxbot.getIndividuals();
            //console.log(Contacts.data.temp[0].NickName);
        },
        loadHeadImgUrl(param) {
            //console.log(wxbot.getHostUrl()+param);
            return wxbot.getHostUrl() + param;
        },
        startChat(recipient) {
            console.log(recipient);
            console.log(Chat.data.title);
            Chat.data.title = recipient;
        }
    }
}