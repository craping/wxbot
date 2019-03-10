package wxrobot.server.pump;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.biz.server.UserServer;
import wxrobot.dao.entity.User;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.MobileParam;
import wxrobot.server.utils.RedisUtil;
import wxrobot.server.utils.Tools;

@Pump("user")
@Component
public class UserPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(UserPump.class);
	
	@Autowired
	private UserServer userServer;
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Pipe("register")
	@BarScreen(
		desc="用户注册",
		params= {
			@Parameter(value="user_name",  desc="登录名", type=MobileParam.class),
			@Parameter(value="user_pwd",  desc="密码"),
			@Parameter(value="confirm_pwd",  desc="确认密码"),
			@Parameter(value="phone_num",  desc="手机号码", type=MobileParam.class),
			@Parameter(value="phone_code",  desc="手机验证码")
		}
	)
	public Errcode register (JSONObject params) throws ErrcodeException {
		String userPwd = params.getString("user_pwd");
		if (!userPwd.equals(params.getString("confirm_pwd")))
			throw new ErrcodeException(CustomErrors.USER_CHANGE_PWD_ERR);
		
		User user = new User();
		UserInfo userInfo = new UserInfo(params.getString("user_name"), userPwd, params.getString("phone_num"));
		user.setUserInfo(userInfo);
		userServer.insert(user);
		return new DataResult(Errors.OK);
	}
	
	@Pipe("login")
	@BarScreen(
		desc="用户登录",
		security=true,
		params= {
			@Parameter(value="login_name",  desc="登录名"),
			@Parameter(value="login_pwd",  desc="密码"),
		}
	)
	public Errcode login (JSONObject params) {
		String userName = params.getString("login_name");
		String userPwd = params.getString("login_pwd");
		User user = userServer.getUser(userName, userPwd);
		if (user == null) //判断用户是否存在
			return new Result(CustomErrors.USER_ACC_ERR);
		
		if (!Tools.isStrEmpty(user.getToken())) {
			redisTemplate.delete("user_" + user.getToken()); //  删除当前缓存
		}
		
		// 生成新的用户token 并持久化
		String new_token = StringUtil.uuid(); 	
		user.setToken(new_token);
		if(userServer.updateToken(user) != 1)
			return new Result(CustomErrors.USER_LOGIN_UPDATE_TOKEN_EX);
		
		// 插入登录日志 
		InetSocketAddress insocket = (InetSocketAddress) getResponse().remoteAddress();
		System.out.println("IP:"+insocket.getAddress().getHostAddress());
		String ip = insocket.getAddress().getHostAddress();
		
		user.getUserInfo().setUserPwd(null);
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("uid", user.getId());
		userMap.put("userInfo", JSONObject.fromObject(user.getUserInfo()).toString());
		userMap.put("token", new_token);
		userMap.put("loginTime", String.valueOf(new Date().getTime()));
		userMap.put("loginIP", ip);
		redisTemplate.opsForHash().putAll("user_" + new_token, userMap);
		
		return new DataResult(Errors.OK, new Data(user));
	}
	
	@Pipe("getUserInfo")
	@BarScreen(
		desc="获取用户信息"
	)
	public Errcode getUserInfo (JSONObject params) {
		if (Tools.isStrEmpty(params.optString("token")))
			return new Result(CustomErrors.USER_PARAM_NULL.setArgs("token"));
		
		String key = "user_" + params.getString("token");
		if (!(new RedisUtil().exists(key))) 
			return new Result(CustomErrors.USER_NOT_LOGIN);
		
		Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
		userMap.put("loginTime", Long.valueOf(userMap.get("loginTime").toString()));
		userMap.put("userInfo", JSONObject.fromObject(userMap.get("userInfo")));
		return new DataResult(Errors.OK, new Data(userMap));
	}
	
	@Pipe("logout")
	@BarScreen(
		desc="用户退出"
	)
	public Errcode logout (JSONObject params) {
		String key = "user_" + params.getString("token");
		redisTemplate.delete(key); // 删除缓存
		return new DataResult(Errors.OK);
	}
}