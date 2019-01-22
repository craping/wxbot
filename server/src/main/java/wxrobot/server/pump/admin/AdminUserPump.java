package wxrobot.server.pump.admin;

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
import org.crap.jrain.core.util.DateUtil;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.crap.jrain.core.validate.security.component.Coder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.admin.server.AdminServer;
import wxrobot.biz.server.UserServer;
import wxrobot.dao.entity.User;
import wxrobot.dao.entity.field.UserInfo;

@Pump("admin_user")
@Component
public class AdminUserPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(AdminUserPump.class);
	
	@Autowired
	private UserServer userServer;
	
	@Autowired
	private AdminServer adminServer;
	
	@Pipe("list")
	@BarScreen(
		desc="用户列表"
	)
	public Errcode list (JSONObject params) {
		return adminServer.getUserList(params);
	}
	
	@Pipe("addUser")
	@BarScreen(
		desc="新增用户",
		params= {
			@Parameter(value="user_name",  desc="登录名"),
			@Parameter(value="user_pwd",  desc="密码"),
			@Parameter(value="server_end",  desc="服务时间")
		}
	)
	public Errcode addUser (JSONObject params) throws ErrcodeException {
		User user = new User();
		UserInfo info = new UserInfo();
		info.setUserName(params.optString("user_name"));
		info.setUserPwd(Coder.encryptMD5(params.getString("user_pwd")));
		info.setServerEnd(DateUtil.parseDate(params.getString("server_end"), "yyyy-MM-dd HH:mm:ss"));
		info.setServerState(true);
		user.setUserInfo(info);
		userServer.insert(user);
		return new DataResult(Errors.OK, new Data(user));
	}
	
	@Pipe("extension")
	@BarScreen(
		desc="更改服务时间",
		params= {
			@Parameter(value="id",  desc="用户id"),
			@Parameter(value="server_end",  desc="服务时间")
		}
	)
	public Errcode extension (JSONObject params) {
		int ret = adminServer.extension(params);
		return new DataResult(Errors.OK, new Data(ret));
	}
	
	@Pipe("lock")
	@BarScreen(
		desc="锁定用户",
		params= {
			@Parameter(value="id",  desc="用户id"),
			@Parameter(value="server_state",  desc="服务状态")
		}
	)
	public Errcode lock (JSONObject params) {
		int ret = adminServer.lockUser(params);
		return new DataResult(Errors.OK, new Data(ret));
	}
}