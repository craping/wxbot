Chat = {
    data:{
        title:"用户昵称",
        text:"",
        chatRecord:[{

        }]
    },
    methods:{
        sendApp(){
            wxbot.sendApp(null);
        },
        sendText(){
            // wxbot.sendText("@@c9aad1c632be8dea138df0d5d3679a8115f6a3aad16a7d2584b6f2a9014a286c", this.chat.text);
            console.log(wxbot.getIndividuals());
            console.log(wxbot.getChatRooms());
            console.log(wxbot.getMediaPlatforms());
            this.chat.text = "";
        }
    }
}