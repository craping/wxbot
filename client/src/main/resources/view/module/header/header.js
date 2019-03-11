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
        }
    },
    computed: {

    },
    methods: {
        deleteEvt(contactMap) {
            Header.data.checkProgress.deleteMapNull = false;
            Object.keys(contactMap).forEach(key => {
                app.$set(Header.data.checkProgress.deleteMap, key, contactMap[key]);
            });
        },
        blacklistEvt(contactMap) {
            Header.data.checkProgress.blacklistMapNull = false;
            Object.keys(contactMap).forEach(key => {
                app.$set(Header.data.checkProgress.blacklistMap, key, contactMap[key]);
            });
        },
        // 检测僵尸粉
        checkFriend() {
            let me = this;
            let title = '僵尸粉检测';
            let content = '<p>确定开始僵尸粉检测吗？</p>';
            if (Header.data.checkLoading) {
                title = '取消检测';
                content = '<p>检测未完成，是否取消操作？</p>';
                me.$Modal.confirm({
                    title: title,
                    content: content,
                    okText: '取消检测',
                    onOk() {
                        Header.data.checkLoading = false;
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
                        Header.data.checkProgress.checkTime = new Date().format("yyyy-MM-dd HH:mm:ss");
                        Header.data.checkProgress.count = 0;
                        Header.data.checkProgress.deleteMap = {};
                        Header.data.checkProgress.deleteMapNull = true;
                        Header.data.checkProgress.blacklistMap = {};
                        Header.data.checkProgress.blacklistMapNull = true;
                        Header.data.checkLoading = true;
                        me.$refs.checkDiv.innerHTML = "检测中...";
                        //创建任务控制类
                        var TaskControl = function (taskFunction, finishFunction) {
                            this.finish = false;
                            this.next = function () {
                                if (!this.finish && Header.data.checkLoading) {
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
                                if (this.index >= this.data.length || !Header.data.checkLoading) {
                                    this.finish = true;
                                } else {
                                    Header.data.checkProgress.count++;
                                    var contact = this.data[this.index];
                                    //wxbot.sendText(contact.seq, contact.NickName, contact.UserName, "HI!");
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
                            Header.data.checkLoading = false;
                            me.$refs.checkDiv.innerHTML = "僵尸粉检测";
                        };

                        var run = new TaskControl(task, finish);
                        run.data = Contacts.data.individuals; //联系人列表
                        run.index = -1;//默认索引
                        run.next();    //开始执行
                    }
                });
            }
        }
    }
}