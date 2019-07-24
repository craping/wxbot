package wxrobot.dao.entity.field;

/**  
* @ClassName: Switchs  
* @Description: 状态控制实体类 
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
public class Switchs {
	
	/**  
	* @Fields 自动接受好友请求开关
	*/  
	private Boolean autoAcceptFriend;
	
	  
	/**  
	* @Fields 全群关键词回复开关
	*/  
	private Boolean globalKeyword;
	
	  
	/**  
	* @Fields 全群定时消息开关
	*/  
	private Boolean globalTimer;


	public Boolean getAutoAcceptFriend() {
		return autoAcceptFriend;
	}


	public void setAutoAcceptFriend(Boolean autoAcceptFriend) {
		this.autoAcceptFriend = autoAcceptFriend;
	}


	public Boolean getGlobalKeyword() {
		return globalKeyword;
	}


	public void setGlobalKeyword(Boolean globalKeyword) {
		this.globalKeyword = globalKeyword;
	}


	public Boolean getGlobalTimer() {
		return globalTimer;
	}


	public void setGlobalTimer(Boolean globalTimer) {
		this.globalTimer = globalTimer;
	}
	
	
}
