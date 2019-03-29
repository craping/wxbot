package wxrobot.server.param;

import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.exception.ValidationException;
import org.crap.jrain.core.validate.support.param.SingleParam;
import org.crap.jrain.core.validate.support.param.StringParam;

import redis.clients.jedis.Jedis;
import wxrobot.biz.server.BaseServer;
import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.utils.RedisUtil;
import wxrobot.server.utils.Tools;

public class TokenParam extends StringParam implements SingleParam {
	
	public TokenParam() {
		this.desc="用户Token";
		this.value = "token";
	}

	
	  
	/* 
	 * 	{token}		PC端token
	*   {token}_m 	_m 后缀手机端token
	*   
	* @param param
	* @return
	* @throws ValidationException  
	* @see org.crap.jrain.core.validate.support.param.StringParam#validateValue(java.lang.Object)  
	*/  
	    
	@Override
	protected Errcode validateValue(Object param) throws ValidationException {
		String token = param.toString();
		if (Tools.isStrEmpty(token))
			return new Result(CustomErrors.USER_PARAM_NULL.setArgs("token"));
		
		String key = "user_" + token.split("_")[0];
		Jedis jedis = null;
		try {
			jedis = RedisUtil.getJedis();
			if (!jedis.exists(key)) {
				return new Result(CustomErrors.USER_NOT_LOGIN);
			}
			
			UserInfo userInfo = BaseServer.JSON_MAPPER.readValue(jedis.hget("key", "userInfo"), UserInfo.class);
			if(Long.valueOf(userInfo.getServerEnd()) <= System.currentTimeMillis())
				return new Result(CustomErrors.USER_SERVER_END);
			
		} catch (Exception e) {
			return new Result(Errors.EXCEPTION_UNKNOW, e.getMessage());
		} finally {
			if (jedis != null)
				jedis.close();
		}
		
		
//		if (!(new RedisUtil().hget(key, "locked").equals("0"))) {
//			return new Result(CustomErrors.USER_LOCKED);
//		}
//		
//		long serverEnd = Long.parseLong(new RedisUtil().hget(key, "serverEnd"));
//		if (Tools.isOverTime(serverEnd, 5)) {
//			return new Result(CustomErrors.USER_SERVER_END);
//		}
		
		return Errors.OK;
	}

	@Override
	protected String cast0(Object param) {
		return param.toString();
	}
}
