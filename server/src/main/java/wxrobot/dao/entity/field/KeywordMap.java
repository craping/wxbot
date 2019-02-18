package wxrobot.dao.entity.field;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class KeywordMap {
	
	@Field(value = "id")
	private String id;
	
	@Field
	private String name;
	
	@Field
	private Map<String, String> keyMap;
}
