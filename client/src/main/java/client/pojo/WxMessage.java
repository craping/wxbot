package client.pojo;

import lombok.Data;

@Data
public class WxMessage {

	public String timestamp;// 操作时间戳
	public String to; // 接收人的username或者接收group的ID
	public String from; // 发送人username
	public int direction; // 2接收，1 发送
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
}
