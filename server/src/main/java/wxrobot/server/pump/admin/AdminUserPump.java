package wxrobot.server.pump.admin;

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
import org.crap.jrain.core.validate.security.component.Coder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.AdminServer;
import wxrobot.biz.server.BaseServer;
import wxrobot.biz.server.SettingServer;
import wxrobot.biz.server.UserServer;
import wxrobot.dao.entity.Setting;
import wxrobot.dao.entity.User;
import wxrobot.dao.entity.field.Permissions;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.param.AdminTokenParam;
import wxrobot.server.sync.SyncContext;
import wxrobot.server.sync.pojo.SyncAction;
import wxrobot.server.sync.pojo.SyncBiz;
import wxrobot.server.sync.pojo.SyncMsg;
import wxrobot.server.utils.Tools;

@Pump("admin")
@Component
public class AdminUserPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(AdminUserPump.class);
	
	@Autowired
	private UserServer userServer;
	@Autowired
	private AdminServer adminServer;
	@Autowired
	private SettingServer settingServer;
	
	@Pipe("list")
	@BarScreen(
		desc="用户列表",
		params= {
			@Parameter(type=AdminTokenParam.class)
		}
	)
	public Errcode list (JSONObject params) {
		return adminServer.getUserList(params);
	}
	
	@Pipe("resetPwd")
	@BarScreen(
		desc="重置密码",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="用户id")
		}
	)
	public Errcode resetPwd(JSONObject params) {
		adminServer.resetPwd(params);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("addUser")
	@BarScreen(
		desc="新增用户",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="user_name",  desc="登录名"),
			@Parameter(value="user_pwd",  desc="密码"),
			@Parameter(value="server_state",  desc="服务状态"),
			@Parameter(value="server_end",  desc="服务时间"),
			@Parameter(value="permissions", desc="Permissions实体类")
		}
	)
	public Errcode addUser (JSONObject params) throws ErrcodeException {
		// 用户名
		String userName = params.getString("user_name");
		User user = new User();
		UserInfo info = new UserInfo();
		info.setUserName(userName);
		info.setUserPwd(Coder.encryptMD5(params.getString("user_pwd")));
		info.setServerEnd(Tools.dateUTCToStamp(params.getString("server_end")));
		info.setServerState(params.getBoolean("server_state"));
		user.setUserInfo(info);
		userServer.insert(user);
		// 用户权限
		Permissions permissions = null;
		try {
			permissions = BaseServer.JSON_MAPPER.readValue(params.getString("permissions"), Permissions.class);
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		settingServer.setPermissions(userName, permissions);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("extension")
	@BarScreen(
		desc="更改服务时间",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="用户id"),
			@Parameter(value="server_end",  desc="服务时间")
		}
	)
	public Errcode extension (JSONObject params) throws ErrcodeException {
		User user = userServer.checkUserExist(params.getString("id"));
		adminServer.extension(params);
		if (userServer.userLogged(user.getToken())) {
			//消息放入关键词事件队列
			SyncMsg msg = new SyncMsg();
			msg.setBiz(SyncBiz.USER);
			msg.setAction(SyncAction.SERVER_TIME);
			msg.setData(Tools.dateToStamp(params.getString("server_end"), "yyyy-MM-dd"));
			SyncContext.toMsg(user.getToken(), msg);
		}
		return new DataResult(Errors.OK);
	}
	
	@Pipe("lock")
	@BarScreen(
		desc="锁定用户",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="用户id"),
			@Parameter(value="server_state",  desc="服务状态")
		}
	)
	public Errcode lock (JSONObject params) throws ErrcodeException {
		User user = userServer.checkUserExist(params.getString("id"));
		adminServer.lockUser(params);
		if (userServer.userLogged(user.getToken())) {
			//消息放入关键词事件队列
			SyncMsg msg = new SyncMsg();
			msg.setBiz(SyncBiz.USER);
			msg.setAction(SyncAction.LOCK);
			msg.setData(params.getBoolean("server_state"));
			SyncContext.toMsg(user.getToken(), msg);
		}
		return new DataResult(Errors.OK);
	}
	
	@Pipe("destroy")
	@BarScreen(
		desc="注销用户",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id", desc="用户id"),
			@Parameter(value="destroy", desc="注销标识")
		}
	)
	public Errcode destroy(JSONObject params) throws ErrcodeException {
		User user = userServer.checkUserExist(params.getString("id"));
		adminServer.destroy(params);
		if (userServer.userLogged(user.getToken())) {
			//消息放入关键词事件队列
			SyncMsg msg = new SyncMsg();
			msg.setBiz(SyncBiz.USER);
			msg.setAction(SyncAction.DESTROY);
			msg.setData(params.getBoolean("destroy"));
			SyncContext.toMsg(user.getToken(), msg);
		}
		return new DataResult(Errors.OK);
	}
	
	@Pipe("getPermissions")
	@BarScreen(
		desc="获取用户权限",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="用户id")
		}
	)
	public Errcode getPermissions (JSONObject params) throws ErrcodeException {
		User user = userServer.checkUserExist(params.getString("id"));
		Setting setting = settingServer.getSetting(user.getUserInfo().getUserName());
		if (setting == null) {
			return new DataResult(Errors.OK, new Data(new Permissions()));
		} else {
			return new DataResult(Errors.OK, new Data(setting.getPermissions()));
		}
	}
	
	@Pipe("syncPermissions")
	@BarScreen(
		desc="修改用户权限",
		params= {
			@Parameter(type=AdminTokenParam.class),
			@Parameter(value="id",  desc="用户id"),
			@Parameter(value="permissions", desc="Permissions实体类")
		}
	)
	public Errcode syncPermissions(JSONObject params) throws ErrcodeException {
		User user = userServer.checkUserExist(params.getString("id"));
		Permissions permissions = null;
		try {
			permissions = BaseServer.JSON_MAPPER.readValue(params.getString("permissions"), Permissions.class);
		} catch (IOException e) {
			e.printStackTrace();
			return new Result(Errors.PARAM_FORMAT_ERROR);
		}
		settingServer.setPermissions(user.getUserInfo().getUserName(), permissions);
		
		if (userServer.userLogged(user.getToken())) {
			//消息放入关键词事件队列
			SyncMsg msg = new SyncMsg();
			msg.setBiz(SyncBiz.PERMISSIONS);
			msg.setAction(SyncAction.SET);
			msg.setData(permissions);
			SyncContext.toMsg(user.getToken(), msg);
		}
		return new DataResult(Errors.OK);
	}
}