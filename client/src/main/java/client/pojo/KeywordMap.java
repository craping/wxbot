package client.pojo;

import java.util.Map;

import lombok.Data;

@Data
public class KeywordMap {
	
	private String seq;
	
	private Map<String, String> keyMap;
}
