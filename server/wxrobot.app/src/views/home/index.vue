<template>
<section>
    <div style="padding:0px 0px 5px">
        <van-search placeholder="请输入群名称关键字..." v-model="keyword" show-action 
            @clear="getContacts"
            @search="onSearch">
            <div slot="action" @click="onSearch">搜索</div>
        </van-search>
    </div>
    <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="getContacts" >
        <van-cell v-for="(item, index) in chatRooms" value="" is-link @click="goSetting(item.seq)" style="white-space:nowrap">
            <template slot="title">
                <div class="img_div">
                    <img class="avatar" :src="item.headImgUrl" mm-src="" alt="" title="">
                </div>
                <div class="info_div" v-html="item.nickName"></div>
            </template>
        </van-cell>
    </van-list>
    <footerNav></footerNav>
</section>
</template>

<script src="./home.js"></script>

<style scoped>
@import "./face.css";
.van-cell__right-icon {
    color: black;
    margin-left: 5px;
}
.van-cell__left-icon, .van-cell__right-icon {
    height: 24px;
    min-width: 1em;
    font-size: 16px;
    line-height: 35px;
}
.img_div {
    display: inline-block;
    vertical-align: middle;
}
.info_div {
    display: inline; padding-left:10px;
}
.info_div span {
    display: inline;
}
.avatar {
    display: block;
    cursor: pointer;
    width: 35px;
    height: 35px;
    background-color: #ccc;
}
</style>
