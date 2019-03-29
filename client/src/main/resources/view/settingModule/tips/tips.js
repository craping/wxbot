Tips = {
    data: {
        form: {
            chatRoomFoundTip: {},
            memberJoinTip: {},
            memberLeftTip: {}
        },
        loading: false,
        modal: false,
        title: '',
        type:'text',
        text:'',
        file:null,
        tipType: '',
        cancelModal: false,
        cancelLoading: false
    },
    methods: {
        showTipMoadl(mode, title) {
            this.tips.title = title;
            this.tips.tipType = mode;
            if (this.tips.form[mode] != null) {
                const tip = this.tips.form[mode];
                if (tip.type == 1 || tip.type == 0) {
                    this.tips.type = 'text';
                } else {
                    this.tips.type = 'file';
                }
                this.tips.text = tip.content;
            }
            this.tips.modal = true;
        },
        cancelTipMoadl(mode, title) {
            this.tips.title = title;
            this.tips.tipType = mode;
            this.tips.cancelModal = true;
        },
        cancelTips(){
            let me = this;
            me.tips.cancelLoading = true;
            Web.ajax("setting/cancelTips", {
                data:{ tipType: me.tips.tipType },
                success: function (data) {
                    me.tips.cancelLoading = false;
                    me.tips.cancelModal = false;
                    console.log(data);
                    me.tips.form = data.info;
                    wxbot.syncTips(me.tips.form);
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.tips.cancelLoading = false;
                    me.tips.cancelModal = false;
                    me.$Message.error("操作失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.tips.cancelLoading = false;
                    me.tips.cancelModal = false;
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        },
        handleTipsUpload (file) {
            this.tips.file = file;
            return false;
        },
        tipsCancel(){
            this.tips.type = "text";
            this.tips.text = null;
            this.tips.file = null;
        },
        getTipType(mode){
            return this.tips.form[mode]?this.tips.form[mode].type:"";
        },
        tipsOk() {
            const me = this;
            me.tips.loading = true;
            var form = new FormData();
            form.append("token", Web.user.token);
            form.append("tipType", me.tips.tipType);
            const content =  me.tips.type=="text"?me.tips.text:me.tips.file;
            form.append("content", content);

            if(!content || content == null || content == ""){
                me.$Message.error("信息不完整");
                me.tips.loading = false;
                me.$nextTick(() => {
                    me.tips.loading = true;
                });
                return;
            }
            $.ajax({
                url: Web.serverURL + "/setting/setTips?format=json",
                type: "post",
                data: form,
                processData: false,
                contentType: false,
                success: function (data) {
                    if (!data.result) {
                        if (Object.keys(data.data).length > 0) {
                            me.tips.form = data.data.info;
                            wxbot.syncTips(me.tips.form);
                        }
                        me.tipsCancel();
                        me.tips.modal = false;
                        me.tips.loading = false;
                        me.$nextTick(() => {
                            me.tips.loading = true;
                        });
                        me.$Message.success("操作成功!");
                    } else {
                        me.tips.loading = false;
                        me.$nextTick(() => {
                            me.tips.loading = true;
                        });
                        me.$Message.error("操作失败："+data.msg);
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.tips.loading = false;
                    me.$nextTick(() => {
                        me.tips.loading = true;
                    });
                    me.$Message.error("操作失败:"+textStatus);
                    console.log(errorThrown);
                }
            });
        },
        showTipContent(mode) {
            const tip = this.tips.form[mode];
            if (!tip || tip.content == null){
                return '未设置';
            }
            return this.tips.form[mode].content
        },
        tipsReset() {
            this.tips.form = Object.assign({}, this.setting.tips);
        }
    }
}