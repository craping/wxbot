package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

  
/**  
* @ClassName: Switchs  
* @Description: 状态控制实体类 
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Switchs {
	
	/**  
	* @Fields 自动接受好友请求开关
	*/  
	@JsonProperty
	private boolean autoAcceptFriend;
	
	  
	/**  
	* @Fields 全群关键词回复开关
	*/  
	@JsonProperty
	private boolean globalKeyword;
	
	/**  
	* @Fields 全群定时消息开关
	*/  
	@JsonProperty
	private boolean globalTimer;
}
