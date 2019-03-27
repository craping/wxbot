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
            blacklistMapNull: true
        },
        notice:{
            drawer:false,
            msgs:[]
        },
        wapSite:{
            modal:false,
            url:Web.serverURL+":88/#/home?token="+Web.user.token
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
        // 检测僵尸粉
        checkFriend() {
            let me = this;
            let title = '僵尸粉检测';
            let content = '<p>确定开始僵尸粉检测吗？</p>';
            if (me.header.checkLoading) {
                title = '取消检测';
                content = '<p>检测未完成，是否取消操作？</p>';
                me.$Modal.confirm({
                    title: title,
                    content: content,
                    okText: '取消检测',
                    onOk() {
                        me.header.checkLoading = false;
                        me.$refs.checkDiv.innerHTML = "僵尸粉检测";
                    }
                });
            } else {
                me.$Modal.confirm({
                    title: title,
                    content: content,
                    okText: '开始检测',
                    onOk() {
                        // 初始化数据
                        me.header.checkProgress.checkTime = new Date().format("yyyy-MM-dd HH:mm:ss");
                        me.header.checkProgress.count = 0;
                        me.header.checkProgress.deleteMap = {};
                        me.header.checkProgress.deleteMapNull = true;
                        me.header.checkProgress.blacklistMap = {};
                        me.header.checkProgress.blacklistMapNull = true;
                        me.header.checkLoading = true;
                        me.$refs.checkDiv.innerHTML = "检测中...";
                        //创建任务控制类
                        var TaskControl = function (taskFunction, finishFunction) {
                            this.finish = false;
                            this.next = function () {
                                if (!this.finish && me.header.checkLoading) {
                                    taskFunction.call(this);
                                } else {
                                    finishFunction.call(this);
                                }
                            };
                        };
                        // 调用发送sendText消息方法
                        var task = function () {
                            var send = function () {
                                this.index++;
                                console.time("任务：" + this.index);
                                //判断列表中还有没有任务
                                if (this.index >= this.data.length || !me.header.checkLoading) {
                                    this.finish = true;
                                } else {
                                    me.header.checkProgress.count++;
                                    var contact = this.data[this.index];
                                    wxbot.sendText(contact.seq, contact.NickName, contact.UserName, "僵尸检测，此消息不用回复!");
                                }
                                console.timeEnd("任务：" + this.index);
                                //继续下一个
                                this.next();
                            }.bind(this);
                            setTimeout(send, 1000);
                            //send();
                        };

                        var finish = function () {
                            console.log("任务完成");
                            me.header.checkLoading = false;
                            me.$refs.checkDiv.innerHTML = "僵尸粉检测";
                        };

                        var run = new TaskControl(task, finish);
                        run.data = me.contacts.individuals; //联系人列表
                        run.index = -1;//默认索引
                        run.next();    //开始执行
                    }
                });
            }
        }
    }
}