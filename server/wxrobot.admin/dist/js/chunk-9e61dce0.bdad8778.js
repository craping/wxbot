(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-9e61dce0"],{"2f21":function(t,e,n){"use strict";var s=n("79e5");t.exports=function(t,e){return!!t&&s(function(){e?t.call(null,function(){},1):t.call(null)})}},"55dd":function(t,e,n){"use strict";var s=n("5ca1"),a=n("d8e8"),o=n("4bf8"),i=n("79e5"),c=[].sort,l=[1,2,3];s(s.P+s.F*(i(function(){l.sort(void 0)})||!i(function(){l.sort(null)})||!n("2f21")(c)),"Array",{sort:function(t){return void 0===t?c.call(o(this)):c.call(o(this),a(t))}})},"64b2":function(t,e,n){"use strict";var s=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("section",{staticClass:"admin-layout-container"},[n("div",{staticClass:"layout"},[n("Layout",[n("Sider",{ref:"sidebar",staticClass:"sidebar",attrs:{"hide-trigger":"",collapsible:"",width:"230","collapsed-width":78},model:{value:t.isCollapsed,callback:function(e){t.isCollapsed="string"===typeof e?e.trim():e},expression:"isCollapsed"}},[n("div",{staticClass:"logo"},[t.isCollapsed?t._e():n("div",{staticClass:"xfind-line"},[n("div",{staticClass:"line-h"})]),t.isCollapsed?n("Avatar",{attrs:{icon:"ios-person",size:"large"}}):n("div",{staticClass:"logo-saiqu"},[n("Avatar",{attrs:{icon:"ios-person",size:"large"}}),n("span",{staticClass:"user-name"},[t._v("admin")])],1)],1),t.isCollapsed?t._e():n("Menu",{ref:"side_menu",class:t.menuitemClasses,attrs:{"active-name":t.activeMenuName,"open-names":t.openMenuName,theme:"light",width:"230px"},on:{"on-select":t.choosedMenu}},[t._l(t.menus,function(e,s){return[e.children?n("Submenu",{key:s,attrs:{name:e.name}},[n("template",{slot:"title"},[n("Icon",{attrs:{size:20,type:e.icon}}),n("span",[t._v(t._s(e.title))])],1),t._l(e.children,function(e,s){return n("MenuItem",{key:s,attrs:{name:e.name}},[n("Icon",{attrs:{size:20,type:e.icon}}),n("span",[t._v(t._s(e.title))])],1)})],2):t._e(),!e.children&&e.showInMenus?n("MenuItem",{key:s,attrs:{name:e.name}},[n("Icon",{key:s,attrs:{size:20,type:e.icon}}),n("span",[t._v(t._s(e.title))])],1):t._e()]})],2),t.isCollapsed?n("div",{staticClass:"dropdown-wrap"},[n("div",{staticClass:"dw-content"},[t._l(t.menus,function(e,s){return[e.children?n("Dropdown",{key:s,attrs:{transfer:"",placement:"right-start"},on:{"on-click":t.dropdownClick}},[n("Button",{staticClass:"dd-btn",attrs:{type:"text"}},[n("Icon",{attrs:{size:25,type:e.icon}})],1),n("DropdownMenu",{staticClass:"dd-menu",attrs:{slot:"list"},slot:"list"},[t._l(e.children,function(e,s){return[n("DropdownItem",{key:s,attrs:{name:e.name}},[n("div",{staticClass:"ddi-wapper"},[n("Icon",{attrs:{size:16,type:e.icon}}),n("span",{staticClass:"ddi-text"},[t._v("\n                                                    "+t._s(e.title)+"\n                                                ")])],1)])]})],2)],1):t._e(),!e.children&&e.showInMenus?n("Dropdown",{key:s,attrs:{transfer:"",placement:"right-start"},on:{"on-click":t.dropdownClick}},[n("Button",{staticClass:"dd-btn",attrs:{type:"text"}},[n("Icon",{attrs:{size:25,type:e.icon}})],1),n("DropdownMenu",{staticClass:"dd-menu",attrs:{slot:"list"},slot:"list"},[n("DropdownItem",{attrs:{name:e.name}},[n("div",{staticClass:"ddi-wapper"},[n("Icon",{attrs:{size:16,type:e.icon}}),n("span",{staticClass:"ddi-text"},[t._v("\n                                                "+t._s(e.title)+"\n                                            ")])],1)])],1)],1):t._e()]})],2)]):t._e()],1),n("Layout",[n("Header",{staticClass:"layout-header-bar"},[n("div",{staticClass:"header-wapper"},[n("div",{staticClass:"header-left"},[n("Icon",{class:t.rotateIcon,attrs:{type:"md-menu",size:"28"},nativeOn:{click:function(e){return t.collapsedSider(e)}}}),n("span",{staticClass:"header-title"},[t._v("微信机器人后台管理系统")])],1),n("div",{staticClass:"header-right"},[n("Button",{staticClass:"btn-blue",attrs:{type:"text",icon:"md-exit",size:"large"},on:{click:t.quit}},[t._v("退出系统")])],1)])]),n("Content",{staticClass:"main-content"},[n("Layout",{staticClass:"main-layout-con"},[n("div",{staticClass:"tags-nav-wapper"},[n("div",{staticClass:"tags-nav"},[n("div",{staticClass:"btn-con left-btn"},[n("Button",{attrs:{type:"text"},on:{click:function(e){t.handleScroll(240)}}},[n("Icon",{attrs:{size:18,type:"ios-arrow-back"}})],1)],1),n("div",{ref:"scrollOuter",staticClass:"scroll-outer",on:{DOMMouseScroll:t.handlescroll,mousewheel:t.handlescroll}},[n("div",{ref:"scrollBody",staticClass:"scroll-body",style:{left:t.tagBodyLeft+"px"}},[n("transition-group",{attrs:{name:"taglist-moving-animation"}},t._l(t.tags,function(e){return n("Tag",{key:e.name,attrs:{type:"dot",closable:e.closable,color:e.choosed?"primary":"#e9eaec",name:e.name},on:{"on-close":t.closeTag},nativeOn:{click:function(n){t.clickTag(e)}}},[t._v("\n                                                    "+t._s(e.title)+"\n                                                ")])}),1)],1)]),n("div",{staticClass:"btn-con right-btn"},[n("Button",{attrs:{type:"text"},on:{click:function(e){t.handleScroll(-240)}}},[n("Icon",{attrs:{size:18,type:"ios-arrow-forward"}})],1)],1)])]),n("Content",{staticClass:"content-wrapper"},[n("keep-alive",[n("router-view")],1)],1)],1)],1)],1)],1)],1)])},a=[],o=(n("7f7f"),n("55dd"),n("ac6a"),n("c276")),i=null,c={data:function(){return{isCollapsed:!1,title:"首页",activeMenuName:"admin",openMenuName:[],tagBodyLeft:0,rightOffset:40,outerPadding:4,contextMenuLeft:0,contextMenuTop:0,visible:!1,menus:[{title:"首页",num:1,name:"admin",icon:"ios-home",href:"/home",closable:!1,showInTags:!0,showInMenus:!0,choosed:!1},{title:"会员管理",name:"user-management",icon:"ios-people",children:[{title:"会员列表",name:"user-list",href:"/user-management/user-list",closable:!0,showInTags:!1,showInMenus:!0,choosed:!1},{title:"新增会员",name:"user-add",href:"/user-management/user-add",closable:!0,showInTags:!1,showInMenus:!0,choosed:!1}]},{title:"公告管理",name:"notice-management",icon:"md-volume-mute",children:[{title:"公告列表",name:"notice-list",href:"/notice-management/notice-list",closable:!0,showInTags:!1,showInMenus:!0,choosed:!1},{title:"发布公告",name:"notice-add",href:"/notice-management/notice-add",closable:!0,showInTags:!1,showInMenus:!0,choosed:!1}]},{title:"系统管理",name:"system-manage",icon:"ios-cog",children:[{title:"系统设置",name:"backwater-setting",href:"/home",closable:!0,showInTags:!1,showInMenus:!0,choosed:!1}]}]}},computed:{tags:function(){var t=[];return this.menus.forEach(function(e){e.showInTags?t.push(e):e.children&&e.children.forEach(function(e){e.showInTags&&t.push(e)})}),t.sort(function(t,e){return t.num-e.num}),t},rotateIcon:function(){return["menu-icon",this.isCollapsed?"rotate-icon":""]},menuitemClasses:function(){return["menu-item",this.isCollapsed?"collapsed-menu":""]}},beforeRouteEnter:function(t,e,n){n(function(t){var e=localStorage.activeMenuName;t.activeMenuName=e;var n=t.tags[t.tags.length-1].num;e&&0!=e.length&&t.menus.forEach(function(s){e==s.name?(s.choosed=!0,s.showInTags=!0,s.num=n+1):s.children?s.children.forEach(function(a){e==a.name&&(a.choosed=!0,a.showInTags=!0,a.num=n+1,t.openMenuName=[s.name])}):"admin"!=s.name?(s.choosed=!1,s.showInTags=!1):s.choosed=!1}),t.$nextTick(function(){})})},mounted:function(){},created:function(){i=this},methods:{handlescroll:function(t){var e=t.type,n=0;"DOMMouseScroll"!==e&&"mousewheel"!==e||(n=t.wheelDelta?t.wheelDelta:40*-(t.detail||0)),this.handleScroll(n)},handleScroll:function(t){var e=this.$refs.scrollOuter.offsetWidth,n=this.$refs.scrollBody.offsetWidth;t>0?this.tagBodyLeft=Math.min(0,this.tagBodyLeft+t):e<n?this.tagBodyLeft<-(n-e)?this.tagBodyLeft=this.tagBodyLeft:this.tagBodyLeft=Math.max(this.tagBodyLeft+t,e-n):this.tagBodyLeft=0},quit:function(){var t=this;this.$http.post("/admin/logout?format=json",{}).then(function(e){var n=e.data;n.result||(Object(o["a"])(),t.$router.push("/login"))}).catch(function(t){console.log(t)})},clickNotice:function(){this.choosedMenu("notice")},collapsedSider:function(){this.$refs.sidebar.toggleCollapse()},closeTag:function(t,e){var n=!1;if(this.menus.forEach(function(t){t.name==e?(n=t.choosed,t.showInTags=!1):t.children&&t.children.forEach(function(t){t.name==e&&(n=t.choosed,t.showInTags=!1)})}),n){var s=this.tags[this.tags.length-1];s.choosed=!0,this.$router.push(s.href),this.activeMenuName=s.name,localStorage.activeMenuName=this.activeMenuName}},clickTag:function(t){var e=this;this.tags.forEach(function(e){e.name==t.name?e.choosed=!0:e.choosed=!1}),this.activeMenuName=t.name,localStorage.activeMenuName=this.activeMenuName,this.$nextTick(function(){e.$refs.side_menu&&e.$refs.side_menu.updateActiveName()}),this.$router.push("".concat(t.href))},choosedMenu:function(t){var e=this.tags[this.tags.length-1].num;this.activeMenuName=t,localStorage.activeMenuName=this.activeMenuName;var n=null;this.menus.forEach(function(s){s.name==t?(s.showInTags||(s.num=e+1),n=s,s.showInTags=!0,s.choosed=!0):s.children?s.children.forEach(function(a){a.name==t?(s.showInTags||(a.num=e+1),n=a,a.showInTags=!0,a.choosed=!0):a.choosed=!1}):s.choosed=!1}),this.$router.push("".concat(n.href))},dropdownClick:function(t){this.choosedMenu(t)}}},l=function(t){i.closeTag(event,t)},r=function(t){i.choosedMenu(t)},u=c,d=(n("df86"),n("2877"));n.d(e,"b",function(){return l}),n.d(e,"a",function(){return r});var h=Object(d["a"])(u,s,a,!1,null,"fce7b114",null);h.options.__file="layout.vue";e["c"]=h.exports},"7f7f":function(t,e,n){var s=n("86cc").f,a=Function.prototype,o=/^\s*function ([^ (]*)/,i="name";i in a||n("9e1e")&&s(a,i,{configurable:!0,get:function(){try{return(""+this).match(o)[1]}catch(t){return""}}})},df86:function(t,e,n){"use strict";var s=n("f0d7"),a=n.n(s);a.a},f0d7:function(t,e,n){}}]);
//# sourceMappingURL=chunk-9e61dce0.bdad8778.js.map