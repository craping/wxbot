<template>
<section>
    <van-nav-bar :title="$route.meta.title" left-text="返回" left-arrow @click-left="goback">
        <van-icon name="add-o" slot="right" size="18px" @click="addPopup=true" />
    </van-nav-bar>
    <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="">
        <van-swipe-cell v-for="e in msgs" :right-width="65">
            <van-cell-group>
                <van-cell :value="getContentTypeDesc(e.type)" icon="clock-o" @click="showContent(e)">
                    <template slot="title">
                        <span class="custom-text timer_type_desc">{{getTimerTypeDesc(e.schedule)}}</span>
                        <span class="custom-text" style="white-space:nowrap">[{{formatTime(e.schedule)}}]</span>
                    </template>
                </van-cell>
            </van-cell-group>
            <span slot="right" @click="delDialog(e.uuid)">删除</span>
        </van-swipe-cell>
    </van-list>

    <!-- 弹出层 新增 -->
    <van-popup v-model="addPopup" position="right" :overlay="false" style="width: 100%; height: 100%;">
        <van-nav-bar title="添加分群定时消息" left-text="返回" left-arrow @click-left="addPopup=false" />
        <van-radio-group v-model="timeRadio">
            <van-cell-group title="时间类型">
                <van-cell title="间隔时间" clickable @click="timeRadio='1'">
                    <van-radio name="1" />
                </van-cell>
                <van-cell title="固定时间" clickable @click="timeRadio='2'">
                    <van-radio name="2" />
                </van-cell>
            </van-cell-group>
        </van-radio-group>

        <van-cell-group v-show="timeRadio=='1'">
            <van-field  label="间隔时间" left-icon="clock-o" v-model="schedule.ss" @focus="show=true" 
                readonly="readonly" clearable placeholder="请输入时间数值，单位：秒"/>
            <p class="timer_desc">
            频率每隔：[{{
                    parseInt(schedule.ss/60/60%60)+"小时"+
                    parseInt(schedule.ss/60%60)+"分钟"+
                    parseInt(schedule.ss%60)+"秒"
                }}]  发送一次
            </p>
        </van-cell-group>
        <van-cell-group v-show="timeRadio=='2'">
            <span class="timer_desc">
                    每当时间到达：[
                <span v-show="schedule.MM != null">{{schedule.MM}}月</span>
                <span v-show="schedule.dd != null">{{schedule.dd}}日</span>
                <span v-show="schedule.HH != null">{{schedule.HH}}时</span>
                <span v-show="schedule.mm != null">{{schedule.mm}}分</span>
                <span v-show="schedule.ss != ''">{{schedule.ss}}秒</span>] 发送一次
            </span>  
            <p class="p_timer_desc">"*" 号为该区域内所有时间段，例："14时5分*秒" 在下午2点5分 每秒发送一次</p>
        </van-cell-group>

        <van-radio-group v-model="contentRadio">
            <van-cell-group title="消息内容">
                <van-cell title="文本" clickable @click="contentRadio='1'">
                <van-radio name="1" />
                    </van-cell>
                <van-cell title="文件" clickable @click="contentRadio='2'">
                    <van-radio name="2" />
                </van-cell>
            </van-cell-group>
        </van-radio-group>

        <div style="margin-bottom:10px;max-height:600px;overflow:hidden;" v-show="contentRadio=='1'">
            <van-field type="textarea" :autosize="{ maxHeight: 200, minHeight: 150 }" 
                v-model="content" 
                clearable 
                placeholder="请输入内容..." />
        </div>

        <div class="uploader" onclick="document.getElementById('hiddenFile').click();" v-show="contentRadio=='2'">
            <div class="ivu-icon">
                <van-icon name="upgrade" />
                <input multiple="multiple" type="file" id="hiddenFile" ref="uploader" @change="onRead" style="display:none;">
            </div>
            <p style="color: rgb(51, 153, 255);">点击上传文件</p>
        </div>

        <div v-if="imageVal&&contentRadio=='2'" @click="showImg">
            <van-icon name="photo-o" size="35px" class="img_icon"/>
            <span class="custom-text" style="display: inline-block; padding-left:5px">
                点击查看图片详情
            </span>
        </div>
        <span v-else-if="contentRadio=='2'" class="custom-text file_txt">{{contentFileName}}</span>

        <van-button size="large" type="info" @click="addTimerMsg" :loading="loading">
            确定提交
        </van-button>

        <!-- popup选择时间 -->
        <van-popup v-model="pickerPopup" position="bottom" >
            <van-picker  show-toolbar title="选择时间"
                :columns="columns" 
                @change="onChange"
                @cancel="pickerPopup=false"
                @confirm="pickerPopup=false" />
        </van-popup>

        <!-- 数字键盘 -->
        <van-number-keyboard :show="show" theme="custom" close-button-text="完成"
            @blur="show = false"
            @input="onInput"
            @delete="onDelete" />
    </van-popup>

    <!-- 删除dialog -->
    <van-dialog v-model="isShow" show-cancel-button :beforeClose="beforeClose">
        <div class="van-dialog__content"><div class="van-dialog__message">确定删除吗？</div></div>
    </van-dialog>

    <footerNav></footerNav>
</section>
</template>

<script src="./timer.js"></script>

<style>
@import "./index.css";
</style>
