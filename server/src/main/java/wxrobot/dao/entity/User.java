package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import wxrobot.dao.entity.field.UserInfo;

@Data
@Document(collection = "robot_user")
public class User {

	@Id
	@Field(value = "_id")
	private String id;
	@Field
	private UserInfo userInfo;
	/** 用户tokend */
	@Field
	private String token;
}
