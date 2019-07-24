package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import wxrobot.server.utils.Tools;

/**
 * 用户操作日志
 * 
 * @author wr
 *
 */
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	
}
