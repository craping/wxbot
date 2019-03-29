<template>
<section>
    <topNav></topNav>
    <van-radio-group v-model="radio">
        <van-cell-group>
            <van-cell title="文本" clickable @click="radio ='1'">
            <van-radio name="1" />
                </van-cell>
            <van-cell title="文件" clickable @click="radio ='2'">
                <van-radio name="2" />
            </van-cell>
        </van-cell-group>
    </van-radio-group>

    <div style="margin-bottom:10px;max-height:600px;overflow:hidden;" v-show="radio=='1'">
        <van-field type="textarea" 
            :autosize="{ maxHeight: 200, minHeight: 150 }" 
            v-model="tipsTxt" 
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

    <div v-if="imageVal&&radio=='2'" @click="showImg">
        <van-icon name="photo-o" size="35px" class="img_icon"/>
        <span class="custom-text" style="display: inline-block; padding-left:5px">
            点击查看图片详情
        </span>
    </div>
    <span v-else-if="radio=='2'" class="custom-text file_txt">
        {{tipFileName}}
    </span>

    <van-button size="large" type="info" @click="setTpis" :loading="loading">确定提交</van-button><br /><br />
    <van-button size="large" type="danger" @click="isShow=true">取消设置</van-button>

    <!-- 取消dialog -->
    <van-dialog v-model="isShow" show-cancel-button :beforeClose="beforeClose">
        <div class="van-dialog__content"><div class="van-dialog__message">取消后，提示语能失效，确定？</div></div>
    </van-dialog>

    <footerNav></footerNav>
</section>
</template>

<script src="./tips.js"></script>

<style scoped>
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
