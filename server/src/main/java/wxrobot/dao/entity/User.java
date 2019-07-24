package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import wxrobot.dao.entity.field.UserInfo;

/**
 * 用户
 * 
 * @author wr
 *
 */
@Document(collection = "robot_user")
public class User {

	@Id
	@Field(value = "_id")
	private String id;
	@Field
	private UserInfo userInfo;
	/** 用户token */
	@Field
	private String token;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}
