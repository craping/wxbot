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
			{ name: "创建时间", width: "6%" },
			{ name: "计划名称", width: "10%" },
			{ name: "计划玩法", width: "5%" },
			{ name: "期号", width: "3%" },
			{ name: "计划方案", width: "8%" },
			{ name: "计划位置", width: "3%" }
		],
		eachItem: function (ui, item) {
			for (var i in item) {
				if (item[i] == null) {
					item[i] = "";
				}
			}

			//var page = $("#system-page").data("lishe.pageSet").options.pageSet.page - 1;
			var time = new Date(item.time).format("yyyy-MM-dd HH:mm");
			var html = "<tr>";
			html += "<td >" + time + "</td>";
			html += "<td>" + item.name + "</td>";
			html += "<td>" + showBetTypeName(item.type) + "</td>";
			html += "<td>" + item.period + "</td>";
			html += "<td>" + item.schema + "</td>";
			if (item.position == "0") {
				html += "<td>-</td>";
			} else {
				html += "<td>第 " + item.position + " 名</td>";
			}
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

	$(document).on("click", "#clear", function () {
		$("#productform input[type='text']").each(function () {
			$(this).val("");
		});
	})
	queryList(1);
})

function queryList(page, listview, pageset, params) {
	var options = {
		curPage: page,
		pageSize: 10
	};
	var datas = $.extend(true, {}, options, $("#bettingform").serializeJson());

	listview = listview ? listview : $("#system-table");
	listview.listview("loading");
	Web.Method.ajax("admin_plan/list", {
		data: datas,
		success: function (data) {
			pageset = pageset ? pageset : $("#system-page");
			pageset.pageset("setData", {
				totalpage: 1,
				page: 1,
				totalnum: 1
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