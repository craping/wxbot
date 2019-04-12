package wxrobot.server.pump;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
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
import net.sf.json.JSONObject;
import wxrobot.biz.server.BaseServer;
import wxrobot.biz.server.SettingServer;
import wxrobot.dao.entity.Setting;
import wxrobot.dao.entity.field.Msg;
import wxrobot.dao.entity.field.Switchs;
import wxrobot.dao.entity.field.Tips;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.dao.enums.SettingModule;
import wxrobot.dao.enums.TipsType;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.PartParam;
import wxrobot.server.param.TipsTypeParam;
import wxrobot.server.param.TokenParam;
import wxrobot.server.sync.SyncContext;
import wxrobot.server.sync.pojo.SyncAction;
import wxrobot.server.sync.pojo.SyncBiz;
import wxrobot.server.sync.pojo.SyncMsg;

@Pump("setting")
@Component
public class SettingPump extends DataPump<FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(SettingPump.class);
	
	@Autowired
	private SettingServer settingServer;
	
	@Pipe("getSetting")
	@BarScreen(
		desc="获取设置",
		params= {
			@Parameter(type=TokenParam.class)
		}
	)
	public Errcode getSetting (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		
		Setting setting = settingServer.getSetting(userInfo.getUserName());
		
		return new DataResult(Errors.OK, new Data(setting));
	}
	
	@Pipe("enableSeq")
	@BarScreen(
		desc="开启seq",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq"),
			@Parameter(value="module", desc="设置模块")
		}
	)
	public Errcode enableSeq (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		String m = params.getString("module");
		SettingModule module;
		if(SettingModule.FORWARDS.getModule().equalsIgnoreCase(m))
			module = SettingModule.FORWARDS;
		else if(SettingModule.KEYWORDS.getModule().equalsIgnoreCase(m))
			module = SettingModule.KEYWORDS;
		else if(SettingModule.TIMERS.getModule().equalsIgnoreCase(m))
			module = SettingModule.TIMERS;
		else
			module = SettingModule.TURING;
		settingServer.addSeq(userInfo.getUserName(), module, params.getString("seq"));
		
		//消息放入事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.SETTING);
		event.setAction(SyncAction.SET);
		SyncContext.putMsg(params.get("token").toString(), event);
		
		return new DataResult(Errors.OK);
	}
	
	@Pipe("disableSeq")
	@BarScreen(
		desc="关闭seq",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq"),
			@Parameter(value="module", desc="设置模块")
		}
	)
	public Errcode disableSeq (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		String m = params.getString("module");
		SettingModule module;
		if(SettingModule.FORWARDS.getModule().equalsIgnoreCase(m))
			module = SettingModule.FORWARDS;
		else if(SettingModule.KEYWORDS.getModule().equalsIgnoreCase(m))
			module = SettingModule.KEYWORDS;
		else if(SettingModule.TIMERS.getModule().equalsIgnoreCase(m))
			module = SettingModule.TIMERS;
		else
			module = SettingModule.TURING;
		long mod = settingServer.delSeq(userInfo.getUserName(), module, params.getString("seq"));
		
		//消息放入事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.SETTING);
		event.setAction(SyncAction.SET);
		SyncContext.putMsg(params.get("token").toString(), event);
				
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
	@Pipe("setSwitchs")
	@BarScreen(
		desc="开关设置",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="switchs", desc="Switchs实体类")
		}
	)
	public Errcode setSwitchs (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		
		Switchs switchs = null;
		try {
			switchs = BaseServer.JSON_MAPPER.readValue(params.getString("switchs"), Switchs.class);
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		settingServer.setSwitchs(userInfo.getUserName(), switchs);
		//消息放入事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.SWITCHS);
		event.setAction(SyncAction.SET);
		event.setData(switchs);
		SyncContext.putMsg(params.get("token").toString(), event);
				
		return new DataResult(Errors.OK);
	}
	
	@Pipe("cancelTips")
	@BarScreen(
		desc="提示语设置",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(type=TipsTypeParam.class)
		}
	)
	public Errcode cancelTips(JSONObject params) throws ErrcodeException {
		UserInfo userInfo = settingServer.getUserInfo(params);
		Tips tips = settingServer.getSetting(userInfo.getUserName()).getTips();
		if (tips == null) {
			return new DataResult(Errors.OK, new Data(tips));
		}
			
		switch (TipsType.getTipsType(params.get("type").toString())) {
			case CHATROOMFOUND:
				tips.setChatRoomFoundTip(null);
				break;
			case MEMBERJOIN:
				tips.setMemberJoinTip(null);
				break;
			case MEMBERLEFT:
				tips.setMemberLeftTip(null);
				break;
			default:
				break;
		}
		settingServer.setTips(userInfo.getUserName(), tips);
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.TIPS);
		event.setAction(SyncAction.SET);
		event.setData(tips);
		SyncContext.putMsg(params.get("token").toString(), event);
		return new DataResult(Errors.OK, new Data(tips));
	}
	
	@Pipe("setTips")
	@BarScreen(
		desc="提示语设置",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(type=TipsTypeParam.class),
			@Parameter(type=PartParam.class, value="content", desc="消息内容")
		}
	)
	public Errcode setTips(Map<?, ?> params) throws ErrcodeException {
		UserInfo userInfo = settingServer.getUserInfo(params);
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
		
		Tips tips = settingServer.getSetting(userInfo.getUserName()).getTips();
		if (tips == null) 
			tips = new Tips();
		switch (TipsType.getTipsType(params.get("type").toString())) {
			case CHATROOMFOUND:
				tips.setChatRoomFoundTip(msg);
				break;
			case MEMBERJOIN:
				tips.setMemberJoinTip(msg);
				break;
			case MEMBERLEFT:
				tips.setMemberLeftTip(msg);
				break;
			default:
				break;
		}
		settingServer.setTips(userInfo.getUserName(), tips);
		//消息放入事件队列
		SyncMsg event = new SyncMsg();
		event.setBiz(SyncBiz.TIPS);
		event.setAction(SyncAction.SET);
		event.setData(tips);
		SyncContext.putMsg(params.get("token").toString(), event);
				
		return new DataResult(Errors.OK, new Data(tips));
	}
}