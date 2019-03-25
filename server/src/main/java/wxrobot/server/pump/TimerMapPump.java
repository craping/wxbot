package wxrobot.server.pump;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
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

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import wxrobot.biz.server.TimerServer;
import wxrobot.dao.entity.field.ScheduleMsg;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.param.PartParam;
import wxrobot.server.param.TokenParam;
import wxrobot.server.sync.pojo.SyncAction;
import wxrobot.server.sync.pojo.SyncBiz;
import wxrobot.server.sync.pojo.SyncMsg;

@Pump("timer")
@Component
public class TimerMapPump extends DataPump<FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(TimerMapPump.class);
	
	@Autowired
	private TimerServer timerServer;
	
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
		//消息放入关键词事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.TIMER);
		event.setAction(SyncAction.ADD);
		
		Map<String, Object> data = new HashMap<>();
		data.put("seq", params.get("seq").toString());
		data.put("timer", msg);
		event.setData(data);
				
		return new DataResult(Errors.OK, new Data(msg));
	}
}