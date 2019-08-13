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

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import wxrobot.biz.server.TipServer;
import wxrobot.dao.entity.field.Msg;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.PartParam;
import wxrobot.server.param.TipsTypeParam;
import wxrobot.server.param.TokenParam;
import wxrobot.server.sync.SyncContext;
import wxrobot.server.sync.pojo.SyncAction;
import wxrobot.server.sync.pojo.SyncBiz;
import wxrobot.server.sync.pojo.SyncMsg;

@Pump("tip")
@Component
public class TipPump extends DataPump<FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(TipPump.class);
	
	@Autowired
	private TipServer tipServer;
	
	@Pipe("getTips")
	@BarScreen(
		desc="获取群提示语",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", required=false, desc="seq")
		}
	)
	public Errcode getTips (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = tipServer.getUserInfo(params);
		
		Map<String, Map<String, Msg>> tips = tipServer.getTips(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(Errors.OK, new Data(tips));
	}
	
	@Pipe("set")
	@BarScreen(
		desc="设置提示语",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq",  desc="seq"),
			@Parameter(type=TipsTypeParam.class),
			@Parameter(type=PartParam.class, value="content", desc="消息内容")
		}
	)
	public Errcode set(Map<?, ?> params) throws ErrcodeException {
		
		UserInfo userInfo = tipServer.getUserInfo(params);
		
		String uuid = StringUtil.uuid();
		Msg msg = new Msg();
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
		
		tipServer.set(userInfo.getUserName(), params.get("seq").toString(), params.get("type").toString(), msg);
		//消息放入事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.TIPS);
		event.setAction(SyncAction.SET);
		
		Map<String, Object> data = new HashMap<>();
		data.put("seq", params.get("seq").toString());
		data.put("type", params.get("type").toString());
		data.put("msg", msg);
		event.setData(data);
		SyncContext.putMsg(params.get("token").toString(), event);
				
		return new DataResult(Errors.OK, new Data(msg));
	}
	
	
	@Pipe("del")
	@BarScreen(
		desc="删除群提示语",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq",  desc="seq"),
			@Parameter(type=TipsTypeParam.class)
		}
	)
	public Errcode del(JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = tipServer.getUserInfo(params);
		long mod = tipServer.del(userInfo.getUserName(), params.getString("seq"), params.get("type").toString());
		//消息放入关键词事件队列
		if (mod > 0) {
			SyncMsg msg = new SyncMsg();
			msg.setBiz(SyncBiz.TIPS);
			msg.setAction(SyncAction.DEL);
			
			Map<String, Object> data = new HashMap<>();
			data.put("seq", params.getString("seq"));
			data.put("type", params.get("type").toString());
			msg.setData(data);
			
			SyncContext.putMsg(params.getString("token"), msg);
		}
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
}