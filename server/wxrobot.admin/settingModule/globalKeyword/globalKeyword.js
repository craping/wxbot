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
            value:""
        },
        chatRoomKeyMap: {},
        chatRoomKeyMapLoading:false
    },
    computed: {
        keyMap() {
            let me = this;
            return me.filterAll(Object.keys(me.globalKeyword.chatRoomKeyMap).map(key => {
                return {
                    key: key,
                    value: me.globalKeyword.chatRoomKeyMap[key]
                };
            }), {
                key: me.globalKeyword.form.filterKey,
                value: me.globalKeyword.form.filterKey
            });
        }
    },
    methods:{
        // loadKeyMap(){
        //     let me = this;
        //     me.globalKeyword.chatRoomKeyMapLoading = true;
        //     Web.ajax("keyword/getKeywords", {
        //         data:{
        //             seq:"global"
        //         },
        //         success: function (data) {
        //             console.log(data)
        //             if(data.info)
        //             me.globalKeyword.chatRoomKeyMap = data.info["global"];
        //             else
        //             me.globalKeyword.chatRoomKeyMap = {};
        //             me.globalKeyword.chatRoomKeyMapLoading = false;
        //         },
        //         fail: function (data) {
        //         }
        //     });
        // },
        getKeyMap(){
            this.globalKeyword.chatRoomKeyMap = wxbot.getKeyMap("global");
        },
        editKeyMapOk(){
            let me = this;
            let keyMap = {};
            keyMap[me.globalKeyword.form.key] = me.globalKeyword.form.value;
            Web.ajax("keyword/setGlobal", {
                data:{
                    keyMap:keyMap
                },
                success: function (data) {
                    me.$set(me.globalKeyword.chatRoomKeyMap, me.globalKeyword.form.key, me.globalKeyword.form.value);
                    wxbot.setKeyMap("global", me.globalKeyword.form.key, me.globalKeyword.form.value);
                    me.globalKeyword.form.key = "";
                    me.globalKeyword.form.value = "";
                    me.globalKeyword.form.modal = false;
                    me.globalKeyword.form.modalLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.globalKeyword.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.globalKeyword.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.globalKeyword.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.globalKeyword.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败:"+textStatus);
                }
            });
        },
        editKeyMapCancel(){
            this.globalKeyword.form.key = "";
            this.globalKeyword.form.value = "";
        },
        delKeyMap(){
            let me = this;
            me.globalKeyword.form.confirmLoading = true;
            Web.ajax("keyword/delGlobal", {
                data:{
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