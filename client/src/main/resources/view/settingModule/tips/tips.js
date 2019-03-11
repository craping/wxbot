Tips = {
    data: {
        form: {
            chatRoomFoundTip: null,
            memberJoinTip: null,
            memberLeftTip: null
        },
        loading: false
    },
    methods: {
        tipsSubmit() {
            const me = this;
            me.tips.loading = true;
            Web.ajax("setting/setTips", {
                data:{
                    tips:JSON.stringify(me.tips.form)
                },
                success: function (data) {
                    me.setting.tips = Object.assign({}, me.tips.form);
                    wxbot.syncTips(me.tips.form);
                    me.tips.loading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.tipsReset();
                    me.tips.loading = false;
                    me.$Message.error("操作失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.tipsReset();
                    me.tips.loading = false;
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        },
        tipsReset() {
            this.tips.form = Object.assign({}, this.setting.tips);
        }
    }
}