Keyword = {
    data: {
        form:{
            seq:"",
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
        columns: [{
            title: "关键词",
            key: "key",
            ellipsis:true,
            tooltip:true,
            width:80
        },{
            title: "回复",
            key: "content",
            ellipsis:true,
            render: (h, params) => {
                return h("Tooltip", {
                    "class":"text-truncate w-100",
                    props:{
                        content:params.row.content,
                        maxWidth:200,
                    }
                }, [h('i', {
                    style: {
                        marginRight: "8px",
                        fontSize:"14px"
                    },
                    "class":"far "+constant.typeIcon[params.row.type]
                }),params.row.content])
            }
        },{
            title: "操作",
            width:70,
            className:"opt",
            render: (h, params) => {
                return [
                    h('Button', {
                        props: {
                            type: 'default',
                            size: 'small',
                            icon: 'ios-create'
                        },
                        style: {
                            marginRight: '8px'
                        },
                        on: {
                            click: () => {
                                Keyword.data.form.modal = true;
                                Keyword.data.form.edit = true;
                                Keyword.data.form.key = params.row.key;
                                Keyword.data.form.type = params.row.type == 1?"text":"file";
                                Keyword.data.form.text = params.row.content;
                            }
                        }
                    }),
                    h('Button', {
                        props: {
                            type: 'default',
                            size: 'small',
                            icon: 'ios-remove'
                        },
                        on: {
                            click: () => {
                                Keyword.data.form.confirm = true;
                                Keyword.data.form.delKey = params.row.key;
                            }
                        }
                    })
                ];
            }
        }],
        chatRoomKeyMap: {},
        chatRoomKeyMapLoading:false
    },
    computed: {
        keyMap() {
            let me = this;
            return me.filterAll(Object.keys(me.keyword.chatRoomKeyMap).map(key => {
                const msg = me.keyword.chatRoomKeyMap[key];
                return {
                    key: key,
                    type:msg.type,
                    content: msg.content
                };
            }), {
                key: me.keyword.form.filterKey,
                content: me.keyword.form.filterKey
            });
        }
    },
    methods: {
        handleKeywordUpload (file) {
            this.keyword.form.file = file;
            return false;
        },
        syncKeywords(){
            let me = this;
            Web.ajax("keyword/getKeywords", {
                success: function (data) {
                    if(data.info)
                        wxbot.syncKeywords(data.info);
                },
                fail: function (data) {
                }
            });
        },
        // loadKeyMap(){
        //     let me = this;
        //     me.keyword.chatRoomKeyMapLoading = true;
        //     Web.ajax("keyword/getKeywords", {
        //         data:{
        //             seq:me.keyword.form.seq
        //         },
        //         success: function (data) {
        //             console.log(data)
        //             if(data.info)
        //             me.keyword.chatRoomKeyMap = data.info[me.keyword.form.seq];
        //             else
        //             me.keyword.chatRoomKeyMap = {};
        //             me.keyword.chatRoomKeyMapLoading = false;
        //         },
        //         fail: function (data) {
        //         }
        //     });
        // },
        getKeyMap(seq){
            this.keyword.form.seq = seq;
            this.keyword.chatRoomKeyMap = wxbot.getKeyMap(seq);
        },
        editKeyMapOk(){
            const me = this;
            var form = new FormData();
            form.append("token", Web.user.token);
            form.append("seq", me.keyword.form.seq);
            form.append("key", me.keyword.form.key);
            const content = me.keyword.form.type=="text"?me.keyword.form.text:me.keyword.form.file;
            form.append("content", content);
            if(!me.keyword.form.key || me.keyword.form.key == "" || !content || content == null || content == ""){
                me.$Message.error("信息不完整");
                me.keyword.form.modalLoading = false;
                me.$nextTick(() => {
                    me.keyword.form.modalLoading = true;
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
                        me.$set(me.keyword.chatRoomKeyMap, me.keyword.form.key, data.data.info);
                        wxbot.setKeyMap(me.keyword.form.seq, me.keyword.form.key, data.data.info.type, data.data.info.content);
                        me.keyword.form.key = "";
                        me.keyword.form.type = "text";
                        me.keyword.form.text = null;
                        me.keyword.form.file = null;
                        me.keyword.form.modal = false;
                        me.keyword.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.keyword.form.modalLoading = true;
                        });
                        me.$Message.success("操作成功!");
                    } else {
                        me.keyword.form.modalLoading = false;
                        me.$nextTick(() => {
                            me.keyword.form.modalLoading = true;
                        });
                        me.$Message.error("操作失败："+data.msg);
                    }
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    me.keyword.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.keyword.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败:"+textStatus);
                    console.log(errorThrown);
                }
            });
        },
        editKeyMapCancel(){
            this.keyword.form.key = "";
            this.keyword.form.type = "text";
            this.keyword.form.text = null;
            this.keyword.form.file = null;
        },
        delKeyMap(){
            let me = this;
            me.keyword.form.confirmLoading = true;
            Web.ajax("keyword/del", {
                data:{
                    seq:me.keyword.form.seq,
                    keyList:[me.keyword.form.delKey]
                },
                success: function (data) {
                    me.$delete(me.keyword.chatRoomKeyMap, me.keyword.form.delKey);
                    wxbot.delKeyMap(me.keyword.form.seq, me.keyword.form.delKey);
                    me.keyword.form.confirm = false;
                    me.keyword.form.confirmLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.keyword.form.confirm = false;
                    me.keyword.form.confirmLoading = false;
                    me.$Message.error("操作失败："+data.msg);
                }
            });
        }
    }
}