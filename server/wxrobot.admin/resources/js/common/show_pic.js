$(function(){
	var params =get("picUrl");
	var paramArray=params.split(',');
	if(paramArray.length <= 1 ){
		$(".prev,.next").hide();
	}
	var pic1='<li><img style="width: 502px; height:502px" src="';
	var pic2='"></li>';
	var pic='';
	for(var i=0;i<paramArray.length;++i){
		pic+=pic1+paramArray[i]+"?"+new Date().getTime()+pic2;
	}
	$("#pic_image").append(pic);

	$('img').each(function() {
		var maxWidth = 502; // 图片最大宽度
		var maxHeight = 502; // 图片最大高度
		var ratio = 0; // 缩放比例
		var width = $(this).width(); // 图片实际宽度
		var height = $(this).height(); // 图片实际高度 // 检查图片是否超宽
		if (width > maxWidth) {
			ratio = maxWidth / width; // 计算缩放比例
			$(this).css("width", maxWidth); // 设定实际显示宽度
			height = height * ratio; // 计算等比例缩放后的高度
			$(this).css("height", height); // 设定等比例缩放后的高度
		} // 检查图片是否超高
		if (height > maxHeight) {
			ratio = maxHeight / height; // 计算缩放比例
			$(this).css("height", maxHeight); // 设定实际显示高度
			width = width * ratio; // 计算等比例缩放后的高度
			$(this).css("width", width); // 设定等比例缩放后的高度
		}
	});
})

function get(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) return unescape(r[2]);
	return null;
}