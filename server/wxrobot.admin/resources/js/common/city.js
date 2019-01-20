var where = new Array(5); 
var ar = new Array();
function comefrom(loca,locachengshi) { this.loca = loca; this.locachengshi = locachengshi; } 
		where[0] = new comefrom("大陆","北京|上海|天津|重庆|河北|山西|内蒙古|辽宁|吉林|黑龙江|江苏|浙江|安徽|福建|江西|山东|河南|湖北|湖南|广东|广西|海南|四川|贵州|云南|西藏|陕西|甘肃|宁夏|青海|新疆");
		where[1] = new comefrom("香港",""); 
		where[2] = new comefrom("澳门",""); 
		where[3] = new comefrom("台湾",""); 
		where[4] = new comefrom("海外","北美洲|南美洲|亚洲|非洲|欧洲|大洋洲|美洲"); 
		
		ar[0] = new comefrom("北京","东城|西城|崇文|宣武|朝阳|丰台|石景山|海淀|门头沟|房山|通州|顺义|昌平|大兴|平谷|怀柔|密云|延庆"); 
		ar[1] = new comefrom("上海","黄浦|卢湾|徐汇|长宁|静安|普陀|闸北|虹口|杨浦|闵行|宝山|嘉定|浦东|金山|松江|青浦|南汇|奉贤|崇明"); 
		ar[2] = new comefrom("天津","和平|东丽|河东|西青|河西|津南|南开|北辰|河北|武清|红挢|塘沽|汉沽|大港|宁河|静海|宝坻|蓟县"); 
		ar[3] = new comefrom("重庆","万州|涪陵|渝中|大渡口|江北|沙坪坝|九龙坡|南岸|北碚|万盛|双挢|渝北|巴南|黔江|长寿|綦江|潼南|铜梁|大足|荣昌|壁山|梁平|城口|丰都|垫江|武隆|忠县|开县|云阳|奉节|巫山|巫溪|石柱|秀山|酉阳|彭水|江津|合川|永川|南川"); 
		ar[4] = new comefrom("河北","石家庄|邯郸|邢台|保定|张家口|承德|廊坊|唐山|秦皇岛|沧州|衡水|涿州|高碑店|泊头|霸州|黄骅|鹿泉|辛集|徐水"); 
		ar[5] = new comefrom("山西","太原|大同|阳泉|长治|晋城|朔州|吕梁|忻州|晋中|临汾|运城|介休|霍州|孝义|离石|高平|原平|侯马|汾阳"); 
		ar[6] = new comefrom("内蒙古","呼和浩特|包头|乌海|赤峰|呼伦贝尔盟|阿拉善盟|哲里木盟|兴安盟|乌兰察布盟|锡林郭勒盟|巴彦淖尔盟|伊克昭盟|鄂尔多斯|乌兰浩特|巴彦淖尔|锡林浩特|牙克石|呼伦贝尔|扎兰屯|海拉尔|满洲里|二连浩特|集宁|通辽|丰镇|临河|乌拉特前"); 
		ar[7] = new comefrom("辽宁","沈阳|大连|鞍山|抚顺|本溪|丹东|锦州|营口|阜新|辽阳|盘锦|铁岭|朝阳|葫芦岛|凌海|北票|海城|盖州|开原|曲阜"); 
		ar[8] = new comefrom("吉林","长春|吉林|四平|辽源|通化|白山|松原|白城|延边|公主岭|九台市|珲春|延吉|桦甸|敦化|双辽|榆树|大安|图们|梅河|舒兰|集安|德惠|磐石|龙井|晖春|泉阳|辽源"); 
		ar[9] = new comefrom("黑龙江","哈尔滨|齐齐哈尔|牡丹江|佳木斯|大庆|绥化|鹤岗|鸡西|黑河|双鸭山|伊春|七台河|大兴安岭|北安|密山|安达|海伦|铁力|肇东|虎林|双城|东宁|穆凌|白安|海林|宁安|五常|绥芬河|讷河|富锦"); 
		ar[10] = new comefrom("江苏","南京|镇江|苏州|南通|扬州|盐城|徐州|连云港|常州|无锡|宿迁|泰州|淮安|靖江|泰兴|锡山|武进|溧阳|江阴|大丰|宜兴"); 
		ar[11] = new comefrom("浙江","杭州|宁波|温州|嘉兴|湖州|绍兴|金华|衢州|舟山|台州|丽水|义乌|东阳|萧山|余杭|永康"); 
		ar[12] = new comefrom("安徽","合肥|芜湖|蚌埠|马鞍山|淮北|铜陵|安庆|黄山|滁州|宿州|池州|淮南|巢湖|阜阳|六安|宣城|亳州|宣州|界首|凤阳|巢洲"); 
		ar[13] = new comefrom("福建","福州|厦门|莆田|三明|泉州|漳州|南平|龙岩|宁德|福清|平潭|晋江"); 
		ar[14] = new comefrom("江西","南昌市|景德镇|九江|鹰潭|萍乡|新馀|赣州|吉安|宜春|抚州|上饶|丰城|樟树|贵溪|新余|瑞昌"); 
		ar[15] = new comefrom("山东","济南|青岛|淄博|枣庄|东营|烟台|潍坊|济宁|泰安|威海|日照|莱芜|临沂|德州|聊城|滨州|菏泽|滕州|寿光|章丘|胶州|滨洲|郓城|青州|昌邑"); 
		ar[16] = new comefrom("河南","郑州|开封|洛阳|平顶山|安阳|鹤壁|新乡|焦作|濮阳|许昌|漯河|三门峡|南阳|商丘|信阳|周口|驻马店|济源|义马|项城|长葛|林州|登封|邓州|灵宝|荥阳|新密|禹州|卫辉|淮阳|郸城"); 
		ar[17] = new comefrom("湖北","武汉|宜昌|荆州|襄樊|黄石|荆门|黄冈|十堰|恩施|潜江|天门|仙桃|随州|咸宁|孝感|鄂州|汉川|应城|武穴|定州|石首|钟祥|洪湖|松滋|枝江|当阳|丹江口|利川|广水|安陆|麻城|宜都|陨阳|农架|襄阳|随州|大冶|丹江");
		ar[18] = new comefrom("湖南","长沙|常德|株洲|湘潭|衡阳|岳阳|邵阳|益阳|娄底|怀化|郴州|永州|湘西|张家界|偃师|浏阳|汨罗|吉首|孟州|邵东|常宁"); 
		ar[19] = new comefrom("广东","广州|深圳|珠海|汕头|东莞|中山|佛山|韶关|江门|湛江|茂名|肇庆|惠州|梅州|汕尾|河源|阳江|清远|潮州|揭阳|云浮|普宁|阳春|陆丰|英德|台山|廉江|雷州|嘉应|罗定|开平|恩平|潮阳|肇兴|高州|化州|惠阳"); 
		ar[20] = new comefrom("广西","南宁|柳州|桂林|梧州|北海|防城港|钦州|贵港|玉林|南宁地区|柳州地区|贺州|百色|河池|来宾|崇左|北流|桂平|融水|岑溪"); 
		ar[21] = new comefrom("海南","海口|三亚"); 
		ar[22] = new comefrom("四川","成都|绵阳|德阳|自贡|攀枝花|广元|内江|乐山|南充|宜宾|广安|达川|雅安|眉山|甘孜|凉山|泸州|峨嵋山|达州|资阳|遂宁|巴中|阿坝|马尔康|彭州|神农架|阆中|西昌|三台"); 
		ar[23] = new comefrom("贵州","贵阳|六盘水|遵义|安顺|铜仁|黔西南|毕节|黔东南|黔南|兴义|凯里|都匀|福泉"); 
		ar[24] = new comefrom("云南","昆明|大理|曲靖|玉溪|昭通|楚雄|红河|文山|思茅|西双版纳|保山|德宏|丽江|怒江|迪庆|临沧|普洱|开远|安宁");
		ar[25] = new comefrom("西藏","拉萨|日喀则|山南|林芝|昌都|阿里|那曲"); 
		ar[26] = new comefrom("陕西","西安|宝鸡|咸阳|铜川|渭南|延安|榆林|汉中|安康|商洛|瑞金|杨凌|韩城"); 
		ar[27] = new comefrom("甘肃","兰州|嘉峪关|金昌|白银|天水|酒泉|张掖|武威|定西|陇南|平凉|庆阳|临夏|甘南|敦煌|秦州|玉门|合作"); 
		ar[28] = new comefrom("宁夏","银川|石嘴山|吴忠|固原|中卫|青铜峡"); 
		ar[29] = new comefrom("青海","西宁|海东|海南|海北|黄南|玉树|果洛|海西|格尔木|德令哈"); 
		ar[30] = new comefrom("新疆","乌鲁木齐|石河子|克拉玛依|伊犁|巴音郭勒|昌吉|克孜勒苏柯尔克孜|博尔塔拉|吐鲁番|哈密|喀什|和田|阿克苏|阜康|塔城|库尔勒|奎屯|博乐|乌苏|阿勒泰|阿图什|阿拉尔|米泉|博州|伊宁|吉州|五家渠"); 
		
		$(function(){
			var html='';
			$.each(where,function(i,j){
				html+='<li value="'+i+'" class="locachengshi"><a>'+where[i].loca+'</a</li>';
			})
			$(".shengfen").append(html);
			
			$(document).on("click",".locachengshi",function(){
				var val = $(this).attr("value");
				
				var list = where[val].locachengshi;
				var html = '';
				$.each(list.split("|"),function(i,j){
					if( j!= ""||j!=''){
						html+='<li class="citychengshi" value="'+i+'" ><a>'+ j +'</a</li>'; 
					}
				})
				if(val == "0"){
					$("#chengshi").html("请选择");
					$(".chengshi,.quyuArea").empty();
					$(".chengshi").append(html);
					
					$("#chengshi,#quyuArea").attr("value","");
					$("#chengshi,#quyuArea").html("请选择");
					$("#shengShidiQu,#quYudiQu").show();
				}else if(val>0 && val<4){
					$(".chengshi,.quyuArea").empty();
					$("#chengshi,#quyuArea").attr("value","");
					$("#chengshi,#quyuArea").html("");
					$("#shengShidiQu,#quYudiQu").hide();
				}else if(val =="4"){
					$("#chengshi").html("请选择");
					$(".chengshi").empty();
					$(".chengshi").append(html);
					
					$("#quyuArea").attr("value","");
					$("#chengshi").html("请选择");
					$("#shengShidiQu").show();
					$("#quYudiQu").hide();
				}
			})
			
			$(document).on("click",".citychengshi",function(){
				var val = $(this).attr("value");
				var list = ar[val].locachengshi;
				var html = '';
				$.each(list.split("|"),function(i,j){
					if( j!= ""||j!=''){
						html+='<li value="'+i+'" ><a>'+ j +'</a</li>'; 
					}
				})
				
				if(html != "" && html != ''){
					$("#quyuArea").html("请选择");
					$(".quyuArea").empty();
					$(".quyuArea").append(html);
				}else{
					$("#quyuArea").attr("value","");
					$(".quyuArea").empty();
					$("#quyuArea").html("");
					$("#quYudiQu").hide();
				}
			})
		})