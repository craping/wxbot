(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-644ad424"],{"32a6":function(t,e,n){var o=n("241e"),a=n("c3a1");n("ce7e")("keys",function(){return function(t){return a(o(t))}})},"355d":function(t,e){e.f={}.propertyIsEnumerable},"386d":function(t,e,n){"use strict";var o=n("cb7c"),a=n("83a1"),i=n("5f1b");n("214f")("search",1,function(t,e,n,c){return[function(n){var o=t(this),a=void 0==n?void 0:n[e];return void 0!==a?a.call(n,o):new RegExp(n)[e](String(o))},function(t){var e=c(n,t,this);if(e.done)return e.value;var s=o(t),r=String(this),l=s.lastIndex;a(l,0)||(s.lastIndex=0);var p=i(s,r);return a(s.lastIndex,l)||(s.lastIndex=l),null===p?-1:p.index}]})},5176:function(t,e,n){t.exports=n("51b6")},"51b6":function(t,e,n){n("a3c3"),t.exports=n("584a").Object.assign},"83a1":function(t,e){t.exports=Object.is||function(t,e){return t===e?0!==t||1/t===1/e:t!=t&&e!=e}},"8aae":function(t,e,n){n("32a6"),t.exports=n("584a").Object.keys},9306:function(t,e,n){"use strict";var o=n("c3a1"),a=n("9aa9"),i=n("355d"),c=n("241e"),s=n("335c"),r=Object.assign;t.exports=!r||n("294c")(function(){var t={},e={},n=Symbol(),o="abcdefghijklmnopqrst";return t[n]=7,o.split("").forEach(function(t){e[t]=t}),7!=r({},t)[n]||Object.keys(r({},e)).join("")!=o})?function(t,e){var n=c(t),r=arguments.length,l=1,p=a.f,u=i.f;while(r>l){var d,f=s(arguments[l++]),h=p?o(f).concat(p(f)):o(f),m=h.length,v=0;while(m>v)u.call(f,d=h[v++])&&(n[d]=f[d])}return n}:r},"9aa9":function(t,e){e.f=Object.getOwnPropertySymbols},a3c3:function(t,e,n){var o=n("63b6");o(o.S+o.F,"Object",{assign:n("9306")})},a4bb:function(t,e,n){t.exports=n("8aae")},a787:function(t,e,n){"use strict";var o=n("e9ed"),a=n.n(o);a.a},ce7e:function(t,e,n){var o=n("63b6"),a=n("584a"),i=n("294c");t.exports=function(t,e){var n=(a.Object||{})[t]||Object[t],c={};c[t]=e(n),o(o.S+o.F*i(function(){n(1)}),"Object",c)}},cef4:function(t,e,n){"use strict";n.r(e);var o=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("section",[n("van-nav-bar",{attrs:{title:t.$route.meta.title,"left-text":"返回","left-arrow":""},on:{"click-left":t.goback}},[n("van-icon",{attrs:{slot:"right",name:"add-o",size:"18px"},on:{click:function(e){t.popup=!0}},slot:"right"})],1),n("van-search",{attrs:{placeholder:"请输入搜索关键词"},model:{value:t.keyword,callback:function(e){t.keyword=e},expression:"keyword"}}),n("van-list",{attrs:{finished:t.finished,"finished-text":"没有更多了"},on:{load:function(t){}},model:{value:t.loading,callback:function(e){t.loading=e},expression:"loading"}},t._l(t.keyMap,function(e){return n("van-swipe-cell",{attrs:{"right-width":150}},[n("van-cell-group",[n("van-cell",{attrs:{title:e.key,value:t.getContentTypeDesc(e.type)},on:{click:function(n){return t.showContent(e.key)}}})],1),n("span",{attrs:{slot:"right"},slot:"right"},[n("van-button",{staticStyle:{float:"left",width:"75px"},attrs:{type:"info",square:""},on:{click:function(n){return t.updPopup(e.key)}}},[t._v("\r\n                    修改\r\n                ")]),n("van-button",{staticStyle:{float:"right",width:"75px"},attrs:{type:"danger",square:""},on:{click:function(n){return t.delDialog(e.key)}}},[t._v("\r\n                    删除\r\n                ")])],1)],1)}),1),n("van-popup",{staticStyle:{width:"100%",height:"100%"},attrs:{position:"right",overlay:!1},model:{value:t.popup,callback:function(e){t.popup=e},expression:"popup"}},[n("van-nav-bar",{attrs:{title:t.popupItem.barTitle,"left-text":"返回","left-arrow":""},on:{"click-left":t.closePopup}}),n("van-field",{attrs:{placeholder:"请输入关键字...",clearable:"",disabled:t.popupItem.txtDisabled},model:{value:t.popupItem.keyword,callback:function(e){t.$set(t.popupItem,"keyword",e)},expression:"popupItem.keyword"}}),n("van-radio-group",{model:{value:t.radio,callback:function(e){t.radio=e},expression:"radio"}},[n("van-cell-group",[n("van-cell",{attrs:{title:"文本",clickable:""},on:{click:function(e){t.radio="1"}}},[n("van-radio",{attrs:{name:"1"}})],1),n("van-cell",{attrs:{title:"文件",clickable:""},on:{click:function(e){t.radio="2"}}},[n("van-radio",{attrs:{name:"2"}})],1)],1)],1),n("div",{directives:[{name:"show",rawName:"v-show",value:"1"==t.radio,expression:"radio=='1'"}],staticStyle:{"margin-bottom":"10px","max-height":"600px",overflow:"hidden"}},[n("van-field",{attrs:{type:"textarea",autosize:{maxHeight:200,minHeight:150},clearable:"",placeholder:"请输入内容..."},model:{value:t.popupItem.content,callback:function(e){t.$set(t.popupItem,"content",e)},expression:"popupItem.content"}})],1),n("div",{directives:[{name:"show",rawName:"v-show",value:"2"==t.radio,expression:"radio=='2'"}],staticClass:"uploader",attrs:{onclick:"document.getElementById('hiddenFile').click();"}},[n("div",{staticClass:"ivu-icon"},[n("van-icon",{attrs:{name:"upgrade"}}),n("input",{ref:"uploader",staticStyle:{display:"none"},attrs:{multiple:"multiple",type:"file",id:"hiddenFile"},on:{change:t.onRead}})],1),n("p",{staticStyle:{color:"rgb(51, 153, 255)"}},[t._v("点击上传文件")])]),t.popupItem.imageVal&&"2"==t.radio?n("div",{on:{click:t.showImg}},[n("van-icon",{staticClass:"img_icon",attrs:{name:"photo-o",size:"35px"}}),n("span",{staticClass:"custom-text",staticStyle:{display:"inline-block","padding-left":"5px"}},[t._v("\r\n                点击查看图片详情\r\n            ")])],1):"2"==t.radio?n("span",{staticClass:"custom-text file_txt"},[t._v("\r\n            "+t._s(t.popupItem.contentFileName)+"\r\n        ")]):t._e(),n("van-button",{attrs:{size:"large",type:"info"},on:{click:t.addKeyword}},[t._v("确定提交")])],1),n("van-dialog",{attrs:{"show-cancel-button":"",beforeClose:t.beforeClose},model:{value:t.isShow,callback:function(e){t.isShow=e},expression:"isShow"}},[n("div",{staticClass:"van-dialog__content"},[n("div",{staticClass:"van-dialog__message"},[t._v("确定删除吗？")])])]),n("footerNav")],1)},a=[],i=(n("386d"),n("7f7f"),n("5176")),c=n.n(i),s=n("a4bb"),r=n.n(s),l=n("dc5d"),p=n("5b59"),u=n("b970"),d=n("c276"),f={components:{topNav:l["a"],footerNav:p["a"],SwipeCell:u["d"],Popup:u["c"],ImagePreview:u["b"],Dialog:u["a"]},data:function(){return{seq:"",keyword:"",chatRoomKeyMap:{},loading:!1,finished:!0,popup:!1,popupItem:{keyword:"",barTitle:"新增",content:null,contentFile:{},contentFileName:"",imageVal:null,txtDisabled:!1},key:"",radio:"1",isShow:!1,token:""}},computed:{keyMap:function(){var t=this;return Object(d["a"])(r()(this.chatRoomKeyMap).map(function(e){var n=t.chatRoomKeyMap[e];return{key:e,type:n.type,content:n.content}}),{key:this.keyword,content:this.keyword})}},mounted:function(){this.token=Object(d["c"])(window.location.href).token_m,this.seq=Object(d["c"])(window.location.href).seq,this.loadKeyword()},methods:{goback:function(){this.$router.go(-1)},getContentTypeDesc:function(t){return Object(d["b"])(t)},closePopup:function(){c()(this.$data.popupItem,this.$options.data().popupItem),this.popup=!1},showContent:function(t){var e=this.chatRoomKeyMap[t],n="关键字："+t+"\n回复类型："+this.getContentTypeDesc(e.type)+"\n回复内容："+e.content;Object(u["e"])(n)},loadKeyword:function(){var t=this,e={seq:this.seq,token:this.token};this.$http.post("keyword/getKeywords?format=json",e).then(function(e){var n=e.data;console.log(n),n.result?u["e"].fail(n.msg):r()(n.data).length>0&&(t.chatRoomKeyMap=n.data.info[t.seq])}).catch(function(t){console.log(t)})},delDialog:function(t){this.key=t,this.isShow=!0},updPopup:function(t){var e=this.chatRoomKeyMap[t];this.popupItem.barTitle="修改",this.popupItem.txtDisabled=!0,this.popupItem.keyword=t,this.popupItem.content=e.content,1!=e.type&&(this.radio="2"),2==e.type&&(this.popupItem.imageVal=e.content),"2"==this.radio&&(this.popupItem.contentFileName=e.content),this.popup=!0},oprSwipe:function(t,e){"right"==t||e.close()},beforeClose:function(t,e){var n=this;if("cancel"==t&&e(),"confirm"==t){var o={seq:this.seq,keyList:[this.key],token:this.token};this.$http.post("keyword/del?format=json",o).then(function(t){var o=t.data;e(),n.$delete(n.chatRoomKeyMap,n.key),setTimeout(function(){Object(u["e"])(o.msg)},500)}).catch(function(t){console.log(t)})}},addKeyword:function(){var t=this;if(""!=this.popupItem.keyword.trim()){var e=new FormData;e.append("token",this.token),e.append("seq",this.seq),e.append("key",this.popupItem.keyword),e.append("content","1"==this.radio?this.popupItem.content:this.popupItem.contentFile),this.$http.post("keyword/set?format=json",e).then(function(e){var n=e.data;n.result?u["e"].fail(n.msg):(t.$set(t.chatRoomKeyMap,t.popupItem.keyword,n.data.info),u["e"].success(n.msg),t.$nextTick(function(){t.closePopup()}))}).catch(function(t){console.log(t)})}else Object(u["e"])("请输入关键字")},showImg:function(){Object(u["b"])([""+this.popupItem.imageVal])},onRead:function(t){var e=this.$refs.uploader,n=e.files[0];if(void 0!=n){this.popupItem.imageVal=null;var o=Math.floor(n.size/1024);if(o>81920)return Object(u["e"])("请选择80M以内的文件！"),!1;if(this.popupItem.contentFile=n,this.popupItem.content=n.name,this.popupItem.contentFileName=n.name,-1!=n.type.search("image/")){var a=this;if(!t||!window.FileReader)return;var i=new FileReader;i.readAsDataURL(n),i.onloadend=function(){a.popupItem.imageVal=this.result}}}}}},h=f,m=(n("a787"),n("2877")),v=Object(m["a"])(h,o,a,!1,null,null,null);e["default"]=v.exports},dc5d:function(t,e,n){"use strict";var o=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("van-nav-bar",{attrs:{title:t.$route.meta.title,"left-text":"返回","left-arrow":""},on:{"click-left":t.goback}})},a=[],i={name:"topNav",data:function(){return{}},methods:{goback:function(){this.$router.go(-1)}}},c=i,s=n("2877"),r=Object(s["a"])(c,o,a,!1,null,"2fe8c154",null);e["a"]=r.exports},e9ed:function(t,e,n){}}]);
//# sourceMappingURL=chunk-644ad424.e4ec7792.js.map