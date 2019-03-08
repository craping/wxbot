package wxrobot.server.pump;

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
import wxrobot.biz.server.SettingServer;
import wxrobot.dao.entity.Setting;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;

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
	
	@Pipe("enableForward")
	@BarScreen(
		desc="开启群转发",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq")
		}
	)
	public Errcode enableForward (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		
		settingServer.addForward(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(Errors.OK);
	}
	
	@Pipe("disableForward")
	@BarScreen(
		desc="关闭群转发",
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="seq", desc="seq")
		}
	)
	public Errcode disableForward (JSONObject params) throws ErrcodeException {
		
		UserInfo userInfo = settingServer.getUserInfo(params);
		
		long mod = settingServer.delForward(userInfo.getUserName(), params.getString("seq"));
		
		return new DataResult(mod > 0?Errors.OK:CustomErrors.USER_OPR_ERR);
	}
}