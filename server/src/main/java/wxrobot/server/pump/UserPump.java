package wxrobot.server.pump;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.UserServer;
import wxrobot.dao.entity.User;
import wxrobot.dao.entity.field.UserInfo;

@Pump("user")
@Component
public class UserPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(UserPump.class);
	
	@Autowired
	private UserServer userServer;
	
	@Pipe("register")
	@BarScreen(
		desc="用户注册"
	)
	public Errcode register (JSONObject params) {
		User user = new User();
		UserInfo info = new UserInfo();
		info.setUserName("admin");
		info.setUserPwd("111111");
		user.setUserInfo(info);
		userServer.insert(user);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("info")
	@BarScreen(
		desc="用户注册"
	)
	public Errcode info (JSONObject params) {
		User user = userServer.find("5c43609a76785a40bca4aa5e");
		return new DataResult(Errors.OK, new Data(user));
	}
}