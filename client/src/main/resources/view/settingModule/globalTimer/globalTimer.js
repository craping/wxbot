Vue.component("timer-schedule", {
    render: function(h){
        const schedule = this.schedule.split("|");
        const type = schedule[0];
        let info = "";
        if (type == "1") {
            const cron = schedule[1].split(",");
            info = [
                h("strong", { 
                    domProps: {innerText: "定时"},
                    style:{color:"#5cadff"}
                }), 
                "["+cron[0] + "月" + cron[1] + "日"+ cron[2] + "点" + cron[3] + "分" + cron[4] + "秒]"
            ];
        } else {
            info = [
                h("strong", { 
                    domProps: {innerText: "间隔" },
                    style:{color:"#5cadff"}
                }), 
                "["+parseInt(parseInt(schedule[1])/60/60%60)+"小时"+ parseInt(parseInt(schedule[1])/60%60)+"分钟"+ parseInt(parseInt(schedule[1])%60)+"秒]"
            ];
        }
        return h('span', info)
    },
    props: {
        schedule: {
            type: String,
            required: true
        }
    }
})

GlobalTimer = {
    data:{
        form:{
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
        msgs: [],
        msgsLoading:false
    },
    methods:{
        handleUpload (file) {
            this.globalTimer.form.file = file;
            return false;
        },
        // loadMsgs(){
        //     let me = this;
        //     me.globalTimer.msgsLoading = true;
        //     Web.ajax("timer/getTimers", {
        //         data:{
        //             seq:"global"
        //         },
        //         success: function (data) {
        //             if(data.info)
        //             me.globalTimer.msgs = data.info["global"];
        //             else
        //             me.globalTimer.msgs = [];
        //             me.globalTimer.msgsLoading = false;
        //         },
        //         fail: function (data) {
        //         }
        //     });
        // },
        getMsgs(){
            this.globalTimer.msgs = wxbot.getMsgs("global");
        },
        editMsgOk(){
            let me = this;
            var form = new FormData();
            form.append("token", wxbot.getToken());
            form.append("content", me.globalTimer.form.type=="text"?me.globalTimer.form.text:me.globalTimer.form.file);
            let schedule = [];

            if(me.globalTimer.form.scheduleType == 1){
                Object.keys(me.globalTimer.form.schedule).forEach(key => {
                    schedule.push(me.globalTimer.form.schedule[key]);
                });
                schedule = me.globalTimer.form.scheduleType+"|"+schedule.join(",");
                if(schedule.indexOf(",,") != -1){
                    me.$Message.error("时间信息不完整");
                    me.globalTimer.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.globalTimer.form.modalLoading = true;
                    });
                    return;
                }
            } else {
                if(me.globalTimer.form.schedule.ss == null){
                    me.$Message.error("时间信息不完整");
                    me.globalTimer.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.globalTimer.form.modalLoading = true;
                    });
                    return;
                }
                schedule = me.globalTimer.form.scheduleType+"|"+me.globalTimer.form.schedule.ss;
            }
            
            form.append("schedule", schedule);

            $.ajax({
                url: Web.serverURL + "timer/addGlobalMsg?format=json",
                type: "post",
                data: form,
                processData: false,
                contentType: false,
                success: function (data) {
                    if (!data.result) {
                        me.globalTimer.msgs.push(data.data.info);
                        wxbot.addMsg("global", data.data.info);
                        me.globalTimer.form.modal = false;
                        me.globalTimer.form.modalLoading = false;
                        me.globalTimer.form.text = null;
                        me.globalTimer.form.file = null;
                        me.globalTimer.form.scheduleType = 1;
                        me.globalTimer.form.schedule = {
                            MM:null,
                            dd:null,
                            HH:null,
                            mm:null,
                            ss:null
                        }
                        me.$Message.success("操作成功!");
                    } else {
                        me.globalTimer.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.globalTimer.form.modalLoading = true;
                        });
                        me.$Message.error("操作失败："+data.msg);
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.globalTimer.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.globalTimer.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        },
        editMsgCancel(){
            this.globalTimer.form.text = null;
            this.globalTimer.form.file = null;
            this.globalTimer.form.scheduleType = 1;
            this.globalTimer.form.schedule = {
                MM:null,
                dd:null,
                HH:null,
                mm:null,
                ss:null
            }
        },
        delMsg(){
            let me = this;
            me.globalTimer.form.confirmLoading = true;
            Web.ajax("timer/delGlobalMsg", {
                data:{
                    uuid:me.globalTimer.form.uuid
                },
                success: function (data) {
                    me.globalTimer.msgs = me.globalTimer.msgs.filter(e => e.uuid != me.globalTimer.form.uuid);
                    wxbot.delMsg("global", me.globalTimer.form.uuid);
                    me.globalTimer.form.confirm = false;
                    me.globalTimer.form.confirmLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.globalTimer.form.confirm = false;
                    me.globalTimer.form.confirmLoading = false;
                    me.$Message.error("操作失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.globalTimer.form.confirm = false;
                    me.globalTimer.form.confirmLoading = false;
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        }
    }
}