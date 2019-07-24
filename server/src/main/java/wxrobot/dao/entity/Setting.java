package wxrobot.dao.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import wxrobot.dao.entity.field.Permissions;
import wxrobot.dao.entity.field.Switchs;

@Document(collection = "robot_setting")
@JsonInclude(Include.NON_NULL)
public class Setting {
	
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field
	private String userName;
	
	/**  
	* @Fields 图灵机器人数组[seq]
	*/  
	@Field
	private List<String> turing;
	
	/**  
	* @Fields 关键词数组[seq]
	*/  
	@Field
	private List<String> keywords;
	
	/**  
	* @Fields 定时消息数组[seq]
	*/  
	@Field
	private List<String> timers;
	/**  
	* @Fields 提示语数组[seq]
	*/  
	@Field
	private List<String> tips;
	/**  
	* @Fields 群转发数组[seq]
	*/  
	@Field
	private List<String> forwards;
	/**  
	* @Fields 功能开关设置
	*/  
	@Field
	private Switchs switchs;
	
	/**  
	* @Fields 用户权限
	*/  
	@Field
	private Permissions permissions;

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

	public List<String> getTuring() {
		return turing;
	}

	public void setTuring(List<String> turing) {
		this.turing = turing;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getTimers() {
		return timers;
	}

	public void setTimers(List<String> timers) {
		this.timers = timers;
	}

	public List<String> getTips() {
		return tips;
	}

	public void setTips(List<String> tips) {
		this.tips = tips;
	}

	public List<String> getForwards() {
		return forwards;
	}

	public void setForwards(List<String> forwards) {
		this.forwards = forwards;
	}

	public Switchs getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switchs switchs) {
		this.switchs = switchs;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
	
	
}
