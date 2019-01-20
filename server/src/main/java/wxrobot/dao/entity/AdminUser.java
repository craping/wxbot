package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "robot_admin")
public class AdminUser {

	@Id
	@Field(value = "_id")
	private String id;
	@Field
	private String userName;
	@Field
	private String userPwd;
	@Field
	private String token;
}
