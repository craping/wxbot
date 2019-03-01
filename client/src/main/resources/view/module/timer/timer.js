Timer = {
    data: {
        form:{
            seq:"123",
            modal:false,
            modalLoading:true,
            confirm:false,
            confirmLoading:false,
            uuid:"",
            type:"text",
            schedule:{
                MM:null,
                dd:null,
                HH:null,
                mm:null,
                ss:null
            },
            scheduleType:1,
            file:null,
            text:null
        },
        columns: [{
            title: "时间",
            key: "schedule",
            width:190,
            render: (h, params) => {
                const timer = params.row.schedule.split("|");
                const type = timer[0];
                const schedule = timer[1].split(",");
                let info = "";
                info += schedule[0] +(type == "1"?"月":"个月");
                info += schedule[1] +(type == "1"?"日":"天");
                info += schedule[2] +(type == "1"?"时":"小时");
                info += schedule[3] +(type == "1"?"分":"分钟");
                info += schedule[4] +"秒";
                return h('span', (type == "1"?"定时":"间隔") + info)
            }
        },{
            title: "内容",
            ellipsis:true,
            key: "content"
        }, {
            title: "操作",
            width:50,
            className:"opt",
            align:"center",
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
        handleUpload (file) {
            this.timer.form.file = file;
            return false;
        },
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
                    if(data.info)
                        me.timer.msgs = data.info[me.timer.form.seq];
                    me.timer.msgsLoading = false;
                },
                fail: function (data) {
                }
            });
        },
        editMsgOk(){
            let me = this;
            var form = new FormData();
            form.append("token", wxbot.getToken());
            form.append("seq", me.timer.form.seq);
            form.append("content", me.timer.form.type=="text"?me.timer.form.text:me.timer.form.file);
            let schedule = [];
            Object.keys(me.timer.form.schedule).forEach(key => {
                schedule.push(me.timer.form.schedule[key]);
            });
            schedule = me.timer.form.scheduleType+"|"+schedule.join(",");
            console.log(schedule);
            if(schedule.indexOf(",,") != -1){
                me.$Message.error("时间信息不完整");
                me.timer.form.modalLoading = false;
                me.$nextTick(() => {
                    me.timer.form.modalLoading = true;
                });
                return;
            }
            form.append("schedule", schedule);

            $.ajax({
                url: Web.serverURL + "timer/addMsg?format=json",
                type: "post",
                data: form,
                processData: false,
                contentType: false,
                success: function (data) {
                    if (!data.result) {
                        me.timer.msgs.push(data.data.info);
                        wxbot.addMsg(me.timer.form.seq, data.data.info);
                        me.timer.form.modal = false;
                        me.timer.form.modalLoading = false;
                        me.timer.form.text = null;
                        me.timer.form.file = null;
                        me.timer.form.scheduleType = 1;
                        me.timer.form.schedule = {
                            MM:null,
                            dd:null,
                            HH:null,
                            mm:null,
                            ss:null
                        }
                        me.$Message.success("操作成功!");
                    } else {
                        me.timer.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.timer.form.modalLoading = true;
                        });
                        me.$Message.error("操作失败："+data.msg);
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    console.log(errorThrown);
                }
            });
        },
        editMsgCancel(){
            this.timer.form.text = null;
            this.timer.form.file = null;
            me.timer.form.scheduleType = 1;
            this.timer.form.schedule = {
                MM:null,
                dd:null,
                HH:null,
                mm:null,
                ss:null
            }
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
                    me.timer.msgs = me.timer.msgs.filter(e => e.uuid != me.timer.form.uuid);
                    wxbot.delMsg(me.timer.form.seq, me.timer.form.uuid);
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