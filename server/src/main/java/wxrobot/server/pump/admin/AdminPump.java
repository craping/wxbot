package wxrobot.server.pump.admin;

import java.util.Map;

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
		
		String flag = "admin_user_";
		String old_token = user.getToken(); 	// 获取上一次用户token
		String new_token = StringUtil.uuid(); 	// 生成新的用户token
	
		Map<Object, Object> userMap = redisTemplate.opsForHash().entries(flag + old_token);
		if (userMap == null || userMap.isEmpty()) {
			userMap.put("uid", user.getId().toString());
			userMap.put("user_name", user.getUserName());
		} else {
			redisTemplate.delete(flag+old_token);
		}
		
		// 保存用户token 持久化
		user.setToken(new_token);
		int result = 1;//userServer.updateAdminUser(user.getId(), new_token);
		if (result == 1) {
			redisTemplate.opsForHash().putAll(flag+new_token, userMap);
		} else {
			return new Result(CustomErrors.USER_LOGIN_ERR_EX);
		}
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
		userMap.remove("user_pwd");
		if (userMap.isEmpty() || userMap == null)
			return new DataResult(CustomErrors.USER_NOT_LOGIN);
		
		return new DataResult(Errors.OK, new Data(userMap));
	}
}