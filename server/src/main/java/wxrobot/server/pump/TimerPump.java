package wxrobot.server.pump;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
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
import org.crap.jrain.core.util.StringUtil;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.crap.jrain.mvc.netty.decoder.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import wxrobot.biz.server.TimerServer;
import wxrobot.dao.entity.field.ScheduleMsg;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.PartParam;
import wxrobot.server.param.TokenParam;
import wxrobot.server.sync.SyncContext;
import wxrobot.server.sync.pojo.SyncAction;
import wxrobot.server.sync.pojo.SyncBiz;
import wxrobot.server.sync.pojo.SyncMsg;

@Pump("timer")
@Component
public class TimerPump extends DataPump<FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(TimerPump.class);
	
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
		
		Map<String, List<ScheduleMsg>> timerMap = timerServer.getTimers(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(Errors.OK, new Data(timerMap));
	}
	
//	@Pipe("addTimer")
//	@BarScreen(
//		desc="添加定时计划",
//		params= {
//			@Parameter(type=TokenParam.class),
//			@Parameter(value="seq", desc="seq")
//		}
//	)
//	public Errcode addTimer (JSONObject params) throws ErrcodeException {
//		
//		UserInfo userInfo = timerServer.getUserInfo(params);
//		
//		long mod = timerServer.addTimer(userInfo.getUserName(), params.getString("seq"));
//		
//		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
//	}
	
//	@Pipe("modTimer")
//	@BarScreen(
//		desc="修改定时计划",
//		params= {
//			@Parameter(type=TokenParam.class),
//			@Parameter(value="oldSeq", desc="oldSeq"),
//			@Parameter(value="newSeq", desc="newSeq")
//		}
//	)
//	public Errcode modTimer (JSONObject params) throws ErrcodeException {
//		
//		UserInfo userInfo = timerServer.getUserInfo(params);
//		
//		long mod = timerServer.modTimer(userInfo.getUserName(), params.getString("oldSeq"), params.getString("newSeq"));
//		
//		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
//	}
	
//	@Pipe("delTimer")
//	@BarScreen(
//		desc="删除定时计划",
//		params= {
//			@Parameter(type=TokenParam.class),
//			@Parameter(value="seq", desc="seq")
//		}
//	)
//	public Errcode delTimer (JSONObject params) throws ErrcodeException {
//		
//		UserInfo userInfo = timerServer.getUserInfo(params);
//		
//		long mod = timerServer.delTimer(userInfo.getUserName(), params.getString("seq"));
//		
//		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
//	}
	
	@Pipe("addMsg")
	@BarScreen(
		desc="添加分群定时消息",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq",  desc="seq"),
			@Parameter(type=PartParam.class, value="content", desc="消息内容"),
			@Parameter(value="schedule", desc="计划方案")
		}
	)
	public Errcode addMsg (Map<?, ?> params) throws ErrcodeException {
		
		UserInfo userInfo = timerServer.getUserInfo(params);
		String uuid = StringUtil.uuid();
		ScheduleMsg msg = new ScheduleMsg();
		msg.setUuid(uuid);
		msg.setSchedule(params.get("schedule").toString());
		Object content = params.get("content");
		if(content instanceof Part){
			Part part = (Part)content;
			String ext = part.getFilename().substring(part.getFilename().lastIndexOf(".")+1);
			String path = "attach/"+userInfo.getUserName() + "/"+ uuid+"."+ext;
			String mimeType = null;
			try {
				part.write(path);
				mimeType = Files.probeContentType(new File(path).toPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			msg.setContent(uuid+"."+ext);
			msg.setType(5);
			if("gif".equals(ext))
				msg.setType(3);
			else if(mimeType != null){
				switch (mimeType.split("/")[0]) {
				case "image":
					msg.setType(2);
					break;
				case "video":
					msg.setType(4);
					break;
				}
			}
				
		} else {
			msg.setContent(content.toString());
			msg.setType(1);
		}
		timerServer.addMsg(userInfo.getUserName(), params.get("seq").toString(), msg);
		
		//消息放入事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.TIMER);
		event.setAction(SyncAction.ADD);
		
		Map<String, Object> data = new HashMap<>();
		data.put("seq", params.get("seq").toString());
		data.put("timer", msg);
		event.setData(data);
		SyncContext.putMsg(params.get("token").toString(), event);
				
		return new DataResult(Errors.OK, new Data(msg));
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
		if(mod > 0){
			//消息放入事件队列
			SyncMsg event = new SyncMsg();
			event.setBiz(SyncBiz.TIMER);
			event.setAction(SyncAction.DEL);
			
			Map<String, Object> data = new HashMap<>();
			data.put("seq", params.getString("seq"));
			data.put("uuid", params.getString("uuid"));
			event.setData(data);
			SyncContext.putMsg(params.getString("token"), event);
		}
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
}