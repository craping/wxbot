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
        tipType: ''
    },
    methods: {
        showMoadl(mode, title) {
            this.tips.title = title;
            this.tips.tipType = mode;
            if (Object.keys(this.tips.form[mode]).length > 0) {
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
        handleKeywordUpload (file) {
            this.tips.file = file;
            return false;
        },
        editCancel(){
            this.tips.type = "text";
            this.tips.text = null;
            this.tips.file = null;
        },
        getType(mode){
            return this.tips.form[mode]?this.tips.form[mode].type:"";
        },
        editOk() {
            const me = this;
            me.tips.loading = true;
            var form = new FormData();
            form.append("token", Web.user.token);
            form.append("tipType", me.tips.tipType);
            form.append("content", me.tips.type=="text"?me.tips.text:me.tips.file);
            $.ajax({
                url: Web.serverURL + "setting/setTips?format=json",
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
                        me.editCancel();
                        me.tips.modal = false;
                        me.tips.loading = false;
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