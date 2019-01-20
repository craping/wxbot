$(function(){
	$(document).on("click","dd[class='selectOption'] ul li",function(){
		$(this).parent().parent().hide();
		$(this).parent().parent().prev().removeClass("cur");
		$(this).parent().parent().prev().html($(this).children("a").html())
		$(this).parent().parent().prev().attr("value",$(this).attr("value"));
	})
	
	// 保存商品
	$("#ok_btn").click(function(){
		var user_name = $("#user_name").val();
		if(isNullOrEmpty(user_name)){
			alert("请填写用户名");
			$("#user_name").focus();
			return false;
		}
	
		var user_pwd = $("#user_pwd").val();
		if(isNullOrEmpty(user_pwd)){
			alert("请填写密码");
			$("#user_pwd").focus();
			return false;
		}
	
		var server_end = $("#server_end").val();
		if(isNullOrEmpty(server_end)){
			alert("请输入服务时间");
			$("#server_end").focus();
			return false;
		}

		showLayer();
		Web.Method.ajax("admin_user/addUser", {
			data: {
				user_name: user_name,
				user_pwd: user_pwd,
				server_end: server_end
			},
			success: function(data){
				hideLayer();
				console.log(data);
				if(data.result==0){
					alert("添加成功");
					window.location.href="user_list.html";
				}
			},
			error: function(data){
				hideLayer();
				alert("添加失败！");
			}
		});
	});

	// 取消
	$("#cancel_btn").click(function(){
		window.location.href="user_list.html";
	});
});

function showLayer(){
	layer.msg('提交中....',{icon:16,time:false});
	$("#hiddensd").show();
}
function hideLayer(){
	$("#layui-layer1").hide();
	layer.closeAll('loading');
	$("#hiddensd").hide();
	setTimeout(function(){
	    layer.closeAll('loading');
	}, 1000);
}
