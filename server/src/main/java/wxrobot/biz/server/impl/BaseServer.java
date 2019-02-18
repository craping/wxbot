package wxrobot.biz.server.impl;

import java.io.IOException;
import java.util.Map;

import org.crap.jrain.core.validate.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import wxrobot.dao.entity.field.UserInfo;
import wxrobot.server.enums.CustomErrors;
import wxrobot.server.param.TokenParam;
import wxrobot.server.utils.RedisUtil;

@Service
public class BaseServer {
	
	public static ObjectMapper JSON_MAPPER = new ObjectMapper();
	static{
		JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		JSON_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		JSON_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		JSON_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	@Autowired
	protected RedisUtil redisUtil;
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
	protected StringRedisTemplate redisTemplate;
	
	
	public UserInfo getUserInfo(Map<?, ?> params) throws ValidationException{
		if(!params.containsKey(new TokenParam().getValue()))
			throw new ValidationException(CustomErrors.USER_TOKEN_NULL);
		String key = "user_"+params.get("token").toString();
		String userInfoJson = redisUtil.hget(key, "userInfo");
		
		try {
			return JSON_MAPPER.readValue(userInfoJson, UserInfo.class);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ValidationException(CustomErrors.USER_NOT_LOGIN);
		}
	}
}
