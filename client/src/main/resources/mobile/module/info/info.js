Info = {
    data: {
        loading: false,
        user: null,
        members: [],
    },
    computed: {
    },
    methods: {
        // 重新加载群成员
        reloadChatRoomMember() {
            if(this.info.user && this.info.user.UserName.includes('@@')){
                this.info.loading = true;
                setTimeout(() => {
                    wxbot.getChatRoomMembers(this.info.user.UserName, data => {
                        this.info.members = data;
                        this.info.loading = false;
                        this.$Message.success("刷新群成员列表成功！");
                    });
                });
            }
        },
        autoLoadChatRoomMember(tab){
            if(tab == "info" && this.info.user && this.info.user.UserName.includes('@@') && !this.info.members.length){
                this.info.loading = true;
                setTimeout(() => {
                    wxbot.getChatRoomMembers(this.info.user.UserName, data => {
                        this.info.members = data;
                        this.info.loading = false;
                    });
                }, 50);
            }
        },
        // 后台主动刷新
        reloadMember() {
            this.info.loading = true;
            wxbot.getChatRoomMembers(this.info.user.UserName, data => {
                this.info.members = data;
                this.info.loading = false;
                this.$Message.success("收到群成员变动消息,刷新群成员列表成功！");
            });
        }
    }
}