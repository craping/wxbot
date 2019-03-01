package client.view.function;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.pojo.WxUser;

@Component
public class SettingFunction {
	@Autowired
	protected Jeeves jeeves;

	public Thread wxbotThread;

	protected SimpleDateFormat dateFormat = new SimpleDateFormat("M,d,H,m,s");
	
	public String userToken = "6dfb108f262845a1bfec3ef6647c28f7";

	protected WxUser user;
	
	@Autowired
	protected WechatHttpService wechatService;
	
	@Autowired
	protected CacheService cacheService;
	
	protected ObjectMapper jsonMapper = new ObjectMapper();
	{
		jsonMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
	}
}
