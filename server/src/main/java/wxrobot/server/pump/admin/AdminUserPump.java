package wxrobot.server.pump.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.UserServer;
import wxrobot.dao.entity.User;

@Pump("admin_user")
@Component
public class AdminUserPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(AdminUserPump.class);
	
	@Autowired
	private UserServer userServer;
	
	@Pipe("list")
	@BarScreen(
		desc="用户列表"
	)
	public Errcode list (JSONObject params) {
		return null;
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
	public Errcode addUser (JSONObject params) {
		User user = new User();
		user.setToken(null);
		userServer.insert(user);
		return new DataResult(Errors.OK);
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
		return new DataResult(Errors.OK);
	}
	
	@Pipe("lock")
	@BarScreen(
		desc="锁定用户",
		params= {
			@Parameter(value="id",  desc="用户id"),
			@Parameter(value="locked",  desc="服务状态")
		}
	)
	public Errcode lock (JSONObject params) {
		return new DataResult(Errors.OK);
	}
}