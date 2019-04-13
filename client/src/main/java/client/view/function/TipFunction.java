package client.view.function;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.Msg;
import client.view.server.BaseServer;

@Component
public class TipFunction extends KeywordFunction {

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, Msg>> TIP_MAP = new ConcurrentHashMap<>();

	public TipFunction() {
		super();
	}
	
	public JSONString getTipMap(String seq){
		ConcurrentHashMap<String, Msg> tipMap = TIP_MAP.get(seq);
		if(tipMap == null)
			return new JSONString("{}");
		try {
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(tipMap));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new JSONString("{}");
		}
	}
	
	public void syncTips(JSObject syncTips) {
		new Thread(() -> {
			try {
				TIP_MAP.clear();
				ConcurrentHashMap<String, ConcurrentHashMap<String, Msg>> tipMap = BaseServer.JSON_MAPPER.readValue(syncTips.toJSONString(), new TypeReference<ConcurrentHashMap<String, ConcurrentHashMap<String, Msg>>>() {});
				tipMap.forEach((k, v) -> {
					v.forEach((key, msg) -> {
						if(msg.getType() != 1){
							System.out.printf("提示语文件[%s]\n", msg.getContent());
							downloadAttach(msg.getContent());
						}
					});
				});
				TIP_MAP.putAll(tipMap);
//				WxbotView.getInstance().executeSettingScript("app.getKeyMap()");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public void setTip(String seq, String tipType, int type, String content){
		ConcurrentHashMap<String, Msg> map = TIP_MAP.get(seq);
		if(type != 1){
			System.out.printf("提示语文件[%s]\n", content);
			downloadAttach(content);
		}
		if(map == null){
			map = new ConcurrentHashMap<>();
			TIP_MAP.put(seq, map);
		}
		map.put(tipType, new Msg(type, content));
//		if(Config.GLOBA_SEQ.equals(seq))
//			WxbotView.getInstance().executeSettingScript("app.getKeyMap()");
	}
	
	public void delTip(String seq, String tipType){
		ConcurrentHashMap<String, Msg> map = TIP_MAP.get(seq);
		if(map != null)
			map.remove(tipType);
		
//		if(Config.GLOBA_SEQ.equals(seq))
//			WxbotView.getInstance().executeSettingScript("app.getKeyMap()");
	}
	
}
