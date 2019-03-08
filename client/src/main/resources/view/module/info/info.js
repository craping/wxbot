Info = {
    data: {
        loading: false,
        chatRoomUserName: "",
        members: [],
    },
    computed: {
    },
    methods: {
        // 重新加载群成员
        reloadChatRoomMember() {
            console.log(Info.data.chatRoomUserName);
            let me = this;
            Info.data.loading = true;
            setTimeout(() => {
                Info.data.members = wxbot.getChatRoomMembers(Info.data.chatRoomUserName);
                this.$nextTick(() => {
                    Info.data.loading = false;
                    me.$Message.success("刷新群成员列表成功！");
                });
            }, 1000);
        },
        // 后台主动刷新
        reloadMember() {
            Info.data.members = wxbot.getChatRoomMembers(Info.data.chatRoomUserName);
            app.$nextTick(() => {
                app.$Message.success("收到群成员变动消息,刷新群成员列表成功！");
            });
        }
    }
}