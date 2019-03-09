package client.view.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class BaseServer {
	@Autowired
	protected WechatHttpService wechatService;
	
	@Autowired
	protected CacheService cacheService;
	
	protected ObjectMapper jsonMapper = new ObjectMapper();
	{
		jsonMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
		jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); 
	}
}
