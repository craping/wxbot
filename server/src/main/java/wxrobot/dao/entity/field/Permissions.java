package wxrobot.dao.entity.field;

/**  
* @ClassName= Permissions  
* @Description= 用户权限实体类
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
public class Permissions {
	
	public Permissions() {
		this.zombieTest= true;
		this.chat= true;
		this.turing = true;
		this.forward = true;
		this.keyword= true;
		this.globalKeyword= true;
		this.timer= true;
		this.globalTimer= true;
		this.acceptFriend= true;
		this.chatRoomFoundTip= true;
		this.memberJoinTip= true;
		this.memberLeftTip= true;
		this.wapSite= true;
	}
 
	  
	/**  
	* @Fields 僵尸粉检测
	*/  
	    
	private Boolean zombieTest;
	
	  
	/**  
	* @Fields 聊天 
	*/  
	    
	private Boolean chat;
	
	/**  
	* @Fields 图灵
	*/  
	    
	private Boolean turing;
	
	/**  
	* @Fields 群转发
	*/  
	    
	private Boolean forward;
	
	/**  
	* @Fields 分群关键词
	*/  
	    
	private Boolean keyword;
	
	  
	/**  
	* @Fields 全群关键词
	*/  
	    
	private Boolean globalKeyword;
	
	  
	/**  
	* @Fields 分群定时消息
	*/  
	    
	private Boolean timer;
	
	  
	/**  
	* @Fields 全群定时消息
	*/  
	    
	private Boolean globalTimer;
	  
	/**  
	* @Fields 自动接受好友请求 
	*/  
	    
	private Boolean acceptFriend;
	
	  
	/**  
	* @Fields 发现新群提示语
	*/  
	    
	private Boolean chatRoomFoundTip;
	
	/**  
	* @Fields 成员加入提示语
	*/  
	    
	private Boolean memberJoinTip;
	
	  
	/**  
	* @Fields 成员退出提示语
	*/  
	    
	private Boolean memberLeftTip;
	
	  
	/**  
	* @Fields 移动端管理
	*/  
	    
	private Boolean wapSite;


	public Boolean getZombieTest() {
		return zombieTest;
	}


	public void setZombieTest(Boolean zombieTest) {
		this.zombieTest = zombieTest;
	}


	public Boolean getChat() {
		return chat;
	}


	public void setChat(Boolean chat) {
		this.chat = chat;
	}


	public Boolean getTuring() {
		return turing;
	}


	public void setTuring(Boolean turing) {
		this.turing = turing;
	}


	public Boolean getForward() {
		return forward;
	}


	public void setForward(Boolean forward) {
		this.forward = forward;
	}


	public Boolean getKeyword() {
		return keyword;
	}


	public void setKeyword(Boolean keyword) {
		this.keyword = keyword;
	}


	public Boolean getGlobalKeyword() {
		return globalKeyword;
	}


	public void setGlobalKeyword(Boolean globalKeyword) {
		this.globalKeyword = globalKeyword;
	}


	public Boolean getTimer() {
		return timer;
	}


	public void setTimer(Boolean timer) {
		this.timer = timer;
	}


	public Boolean getGlobalTimer() {
		return globalTimer;
	}


	public void setGlobalTimer(Boolean globalTimer) {
		this.globalTimer = globalTimer;
	}


	public Boolean getAcceptFriend() {
		return acceptFriend;
	}


	public void setAcceptFriend(Boolean acceptFriend) {
		this.acceptFriend = acceptFriend;
	}


	public Boolean getChatRoomFoundTip() {
		return chatRoomFoundTip;
	}


	public void setChatRoomFoundTip(Boolean chatRoomFoundTip) {
		this.chatRoomFoundTip = chatRoomFoundTip;
	}


	public Boolean getMemberJoinTip() {
		return memberJoinTip;
	}


	public void setMemberJoinTip(Boolean memberJoinTip) {
		this.memberJoinTip = memberJoinTip;
	}


	public Boolean getMemberLeftTip() {
		return memberLeftTip;
	}


	public void setMemberLeftTip(Boolean memberLeftTip) {
		this.memberLeftTip = memberLeftTip;
	}


	public Boolean getWapSite() {
		return wapSite;
	}


	public void setWapSite(Boolean wapSite) {
		this.wapSite = wapSite;
	}
	
	
}
