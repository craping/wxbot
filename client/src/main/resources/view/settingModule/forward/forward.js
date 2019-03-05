Forward = {
    data:{
        form:{
            filterKey:""
        },
        columns: [{
            title: "群ID",
            width:80,
            key: "seq"
        },{
            title: "群名称",
            key: "NickName"
        }, {
            title: "状态",
            key:"status",
            width:80,
            slot:"status"
        }, {
            title: "操作",
            width:80,
            slot:"opt"
        }],
        chatRooms:[{
            seq:"65535",
            NickName:"上帝群 明天香港采购🇭🇰",
            status:1
        },{
            seq:"65536",
            NickName:"华强电脑 古城便民服务（一）群",
            status:1
        }],
        forwardChatRooms:["65535"]
    },
    computed: {
        forwards() {
            let me = this;
            return me.filterAll(me.chatRooms.map(e => {
                return {
                    seq: e.seq,
                    NickName: e.NickName,
                    status:me.forward.forwardChatRooms.indexOf(e.seq) != -1
                };
            }), {
                NickName: me.forward.form.filterKey
            });
        }
    },
    methods:{
        syncForwards(){
            let me = this;
            Web.ajax("forward/getForward", {
                success: function (data) {
                    console.log(data.info)
                    wxbot.syncKeywords(data.info);
                },
                fail: function (data) {
                }
            });
        },
        addForward(){

        },
        delForward(){

        }
    }
}