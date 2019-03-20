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
                    turing:me.setting.turing.includes(e.seq),
                    turingLoading:false,
                    keywords:me.setting.keywords.includes(e.seq),
                    keywordsLoading:false,
                    timers:me.setting.timers.includes(e.seq),
                    timersLoading:false,
                    forwards:me.setting.forwards.includes(e.seq),
                    forwardsLoading:false
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

            Web.ajax("setting/enableSeq", {
                data:{
                    seq:e.seq,
                    module:"forwards"
                },
                success: function (data) {
                    wxbot.enableSeq("forwards", e.seq);
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

            Web.ajax("setting/disableSeq", {
                data:{
                    seq:e.seq,
                    module:"forwards"
                },
                success: function (data) {
                    wxbot.disableSeq("forwards", e.seq);
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
        },
        modForward(oldSeq, newSeq){
            Object.keys(seqMap).forEach(oldSeq => {
                const newSeq = seqMap[oldSeq];
                me.setting.forwards.splice(me.setting.forwards.indexOf(oldSeq), 1, newSeq);
            });
        }
    }
}