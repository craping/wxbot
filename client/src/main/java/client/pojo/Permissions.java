package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

  
/**  
* @ClassName: Permissions  
* @Description: 用户权限实体类
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
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


	public boolean isZombieTest() {
		return zombieTest;
	}


	public void setZombieTest(boolean zombieTest) {
		this.zombieTest = zombieTest;
	}


	public boolean isChat() {
		return chat;
	}


	public void setChat(boolean chat) {
		this.chat = chat;
	}


	public boolean isKeyword() {
		return keyword;
	}


	public void setKeyword(boolean keyword) {
		this.keyword = keyword;
	}


	public boolean isGlobalKeyword() {
		return globalKeyword;
	}


	public void setGlobalKeyword(boolean globalKeyword) {
		this.globalKeyword = globalKeyword;
	}


	public boolean isTimer() {
		return timer;
	}


	public void setTimer(boolean timer) {
		this.timer = timer;
	}


	public boolean isGlobalTimer() {
		return globalTimer;
	}


	public void setGlobalTimer(boolean globalTimer) {
		this.globalTimer = globalTimer;
	}


	public boolean isAcceptFriend() {
		return acceptFriend;
	}


	public void setAcceptFriend(boolean acceptFriend) {
		this.acceptFriend = acceptFriend;
	}


	public boolean isChatRoomFoundTip() {
		return chatRoomFoundTip;
	}


	public void setChatRoomFoundTip(boolean chatRoomFoundTip) {
		this.chatRoomFoundTip = chatRoomFoundTip;
	}


	public boolean isMemberJoinTip() {
		return memberJoinTip;
	}


	public void setMemberJoinTip(boolean memberJoinTip) {
		this.memberJoinTip = memberJoinTip;
	}


	public boolean isMemberLeftTip() {
		return memberLeftTip;
	}


	public void setMemberLeftTip(boolean memberLeftTip) {
		this.memberLeftTip = memberLeftTip;
	}


	public boolean isWapSite() {
		return wapSite;
	}


	public void setWapSite(boolean wapSite) {
		this.wapSite = wapSite;
	}
	
	
}
