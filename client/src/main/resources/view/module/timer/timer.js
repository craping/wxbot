Timer = {
    data: {
        form:{
            seq:"123",
            modal:false,
            confirm:false,
            confirmLoading:false,
            uuid:"",
            type:"text",
            value:""
        },
        columns: [{
            title: "时间",
            key: "schedule",
            render: (h, params) => {
                let schedule = params.row.schedule.split(",");
                return h('span', params.row.schedule)
            }
        },{
            title: "内容",
            key: "content"
        }, {
            title: "操作",
            width:70,
            className:"opt",
            render: (h, params) => {
                return [
                    h('Button', {
                        props: {
                            type: 'default',
                            size: 'small',
                            icon: 'ios-remove'
                        },
                        on: {
                            click: () => {
                                Timer.data.form.confirm = true;
                                Timer.data.form.uuid = params.row.uuid;
                            }
                        }
                    })
                ];
            }
        }],
        msgs: [],
        msgsLoading:false
    },
    computed: {
    },
    methods: {
        syncTimers(){
            let me = this;
            Web.ajax("timer/getTimers", {
                success: function (data) {
                    console.log(data.info)
                    wxbot.syncTimers(data.info);
                },
                fail: function (data) {
                }
            });
            if(me.timer.form.seq){
                me.getMsgs();
            }
        },
        loadMsgs(seq){
            this.timer.form.seq = seq;
            this.getMsgs();
        },
        getMsgs(){
            let me = this;
            me.timer.msgsLoading = true;
            Web.ajax("timer/getTimers", {
                data:{
                    seq:me.timer.form.seq
                },
                success: function (data) {
                    console.log(data)
                    me.timer.msgs = data.info[0].msgs;
                    me.timer.msgsLoading = false;
                },
                fail: function (data) {
                }
            });
        },
        editMsgOk(){
            let me = this;
            me.timer.form.confirmLoading = true;
        },
        editMsgCancel(){
        },
        delMsg(){
            let me = this;
            me.timer.form.confirmLoading = true;
            Web.ajax("timer/delMsg", {
                data:{
                    seq:me.timer.form.seq,
                    uuid:me.timer.form.uuid
                },
                success: function (data) {
                    console.log(data);
                    me.timer.form.confirm = false;
                    me.timer.form.confirmLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.timer.form.confirm = false;
                    me.timer.form.confirmLoading = false;
                    me.$Message.error("操作失败："+data.msg);
                }
            });
        }
    }
}