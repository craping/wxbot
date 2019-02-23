package client.view.function;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSArray;

import client.pojo.KeywordMap;

@Component
public class KeywordFunction extends ChatFunction {

	public static List<KeywordMap> keyMaps;
	
	public final static String GLOBA_SEQ = "globa";

	public KeywordFunction() {
		super();

	}
	
	public void syncKeywords(JSArray keyMaps) {
		System.out.println(keyMaps);
		try {
			KeywordFunction.keyMaps = jsonMapper.readValue(keyMaps.toJSONString(), new TypeReference<List<KeywordMap>>() {});
			System.out.println(KeywordFunction.keyMaps);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setKeyMap(String seq, Map<String, String> setkeyMap){
		KeywordMap keyMap = KeywordFunction.keyMaps.stream().filter(x -> x.getSeq().equals(seq)).findFirst().orElse(null);
		if(keyMap != null)
			keyMap.getKeyMap().putAll(setkeyMap);
	}
	
	public void delKeyMap(String seq, String key){
		KeywordMap keyMap = KeywordFunction.keyMaps.stream().filter(x -> x.getSeq().equals(seq)).findFirst().orElse(null);
		if(keyMap != null)
			keyMap.getKeyMap().remove(key);
	}

}
