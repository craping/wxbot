GlobalKeyword = {
    data:{
        form:{
            modal:false,
            modalLoading:true,
            confirm:false,
            confirmLoading:false,
            edit:false,
            delKey:"",
            filterKey:"",
            key:"",
            type:"text",
            file:null,
            text:null
        },
        chatRoomKeyMap: {},
        chatRoomKeyMapLoading:false
    },
    computed: {
        keyMap() {
            let me = this;
            return me.filterAll(Object.keys(me.globalKeyword.chatRoomKeyMap).map(key => {
                const msg = me.globalKeyword.chatRoomKeyMap[key];
                return {
                    key: key,
                    type:msg.type,
                    content: msg.content
                };
            }), {
                key: me.globalKeyword.form.filterKey,
                content: me.globalKeyword.form.filterKey
            });
        }
    },
    methods:{
        changeKeyword(status){
            if(status)
                this.enableSeq(status);
            else
                this.disableSeq(e);
        },
        enableKeyword(e){
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
        disableKeyword(e){
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
        handleKeywordUpload (file) {
            this.globalKeyword.form.file = file;
            return false;
        },
        getKeyMap(){
            this.globalKeyword.chatRoomKeyMap = wxbot.getKeyMap("global");
        },
        editKeyMapOk(){
            const me = this;
            var form = new FormData();
            form.append("token", Web.user.token);
            form.append("seq", "global");
            form.append("key", me.globalKeyword.form.key);
            const content = me.globalKeyword.form.type=="text"?me.globalKeyword.form.text:me.globalKeyword.form.file;
            form.append("content", content);
            if(!me.globalKeyword.form.key || me.globalKeyword.form.key == "" || !content || content == null || content == ""){
                me.$Message.error("信息不完整");
                me.globalKeyword.form.modalLoading = false;
                me.$nextTick(() => {
                    me.globalKeyword.form.modalLoading = true;
                });
                return;
            }
            $.ajax({
                url: Web.serverURL + "/keyword/set?format=json",
                type: "post",
                data: form,
                processData: false,
                contentType: false,
                success: function (data) {
                    if (!data.result) {
                        me.$set(me.globalKeyword.chatRoomKeyMap, me.globalKeyword.form.key, data.data.info);
                        wxbot.setKeyMap("global", me.globalKeyword.form.key, data.data.info.type, data.data.info.content);
                        me.globalKeyword.form.key = "";
                        me.globalKeyword.form.type = "text";
                        me.globalKeyword.form.text = null;
                        me.globalKeyword.form.file = null;
                        me.globalKeyword.form.modal = false;
                        me.globalKeyword.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.globalKeyword.form.modalLoading = true;
                        });
                        me.$Message.success("操作成功!");
                    } else {
                        me.globalKeyword.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.globalKeyword.form.modalLoading = true;
                        });
                        me.$Message.error("操作失败："+data.msg);
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.globalKeyword.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.globalKeyword.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败:"+textStatus);
                    console.log(errorThrown);
                }
            });
        },
        editKeyMapCancel(){
            this.globalKeyword.form.key = "";
            this.globalKeyword.form.type = "text";
            this.globalKeyword.form.text = null;
            this.globalKeyword.form.file = null;
        },
        delKeyMap(){
            let me = this;
            me.globalKeyword.form.confirmLoading = true;
            Web.ajax("keyword/del", {
                data:{
                    seq:"global",
                    keyList:[me.globalKeyword.form.delKey]
                },
                success: function (data) {
                    console.log(data);
                    me.$delete(me.globalKeyword.chatRoomKeyMap, me.globalKeyword.form.delKey);
                    wxbot.delKeyMap("global", me.globalKeyword.form.delKey);
                    me.globalKeyword.form.confirm = false;
                    me.globalKeyword.form.confirmLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.globalKeyword.form.confirm = false;
                    me.globalKeyword.form.confirmLoading = false;
                    me.$Message.error("操作失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.globalKeyword.form.confirm = false;
                    me.globalKeyword.form.confirmLoading = false;
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        }
    }
}