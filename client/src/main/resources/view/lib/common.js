var Store = Store||{
	get:function(key){
		return JSON.parse(localStorage.getItem(key));
	},
	
	set:function(key, value){
		localStorage.setItem(key, JSON.stringify(value));
	},
	remove:function(key){
		localStorage.removeItem(key);
	}
}


/** ----------------浏览器模式开发时使用-------------*/
// var wxbot = {
// 	showLogin(){

// 	},
// 	getUserInfo(){
// 		return {
// 			userInfo:{
// 				serverState:true
// 			},
// 			token:"b816d932a19f48f0ae2e273d0906a82d"
// 		}
// 	},
// 	getDomain(){
// 		return "http://localhost"
// 	},
// 	getHostUrl(){},
// 	syncSetting(){},
// 	syncKeywords(){},
// 	syncTimers(){},
// 	syncTips(){},
// 	syncTuringKey(){},
// 	getIndividuals(){
// 		let list = [];
// 		for (let i = 0; i < 50; i++) {
// 			list.push({
// 				NickName:"名称"+i,
// 				HeadImgUrl:"",
// 				UserName:"username"+i,
// 				seq:"seq"+i
// 			});
// 		}
// 		return list;
// 	},
// 	getChatRooms(callback){
// 		let list = [];
// 		for (let i = 0; i < 500; i++) {
// 			list.push({
// 				NickName:"名称<span class=\"emoji emoji1f495\"></span><span class=\"emoji emoji1f48d\"></span>🦋燕燕"+i,
// 				HeadImgUrl:"",
// 				UserName:"@@username"+i,
// 				seq:"seq"+i
// 			});
// 		}
// 		callback(list);
// 	},
// 	getChatRoomMembers(){return []},
// 	resetChatRecord(){},
// 	getKeyMap(){
// 		return {};
// 	},
// 	getMsgs(){
// 		return [];
// 	},
// 	getSetting(){
// 		return {
// 			"timers": [
// 				"664197556"
// 			],
// 			"keywords": [
// 				"664176954",
// 				"664176791",
// 				"664197556"
// 			],
// 			"forwards": [
// 				"664175594",
// 				"664176785",
// 				"664176954",
// 				"664176791",
// 				"664197556"
// 			],
// 			"turing": [ ],
// 			"permissions": {
// 				"zombieTest": true,
// 				"chat": null,
// 				"keyword": true,
// 				"globalKeyword": true,
// 				"timer": true,
// 				"globalTimer": true,
// 				"acceptFriend": null,
// 				"chatRoomFoundTip": true,
// 				"memberJoinTip": true,
// 				"memberLeftTip": true,
// 				"wapSite": true
// 			},
// 			"switchs": {
// 				"autoAcceptFriend": false,
// 				"globalKeyword": false,
// 				"globalTimer": false
// 			}
// 		};
// 	},
// 	getEmoji(){
// 		return "";
// 	},
// 	getOwnerHeadImgUrl(){
// 		return "";
// 	},
// 	getOwner(){
// 		return {};
// 	},
// 	getRootPath(){
// 		return "";
// 	}
// };
/** ----------------浏览器模式开发时使用-------------*/


var Web = {
	// serverURL: wxbot.getDomain(),
	// wxHost:wxbot.getHostUrl(),
	// root:wxbot.getRootPath(),
    // owner:wxbot.getOwner(),
	// user:wxbot.getUserInfo(),

	serverURL: wxbot.getDomain(),
	wxHost:"",
	root:wxbot.getRootPath(),
    owner:{},
	user:{
		userInfo:{}
	},
	/* Common - Ajax request */
	ajax: function (method, param, format) {
		var cipher = Crypto.generateCipher();
		if (!param)
			param = {};
		var defaultParam = {
			param: {},
			type: "post",
			timeout: 20000,
			safe: false,
			data: {
				token:  Web.user?Web.user.token:null,
			},
			url: Web.serverURL+"/",
			success: function () { },
			fail: function () { },
			error: function () { }
		};
		$.extend(true, defaultParam, param);

		if (defaultParam.safe) {
			defaultParam.data = {
				encrypt_data: cipher.encrypt(JSON.stringify(defaultParam.data)),
				encrypt_source: "js",
				encrypt_flag: Crypto.encryptFlag,
			};
		}

		$.ajax({
			type: defaultParam.type,
			data: defaultParam.type == "get" ? defaultParam.data : JSON.stringify(defaultParam.data),
			async: defaultParam.async,
			timeout: defaultParam.timeout,
			url: defaultParam.url + method + "?format="+(format?format:"json"),
			contentType: "application/json",
			processData: false,
			dataType: "text",
			success: function (data) {
				if (param.safe) {
					data = decodeURIComponent(cipher.decrypt(data).replace(/\+/g, '%20'));
				}
				data = JSON.parse(data);
				if (!data.result) {
					if (defaultParam.success)
						defaultParam.success(data.data ? data.data : data, defaultParam.param);
				} else {
					switch (data.errcode) {
						case 504:
						case 507:
						case 508:
							break;
						case 506:
							break;
						default:
							break;
					}
					if (defaultParam.fail)
						defaultParam.fail(data, defaultParam.param);
					if(data.result == -1)
						console.log("%c服务器异常："+data.data.info, "color:red");
				}
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
				var status = XMLHttpRequest.status;
				if (status == 403) {
				}
				if (defaultParam.error)
					defaultParam.error(XMLHttpRequest, textStatus, errorThrown, defaultParam.param);
			}
		});
	}
};

//桌面提醒
function notify(title, properties, timeout) {
        
    if(!title){
        title = "桌面提醒";
    }
    
    var iconUrl = "icon/icon.png";
    var p = {
		title:title,
		icon:"icon/icon.png",
		requireInteraction:false
	};
	Object.assign(p, properties);
    
    if("Notification" in window){
        // 判断是否有权限
        if (Notification.permission === "granted") {
			var notification = new Notification(title, p);
			if(timeout){
				setTimeout(() => {
					notification.close();
				}, timeout);
			}
            return notification;
        }
        //如果没权限，则请求权限
        else {
            Notification.requestPermission(function(permission) {
                // Whatever the user answers, we make sure we store the
                // information
                if (!('permission' in Notification)) {
                    Notification.permission = permission;
                }
                //如果接受请求
                if (permission === "granted") {
					var notification = new Notification(title, p);
					if(timeout){
						setTimeout(() => {
							notification.close();
						}, timeout);
					}
                    return notification;
                }
            });
        }
	}
}

String.prototype.replaceAll = function (FindText, RepText) {
    regExp = new RegExp(FindText, "g");
    return this.replace(regExp, RepText);
}
Date.prototype.format = function (format) {
	var date = {
		"M+": this.getMonth() + 1,
		"d+": this.getDate(),
		"h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12,
		"H+": this.getHours(),
		"m+": this.getMinutes(),
		"s+": this.getSeconds(),
		"q+": Math.floor((this.getMonth() + 3) / 3),
		"S+": this.getMilliseconds()
	};
	if (/(y+)/i.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
	}
	for (var k in date) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1
				? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
		}
	}
	return format;
};