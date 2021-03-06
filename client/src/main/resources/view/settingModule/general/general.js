General = {
    data:{
        form:{
            autoAcceptFriend:null,
            globalKeyword:null,
            globalTimer:null
        },
        turingKey:"",
        loading:false
    },
    methods:{
        generalSubmit() {
            const me = this;
            me.general.loading = true;
            Web.ajax("setting/setSwitchs", {
                data:{
                    switchs:JSON.stringify(me.general.form)
                },
                success: function (data) {
                    me.setting.switchs = Object.assign({}, me.general.form);
                    wxbot.syncSwitchs(me.general.form);

                    wxbot.syncTuringKey(me.general.turingKey);
                    localStorage.setItem("turingKey", me.general.turingKey);
                    
                    me.general.loading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.generalReset();
                    me.general.loading = false;
                    me.$Message.error("操作失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.generalReset();
                    me.general.loading = false;
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        },
        generalReset() {
            this.general.form = Object.assign({}, this.setting.switchs);
        }
    }
}