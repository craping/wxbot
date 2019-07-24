package wxrobot.dao.entity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import wxrobot.dao.entity.field.Msg;

@Document(collection = "robot_keyword")
@JsonInclude(Include.NON_NULL)
public class Keyword {
	
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field
	private String userName;
	
	@Field
	private Map<String, Map<String, Msg>> keyMap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Map<String, Map<String, Msg>> getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(Map<String, Map<String, Msg>> keyMap) {
		this.keyMap = keyMap;
	}
	
	
}
