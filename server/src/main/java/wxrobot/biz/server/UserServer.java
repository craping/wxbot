package wxrobot.biz.server;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.crap.jrain.core.ErrcodeException;
import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.security.component.Coder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.core.type.TypeReference;

import wxrobot.dao.entity.User;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.utils.RedisUtil;

@Service
public class UserServer extends BaseServer{
	
	private static final String ACCESS_KEY_ID = "LTAIJQ9bRIRGPUR6";
	private static final String ACCESS_SECRET = "ucYH1qwFZVDz917DisxI4YDz6jsVyB";
	
	public boolean exist(String userName) {
		// 判断用户名是否已存在
		Query query = new Query(Criteria.where("userInfo.userName").is(userName));
		return mongoTemplate.count(query, User.class) > 0;
	}
	
	/**
	 * 新增用户
	 * @param user
	 * @return
	 * @throws ErrcodeException
	 */
	public User insert(User user) throws ErrcodeException {
		String userName = user.getUserInfo().getUserName();
		// 判断用户名是否已存在
		Query query = new Query(Criteria.where("userInfo.userName").is(userName));
		if (mongoTemplate.count(query, User.class) > 0)
			throw new ErrcodeException(CustomErrors.USER_EXIST_ERR.setArgs(userName));
		return mongoTemplate.insert(user);
	}

	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User find(String id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), User.class);
	}
	
	/**
	 * 判断用户是否存在 不存在则报错
	 * @param id
	 * @return User.class
	 * @throws ErrcodeException
	 */
	public User checkUserExist(String id) throws ErrcodeException {
		// 判断用户名是否已存在
		User user = find(id);
		if (user == null)
			throw new ErrcodeException(CustomErrors.USER_NOT_EXIST);
		return user;
	}

	/**
	 * 获取用户
	 * @param userName
	 * @param userPwd
	 * @return
	 */
	public User getUser(String userName, String userPwd) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userInfo.userName").is(userName).and("userInfo.userPwd").is(userPwd));
		return mongoTemplate.findOne(query, User.class);
	}

	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateToken(User user) {		
		try {
			Query query = new Query(Criteria.where("id").is(user.getId()));
			Update update = Update.update("token", user.getToken());
			return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	  
	/**  
	* @Title: sendRegCode  
	* @Description: 注册验证码
	* @param @param userName
	* @param @return    参数  
	* @return Errcode    返回类型  
	* @throws  
	*/  
	    
	public Errcode sendRegCode(String userName){
		String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", userName);
        request.putQueryParameter("SignName", "微信机器人");
        request.putQueryParameter("TemplateCode", "SMS_167300168");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+verifyCode+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String, String> data = JSON_MAPPER.readValue(response.getData(), new TypeReference<Map<String, String>>() {});
			if (!"OK".equals(data.get("Code")))
				return new Result(CustomErrors.USER_CODE_FAIL, data.get("Message"));
		} catch (ClientException | IOException e) {
			e.printStackTrace();
			return new Result(CustomErrors.USER_CODE_FAIL);
		}
        RedisUtil.set("code_reg_" + userName, verifyCode);
        RedisUtil.expire("code_reg_" + userName, 900);
		return new Result(Errors.OK);
	}
	
	public String getRegCode(String userName){
		return RedisUtil.get("code_reg_"+userName);
	}
	
	/**
	 * 重置用户密码
	 * @param user
	 * @return
	 */
	public String resetPwd(String userName) {
		String pwd = String.valueOf(new Random().nextInt(899999) + 100000);
		try {
			Query query = new Query(Criteria.where("userInfo.userName").is(userName));
			Update update = Update.update("userInfo.userPwd", Coder.encryptMD5(pwd).toLowerCase());
			long count = mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
			if(count > 0){
				RedisUtil.del("code_reset_" + userName);
				return pwd;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	  
	/**  
	* @Title: sendResetCode  
	* @Description: 重置密码验证码
	* @param @param userName
	* @param @return    参数  
	* @return Errcode    返回类型  
	* @throws  
	*/  
	    
	public Errcode sendResetCode(String userName){
		String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", userName);
        request.putQueryParameter("SignName", "微信机器人");
        request.putQueryParameter("TemplateCode", "SMS_167300166");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+verifyCode+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String, String> data = JSON_MAPPER.readValue(response.getData(), new TypeReference<Map<String, String>>() {});
			if (!"OK".equals(data.get("Code")))
				return new Result(CustomErrors.USER_CODE_FAIL, data.get("Message"));
		} catch (ClientException | IOException e) {
			e.printStackTrace();
			return new Result(CustomErrors.USER_CODE_FAIL);
		}
        RedisUtil.set("code_reset_" + userName, verifyCode);
        RedisUtil.expire("code_reset_" + userName, 900);
		return new Result(Errors.OK);
	}
	
	public String getResetCode(String userName){
		return RedisUtil.get("code_reset_"+userName);
	}
	
	
	/**
	 * 修改密码
	 * @param user
	 * @return
	 */
	public long changePwd(String userName, String pwd) {
		try {
			Query query = new Query(Criteria.where("userInfo.userName").is(userName));
			Update update = Update.update("userInfo.userPwd", pwd);
			return mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
