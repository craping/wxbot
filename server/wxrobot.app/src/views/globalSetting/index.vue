<template>
<section>
    <topNav></topNav>
    <div style="padding:10px 0px 10px 0px">
        <van-cell-group>
            <van-cell icon="friends-o">
                <template slot="title">
                    <span class="custom-text">自动接收好友添加请求</span>
                    <van-switch v-model="switchs.autoAcceptFriend" size="24px" style="float:right" 
                        :loading="aafLoading" 
                        @change="updSwitchs('aafLoading')"/>
                </template>
            </van-cell>
            <van-cell icon="label-o">
                <template slot="title">
                    <span class="custom-text">关键词功能</span>
                    <van-switch v-model="switchs.globalKeyword" size="24px" style="float:right" 
                        :loading="gkLoading"  
                        @change="updSwitchs('gkLoading')"/>
                </template>
            </van-cell>
            <van-cell icon="clock-o">
                <template slot="title">
                    <span class="custom-text">定时消息功能</span>
                    <van-switch v-model="switchs.globalTimer" size="24px" style="float:right" 
                        :loading="gtLoading"  
                        @change="updSwitchs('gtLoading')"/>
                </template>
            </van-cell>
        </van-cell-group>
    </div>
    
    <van-cell-group>

        <van-cell icon="label-o">
            <template slot="title">
                <span class="custom-text">全域关键词</span>
                <van-switch v-model="globalKeywords" size="24px" style="float:right" @change="updGlobalSetting($event,'keywords')"/>
            </template>
        </van-cell>
        <van-cell title="全域关键词设置" icon="setting-o" is-link v-show="globalKeywords" @click="goGlobalKeyword" />
   
        <van-cell icon="clock-o">
            <template slot="title">
                <span class="custom-text">全域定时群发</span>
                <van-switch v-model="globalTimers" size="24px" style="float:right" @change="updGlobalSetting($event,'timers')"/>
            </template>
        </van-cell>
        <van-cell title="全域定时群发设置" icon="setting-o" is-link v-show="globalTimers" @click="goGlobalTimer" />
    </van-cell-group>
    <br/>
    <van-cell-group>
        <van-cell title="充值" is-link :url="'http://qd.qyt8.com/u.php?m=Home&c=Order&a=form&token='+token+'&username='+user" />
    </van-cell-group>

    <footerNav></footerNav>
</section>
</template>

<script src="./global-setting.js"></script>

<style scoped>
</style>
