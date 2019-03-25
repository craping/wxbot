<template>
<section>
    <topNav></topNav>
    <van-cell-group>
        <van-cell icon="manager-o">
            <template slot="title">
                <span class="custom-text">图灵AI</span>
                <van-switch v-model="turing" size="24px" style="float:right" @change="updSwitchs($event,'turing')"/>
            </template>
        </van-cell>
    </van-cell-group>

    <van-cell-group>
        <van-cell icon="chat-o">
            <template slot="title">
                <span class="custom-text">自己对自己聊天消息群转发</span>
                <van-switch v-model="forwards" size="24px" style="float:right" @change="updSwitchs($event,'forwards')"/>
            </template>
        </van-cell>
    </van-cell-group>

    <van-cell-group>
        <van-cell icon="label-o">
            <template slot="title">
                <span class="custom-text">群关键词</span>
                <van-switch v-model="keywords" size="24px" style="float:right" @change="updSwitchs($event,'keywords')"/>
            </template>
        </van-cell>
        <van-cell title="群关键词设置" icon="setting-o" is-link v-show="keywords" @click="goSetting('keyword', seq)" />
    </van-cell-group>

    <van-cell-group>
        <van-cell icon="clock-o">
            <template slot="title">
                <span class="custom-text">群定时发消息</span>
                <van-switch v-model="timers" size="24px" style="float:right" @change="updSwitchs($event,'timers')"/>
            </template>
        </van-cell>
        <van-cell title="群定时发消息设置" icon="setting-o" is-link v-show="timers" @click="goSetting('timer', seq)" />
    </van-cell-group>
    <footerNav></footerNav>
</section>
</template>

<script src="./setting.js"></script>

<style scoped>
</style>
