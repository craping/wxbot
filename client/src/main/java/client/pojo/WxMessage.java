package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class WxMessage {
	public String msgId;
	public String timestamp;// 操作时间戳
	public String to; // 接收人的username或者接收group的ID
	public String from; // 发送人username
	public String avatar;//发送者头像
	public int chatType; // 聊天类型，1私聊，2 群聊
	public int direction; // 1 发送 , 2接收，
	public int msgType; // 参考com.cherry.jeeves.enums MessageType.java
	public WxMessageBody body;

	public WxMessage() {
	}

	public WxMessage(String timestamp, String to, String from, int direction, int msgType, WxMessageBody body) {
		this.timestamp = timestamp;
		this.to = to;
		this.from = from;
		this.direction = direction;
		this.msgType = msgType;
		this.body = body;
	}

	public WxMessage(int msgType, WxMessageBody body) {
		this.msgType = msgType;
		this.body = body;
	}

	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public WxMessageBody getBody() {
		return body;
	}

	public void setBody(WxMessageBody body) {
		this.body = body;
	}
	
	
}
