package client.view.function;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.Setting;
import client.pojo.Switchs;
import client.pojo.WxUser;
import client.view.WxbotView;
import javafx.application.Platform;

@Component
public class SettingFunction {
	@Autowired
	protected Jeeves jeeves;

	public Thread wxbotThread;

	protected SimpleDateFormat dateFormat = new SimpleDateFormat("M,d,H,m,s");
	
	protected WxUser user;
	
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
	
	public static Setting SETTING;
	
	public void openSetting(){
		Platform.runLater(() -> {
			WxbotView.getInstance().setting();
		});
	}
	
	public void syncSetting(JSObject syncSetting){
		try {
			SETTING = jsonMapper.readValue(syncSetting.toJSONString(), Setting.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void enableForward(String seq){
		if(SETTING != null){
			SETTING.getForwards().add(seq);
		}
	}
	
	public void disableForward(String seq){
		if(SETTING != null){
			SETTING.getForwards().remove(seq);
		}
	}
	
	public void modForward(String oldSeq, String newSeq){
		if(SETTING != null){
			if(SETTING.getForwards().remove(oldSeq))
				SETTING.getForwards().add(newSeq);
		}
	}
	
	public void syncSwitchs(JSObject syncSwitchs){
		if(SETTING != null){
			try {
				SETTING.setSwitchs(jsonMapper.readValue(syncSwitchs.toJSONString(), Switchs.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
