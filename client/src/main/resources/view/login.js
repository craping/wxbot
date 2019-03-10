$script("lib/vue/vue.js", "vue");
$script("lib/iview-3/iview.min.js", "iview");
$script("lib/jquery/jquery-3.3.1.min.js", "jquery");
$script("lib/crypto.min.js", "crypto");
$script("lib/common.js", "common");
var app;
$script.ready(["vue", "iview", "jquery", "crypto", "common"], function () {
    app = new Vue({
        el: "#app",
        data: {
            skin: "dark",
            modal:false,
            Login:{
                login_name:"",
                login_pwd:"",
                loading:false,
                error:{}
            },
            Register:{
                login_name:"",
                login_pwd:"",
                code:"",
                loading:false,
                getMessageText:"获取验证码",
                error:{}
            },
            ruleInline:{
                login_name: [
                    { required: true, message: "手机号码不能为空", trigger: 'blur' }
                ],
                login_pwd: [
                    { required: true, message: "密码不能为空", trigger: 'blur' }
                ],
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
                me.$refs.Login.validate((valid) => {
                    if (valid) {
                        me.Login.loading = true;
                        Web.ajax("user/login", {
                            data:me.Login,
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
                me.$refs.Register.validate((valid) => {
                    if (valid) {
                        me.Register.loading = true;
                        Web.ajax("user/register", {
                            data:me.Register,
                            success: function (data) {
                                me.Register.loading = false;
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

            },
        }
    });
})