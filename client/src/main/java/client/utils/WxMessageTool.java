package client.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.Owner;
import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.enums.ChatType;
import client.enums.Direction;
import client.pojo.ScheduleMsg;
import client.pojo.WxMessage;
import client.view.WxbotView;
import client.view.function.KeywordFunction;
import client.view.function.SettingFunction;
import client.view.function.TimerFunction;

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

	private static Map<String, Integer> noRead = new HashMap<String, Integer>();

	/**
	 * 保存自己发送的聊天信息
	 * 
	 * @param recipient 接收者
	 * @param message   消息体
	 * @param from      发送者
	 * @param chatType  聊天类型
	 */
	public static void sendMessage(Contact recipient, WxMessage message, String from, int chatType) {
		String timestamp = Tools.getTimestamp();
		message.setTimestamp(timestamp);
		message.setTo(recipient.getNickName());
		message.setFrom(from);
		message.setDirection(Direction.SEND.getCode());
		message.setChatType(chatType);
		String jsonStr = "";
		try {
			jsonStr = jsonMapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String filePath = Config.CHAT_RECORD_PATH + recipient.getSeq();
		FileUtil.writeFile(filePath, Tools.getSysDate() + ".txt", jsonStr);
		WxMessageTool.avatarBadge(recipient.getSeq());
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
	 * 处理已读消息，
	 * @param seq
	 */
	public static void haveRead(String seq) {
		noRead.remove(seq);
	}

	/**
	 * 处理新消息，头像加消息提醒
	 * 
	 * @param seq
	 */
	public static void avatarBadge(String seq) {
		int noReadCount = 1;
		if (noRead.get(seq) == null) {
			noRead.put(seq, noReadCount);
		} else {
			noReadCount = noRead.get(seq) + 1;
			noRead.put(seq, noReadCount);
		}
		WxbotView wxbotView = WxbotView.getInstance();
		String script = "Chat.methods.newMessage(" + seq + ", " + noReadCount + ")";
		if (noReadCount > 100) {
			script = "Chat.methods.newMessage(" + seq + ", '99+')";
		}
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
	
	public static void syncSeq(Map<String, String> seqMap){
		seqMap.forEach((k, v) -> {
			//同步关键词seq
			if(KeywordFunction.KEY_MAP != null){
				ConcurrentHashMap<String, String> map = KeywordFunction.KEY_MAP.remove(k);
				if(map != null)
					KeywordFunction.KEY_MAP.put(v, map);
			}
			
			//同步定时消息seq
			if(TimerFunction.TIMER_MAP != null){
				ConcurrentLinkedQueue<ScheduleMsg> queue = TimerFunction.TIMER_MAP.remove(k);
				if(queue != null)
					TimerFunction.TIMER_MAP.put(v, queue);
			}
			
			//同步群转发seq
			if(SettingFunction.SETTING != null && SettingFunction.SETTING.getForwards() != null){
				if(SettingFunction.SETTING.getForwards().remove(k))
					SettingFunction.SETTING.getForwards().add(v);
			}
		});
	}

	/**
	 * 刷新联系人、群聊列表
	 * 
	 * @param msg
	 */
	public static void execContactsChanged(String msg) {
		WxbotView wxbotView = WxbotView.getInstance();
		String script = "Contacts.methods.execContactsChanged(" + msg + ")";
		wxbotView.executeScript(script);
	}

	/**
	 * 刷新群成员列表
	 */
	public static void reloadMember() {
		WxbotView wxbotView = WxbotView.getInstance();
		String script = "Info.methods.reloadMember()";
		wxbotView.executeScript(script);
	}
}
