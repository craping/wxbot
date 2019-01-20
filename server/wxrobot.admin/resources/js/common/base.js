/**
 * Created by lei on 2015/12/1.
 */
$(document).ready( function(){
    $('#menuDIV').delegate(".system-subNav-lv1 span","click", function(){ 
        $(this).css("background-color","#7e1918");
        $(this).parent().siblings().find('span').css("background-color","#aa3231");
        $(this).parent().siblings().find('.system-subNav-lv2').slideUp();
        $(this).next().find('.system-subNav-lv2').slideToggle();
    })
    $('#menuDIV').delegate(".system-subNav-lv2","click", function(){
        $(this).css("background-color","#551918");
        $(this).siblings().css("background-color","#aa3231");
        $(this).parent().parent().siblings().find('.system-subNav-lv2').css("background-color","#aa3231");
    })
//    $('.system-table th').css('background-color','#898989'); 
//    $('.system-table tr:odd').css('background-color','#eee');

    $(".select").each(function(){ 
        var s=$(this);
        var z=parseInt(s.css("z-index"));
        var dt=$(this).children("dt");
        var dd=$(this).children("dd");
        var _show=function(){dd.slideDown(200);dt.addClass("cur");s.css("z-index",z+1);};    
        var _hide=function(){dd.slideUp(200);dt.removeClass("cur");s.css("z-index",z);};     
        dt.click(function(){dd.is(":hidden")?_show():_hide();});
        $(dt).delegate(dt, "click", function(){
        	dd.find("li").bind("click", function(){
        		dt.html($(this).html());_hide();
        	});
        });
//        dd.find("li").click(function(){dt.html($(this).html());_hide();}); 
        $("body").click(function(i){ !$(i.target).parents(".select").first().is(s) ? _hide():"";});
    })

     
    $('.system-fold').click(function () {

        if(!$(this).is(".cur")){
            $(this).addClass("cur");
            $(this).animate({left:'-=200px'},'fast');
            $('.system-subNav-box').animate({left: '-=230px'},'fast');
            $('.system-right').animate({left:'-=200px'},'1000')
        }else{
            $(this).removeClass("cur");
            $(this).animate({left:'+=200px'},'fast');
            $('.system-subNav-box').animate({left: '+=230px'},'fast');
            $('.system-right').animate({left:'+=200px'},'1000')
        }
    })

})