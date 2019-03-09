package client.pojo;

import java.security.Permissions;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Setting {
	/**  
	* @Fields 群转发数组[seq]
	*/  
	private ConcurrentLinkedQueue<String> forwards;
	
	/**  
	* @Fields 提示语设置
	*/  
	private Tips tips;
	
	/**  
	* @Fields 功能开关设置
	*/  
	private Switchs switchs;
	
	/**  
	* @Fields 用户权限
	*/  
	private Permissions permissions;
}
