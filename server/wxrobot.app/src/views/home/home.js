import { Toast } from 'vant';
import footerNav from '@/components/nav/footer-nav'
import { getParams } from '@/libs/util'
export default {
    components: {
        Toast,
        footerNav
    },
    data() {
        return {
            keyword: '',
            chatRooms: [],
            loading: true,
            finished: false,
            token: '',
            user:''
        }
    },
    computed: {
        keyMap() {
            return this.chatRooms.filter(e => 
                e.nickName.indexOf(this.keyword.trim()) >= 0
            );
        }
    },
    mounted() {
        this.token = getParams(window.location.href).token;
        this.user = getParams(window.location.href).user;
        this.getContacts();
    },
    methods: {
        goSetting(seq) {
            this.$config.active = null;
            this.$router.push({
                path: '/setting',
                query: {
                    seq: seq,
                    token: this.token,
                    user: this.user
                }
            })
        },
        getContacts() {
            if (this.$config.chatRooms.length) {
                this.chatRooms = this.$config.chatRooms;
                this.loading = false;
                this.finished = true;
                return;
            }
            Toast.loading({
                forbidClick: true,
                duration: 0,
                message: '正在请求...'
            });
            this.$http.post("/contact/getContacts?format=json", {token: this.token}).then(response => {
                const data = response.data;
                if (!data.result) {
                    this.chatRooms = this.$config.chatRooms = data.data.info;
                    setTimeout(() => {
                        Toast('加载群列表成功');
                    }, 500);
                    this.loading = false;
                    this.finished = true;
                } else {
                    Toast.fail(data.msg);
                }
            })
            .catch(error => {
                console.log(error);
            });
        }
    }
}