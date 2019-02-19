package client.pojo;

import lombok.Data;

@Data
public class WxMessage {

	public String msgId;	// 消息ID
	public String timestamp;// 消息发送时间
	public String to;		// 接收人的username或者接收group的ID
	public String from;		// 发送人username
	public String sendType; // receive 接收，send 发送
	public String chatType;	// 用来判断单聊还是群聊。chat: 单聊；groupchat: 群聊
	public String msgType; 	// txt文本；img图片，表情；loc位置信息；audio语音；video视频；file文件信息
	public String body; 	// 消息体
}
