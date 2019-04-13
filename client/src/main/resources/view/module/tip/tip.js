Tip = {
    data: {
        form:{
            seq:"",
            modal:false,
            modalLoading:true,
            confirm:false,
            confirmLoading:false,
            edit:false,
            title:"",
            tipType:"",
            type:"text",
            file:null,
            text:null
        },
        tipMap: {}
    },
    computed: {
    },
    methods: {
        handleTipUpload (file) {
            this.tip.form.file = file;
            return false;
        },
        editTip(mode, title) {
            this.tip.form.title = title;
            this.tip.form.tipType = mode;
            this.tip.form.modal = true;
        },
        cancelTip(mode, title) {
            this.tip.form.title = title;
            this.tip.form.tipType = mode;
            this.tip.form.confirm = true;
        },
        syncTips(){
            let me = this;
            Web.ajax("tip/getTips", {
                success: function (data) {
                    if(data.info)
                        wxbot.syncTips(data.info);
                },
                fail: function (data) {
                }
            });
        },
        getTipMap(seq){
            this.tip.form.seq = seq;
            this.tip.tipMap = wxbot.getTipMap(seq);
        },
        editTipOk(){
            const me = this;
            var form = new FormData();
            form.append("token", Web.user.token);
            form.append("seq", me.tip.form.seq);
            form.append("type", me.tip.form.tipType);
            const content = me.tip.form.type=="text"?me.tip.form.text:me.tip.form.file;
            form.append("content", content);
            if(!content || content == null || content == ""){
                me.$Message.error("信息不完整");
                me.tip.form.modalLoading = false;
                me.$nextTick(() => {
                    me.tip.form.modalLoading = true;
                });
                return;
            }

            $.ajax({
                url: Web.serverURL + "/tip/set?format=json",
                type: "post",
                data: form,
                processData: false,
                contentType: false,
                success: function (data) {
                    if (!data.result) {
                        me.$set(me.tip.tipMap, me.tip.form.tipType, data.data.info);
                        wxbot.setTip(me.tip.form.seq, me.tip.form.tipType, data.data.info.type, data.data.info.content);
                        me.tip.form.tipType = "";
                        me.tip.form.type = "text";
                        me.tip.form.text = null;
                        me.tip.form.file = null;
                        me.tip.form.modal = false;
                        me.tip.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.tip.form.modalLoading = true;
                        });
                        me.$Message.success("操作成功!");
                    } else {
                        me.tip.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.tip.form.modalLoading = true;
                        });
                        me.$Message.error("操作失败："+data.msg);
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.tip.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.tip.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败:"+textStatus);
                    console.log(errorThrown);
                }
            });
        },
        editTipCancel(){
            this.tip.form.tipType = "";
            this.tip.form.type = "text";
            this.tip.form.text = null;
            this.tip.form.file = null;
        },
        delTip(){
            let me = this;
            me.tip.form.confirmLoading = true;
            Web.ajax("tip/del", {
                data:{
                    seq:me.tip.form.seq,
                    type:me.tip.form.tipType
                },
                success: function (data) {
                    me.$delete(me.tip.tipMap, me.tip.form.tipType);
                    wxbot.delTip(me.tip.form.seq, me.tip.form.tipType);
                    me.tip.form.confirm = false;
                    me.tip.form.confirmLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.tip.form.confirm = false;
                    me.tip.form.confirmLoading = false;
                    me.$Message.error("操作失败："+data.msg);
                }
            });
        }
    }
}