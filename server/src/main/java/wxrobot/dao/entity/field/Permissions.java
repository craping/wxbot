package wxrobot.dao.entity.field;

import lombok.Data;

  
/**  
* @ClassName: Permissions  
* @Description: 用户权限实体类
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
@Data
public class Permissions {
 
	  
	/**  
	* @Fields 僵尸粉检测
	*/  
	    
	private boolean zombieTest;
	
	  
	/**  
	* @Fields 聊天 
	*/  
	    
	private boolean chat;
	
	  
	/**  
	* @Fields 分群关键词
	*/  
	    
	private boolean keyword;
	
	  
	/**  
	* @Fields 全群关键词
	*/  
	    
	private boolean globalKeyword;
	
	  
	/**  
	* @Fields 分群定时消息
	*/  
	    
	private boolean timer;
	
	  
	/**  
	* @Fields 全群定时消息
	*/  
	    
	private boolean globalTimer;
	  
	/**  
	* @Fields 自动接受好友请求 
	*/  
	    
	private boolean acceptFriend;
	
	  
	/**  
	* @Fields 发现新群提示语
	*/  
	    
	private boolean chatRoomFoundTip;
	
	/**  
	* @Fields 成员加入提示语
	*/  
	    
	private boolean memberJoinTip;
	
	  
	/**  
	* @Fields 成员退出提示语
	*/  
	    
	private boolean memberLeftTip;
	
	  
	/**  
	* @Fields 移动端管理
	*/  
	    
	private boolean wapSite;
}