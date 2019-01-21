$(function(){
	$.fn.appendArea=function(html){
		$(this).empty();
		$(this).append(html);
		$(this).parent().prev().html("请选择");
		$(this).parent().prev().attr("value","");
	}
	$(".onearea").appendArea(getarea({parentId:0},true,"oneAreaop"));
	
	$(document).on("click",".oneAreaop",function(){
		var parent = $(this).attr("value");
		$(".twoarea").appendArea(getarea({parentId:parent},true,"twoAreaop"))
		$("#locusArea").html("请选择");
		$("#locusArea").attr("value","");
		
	})
	$(document).on("click",".twoAreaop",function(){
		var parent = $(this).attr("value");
		var v= getarea({parentId:parent},true,"threeArea");
		if(v == ''){
			$("#ququ").hide();
		}else{
			$("#ququ").show();
		}
		$(".threearea").appendArea(v)
		
	})
	$(".one_area").appendArea(getarea({parentId:0},true,"oneArea_op"));
	
	$(document).on("click",".oneArea_op",function(){
		var parent = $(this).attr("value");
		$(".two_area").appendArea(getarea({parentId:parent},true,"twoArea_op"))
	})
	$(document).on("click",".twoArea_op",function(){
		var parent = $(this).attr("value");
		var v= getarea({parentId:parent},true,"three_Area");
		if(v == ''){
			$("#selArea").hide();
		}else{
			$("#selArea").show();
		}
		$(".three_area").appendArea(v);
	})
})
function getarea(area,bool,classname){
	var html='';
	Web.Method.ajax("pubArea/getListArea",{
		async:false,
		data:area,
		success:function(data){
			obj = data.info;
			for (var i = 0; i < obj.length; i++) {
				
				html+='<li value="'+obj[i].areaNo+'" class="'+classname+'"><a href="javascript:;">'+obj[i].areaName+'</a></li>';
			}
		}
	});
	if(bool){
		return html;
	}else if(!bool){
		return obj
	}
}