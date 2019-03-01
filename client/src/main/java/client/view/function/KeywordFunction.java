package client.view.function;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSObject;

@Component
public class KeywordFunction extends TimerFunction {

	public static Map<String, Map<String, String>> keyMap;
	
	public final static String GLOBA_SEQ = "globa";

	public KeywordFunction() {
		super();

	}
	
	public void syncKeywords(JSObject keyMap) {
		System.out.println(keyMap);
		try {
			KeywordFunction.keyMap = jsonMapper.readValue(keyMap.toJSONString(), new TypeReference<Map<String, Map<String, String>>>() {});
			System.out.println(KeywordFunction.keyMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setKeyMap(String seq, String key, String value){
		if(keyMap != null && keyMap.containsKey(seq))
			keyMap.get(seq).put(key, value);
	}
	
	public void delKeyMap(String seq, String key){
		if(keyMap != null && keyMap.containsKey(seq))
			keyMap.get(seq).remove(key);
	}

}
