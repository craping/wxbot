package wxrobot.server.pump;

import java.net.InetSocketAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.asm.annotation.Pipe;
import org.crap.jrain.core.asm.annotation.Pump;
import org.crap.jrain.core.asm.handler.DataPump;
import org.crap.jrain.core.bean.DateFormat;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.util.DateUtil;
import org.crap.jrain.core.util.StringUtil;
import org.crap.jrain.core.validate.annotation.BarScreen;
import org.crap.jrain.core.validate.annotation.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import wxrobot.biz.server.NoticeServer;
import wxrobot.biz.server.SettingServer;
import wxrobot.biz.server.UserServer;
import wxrobot.dao.entity.User;
import wxrobot.dao.entity.field.Permissions;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.MobileParam;
import wxrobot.server.param.TokenParam;
import wxrobot.server.utils.RedisUtil;
import wxrobot.server.utils.Tools;

@Pump("user")
@Component
public class UserPump extends DataPump<FullHttpRequest, Channel> {
	
	public static final Logger log = LogManager.getLogger(UserPump.class);
	
	@Autowired
	private UserServer userServer;
	@Autowired
	private SettingServer settingServer;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private NoticeServer noticeServer;
	
	@Pipe("noticeList")
	@BarScreen(
		desc="公告列表",
		params= {
			@Parameter(type=TokenParam.class),
		}
	)
	public Errcode noticeList(JSONObject params) {
		return noticeServer.getNoticeList(params);
	}
	
	@Pipe("registerCode")
	@BarScreen(
		desc="注册验证码",
		params= {
			@Parameter(value="user_name",  desc="登录名", type=MobileParam.class)
		}
	)
	public Errcode registerCode(JSONObject params) {
		String userName = params.getString("user_name");
		if(userServer.exist(userName))
			return new Result(CustomErrors.USER_EXIST_ERR.setArgs(userName));
		return userServer.sendRegCode(userName);
	}
	
	@Pipe("register")
	@BarScreen(
		desc="用户注册",
		security=true,
		params= {
			@Parameter(value="user_name",  desc="登录名", type=MobileParam.class),
			@Parameter(value="user_pwd",  desc="密码"),
			@Parameter(value="confirm_pwd",  desc="确认密码"),
			@Parameter(value="code",  desc="手机验证码")
		}
	)
	public Errcode register (JSONObject params) throws ErrcodeException {
		String userPwd = params.getString("user_pwd");
		String userName = params.getString("user_name");
		String code = params.getString("code");
		
		if(userServer.exist(userName))
			return new Result(CustomErrors.USER_EXIST_ERR.setArgs(userName));
		
		if (!userPwd.equals(params.getString("confirm_pwd")))
			throw new ErrcodeException(CustomErrors.USER_CHANGE_PWD_ERR);
		
		String verifyCode = userServer.getRegCode(userName);
		if(verifyCode == null || !verifyCode.equals(code))
			return new Result(CustomErrors.USER_CODE_ERR);
		
		User user = new User();
		UserInfo userInfo = new UserInfo(userName, userPwd, params.getString("user_name"));
		// 新用户注册免费试用天数
		userInfo.setServerEnd(Tools.dateToStamp(DateUtil.getAmountDate(new Date(), Calendar.DATE, 2, DateFormat.Y_M_D$H_M_S)));
		user.setUserInfo(userInfo);
		userServer.insert(user);
		
		// 初始化用户权限设置
		settingServer.initSetting(userInfo.getUserName(), new Permissions());
		return new DataResult(Errors.OK);
	}
	
