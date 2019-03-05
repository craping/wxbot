package wxrobot.server.pump;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.TimerServer;
import wxrobot.dao.entity.field.ScheduleMsg;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;

@Pump("timer")
@Component
public class TimerJSONPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(TimerJSONPump.class);
	
	@Autowired
	private TimerServer timerServer;
	
	@Pipe("getTimers")
	@BarScreen(
		desc="获取定时计划",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", required=false, desc="seq")
		}
	)
	public Errcode getTimers (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		
		Map<String, List<ScheduleMsg>> timerMap = timerServer.getTimers(userInfo.getUserName(), params.optString("seq", null));
		
		return new DataResult(Errors.OK, new Data(timerMap));
	}
	
	@Pipe("addTimer")
	@BarScreen(
		desc="添加定时计划",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq")
		}
	)
	public Errcode addTimer (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		
		long mod = timerServer.addTimer(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("modTimer")
	@BarScreen(
		desc="修改定时计划",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="oldSeq", desc="oldSeq"),
			@Parameter(value="newSeq", desc="newSeq")
		}
	)
	public Errcode modTimer (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		
		long mod = timerServer.modTimer(userInfo.getUserName(), params.getString("oldSeq"), params.getString("newSeq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("delTimer")
	@BarScreen(
		desc="删除定时计划",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq")
		}
	)
	public Errcode delTimer (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		
		long mod = timerServer.delTimer(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("delGlobalMsg")
	@BarScreen(
		desc="删除全局群定时消息",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="uuid",  desc="定时消息uuid")
		}
	)
	public Errcode delGlobalMsg (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		
		long mod = timerServer.delMsg(userInfo.getUserName(), "global", params.getString("uuid"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	
	@Pipe("delMsg")
	@BarScreen(
		desc="删除分群定时消息",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq",  desc="seq"),
			@Parameter(value="uuid",  desc="定时消息uuid")
		}
	)
	public Errcode delMsg (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		
		long mod = timerServer.delMsg(userInfo.getUserName(), params.getString("seq"), params.getString("uuid"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
}