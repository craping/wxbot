(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-b6132850"],{"32a6":function(t,e,n){var s=n("241e"),i=n("c3a1");n("ce7e")("keys",function(){return function(t){return i(s(t))}})},7424:function(t,e,n){"use strict";n.r(e);var s=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("section",[n("topNav"),n("van-cell-group",[n("van-cell",{attrs:{icon:"manager-o"}},[n("template",{slot:"title"},[n("span",{staticClass:"custom-text"},[t._v("图灵AI")]),n("van-switch",{staticStyle:{float:"right"},attrs:{size:"24px"},on:{change:function(e){return t.updSwitchs(e,"turing")}},model:{value:t.turing,callback:function(e){t.turing=e},expression:"turing"}})],1)],2)],1),n("van-cell-group",[n("van-cell",{attrs:{icon:"chat-o"}},[n("template",{slot:"title"},[n("span",{staticClass:"custom-text"},[t._v("自己对自己聊天消息群转发")]),n("van-switch",{staticStyle:{float:"right"},attrs:{size:"24px"},on:{change:function(e){return t.updSwitchs(e,"forwards")}},model:{value:t.forwards,callback:function(e){t.forwards=e},expression:"forwards"}})],1)],2)],1),n("van-cell-group",[n("van-cell",{attrs:{icon:"label-o"}},[n("template",{slot:"title"},[n("span",{staticClass:"custom-text"},[t._v("群关键词")]),n("van-switch",{staticStyle:{float:"right"},attrs:{size:"24px"},on:{change:function(e){return t.updSwitchs(e,"keywords")}},model:{value:t.keywords,callback:function(e){t.keywords=e},expression:"keywords"}})],1)],2),n("van-cell",{directives:[{name:"show",rawName:"v-show",value:t.keywords,expression:"keywords"}],attrs:{title:"群关键词设置",icon:"setting-o","is-link":""},on:{click:function(e){return t.goSetting("keyword",t.seq)}}})],1),n("van-cell-group",[n("van-cell",{attrs:{icon:"clock-o"}},[n("template",{slot:"title"},[n("span",{staticClass:"custom-text"},[t._v("群定时发消息")]),n("van-switch",{staticStyle:{float:"right"},attrs:{size:"24px"},on:{change:function(e){return t.updSwitchs(e,"timers")}},model:{value:t.timers,callback:function(e){t.timers=e},expression:"timers"}})],1)],2),n("van-cell",{directives:[{name:"show",rawName:"v-show",value:t.timers,expression:"timers"}],attrs:{title:"群定时发消息设置",icon:"setting-o","is-link":""},on:{click:function(e){return t.goSetting("timer",t.seq)}}})],1),n("footerNav")],1)},i=[],o=n("a4bb"),a=n.n(o),c=(n("6762"),n("2fdb"),n("dc5d")),r=n("5b59"),l=n("b970"),u=n("c276"),f={components:{topNav:c["a"],footerNav:r["a"],Toast:l["e"]},data:function(){return{seq:"",turing:!1,keywords:!1,timers:!1,forwards:!1,setting:{},token:""}},mounted:function(){this.token=Object(u["c"])(window.location.href).token_m,this.seq=Object(u["c"])(window.location.href).seq,this.loadSetting()},methods:{goSetting:function(t,e){this.$config.active=null,this.$router.push({path:"/"+t,query:{seq:e,token_m:this.token}})},updSwitchs:function(t,e){var n=this,s="setting/disableSeq?format=json";t&&(s="setting/enableSeq?format=json");var i={seq:this.seq,module:e,token:this.token};this.$http.post(s,i).then(function(s){var i=s.data;i.result?l["e"].fail(i.msg):(t?n.setting[e].push(n.seq):n.setting[e].splice(n.setting[e].indexOf(n.seq),1),setTimeout(function(){Object(l["e"])("操作成功")},500),n.$config.setting=n.setting)}).catch(function(t){console.log(t)})},initSwitchs:function(){this.turing=this.setting.turing.includes(this.seq),this.keywords=this.setting.keywords.includes(this.seq),this.timers=this.setting.timers.includes(this.seq),this.forwards=this.setting.forwards.includes(this.seq)},loadSetting:function(){var t=this;if(a()(this.$config.setting).length)return this.setting=this.$config.setting,void this.initSwitchs();l["e"].loading({forbidClick:!0,duration:0,message:"正在请求..."}),this.$http.post("setting/getSetting?format=json",{token:this.token}).then(function(e){var n=e.data;console.log(n),n.result?l["e"].fail(n.msg):(t.setting=t.$config.setting=n.data.info,setTimeout(function(){Object(l["e"])("初始化用户配置成功")},500),t.initSwitchs())}).catch(function(t){console.log(t)})}}},h=f,g=n("2877"),d=Object(g["a"])(h,s,i,!1,null,"fafdef80",null);e["default"]=d.exports},"8aae":function(t,e,n){n("32a6"),t.exports=n("584a").Object.keys},a4bb:function(t,e,n){t.exports=n("8aae")},ce7e:function(t,e,n){var s=n("63b6"),i=n("584a"),o=n("294c");t.exports=function(t,e){var n=(i.Object||{})[t]||Object[t],a={};a[t]=e(n),s(s.S+s.F*o(function(){n(1)}),"Object",a)}},dc5d:function(t,e,n){"use strict";var s=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("van-nav-bar",{attrs:{title:t.$route.meta.title,"left-text":"返回","left-arrow":""},on:{"click-left":t.goback}})},i=[],o={name:"topNav",data:function(){return{}},methods:{goback:function(){this.$router.go(-1)}}},a=o,c=n("2877"),r=Object(c["a"])(a,s,i,!1,null,"2fe8c154",null);e["a"]=r.exports}}]);
//# sourceMappingURL=chunk-b6132850.8cb0dfe5.js.map