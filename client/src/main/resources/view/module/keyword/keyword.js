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
            value:""
        },
        columns: [{
            title: "关键词",
            key: "key",
            render: (h, params) => {
                return h('span', [
                    h('Icon', {
                        props: {
                            type: 'ios-paper-outline'
                        },
                        style: {
                            marginRight: '8px'
                        }
                    }),
                    h('span', params.row.key + "：" + params.row.value)
                ])
            }
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
                                Keyword.data.form.value = params.row.value;
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
                return {
                    key: key,
                    value: me.keyword.chatRoomKeyMap[key]
                };
            }), {
                key: me.keyword.form.filterKey,
                value: me.keyword.form.filterKey
            });
        }
    },
    methods: {
        syncKeywords(){
            let me = this;
            Web.ajax("keyword/getKeywords", {
                success: function (data) {
                    console.log(data.info)
                    wxbot.syncKeywords(data.info);
                },
                fail: function (data) {
                }
            });
            if(me.keyword.form.seq){
                me.getKeyMap();
            }
        },
        loadKeyMap(seq){
            this.keyword.form.seq = seq;
            this.getKeyMap();
        },
        getKeyMap(){
            let me = this;
            me.keyword.chatRoomKeyMapLoading = true;
            Web.ajax("keyword/getKeywords", {
                data:{
                    seq:me.keyword.form.seq
                },
                success: function (data) {
                    console.log(data)
                    if(data.info)
                        me.keyword.chatRoomKeyMap = data.info[me.keyword.form.seq];
                    else
                        me.keyword.chatRoomKeyMap = {};
                    me.keyword.chatRoomKeyMapLoading = false;
                },
                fail: function (data) {
                }
            });
        },
        editKeyMapOk(){
            let me = this;
            let keyMap = {};
            keyMap[me.keyword.form.key] = me.keyword.form.value;
            Web.ajax("keyword/set", {
                data:{
                    seq:me.keyword.form.seq,
                    keyMap:keyMap
                },
                success: function (data) {
                    me.$set(me.keyword.chatRoomKeyMap, me.keyword.form.key, me.keyword.form.value);
                    wxbot.setKeyMap(me.keyword.form.seq, me.keyword.form.key, me.keyword.form.value);
                    me.keyword.form.key = "";
                    me.keyword.form.value = "";
                    me.keyword.form.modal = false;
                    me.keyword.form.modalLoading = false;
                    me.$Message.success("操作成功!");
                },
                fail: function (data) {
                    me.keyword.form.modalLoading = false;
                    me.$nextTick(() => {
                        me.keyword.form.modalLoading = true;
                    });
                    me.$Message.error("操作失败："+data.msg);
                }
            });
        },
        editKeyMapCancel(){
            this.keyword.form.key = "";
            this.keyword.form.value = "";
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
                    console.log(data);
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