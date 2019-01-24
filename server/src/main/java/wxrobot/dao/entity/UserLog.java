package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import wxrobot.server.utils.Tools;

/**
 * 用户操作日志
 * 
 * @author wr
 *
 */
@Data
@Document(collection = "robot_user_log")
public class UserLog {

	@Id
	@Field(value = "_id")
	public String id;
	@Field
	public String uid;
	@Field
	public String createTime = Tools.getTimestamp();
	@Field
	public int type;
	@Field
	public String msg;

}
