$(function () {
	$(document).keydown(function (event) {
		if (event.keyCode == 13) {
			queryList(1);
			return false;
		}
	});

	$("#system-table").listview({
		style: "system-table",
		headerStyle: "noborder",
		module: [
			{ name: "编号", width: "5%" },
			{ name: "用户名", width: "25%" },
			{ name: "服务截止时间", width: "25%" },
			{ name: "服务状态", width: "10%" },
			{ name: "操作" },
		],
		eachItem: function (ui, item) {
			for (var i in item) {
				if (item[i] == null) {
					item[i] = "";
				}
			}

			var idx = parseInt(item.indexKey) + 1;
			var date = new Date();
			date.setTime(item.userInfo.serverEnd);
			date = date.format("yyyy/MM/dd HH:mm");
			var page = $("#system-page").data("lishe.pageSet").options.pageSet.page - 1;
			var html = "<tr>";
			//html+="<td width='3%'><input type='checkbox' accept='" + item.checkStatus + "' class='middle' value='"+item.id+"'></label></td>" ;
			html += "<td >" + idx + "</td>";
			html += "<td>" + item.userInfo.userName + "</td>";
			html += "<td>" + date + "</td>";
			if (item.userInfo.serverState) {
				html += "<td>有效</td>";
			} else {
				html += "<td>无效</td>";
			}

			html += "<td><div class='system-table-list'>";
			html += "<ul>";
			html += "<li value='" + item.id + "'><a target='_blank' href='http://www.wufk.net/product.html?id=" + item.id + "'  class='operate-icon23' target='iframe' title='详情'></a></li>";
			if (item.userInfo.serverState) {
				html += "<li><a target='iframe' href='javascript:;' onclick='oprStatus(&quot;" + item.id + "&quot;, false)' target='iframe' class='operate-icon26' title='置为无效'></a></li>";
			} else {
				html += "<li><a target='iframe' href='javascript:;' onclick='oprStatus(&quot;" + item.id + "&quot;, true)' target='iframe' class='operate-icon25' title='置为有效'></a></li>";
			}
			html += "<li><a target='iframe' href='javascript:;' target='iframe' class='operate-icon5' title='编辑服务时间' rel='"+item.id+"'></a></li>";
			html += "</ul>";
			html += "</div> </td>";
			html += "</tr>";
			var tr = $(html);
			return tr;
		}
	});

	//分页处理
	$("#system-page").pageset({
		itemClick: function (page) {
			queryList(page);
		}
	});
	$(document).on("click", "#ok", function () {
		queryList(1);
	})

	$(document).on("click", "#server_end", function () {
		WdatePicker({ minDate: '', dateFmt: 'yyyy-MM-dd HH:mm:ss' });
	})

	$(document).on("click", ".operate-icon5", function () {
		var id = $(this).attr('rel');
		var content = "<table class='product-publish-table1'>" +
			"<tr class='product-publish-tr'>" +
			"<td><span style='padding:0px 0px 5px 10px;'>选择时间：</span></td>" +
			"<td><input type='text' id='server_end' name='server_end' class='site-template-box block' onclick='' readonly='readonly' style='width: 164px;'></td>" +
			"</tr></table>";
		var dialog7 = jqueryAlert({
			'title': '服务截止时间',
			'content': content,
			'modal': true,
			'contentTextAlign': 'left',
			'width': '400px',
			'animateType': 'linear',
			'buttons': {
				'确定': function () {
					var server_end = $("#server_end").val();
					if(isNullOrEmpty(server_end)){
						alert("请输入服务时间");
						$("#server_end").focus();
						return false;
					}
					Web.Method.ajax("admin_user/extension", {
						data: { id: id, server_end: server_end },
						success: function (data) {
							dialog7.destroy();
							queryList(1);
						},
						fail: function (data) {
							$.confAlert({
								size: "sm",
								context: data.msg,
								noButton: false
							})
						}
					});
				},
				'取消': function () {
					dialog7.destroy();
				}
			}
		})
	})

	$(document).on("click", "#clear", function () {
		$("#productform input[type='text']").each(function () {
			$(this).val("");
		});
	})
	queryList(1);
})



function oprStatus(id, status) {
	Web.Method.ajax("admin_user/lock", {
		data: { id: id, server_state: status },
		success: function (data) {
			$.confAlert({
				size: "sm",
				context: "操作成功",
				noButton: false,
				onOk: function () {
					queryList(1);
				}
			})	
		},
		fail: function (data) {
			$.confAlert({
				size: "sm",
				context: data.msg,
				noButton: false
			})
		}
	});
}

function queryList(page, listview, pageset, params) {
	var options = {
		curPage: page,
		pageSize: 10
	};
	var datas = $.extend(true, {}, options, $("#userform").serializeJson());

	listview = listview ? listview : $("#system-table");
	listview.listview("loading");
	Web.Method.ajax("admin_user/list", {
		data: datas,
		success: function (data) {
			pageset = pageset ? pageset : $("#system-page");
			pageset.pageset("setData", {
				totalpage: data.totalpage,
				page: data.page,
				totalnum: data.totalnum
			});

			listview.listview("setData", data.info);
		},
		fail: function (data) {
			$.confAlert({
				size: "sm",
				context: data.msg,
				noButton: false
			})
		}
	});
}