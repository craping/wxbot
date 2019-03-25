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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tips {

	/**
	 * @Fields 发现新群提示语
	 */
	@JsonProperty
	private Msg chatRoomFoundTip;

	/**
	 * @Fields 成员加入提示语
	 */
	@JsonProperty
	private Msg memberJoinTip;

	/**
	 * @Fields 成员退出提示语
	 */
	@JsonProperty
	private Msg memberLeftTip;

	public Msg getChatRoomFoundTip() {
		return chatRoomFoundTip;
	}

	public void setChatRoomFoundTip(Msg chatRoomFoundTip) {
		this.chatRoomFoundTip = chatRoomFoundTip;
	}

	public Msg getMemberJoinTip() {
		return memberJoinTip;
	}

	public void setMemberJoinTip(Msg memberJoinTip) {
		this.memberJoinTip = memberJoinTip;
	}

	public Msg getMemberLeftTip() {
		return memberLeftTip;
	}

	public void setMemberLeftTip(Msg memberLeftTip) {
		this.memberLeftTip = memberLeftTip;
	}

}
