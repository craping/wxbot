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
var wxbot = {
	getToken(){
		return "6dfb108f262845a1bfec3ef6647c28f7";
	},
	getHostUrl(){
		return "";
	},
	getEmoji(){
		return "";
	},
	getIndividuals(){
		return [];
	},
	getChatRooms(){
		return [];
	}
}
var Web = {
	serverURL: "http://127.0.0.1:9527/",
	// serverURL: "http://118.89.37.101:9527/",
	/* Common - Ajax request */
	ajax: function (method, param) {
		var cipher = Crypto.generateCipher();
		if (!param)
			param = {};
		var defaultParam = {
			param: {},
			type: "post",
			timeout: 20000,
			safe: false,
			data: {
				token: wxbot.getToken(),
			},
			url: Web.serverURL,
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
			url: defaultParam.url + method + "?format=json",
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