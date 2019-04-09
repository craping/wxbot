package client.view.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.cherry.jeeves.enums.MessageType;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import client.pojo.WxMessage;
import client.pojo.WxMessageBody;

public abstract class BaseServer {
	@Autowired
	protected WechatHttpService wechatService;
	
	@Autowired
	protected CacheService cacheService;
	
	public static ObjectMapper JSON_MAPPER = new ObjectMapper();
	static {
		JSON_MAPPER.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
		JSON_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		JSON_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		JSON_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); 
	}
	
	public static ObjectMapper XML_MAPPER = new XmlMapper();
	
	public static void main(String[] args) throws Exception{
		System.out.println(JSON_MAPPER.writeValueAsString(new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody("案说法"))));
	}
}
