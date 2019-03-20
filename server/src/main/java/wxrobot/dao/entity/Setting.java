package wxrobot.dao.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import wxrobot.dao.entity.field.Permissions;
import wxrobot.dao.entity.field.Switchs;
import wxrobot.dao.entity.field.Tips;

@Data
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
	* @Fields 群转发数组[seq]
	*/  
	@Field
	private List<String> forwards;
	
	/**  
	* @Fields 提示语设置
	*/  
	@Field
	private Tips tips;
	
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
}
