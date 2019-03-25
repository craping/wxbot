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
public class KeywordFunction extends TimerFunction {

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, Msg>> KEY_MAP = new ConcurrentHashMap<>();

	public KeywordFunction() {
		super();

	}
	
	public JSONString getKeyMap(String seq){
		ConcurrentHashMap<String, Msg> keyMap = KEY_MAP.get(seq);
		if(keyMap == null)
			return new JSONString("{}");
		try {
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(keyMap));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new JSONString("{}");
		}
	}
	
	public void syncKeywords(JSObject syncKeyMap) {
		try {
			KEY_MAP.clear();
			ConcurrentHashMap<String, ConcurrentHashMap<String, Msg>> keyMap = BaseServer.JSON_MAPPER.readValue(syncKeyMap.toJSONString(), new TypeReference<ConcurrentHashMap<String, ConcurrentHashMap<String, Msg>>>() {});
			keyMap.forEach((k, v) -> {
				v.forEach((key, msg) -> {
					if(msg.getType() != 1){
						System.out.printf("关键词文件[%s]\n", msg.getContent());
						downloadAttach(msg.getContent());
					}
				});
			});
			KEY_MAP.putAll(keyMap);
			System.out.println(KeywordFunction.KEY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setKeyMap(String seq, String key, int type, String content){
		ConcurrentHashMap<String, Msg> map = KEY_MAP.get(seq);
		if(type != 1){
			System.out.printf("关键词文件[%s]\n", content);
			downloadAttach(content);
		}
		if(map == null){
			map = new ConcurrentHashMap<>();
			KEY_MAP.put(seq, map);
		}
		map.put(key, new Msg(type, content));
	}
	
	public void delKeyMap(String seq, String key){
		ConcurrentHashMap<String, Msg> map = KEY_MAP.get(seq);
		if(map != null)
			map.remove(key);
	}
	
}
