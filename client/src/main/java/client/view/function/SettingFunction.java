package client.view.function;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.Permissions;
import client.pojo.Setting;
import client.pojo.Switchs;
import client.pojo.Tips;
import client.view.WxbotView;
import client.view.server.BaseServer;
import client.view.server.ChatServer;
import javafx.application.Platform;

@Component
public class SettingFunction {
	@Autowired
	protected Jeeves jeeves;

	public Thread wxbotThread;

	protected SimpleDateFormat dateFormat = new SimpleDateFormat("M,d,H,m,s");
	
	protected JSObject user;
	
	@Autowired
	protected WechatHttpService wechatService;
	
	@Autowired
	protected CacheService cacheService;
	
	@Autowired
	protected ChatServer chatServer;
	
	public static Setting SETTING = new Setting();
	static {
		SETTING.setForwards(new ConcurrentLinkedQueue<>());
	}
	
	public static String TURING_KEY;
	
	public void openSetting(String menu){
		Platform.runLater(() -> {
			WxbotView.getInstance().openSetting(menu);
		});
	}
	
	public JSONString getSetting(){
		try {
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(SETTING));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new JSONString("{}");
		}
	}
	
	public void syncSetting(JSObject syncSetting){
		if(syncSetting != null && syncSetting.toJSONString() != null && !syncSetting.toJSONString().isEmpty()){
			try {
				SETTING = BaseServer.JSON_MAPPER.readValue(syncSetting.toJSONString(), Setting.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void syncTuringKey(String key){
		TURING_KEY = key;
	}
	
	public void enableSeq(String module, String seq){
		switch (module) {
		case "turing":
			SETTING.getTuring().add(seq);
			break;
		case "keywords":
			SETTING.getKeywords().add(seq);
			break;
		case "timers":
			SETTING.getTimers().add(seq);
			break;
		case "forwards":
			SETTING.getForwards().add(seq);
			break;
		}
	}
	
	public void disableSeq(String module, String seq){
		switch (module) {
		case "turing":
			SETTING.getTuring().remove(seq);
			break;
		case "keywords":
			SETTING.getKeywords().remove(seq);
			break;
		case "timers":
			SETTING.getTimers().remove(seq);
			break;
		case "forwards":
			SETTING.getForwards().remove(seq);
			break;
		}
	}
	
	public void modSeq(String module, String oldSeq, String newSeq){
		switch (module) {
		case "turing":
			if(SETTING.getTuring().remove(oldSeq))
				SETTING.getTuring().add(newSeq);
			break;
		case "keywords":
			if(SETTING.getKeywords().remove(oldSeq))
				SETTING.getKeywords().add(newSeq);
			break;
		case "timers":
			if(SETTING.getTimers().remove(oldSeq))
				SETTING.getTimers().add(newSeq);
			break;
		case "forwards":
			if(SETTING.getForwards().remove(oldSeq))
				SETTING.getForwards().add(newSeq);
			break;
		}
	}
	
	public void syncSwitchs(JSObject syncSwitchs){
		if(syncSwitchs != null && syncSwitchs.toJSONString() != null && !syncSwitchs.toJSONString().isEmpty()){
			try {
				SETTING.setSwitchs(BaseServer.JSON_MAPPER.readValue(syncSwitchs.toJSONString(), Switchs.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void syncTips(JSObject syncTips){
		if(syncTips != null && syncTips.toJSONString() != null && !syncTips.toJSONString().isEmpty()){
			try {
				SETTING.setTips(BaseServer.JSON_MAPPER.readValue(syncTips.toJSONString(), Tips.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void syncPermissions(JSObject syncPermissions){
		if(syncPermissions != null && syncPermissions.toJSONString() != null && !syncPermissions.toJSONString().isEmpty()){
			try {
				SETTING.setPermissions(BaseServer.JSON_MAPPER.readValue(syncPermissions.toJSONString(), Permissions.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
