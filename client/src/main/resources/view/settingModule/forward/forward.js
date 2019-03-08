Forward = {
    data:{
        form:{
            filterKey:""
        },
    },
    computed: {
        forwards() {
            let me = this;
            me.forward.form.loadings = {};
            return me.filterAll(me.chatRooms.map(e => {
                me.forward.form.loadings[e.seq] = false;
                return {
                    seq: e.seq,
                    NickName: e.NickName,
                    status:me.setting.forwards.indexOf(e.seq) != -1,
                    loading:false
                };
            }), {
                NickName: me.forward.form.filterKey
            });
        }
    },
    methods:{
        changeForward(e){
            console.log(e);
            if(e.status)
                this.enableForward(e);
            else
                this.disableForward(e);
        },
        enableForward(e){
            const me = this;
            e.loading = true;
            me.$forceUpdate();

            Web.ajax("setting/enableForward", {
                data:{
                    seq:e.seq
                },
                success: function (data) {
                    // wxbot.enableForward(e.seq);
                    me.setting.forwards.push(e.seq);
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.$Message.error("操作失败："+data.msg);
                    e.status = !e.status;
                    e.loading = false;
                    me.$forceUpdate();
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.$Message.error("操作失败:"+textStatus);
                    e.status = !e.status;
                    e.loading = false;
                    me.$forceUpdate();
                }
            });
        },
        disableForward(e){
            const me = this;
            e.loading = true;
            me.$forceUpdate();

            Web.ajax("setting/disableForward", {
                data:{
                    seq:e.seq
                },
                success: function (data) {
                    // wxbot.disableForward(e.seq);
                    me.setting.forwards.splice(me.setting.forwards.indexOf(e.seq), 1);
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.$Message.error("操作失败："+data.msg);
                    e.status = !e.status;
                    e.loading = false;
                    me.$forceUpdate();
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.$Message.error("操作失败:"+textStatus);
                    e.status = !e.status;
                    e.loading = false;
                    me.$forceUpdate();
                }
            });
        }
    }
}