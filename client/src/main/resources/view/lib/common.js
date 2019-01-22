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
        else if (Notification.permission !== 'denied') {
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