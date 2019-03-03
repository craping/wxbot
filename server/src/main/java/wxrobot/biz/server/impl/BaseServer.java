package wxrobot.biz.server.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.crap.jrain.core.bean.result.criteria.Data;
import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
	
	public DataResult findPage(Page page, Query query, Class<?> clazz) {
		DataResult dataResult = new DataResult(Errors.EXCEPTION_UNKNOW);
		query = query == null ? new Query(Criteria.where("_id").exists(true)) : query;
		long count = mongoTemplate.count(query, clazz);
		page.setTotalnum((int) count);
		
		int currentPage = page.getPage();
		int pageSize = page.getNum();
		
		query.skip((currentPage - 1) * pageSize).limit(pageSize);
		List<?> rows = mongoTemplate.find(query, clazz);
		
		dataResult.setErrcode(Errors.OK);
		dataResult.setData(new Data(rows, page));
		return dataResult;
	}
}
