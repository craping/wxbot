package client.utils;

import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.Owner;
import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.enums.ChatType;
import client.enums.Direction;
import client.pojo.WxMessage;
import client.view.WxbotView;

/**
 * 
 * 微信消息处理工具类
 * 
 * @author wr
 *
 */
public class WxMessageTool {
	
	private static ObjectMapper jsonMapper = new ObjectMapper();
	{
		jsonMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
	}

	/**
	 * 处理私聊消息
	 * 
	 * @param sender
	 * @param owner
	 * @param message
	 * @param msg
	 */
	public static void receiveMessage(Contact sender, Owner owner, WxMessage msg) {
		String seq = sender.getSeq();
		String timestamp = Tools.getTimestamp();
		msg.setChatType(ChatType.CHAT.getCode());
		msg.setTimestamp(timestamp);
		msg.setTo(owner.getNickName());
		msg.setFrom(sender.getNickName());
		msg.setDirection(Direction.RECEIVE.getCode());
		String jsonStr = "";
		try {
			jsonStr = jsonMapper.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String path = Config.CHAT_RECORD_PATH + seq;
		FileUtil.writeFile(path, Tools.getSysDate() + ".txt", jsonStr);
		avatarBadge(seq);
		// 处理语音消息
		if (msg.getMsgType() == MessageType.VOICE.getCode()) {
			newVoiceMessage(timestamp);
		}
	}

	/**
	 * 处理群聊消息
	 * 
	 * @param sender
	 * @param owner
	 * @param message
	 * @param msg
	 */
	public static void receiveGroupMessage(Contact chatRoom, Contact sender, WxMessage msg) {
		String seq = chatRoom.getSeq();
		String timestamp = Tools.getTimestamp();
		msg.setChatType(ChatType.GROUPCHAT.getCode());
		msg.setTimestamp(timestamp);
		msg.setTo(chatRoom.getNickName());
		msg.setFrom(sender.getNickName());
		msg.setDirection(Direction.RECEIVE.getCode());
		String jsonStr = "";
		try {
			jsonStr = jsonMapper.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String path = Config.CHAT_RECORD_PATH + seq;
		FileUtil.writeFile(path, Tools.getSysDate() + ".txt", jsonStr);
		avatarBadge(seq);
		// 处理语音消息
		if (msg.getMsgType() == MessageType.VOICE.getCode()) {
			newVoiceMessage(timestamp);
		}
	}

	/**
	 * 处理新消息，头像加消息提醒
	 * 
	 * @param seq
	 */
	public static void avatarBadge(String seq) {
		WxbotView wxbotView = WxbotView.getInstance();
		String script = "Chat.methods.newMessage(" + seq + ")";
		wxbotView.executeScript(script);
	}

	/**
	 * 处理新语音消息，未读提醒
	 * 
	 * @param timestamp
	 */
	public static void newVoiceMessage(String timestamp) {
		WxbotView wxbotView = WxbotView.getInstance();
		String script = "Chat.methods.newVoiceMessage(" + timestamp + ")";
		wxbotView.executeScript(script);
	}
}
