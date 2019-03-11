package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

  
/**  
* @ClassName: Permissions  
* @Description: 用户权限实体类
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class Permissions {
 
	  
	/**  
	* @Fields 僵尸粉检测
	*/  
	@JsonProperty
	private boolean zombieTest;
	
	  
	/**  
	* @Fields 聊天 
	*/  
	@JsonProperty
	private boolean chat;
	
	  
	/**  
	* @Fields 分群关键词
	*/  
	@JsonProperty
	private boolean keyword;
	
	  
	/**  
	* @Fields 全群关键词
	*/  
	@JsonProperty
	private boolean globalKeyword;
	
	  
	/**  
	* @Fields 分群定时消息
	*/  
	@JsonProperty
	private boolean timer;
	
	  
	/**  
	* @Fields 全群定时消息
	*/  
	@JsonProperty
	private boolean globalTimer;
	  
	/**  
	* @Fields 自动接受好友请求 
	*/  
	@JsonProperty
	private boolean acceptFriend;
	
	  
	/**  
	* @Fields 发现新群提示语
	*/  
	@JsonProperty
	private boolean chatRoomFoundTip;
	
	/**  
	* @Fields 成员加入提示语
	*/  
	@JsonProperty
	private boolean memberJoinTip;
	
	  
	/**  
	* @Fields 成员退出提示语
	*/  
	@JsonProperty
	private boolean memberLeftTip;
	
	  
	/**  
	* @Fields 移动端管理
	*/  
	@JsonProperty
	private boolean wapSite;
}
