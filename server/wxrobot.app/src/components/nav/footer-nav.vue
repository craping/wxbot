<template>
    <van-tabbar v-model="active" @change="barChange()">
        <van-tabbar-item icon="setting-o" >分群设置</van-tabbar-item>
        <van-tabbar-item icon="setting-o">全群设置</van-tabbar-item>
    </van-tabbar>
</template>

<script>
import { getParams } from '@/libs/util'
export default {
    name: 'footerNav',
    data() {
        return {
            active: this.$config.active,
            token: ''
        }
    },
    mounted() {
        this.token = getParams(window.location.href).token;
    },
    methods: {
        barChange(active) {
            if (this.active) {
                this.$config.active = 1;
                this.$router.push({
                    path: '/globalSetting',
                    query: {
                        token: this.token
                    }
                })
            } else {
                this.$config.active = 0;
                this.$router.push({
                    path: '/home',
                    query: {
                        token: this.token
                    }
                })
            }
        }
    }
}
</script>

<style lang="" scoped>

</style>