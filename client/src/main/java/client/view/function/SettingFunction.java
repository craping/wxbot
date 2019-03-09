package client.view.function;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.Setting;
import client.pojo.Switchs;
import client.pojo.Tips;
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
	
	public static Setting SETTING = new Setting();
	static {
		SETTING.setForwards(new ConcurrentLinkedQueue<>());
	}
	
	public void openSetting(){
		Platform.runLater(() -> {
			WxbotView.getInstance().openSetting();
		});
	}
	
	public JSONString getSetting(){
		try {
			return new JSONString(jsonMapper.writeValueAsString(SETTING));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new JSONString("{}");
		}
	}
	
	public void syncSetting(JSObject syncSetting){
		if(syncSetting != null && syncSetting.toJSONString() != null && !syncSetting.toJSONString().isEmpty()){
			try {
				SETTING = jsonMapper.readValue(syncSetting.toJSONString(), Setting.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enableForward(String seq){
		SETTING.getForwards().add(seq);
	}
	
	public void disableForward(String seq){
		SETTING.getForwards().remove(seq);
	}
	
	public void modForward(String oldSeq, String newSeq){
		if(SETTING.getForwards().remove(oldSeq))
			SETTING.getForwards().add(newSeq);
	}
	
	public void syncSwitchs(JSObject syncSwitchs){
		if(syncSwitchs != null && syncSwitchs.toJSONString() != null && !syncSwitchs.toJSONString().isEmpty()){
			try {
				SETTING.setSwitchs(jsonMapper.readValue(syncSwitchs.toJSONString(), Switchs.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void syncTips(JSObject syncTips){
		if(syncTips != null && syncTips.toJSONString() != null && !syncTips.toJSONString().isEmpty()){
			try {
				SETTING.setTips(jsonMapper.readValue(syncTips.toJSONString(), Tips.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
