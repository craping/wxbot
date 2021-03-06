$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script("lib/jquery/jquery-3.3.1.min.js", "jquery");
$script("lib/crypto.min.js", "crypto");
$script("lib/md5.min.js", "md5");
$script("lib/common.js", "common");
var app;
$script.ready(["vue", "iview", "jquery", "crypto", "md5", "common"], function () {
    app = new Vue({
        el: "#app",
        data: {
            skin: "dark",
            tab:"login",
            modal:false,
            user:{},
            users:[],
            remember:false,
            loading:true,
            Login:{
                mobile:"",
                password:"",
                loading:false,
                error:{}
            },
            Register:{
                mobile:"",
                password:"",
                repeatPwd:"",
                code:"",
                loading:false,
                disabled:false,
                getMessageText:"获取验证码",
                error:{}
            },
            Reset:{
                mobile:"",
                code:"",
                loading:true,
                disabled:false,
                getMessageText:"获取验证码",
                error:{}
            },
            ruleInline:{
                mobile: [
                    { required: true, message: "手机号码不能为空", trigger: 'blur' }
                ],
                password: [
                    { required: true, message: "密码不能为空", trigger: 'blur' }
                ],
                repeatPwd: [{
                    required: true, validator: (rule, value, callback) => {
                        if (value === '') {
                            callback(new Error("确认密码不能为空"));
                        } else if (value !== app.Register.password) {
                            callback(new Error("两次输入的密码不一致"));
                        } else {
                            callback();
                        }
                    }, trigger: 'blur'
                }],
                code: [
                    { required: true, message: "验证码", trigger: 'blur' }
                ]
            }
        },
        computed:{
        },
        mounted() {
            const me = this;
            let users = localStorage.getItem("users");
            users = users?JSON.parse(users):[];
            this.users = users;
            const user = users[0];
            if(user){
                this.Login.mobile = user.mobile;
                this.Login.password = user.pwd;
                this.remember = user.remember;
            }

            Web.ajax("api/getPublicKey", {
                success: function (data) {
                    Crypto.setRSAPublicKey(data.info.n);
                    Crypto.encryptFlag = data.info.id;
                    me.loading = false;
                },
                fail: function (data) {
                    wxbot.exit("错误", "机器人始化失败："+data.msg);
                },
                error:function(XMLHttpRequest, textStatus, errorThrown){
                    wxbot.exit("错误", "程序启动失败，请检查网络是否连接");
                }
            });
        },
        updated() {
        },
        methods:{
            search(event){
                const keyCode = event.keyCode;
                if(keyCode == 13){
                    event.target.setSelectionRange(-1, -1);
                    this.loginSubmit();
                } else if(((keyCode >= 48 && keyCode <= 57) || (keyCode >= 96 && keyCode <= 105))) {
                    if(window.getSelection().toString() != "")
                        return;
                    if(event.target.value){
                        const user = this.users.find(u => u.mobile.indexOf(event.target.value) == 0);
                        if(user){
                            const l = event.target.value.length;
                            // event.target.value = user.mobile;
                            this.Login.mobile = user.mobile;
                            this.$nextTick(() => {
                                event.target.setSelectionRange(l, -1);
                            });
                            this.remember = user.remember;
                            this.Login.password = user.pwd;
                        } else {
                            this.remember = false;
                            this.Login.password = "";
                        }
                    } else {
                        this.remember = false;
                        this.Login.password = "";
                    }
                }
            },
            selectUser(name) {
                this.Login.mobile = name;
                const user = this.users.find(u => u.mobile == name);
                this.Login.password = user.pwd;
                this.remember = user.remember;
            },
            loginSubmit(){
                const me = this;
                if(me.Login.loading)
                    return;
                me.$refs.Login.validate((valid) => {
                    if (valid) {
                        me.Login.loading = true;
                        Web.ajax("user/login", {
                            safe:true,
                            data:{
                                login_name:me.Login.mobile,
                                login_pwd:md5(me.Login.password)
                            },
                            success: function (data) {
                                const user = {
                                    mobile:me.Login.mobile,
                                    pwd:me.remember?me.Login.password:"",
                                    remember:me.remember
                                };

                                let users = localStorage.getItem("users");
                                users = users?JSON.parse(users):[];
                                const index = users.findIndex(u => u.mobile == user.mobile);
                                if(index != -1){
                                    users.splice(index, 1);
                                }
                                users.unshift(user);
                                localStorage.setItem("users", JSON.stringify(users));

                                wxbot.start(data.info);
                            },
                            fail: function (data) {
                                me.Login.loading = false;
                                me.$Message.error("登录失败："+data.msg);
                            },
                            error:function(XMLHttpRequest, textStatus, errorThrown){
                                me.Login.loading = false;
                                me.$Message.error("登录失败:"+textStatus);
                            }
                        });
                    } else {
                    }
                })
            },
            registerSubmit(){
                const me = this;
                // me.Login.mobile = me.$refs.mobile.value;
                if(me.Register.loading)
                    return;
                me.$refs.Register.validate((valid) => {
                    if (valid) {
                        me.Register.loading = true;
                        Web.ajax("user/register", {
                            safe:true,
                            data:{
                                user_name:me.Register.mobile,
                                user_pwd:md5(me.Register.password),
                                confirm_pwd:md5(me.Register.repeatPwd),
                                code:me.Register.code
                            },
                            success: function (data) {
                                me.Register.loading = false;
                                me.$refs.Register.resetFields();
                                me.tab = "login";
                                me.$Message.success("注册成功");
                            },
                            fail: function (data) {
                                me.Register.loading = false;
                                me.$Message.error("注册失败："+data.msg);
                            },
                            error:function(XMLHttpRequest, textStatus, errorThrown){
                                me.Register.loading = false;
                                me.$Message.error("注册失败:"+textStatus);
                            }
                        });
                    } else {
                    }
                })
            },
            registerCode(){
                const me = this;
                me.$refs.Register.validateField("mobile", (valid) => {
                    if (!valid && !me.Register.disabled) {
                        me.Register.disabled = true;
                        Web.ajax("user/registerCode", {
                            data:{
                                user_name:me.Register.mobile
                            },
                            success: function (data) {
                                let i = 60;
                                let timer = setInterval(() => {
                                    me.Register.getMessageText = "获取验证码("+(i--)+"s)";
                                    if(i <= 0){
                                        clearInterval(timer);
                                        me.Register.disabled = false;
                                        me.Register.getMessageText = "获取验证码";
                                    }
                                }, 1000);
                                me.$Message.success("验证码发送成功");
                            },
                            fail: function (data) {
                                me.Register.disabled = false;
                                me.$Message.error("验证码发送失败："+data.msg);
                            },
                            error:function(XMLHttpRequest, textStatus, errorThrown){
                                me.Register.disabled = false;
                                me.$Message.error("验证码发送失败:"+textStatus);
                            }
                        });
                    }
                });
            },
            resetModel(){
                const me = this;
                me.modal = true;
                me.$refs.Reset.resetFields();
            },
            resetCode(){
                const me = this;
                
                me.$refs.Reset.validateField("mobile", (valid) => {
                    if (!valid && !me.Reset.disabled) {
                        me.Reset.disabled = true;
                        Web.ajax("user/resetCode", {
                            data:{
                                user_name:me.Reset.mobile
                            },
                            success: function (data) {
                                let i = 60;
                                let timer = setInterval(() => {
                                    me.Reset.getMessageText = "获取验证码("+(i--)+"s)";
                                    if(i <= 0){
                                        clearInterval(timer);
                                        me.Reset.disabled = false;
                                        me.Reset.getMessageText = "获取验证码";
                                    }
                                }, 1000);
                                me.$Message.success("验证码发送成功");
                            },
                            fail: function (data) {
                                me.Reset.disabled = false;
                                me.$Message.error("验证码发送失败："+data.msg);
                            },
                            error:function(XMLHttpRequest, textStatus, errorThrown){
                                me.Reset.disabled = false;
                                me.$Message.error("验证码发送失败:"+textStatus);
                            }
                        });
                    }
                });
            },
            resetPassword(){
                const me = this;
                
                me.$refs.Reset.validate((valid) => {
                    if (valid) {
                        Web.ajax("user/resetPwd", {
                            data:{
                                user_name:me.Reset.mobile,
                                code:me.Reset.code
                            },
                            success: function (data) {
                                me.modal = false;
                                me.Reset.loading = false;
                                me.$nextTick(() => {
                                    me.Reset.loading = true;
                                });
                                me.$Message.success({
                                    content:"您的密码已经重置为<"+data.info+">请牢记",
                                    duration:0,
                                    closable: true
                                });
                            },
                            fail: function (data) {
                                me.Reset.loading = false;
                                me.$nextTick(() => {
                                    me.Reset.loading = true;
                                });
                                me.$Message.error("重置密码失败："+data.msg);
                            },
                            error:function(XMLHttpRequest, textStatus, errorThrown){
                                me.Reset.loading = false;
                                me.$nextTick(() => {
                                    me.Reset.loading = true;
                                });
                                me.$Message.error("重置密码失败:"+textStatus);
                            }
                        });
                    } else {
                        me.Reset.loading = false;
                        me.$nextTick(() => {
                            me.Reset.loading = true;
                        });
                    }
                });
            },
            call(){
                wxbot.call(function(data){
                    console.log(data);
                });
            }
        }
    });
    // wxbot.showLogin();
})