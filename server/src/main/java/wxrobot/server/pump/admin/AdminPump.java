package wxrobot.server.pump.admin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.crap.jrain.core.validate.security.component.Coder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import net.sf.json.JSONObject;
import wxrobot.admin.server.AdminServer;
import wxrobot.dao.entity.AdminUser;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.utils.Tools;

@Pump("admin")
@Component
public class AdminPump extends DataPump<JSONObject, FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(AdminPump.class);
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private AdminServer adminServer;
	
	@Pipe("login")
	@BarScreen(
		security=true,
		desc="用户登录",
		params= {
			@Parameter(value="user_name",  desc="登录名"),
			@Parameter(value="user_pwd",  desc="密码"),
		}
	)
	public Errcode login (JSONObject params) {
		String userName = params.getString("user_name");
		String userPwd = params.getString("user_pwd");
		AdminUser user = adminServer.getUser(userName, Coder.encryptMD5(userPwd));
		if (user == null) //判断用户是否存在
			return new Result(CustomErrors.USER_ACC_ERR);
		
		// 删除正在登录中的管理员
		String flag = "admin_user_";
		Set<String> keys = redisTemplate.keys(flag + "*");
		redisTemplate.delete(keys); 
		
		String token = StringUtil.uuid(); 	// 生成新的用户token
		Map<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("uid", user.getId());
		userMap.put("userName", user.getUserName());
		userMap.put("token", token);
		userMap.put("loginTime", Tools.getTimestamp());
		userMap.put("loginIP", ((InetSocketAddress) getResponse().remoteAddress()).getAddress().getHostAddress());
		redisTemplate.opsForHash().putAll(flag+token, userMap);
		
		user.setToken(token);
		user.setUserPwd(null);
		return new DataResult(Errors.OK, new Data(user));
	}
	
	@Pipe("logout")
	@BarScreen(
		desc="用户退出"
	)
	public Errcode logout (JSONObject params) {
		String key = "admin_user_" + params.getString("token");
		redisTemplate.delete(key); // 删除缓存
		return new DataResult(Errors.OK);
	}
	
	@Pipe("userInfo")
	@BarScreen(
		desc="获取用户信息"
	)
	public Errcode userInfo (JSONObject params) {
		String key = "admin_user_" + params.optString("token");
		Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
		if (userMap.isEmpty() || userMap == null)
			return new DataResult(CustomErrors.USER_NOT_LOGIN);
	
		return new DataResult(Errors.OK, new Data(userMap));
	}
}