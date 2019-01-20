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
			{ name: "计划期号", width: "8%" },
			{ name: "计划名称", width: "10%" },
			{ name: "彩票名称", width: "5%" },
			{ name: "计划玩法", width: "10%" },
			{ name: "计划期数", width: "8%" },
			{ name: "推荐方案", width: "8%" },
			{ name: "计划位置", width: "8%" },
			{ name: "推荐时间", width: "10%" },
			{ name: "中奖奖结果", width: "8%" },
			{ name: "中奖期号", width: "8%" },
			{ name: "中奖", width: "5%" },
		],
		eachItem: function (ui, item) {
			for (var i in item) {
				if (item[i] == null) {
					item[i] = "";
				}
			}

			var date = new Date();
			date.setTime(item.create_time);
			date = date.format("yyyy/MM/dd HH:mm");
			var page = $("#system-page").data("lishe.pageSet").options.pageSet.page - 1;
			var html = "<tr>";
			//html+="<td width='3%'><input type='checkbox' accept='" + item.checkStatus + "' class='middle' value='"+item.id+"'></label></td>" ;
			html += "<td >" + date + "</td>";
			html += "<td>" + item.user_name + "</td>";
			html += "<td>" + item.lottery_type + "_" + showBetTypeName(item.bet_type) +"</td>";
			html += "<td>" + item.period + "</td>";
			html += "<td>" + item.bet_schema + "</td>";
			if (item.position == "0") {
				html += "<td>-</td>";
			} else {
				html += "<td>第 " + item.position + " 名</td>";
			}
			html += "<td>" + item.amount + "</td>";
			html += "<td>" + item.lottery_result + "</td>";
			html += "<td>" + item.bonus + "</td>";
			if (item.win == "1") {
				html += "<td><span class='plan_ok'>中</span></td>";
			} else {
				html += "<td><span class='plan_no'>挂</span></td>";
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
	Web.Method.ajax("admin_betting/list", {
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