	@Pipe("resetCode")
	@BarScreen(
		desc="重置密码验证码",
		params= {
			@Parameter(value="user_name",  desc="登录名", type=MobileParam.class)
		}
	)
	public Errcode resetCode(JSONObject params) throws ErrcodeException {
		String userName = params.getString("user_name");
		if(!userServer.exist(userName))
			return new Result(CustomErrors.USER_NOT_EXIST);
		return userServer.sendResetCode(userName);
	}
	
	
	@Pipe("resetPwd")
	@BarScreen(
		desc="重置密码",
		params= {
			@Parameter(value="user_name",  desc="登录名", type=MobileParam.class),
			@Parameter(value="code",  desc="手机验证码")
		}
	)
	public Errcode resetPwd(JSONObject params) throws ErrcodeException  {
		String userName = params.getString("user_name");
		String code = params.getString("code");
		
		if(!userServer.exist(userName))
			return new Result(CustomErrors.USER_NOT_EXIST);
		
		String verifyCode = userServer.getResetCode(userName);
		if(verifyCode == null || !verifyCode.equals(code))
			return new Result(CustomErrors.USER_CODE_ERR);
		
		String newPwd = userServer.resetPwd(userName);
		if(newPwd != null)
			return new DataResult(Errors.OK, new Data(newPwd));
		return new Result(CustomErrors.USER_OPR_ERR);
	}
	
	
	@Pipe("changePwd")
	@BarScreen(
		desc="修改密码",
		security=true,
		params= {
			@Parameter(type=TokenParam.class),
			@Parameter(value="old_pwd",  desc="旧密码"),
			@Parameter(value="new_pwd",  desc="新密码")
		}
	)
	public Errcode changePwd(JSONObject params) throws ErrcodeException {
		UserInfo userInfo = userServer.getUserInfo(params);
		
		String oldPwd = params.getString("old_pwd");
		String newPwd = params.getString("new_pwd");
		
		User user = userServer.getUser(userInfo.getUserName(), oldPwd);
		if (user == null)
			throw new ErrcodeException(CustomErrors.USER_PWD_ERR);
		
		if(userServer.changePwd(userInfo.getUserName(), newPwd) > 0)
			return new Result(Errors.OK);
		return new Result(CustomErrors.USER_OPR_ERR);
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
		// 判断用户是否存在
		if (user == null) 
			return new Result(CustomErrors.USER_ACC_ERR);
		// 已登录 则删除当前登录状态，和所有队列的通知消息
		if (!Tools.isStrEmpty(user.getToken())) {
			redisTemplate.delete("user_" + user.getToken()); 
			redisTemplate.delete("queue_" + user.getToken()); 
			redisTemplate.delete("queue_" + user.getToken() + "_m"); 
		}
		// 已注销，不可登录
		if (user.getUserInfo().getDestroy()) 
			return new Result(CustomErrors.USER_DESTROY);
		// 用户服务状态已过期
		if (Tools.isOverTime(Long.valueOf(user.getUserInfo().getServerEnd()), 1))
			return new Result(CustomErrors.USER_SERVER_END);
			
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
		userMap.put("userInfo", JSON.toJSONString(user.getUserInfo()));
		userMap.put("token", new_token);
		userMap.put("loginTime", String.valueOf(new Date().getTime()));
		userMap.put("loginIP", ip);
		redisTemplate.opsForHash().putAll("user_" + new_token, userMap);
		
		return new DataResult(Errors.OK, new Data(user));
	}
	
	@Pipe("getUserInfo")
	@BarScreen(
		desc="获取用户信息",
		params = {
			@Parameter(type=TokenParam.class)
		}
	)
	public Errcode getUserInfo (JSONObject params) {
		if (Tools.isStrEmpty(params.getString("token")))
			return new Result(CustomErrors.USER_PARAM_NULL.setArgs("token"));
		
		String key = "user_" + params.getString("token").split("_")[0];
		if (!(RedisUtil.exists(key))) 
			return new Result(CustomErrors.USER_NOT_LOGIN);
		
		Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
		userMap.put("loginTime", Long.valueOf(userMap.get("loginTime").toString()));
		userMap.put("userInfo", JSON.toJSON(userMap.get("userInfo")));
		return new DataResult(Errors.OK, new Data(userMap));
	}
	
	@Pipe("logout")
	@BarScreen(
		desc="用户退出",
		params = {
			@Parameter(type=TokenParam.class)
		}
	)
	public Errcode logout (JSONObject params) {
		String key = "user_" + params.getString("token").split("_")[0];
		redisTemplate.delete(key); // 删除缓存
		return new DataResult(Errors.OK);
	}
}