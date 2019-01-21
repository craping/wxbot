/**

<input type="file" style="display:none" name="iconFile" id="iconFile" />
<input class="sbutton" type="button" value="本地上传" id="uploadImage"/>
激活上传图片按钮
$("#uploadImage").click(function(){
	$("#iconFile").click();
});

检查文件内容格式 大小
$('#iconFile').checkFileTypeAndSize({
	allowedExtensions: ['jpg','png'],
	maxSize: 200000,
	widthAndHeight：100*100
	extensionerror: function(){
		图片的格式只能为：jpg,png
	},
	sizeerror: function(){
		图片大小不能超过20kb
	},
	success: function(){
		Web.Method.getObjectURL($("#iconFile")[0])  获取图片
    }
});

传入参数序列化： url:Web.Recource.serverURL +"manUser/getUserList?"+$("#product_infoForm").serializeJson(true),
*/
(function ($) {
    $.fn.checkFileTypeAndSize = function (options,target) {
        var defaults = {
            allowedExtensions: [],
            maxSize: 20,
            widthAndHeight: 100*100,
            success: function(){},
            extensionerror: function(){},
            sizeerror: function(){},
            widthAndHeightError: function(){},
        };
        options = $.extend(defaults, options);
        return target.delegate("input[type='file']","change",function(){
            var filePath = $(this).val();
            var path = Web.Method.getObjectURL(this);
            var fileLowerPath = filePath.toLowerCase();
            var extension = fileLowerPath.substring(fileLowerPath.lastIndexOf('.') + 1);

            if ($.inArray(extension, options.allowedExtensions) == -1) {
                options.extensionerror($(this).val(""),$(this));
                $(this).focus();
            } else {
            	 
                try {
                	var size = 0;
                	size = $(this)[0].files[0].size;
                	size = size / 1024;
                    if (size > options.maxSize) {
                        options.sizeerror($(this).val(""),$(this));
                    } else {
                        options.success(this, path,$(this));
                    }
                } catch (e) {
                    alert("错误：" + e);
                }
            }
        });
    };
})(jQuery);