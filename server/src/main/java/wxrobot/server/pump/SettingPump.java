package wxrobot.server.pump;

import java.io.IOException;

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
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.BaseServer;
import wxrobot.biz.server.SettingServer;
import wxrobot.dao.entity.Setting;
import wxrobot.dao.entity.field.Switchs;
import wxrobot.dao.entity.field.Tips;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.dao.enums.SettingModule;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;
import wxrobot.server.param.enums.SettingModuleEParam;

@Pump("setting")
@Component
public class SettingPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
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
			@Parameter(type=SettingModuleEParam.class, value="module", desc="设置模块")
		}
	)
	public Errcode enableForward (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		settingServer.addSeq(userInfo.getUserName(), SettingModule.valueOf(params.getString("module")), params.getString("seq"));
		
		return new DataResult(Errors.OK);
	}
	
	@Pipe("disableSeq")
	@BarScreen(
		desc="关闭seq",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq"),
			@Parameter(type=SettingModuleEParam.class, value="module", desc="设置模块")
		}
	)
	public Errcode disableForward (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		
		long mod = settingServer.delSeq(userInfo.getUserName(), SettingModule.valueOf(params.getString("module")), params.getString("seq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
	
//	@Pipe("modForward")
//	@BarScreen(
//		desc="修改群转发",
//		params= {
//			@Parameter(type=TokenParam.class),
//			@Parameter(value="oldSeq", desc="oldSeq"),
//			@Parameter(value="newSeq", desc="newSeq")
//		}
//	)
//	public Errcode modForward (JSONObject params) throws ErrcodeException {
//		
//		UserInfo userInfo = settingServer.getUserInfo(params);
//		
//		long mod = settingServer.modForward(userInfo.getUserName(), params.getString("oldSeq"), params.getString("newSeq"));
//		
//		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
//	}
	
	
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
		
		return new DataResult(Errors.OK);
	}
	
	@Pipe("setTips")
	@BarScreen(
		desc="提示语设置",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="tips", desc="Tips实体类")
		}
	)
	public Errcode setTips(JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		
		Tips tips = null;
		try {
			tips = BaseServer.JSON_MAPPER.readValue(params.getString("tips"), Tips.class);
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		
		settingServer.setTips(userInfo.getUserName(), tips);
		
		return new DataResult(Errors.OK);
	}
}