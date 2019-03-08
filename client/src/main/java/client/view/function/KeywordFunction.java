package client.view.function;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSObject;

@Component
public class KeywordFunction extends TimerFunction {

	public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> KEY_MAP;

	public KeywordFunction() {
		super();

	}
	
	public void syncKeywords(JSObject syncKeyMap) {
		System.out.println(syncKeyMap);
		try {
			KEY_MAP= jsonMapper.readValue(syncKeyMap.toJSONString(), new TypeReference<ConcurrentHashMap<String, ConcurrentHashMap<String, String>>>() {});
			System.out.println(KeywordFunction.KEY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setKeyMap(String seq, String key, String value){
		if(KEY_MAP != null && KEY_MAP.containsKey(seq))
			KEY_MAP.get(seq).put(key, value);
	}
	
	public void delKeyMap(String seq, String key){
		if(KEY_MAP != null && KEY_MAP.containsKey(seq))
			KEY_MAP.get(seq).remove(key);
	}
	
}
