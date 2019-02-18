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
            
            Chat.data.text = "";
        }
    }
}