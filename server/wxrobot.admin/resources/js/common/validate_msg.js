$(document).ready( function(){
	
	// 验证消息
	if($.validator != null) {
		$.extend($.validator.messages, {
		    required: "必填",
			email: "E-mail格式错误",
			url: "网址格式错误",
			date: "日期格式错误",
			dateISO: "日期格式错误",
			pointcard: "信用卡格式错误",
			number: "输入数字",
			digits: "能入输零或正整数",
			minlength: $.validator.format("长度不能小于{0}"),
			maxlength: $.validator.format("长度不能大于{0}"),
			rangelength: $.validator.format("长度必须在{0}-{1}之间"),
			min: $.validator.format("不能小于{0}"),
			max: $.validator.format("不能大于{0}"),
			range: $.validator.format("必须在{0}-{1}之间"),
			accept: "输入后缀错误",
			equalTo: "两次输入不一致",
			remote: "输入错误",
			integer: "只能输整数",
			positive: "只能输正数",
			negative: "只能输负数",
			decimal: "数值超出范围",
			pattern: "格式错误",
			extension: "文件格式错误"
		});
		

		$.validator.setDefaults({
			errorClass: "system-advert-hint",
			ignore: ".ignore",
			ignoreTitle: true,
			errorPlacement: function(error, element) {
				var fieldSet = element.closest("div.fieldSet");
				if (fieldSet.size() > 0) {
					error.appendTo(fieldSet);
				} else {
					error.insertAfter(element);
				}
			},
			onfocusout: function(element) { $(element).valid(); },
		});

		
		$.validator.addMethod("isPhone", function(value, element) {   

	        var tel = /((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)/;

	        return this.optional(element) || (tel.test(value));

	    }, "请正确填写您的联系方式");
		
		$.validator.addMethod("isBankCard", function(value, element) {   

	        var tel = /^(\d{16}|\d{19})$/;

	        return this.optional(element) || (tel.test(value));

	    }, "请正确填写您的银行卡号");
		
		// 验证值小数位数不能超过两位
		$.validator.addMethod("decimal", function(value, element) {
			var decimal = /^-?\d+(\.\d{1,2})?$/;
			return this.optional(element) || (decimal.test(value));
		}, $.validator.format("两位小数!"));
	    
	    
	    $.validator.addMethod("noZore", function(value, element) {
			var decimal = /^[1-9]\d*\.?\d*/;
			return this.optional(element) || (decimal.test(value));
		}, $.validator.format("首位非0!"));

		
	}
	
});