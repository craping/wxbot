Header = {
    data: {
        checkLoading: false,    // 检测僵尸粉loading
        checkProgress: {        // 检测 进度参数
            checkTime: '',
            count: 0,
            begin: false,
            deleteMap: {},
            deleteMapNull: true,
            blacklistMap: {},
            blacklistMapNull: true,
            modal: false,
            type: 'text',
            msg: {},
            text: '',
            interval:3
        },
        notice:{
            drawer:false,
            msgs:[]
        },
        wapSite:{
            modal:false,
            url:""
        }
    },
    computed: {
        noticeCount(){
            // let readNotices = localStorage.getItem("readNotices");
            // if(readNotices)
            //     readNotices = readNotices.split(",");
            // else
            //     readNotices = [];
            // return this.header.notice.msgs.filter(m => !readNotices.includes(m.id+"")).length;
            return this.header.notice.msgs.filter(m => !m.read).length;
        }
    },
    methods: {
        syncTuringKey(){
            wxbot.syncTuringKey(localStorage.getItem("turingKey"));
        },
        noticeList(){
            const me = this;
            me.$Notice.config({
                top: 545
            });
            Web.ajax("user/noticeList", {
                success: function (data) {
                    if(data.info){
                        let readNotices = localStorage.getItem("readNotices");
                        if(readNotices)
                            readNotices = readNotices.split(",");
                        else
                            readNotices = [];
                        me.header.notice.msgs = data.info.map(n => {
                            n.read = readNotices.includes(n.id+"");
                            return n;
                        });
                    }
                },
                fail: function (data) {
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                }
            });
        },
        notify(msg){
            const me = this;
            me.header.notice.count++;
            me.header.notice.msgs.push(msg);
            me.$Notice.info({
                title: "公告消息",
                name:msg.id,
                render: h => {
                    return [
                        h('span', {
                            "class":"text-truncate",
                            style:{
                                maxWidth:"200px",
                                display:"inline-block",
                                verticalAlign: "middle"
                            }
                        }, msg.title), 
                        h("a", {
                            "class":"ml-1",
                            style:{
                                display:"inline-block",
                                verticalAlign: "middle"
                            },
                            attrs:{
                                href:"javascript:;"
                            },
                            on:{
                                click(){
                                    me.header.notice.drawer = true;
                                    me.$Notice.close(msg.id);
                                }
                            }
                        }, "查看")
                    ]
                },
                duration: 0
            });
        },
        noticeRead(id){
            if(id[0]){
                let readNotices = localStorage.getItem("readNotices");
                if(readNotices)
                    readNotices = readNotices.split(",");
                else
                    readNotices = [];
                const msg = this.header.notice.msgs.find(m => m.id == id);
                if(msg){
                    msg.read = true;
                    if(readNotices.findIndex(e => e == id+"") == -1){
                        readNotices.push(id);
                        localStorage.setItem("readNotices", readNotices.join(","));
                    }
                }
            }
        },
        deleteEvt(contactMap) {
            let me = this;
            me.header.checkProgress.deleteMapNull = false;
            Object.keys(contactMap).forEach(key => {
                app.$set(me.header.checkProgress.deleteMap, key, contactMap[key]);
            });
        },
        blacklistEvt(contactMap) {
            let me = this;
            me.header.checkProgress.blacklistMapNull = false;
            Object.keys(contactMap).forEach(key => {
                app.$set(me.header.checkProgress.blacklistMap, key, contactMap[key]);
            });
        },
        chooseFile() {
            wxbot.openPicFile();
        },
        // 检测僵尸粉
        onZombieTest() {
            let me = this;
            if (me.header.checkLoading) {
                me.$Modal.confirm({
                    title: '取消检测',
                    content: '<p>检测未完成，是否取消操作？</p>',
                    okText: '取消检测',
                    onOk() {
                        wxbot.stopZombieTest();
                        me.header.checkLoading = false;
                        me.$refs.checkDiv.innerHTML = "僵尸粉检测";
                    }
                });
            } else {
                this.getZombieTestMsg();
                me.header.checkProgress.modal = true;
            }
        },
        getZombieTestMsg() {
            this.header.checkProgress.msg = wxbot.getZombieTestMsg();
        },
        doneTest() {
            let me = this;
            me.header.checkLoading = false;
            me.$refs.checkDiv.innerHTML = "僵尸粉检测";
        },
        beginTest() {
            let me = this;
            me.header.checkProgress.checkTime = new Date().format("yyyy-MM-dd HH:mm:ss");
            //me.header.checkProgress.count = 0;
            me.header.checkProgress.deleteMap = {};
            me.header.checkProgress.deleteMapNull = true;
            me.header.checkProgress.blacklistMap = {};
            me.header.checkProgress.blacklistMapNull = true;
            me.header.checkLoading = true;
            me.$refs.checkDiv.innerHTML = "检测中...";
            // 开始检测
            wxbot.zombieTestTxt(me.header.checkProgress.interval, me.header.checkProgress.type, me.header.checkProgress.text);
        }
    }
}