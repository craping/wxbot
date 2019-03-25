<template>
<section>
    <van-nav-bar :title="$route.meta.title" left-text="返回" left-arrow @click-left="goback">
        <van-icon name="add-o" slot="right" size="18px" @click="popup = true" />
    </van-nav-bar>
    <van-search placeholder="请输入搜索关键词" v-model="keyword" />
    <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="" >
        <van-swipe-cell v-for="e in keyMap" :right-width="150" >
            <van-cell-group>
                <van-cell :title="e.key" :value="getContentTypeDesc(e.type)" @click="showContent(e.key)"/>
            </van-cell-group>
            <span slot="right">
                <van-button type="info" style="float:left;width:75px" square @click="updPopup(e.key)">
                    修改
                </van-button>
                <van-button type="danger" style="float:right;width:75px" square @click="delDialog(e.key)">
                    删除
                </van-button>
            </span>
        </van-swipe-cell>
    </van-list>

    <!-- 弹出层 修改 新增 -->
    <van-popup v-model="popup" position="right" :overlay="false" style="width: 100%; height: 100%;">
        <van-nav-bar :title="popupItem.barTitle" left-text="返回" left-arrow @click-left="closePopup" />
        <van-field v-model="popupItem.keyword" placeholder="请输入关键字..." clearable :disabled="popupItem.txtDisabled"/>    
        <van-radio-group v-model="radio">
            <van-cell-group>
                <van-cell title="文本" clickable @click="radio='1'">
                    <van-radio name="1" />
                </van-cell>
                <van-cell title="文件" clickable @click="radio='2'">
                    <van-radio name="2" />
                </van-cell>
            </van-cell-group>
        </van-radio-group>

        <div style="margin-bottom:10px;max-height:600px;overflow:hidden;" v-show="radio=='1'">
            <van-field type="textarea" 
                :autosize="{ maxHeight: 200, minHeight: 150 }" 
                v-model="popupItem.content" 
                clearable 
                placeholder="请输入内容..." />
        </div>

        <div class="uploader" onclick="document.getElementById('hiddenFile').click();" v-show="radio=='2'">
            <div class="ivu-icon">
                <van-icon name="upgrade" />
                <input multiple="multiple" type="file" id="hiddenFile" ref="uploader" @change="onRead" style="display:none;">
            </div>
            <p style="color: rgb(51, 153, 255);">点击上传文件</p>
        </div>

        <div v-if="popupItem.imageVal&&radio=='2'" @click="showImg">
            <van-icon name="photo-o" size="35px" class="img_icon"/>
            <span class="custom-text" style="display: inline-block; padding-left:5px">
                点击查看图片详情
            </span>
        </div>
        <span v-else-if="radio=='2'" class="custom-text file_txt">
            {{popupItem.contentFileName}}
        </span>

        <van-button size="large" type="info" @click="addKeyword">确定提交</van-button>
    </van-popup>

    <!-- 删除dialog -->
    <van-dialog v-model="isShow" show-cancel-button :beforeClose="beforeClose">
        <div class="van-dialog__content"><div class="van-dialog__message">确定删除吗？</div></div>
    </van-dialog>

    <footerNav></footerNav>
</section>
</template>

<script src="./keyword.js"></script>
<style>
.van-cell__title {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    white-space: nowrap;  
    text-overflow:ellipsis; 
    overflow:hidden;
}
.van-swipe-cell__right {
  display: inline-block;
  width: 150px;
  height: 44px;
  font-size: 15px;
  line-height: 44px;
}
.file_txt{
    display: inline-block; 
    padding:0px 0px 10px 8px
}
.img_icon{
    padding-left:8px; 
    display: inline-block; 
    vertical-align: middle;
}
.uploader {
    text-align:center;
    border:1px dashed #1989fa; 
    background:#fff;
    padding-top:10px;
    margin:10px;
}
.ivu-icon {
    font-size: 52px;
    color: rgb(51, 153, 255);
    display: inline-block;
    font-family: Ionicons;
    speak: none;
    font-style: normal;
    font-weight: 400;
    font-variant: normal;
    text-transform: none;
    text-rendering: auto;
    line-height: 1;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    vertical-align: middle;
    text-align: center;
}
</style>
