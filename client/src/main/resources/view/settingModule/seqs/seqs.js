Seqs = {
    data:{
        form:{
            filterKey:""
        },
    },
    computed: {
        seqData() {
            let me = this;
            me.seqs.form.loadings = {};
            return me.filterAll(me.chatRooms.map(e => {
                me.seqs.form.loadings[e.seq] = false;
                return {
                    seq: e.seq,
                    NickName: e.NickName,
                    turing: {
                        seq: e.seq,
                        module:"turing",
                        state: me.setting.turing.includes(e.seq),
                        loading: false,
                    },
                    keywords: {
                        seq: e.seq,
                        module:"keywords",
                        state: me.setting.keywords.includes(e.seq),
                        loading: false,
                    },
                    timers: {
                        seq: e.seq,
                        module:"timers",
                        state: me.setting.timers.includes(e.seq),
                        loading: false,
                    },
                    forwards: {
                        seq: e.seq,
                        module:"forwards",
                        state: me.setting.forwards.includes(e.seq),
                        loading: false
                    }
                };
            }), {
                NickName: me.seqs.form.filterKey
            });
        }
    },
    methods:{
        changeSeq(e){
            console.log(e);
            if(e.state)
                this.enableSeq(e);
            else
                this.disableSeq(e);
        },
        enableSeq(e){
            const me = this;
            e.loading = true;
            me.$forceUpdate();

            Web.ajax("setting/enableSeq", {
                data:{
                    seq:e.seq,
                    module:e.module
                },
                success: function (data) {
                    wxbot.enableSeq(e.module, e.seq);
                    me.setting[e.module].push(e.seq);
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.$Message.error("操作失败："+data.msg);
                    e.state = !e.state;
                    e.loading = false;
                    me.$forceUpdate();
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.$Message.error("操作失败:"+textStatus);
                    e.state = !e.state;
                    e.loading = false;
                    me.$forceUpdate();
                }
            });
        },
        disableSeq(e){
            const me = this;
            e.loading = true;
            me.$forceUpdate();

            Web.ajax("setting/disableSeq", {
                data:{
                    seq:e.seq,
                    module:e.module
                },
                success: function (data) {
                    wxbot.disableSeq(e.module, e.seq);
                    me.setting[e.module].splice(me.setting[e.module].indexOf(e.seq), 1);
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.$Message.error("操作失败："+data.msg);
                    e.state = !e.state;
                    e.loading = false;
                    me.$forceUpdate();
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.$Message.error("操作失败:"+textStatus);
                    e.state = !e.state;
                    e.loading = false;
                    me.$forceUpdate();
                }
            });
        },
        modSeq(seqMap){
            Object.keys(seqMap).forEach(oldSeq => {
                const newSeq = seqMap[oldSeq];
                me.setting.turing.splice(me.setting.turing.indexOf(oldSeq), 1, newSeq);
                me.setting.forwards.splice(me.setting.forwards.indexOf(oldSeq), 1, newSeq);
                me.setting.keywords.splice(me.setting.keywords.indexOf(oldSeq), 1, newSeq);
                me.setting.timers.splice(me.setting.timers.indexOf(oldSeq), 1, newSeq);
            });
        }
    }
}