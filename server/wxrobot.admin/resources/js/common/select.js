$(function(){
    $(document).on("click","dt.selectValve",function(){
		if($(this).is(".cur")){
			$(this).removeClass("cur");
			$(this).next("dd").hide();
		}else{
			$(this).addClass("cur");
			$("dt.selectValve").next("dd").hide();
			$(this).next("dd").show();
		}
	})
	$(document).on("click","dd[class='selectOption'] ul li",function(){
		$(this).parent().parent().hide();
		$(this).parent().parent().prev().removeClass("cur");
		$(this).parent().parent().prev().html($(this).children("a").html())
		$(this).parent().parent().prev().attr("value",$(this).attr("value"));
	})
	
	$(document).click(function() { 
		 
		/*$("dt.selectValve").removeClass("cur");
		$("dt.selectValve").next("dd").hide();*/
	});
    
	$('.system-table th').css('background-color','#898989');//隔行换色
    $('.system-table tr:even').css('background-color','#eeeeee');
	$.fn.serializeJson=function(includeEmpty){  
	    var json={};
	    $(this.serializeArray()).each(function(){
	    	if(includeEmpty || (this.value && this.value != ""))
	    		json[this.name]=this.value;
	    });
	    return json;  
	};
	$(document).on("change",".allmiddle",function(){
		$(".middle").prop("checked",$(this).prop("checked"))
		if($(this).prop("checked")){
			$("#actionOk,#caozuo").css("background","#AA3231") ;
			$("#actionOk,#caozuo").removeAttr("disabled");
		}else{
			$("#actionOk,#caozuo").css("background","#898989");
			$("#actionOk,#caozuo").attr("disabled","disabled");
		}
	})

	$(document).on("change",".middle",function(){
		var select = $(".middle").length;
		var Allselect = $(".middle:checked").length;
		
		if(select == Allselect ){
			$(".allmiddle").prop("checked",true);
		}else{
			$(".allmiddle").prop("checked",false);
		}
		if(Allselect >=1 ){
			$("#actionOk,#caozuo").removeAttr("disabled");
			$("#actionOk,#caozuo").css("background","#AA3231") ;
		}else{
			$("#actionOk,#caozuo").attr("disabled","disabled");
			$("#actionOk,#caozuo").css("background","#898989");
		}
		
	})
	
	$.fn.valibtValue= function(){
		var val = $(this).attr("value");
		if(val == undefined){
			val = $(this).val();
			if(val == undefined){
				return false;
			}
		}
		if(val != null && val !='' && val !="" && val.length > 0){
			$("[for='"+$(this).attr("id")+"']").hide();
			return true
		}else{
			window.location.href = "#"+$(this).attr("id")+"Div"; // focus 当前div
			$("[for='"+$(this).attr("id")+"']").show();
			return false
		}
	}
})
function isSeq(params) {
	var i = false;
	Web.Method.ajax("/classify/checkSeq", {
		async:false,
		data : params,
		success : function(data) {
			if(data.info == "0"){
				i=true;
			};
		}
	});
	
	if(!i){
		$("[for='seql']").html("已存在的序号");
		$("[for='seql']").show()
	}else{
		$("[for='seql']").hide()
	}
	return i;
}


function showLayer(){
	layer.msg('提交中....',{icon:16,time:false});
	$("#hiddensd").show();
}
function hideLayer(i){
	$("[type='dialog']").hide();
	layer.closeAll('loading');
	$("#hiddensd").hide();
	setTimeout(function(){
	    layer.closeAll('loading');
	}, 1000);
}
function StringN(string,number){
	if(string.length > parseInt(number)){
		return string.substring(0,parseInt(number))+"......";
	}else{
		return string;
	}
}
var dr = {
	status:["有效","无效"],
	infoType:["行业资讯","企业资讯"],
	reade:["计算机硬件","互联网","计算机软件"]
}




