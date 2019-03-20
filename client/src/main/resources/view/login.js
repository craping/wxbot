$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script("lib/jquery/jquery-3.3.1.min.js", "jquery");
$script("lib/crypto.min.js", "crypto");
$script("lib/md5.min.js", "md5");
$script("lib/common.js", "common");
var app;
$script.ready(["vue", "iview", "jquery", "crypto", "md5", "common"], function () {
    Web.ajax("api/getPublicKey", {
        success: function (data) {
            Crypto.setRSAPublicKey(data.info.n);
            Crypto.encryptFlag = data.info.id;

            app = new Vue({
                el: "#app",
                data: {
                    skin: "dark",
                    modal:false,
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
                },
                updated() {
                },
                methods:{
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
                                        me.Login.loading = false;
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
                    seedMessage(){
                        const me = this;
                        me.Register.disabled = true;
                        let i = 30;
                        let timer = setInterval(() => {
                            me.Register.getMessageText = "获取验证码("+(i--)+"s)";
                            if(i <= 0){
                                clearInterval(timer);
                                me.Register.disabled = false;
                                me.Register.getMessageText = "获取验证码";
                            }
                        }, 1000);
                    },
                    call(){
                        wxbot.call(function(data){
                            console.log(data);
                        });
                    }
                }
            });
            wxbot.showLogin();
        },
        fail: function (data) {
            wxbot.shutdown("机器人始化失败："+data.msg);
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            wxbot.shutdown("程序启动失败，请检查网络是否连接");
        }
    });
})