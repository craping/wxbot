User = {
    data:{
        pwd:{
            loading:false,
            oldPwd:"",
            newPwd:"",
            repeatPwd:""
        },
        ruleInline:{
            oldPwd: [
                { required: true, message: "当前密码不能为空", trigger: 'blur' }
            ],
            newPwd: [
                { required: true, message: "新密码不能为空", trigger: 'blur' }
            ],
            repeatPwd: [{
                required: true, validator: (rule, value, callback) => {
                    if (value === '') {
                        callback(new Error("确认密码不能为空"));
                    } else if (value !== app.user.pwd.newPwd) {
                        callback(new Error("两次输入的密码不一致"));
                    } else {
                        callback();
                    }
                }, trigger: 'blur'
            }]
        }
    },
    methods:{
        changePwd(){
            const me = this;
            me.$refs.Pwd.validate((valid) => {
                if (valid) {
                    me.user.pwd.loading = true;
                    Web.ajax("user/changePwd", {
                        safe:true,
                        data:{
                            old_pwd:md5(me.user.pwd.oldPwd),
                            new_pwd:md5(me.user.pwd.newPwd)
                        },
                        success: function (data) {
                            me.user.pwd.loading = false;
                            me.$refs.Pwd.resetFields();
                            me.$Message.success("密码修改成功");
                        },
                        fail: function (data) {
                            me.user.pwd.loading = false;
                            me.$Message.error("密码修改失败："+data.msg);
                        },
                        error:function(XMLHttpRequest, textStatus, errorThrown){
                            me.user.pwd.loading = false;
                            me.$Message.error("密码修改失败:"+textStatus);
                        }
                    });
                }
            })
        }
    }
}