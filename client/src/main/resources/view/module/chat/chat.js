Chat = {
    data:{
        title:"用户昵称",
        text:"",
        chatRecord:[{

        }]
    },
    methods:{
        sendApp(){
            wxbot.sendApp();
        },
        sendText(){
            // wxbot.sendText(null, this.chat.text);
            console.log(wxbot.getContact())
            this.chat.text = "";
        }
    }
}