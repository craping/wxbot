package client.pojo;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Setting {
	/**  
	* @Fields 群转发数组[seq]
	*/  
	@JsonProperty
	private ConcurrentLinkedQueue<String> forwards = new ConcurrentLinkedQueue<>();
	
	/**  
	* @Fields 提示语设置
	*/  
	@JsonProperty
	private Tips tips = new Tips();
	
	/**  
	* @Fields 功能开关设置
	*/  
	@JsonProperty
	private Switchs switchs = new Switchs();
	
	/**  
	* @Fields 用户权限
	*/  
	@JsonProperty
	private Permissions permissions = new Permissions();
}
