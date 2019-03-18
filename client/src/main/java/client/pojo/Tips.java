package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

  
/**  
* @ClassName: Tips  
* @Description: 提示语实体类
* @author Crap  
* @date 2019年3月5日  
*    
*/  
    
@JsonIgnoreProperties(ignoreUnknown=true)
public class Tips {
	
	/**  
	* @Fields 发现新群提示语
	*/  
	@JsonProperty
	private String chatRoomFoundTip;
	
	  
	/**  
	* @Fields 成员加入提示语
	*/  
	@JsonProperty
	private String memberJoinTip;
	
	  
	/**  
	* @Fields 成员退出提示语
	*/  
	@JsonProperty
	private String memberLeftTip;


	public String getChatRoomFoundTip() {
		return chatRoomFoundTip;
	}


	public void setChatRoomFoundTip(String chatRoomFoundTip) {
		this.chatRoomFoundTip = chatRoomFoundTip;
	}


	public String getMemberJoinTip() {
		return memberJoinTip;
	}


	public void setMemberJoinTip(String memberJoinTip) {
		this.memberJoinTip = memberJoinTip;
	}


	public String getMemberLeftTip() {
		return memberLeftTip;
	}


	public void setMemberLeftTip(String memberLeftTip) {
		this.memberLeftTip = memberLeftTip;
	}
	
}
