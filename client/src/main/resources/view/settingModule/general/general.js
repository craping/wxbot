General = {
    data:{
        form:{
            autoAcceptFriend:null,
            globalKeyword:null,
            globalTimer:null
        }
    },
    methods:{
        generalSubmit () {
            console.log(this.setting.switchs)
            console.log(this.general.form)
        },
        generalReset () {
            this.general.form = Object.assign({}, this.setting.switchs);
        }
    }
}