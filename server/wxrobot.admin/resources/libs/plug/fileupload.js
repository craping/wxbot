/**
 * 
 */
$(function(){
	$("#iconFile").change(function(){
		$("#icon").attr({"src":Web.Method.getObjectURL($("#iconFile")[0])});
	});
	
	$("#submit").click(function(){
		var imgFile = $("#icon").attr("src");
		var image = new Image();
		image.src = imgFile;
		var width = image.width;
		var height = image.height;
		var filesize = image.filesize;
		alert("width: " + width + "    height: " + height);
		if(width == 100 && height == 100){
			alert("OK!");
		}else{
			alert("图片尺寸必须为100*100!");
		}
	});
});