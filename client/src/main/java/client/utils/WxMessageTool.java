package client.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.Owner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.enums.ChatType;
import client.enums.Direction;
import client.pojo.Msg;
import client.pojo.ScheduleMsg;
import client.pojo.WxMessage;
import client.view.WxbotView;
import client.view.function.KeywordFunction;
import client.view.function.SettingFunction;
import client.view.function.TimerFunction;
import client.view.server.BaseServer;

/**
 * 
 * 微信消息处理工具类
 * 
 * @author wr
 *
 */
@Component
public class WxMessageTool extends BaseServer {

	/**
	 * 保存自己发送的聊天信息
	 * 
	 * @param recipient 接收者
	 * @param message   消息体
	 * @param from      发送者
	 * @param chatType  聊天类型
	 */
	public void sendMessage(Contact recipient, WxMessage message, String from, int chatType) {
		String timestamp = Tools.getTimestamp();
		message.setTimestamp(timestamp);
		message.setTo(recipient.getNickName());
		message.setFrom(from);
		message.setDirection(Direction.SEND.getCode());
		message.setChatType(chatType);
		String jsonStr = "";
		try {
			jsonStr = JSON_MAPPER.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String filePath = Config.CHAT_RECORD_PATH + recipient.getSeq();
		FileUtil.writeFile(filePath, Tools.getSysDate() + ".txt", jsonStr);
		avatarBadge(recipient.getUserName(), jsonStr);
	}

	/**
	 * 处理私聊消息
	 * 
	 * @param sender
	 * @param owner
	 * @param message
	 * @param msg
	 */
	public void receiveMessage(Contact sender, Owner owner, WxMessage msg) {
		String seq = sender.getSeq();
		String timestamp = Tools.getTimestamp();
		msg.setChatType(ChatType.CHAT.getCode());
		msg.setTimestamp(timestamp);
		msg.setTo(owner.getNickName());
		msg.setFrom(sender.getNickName());
		msg.setDirection(Direction.RECEIVE.getCode());
		String jsonStr = "";
		try {
			jsonStr = JSON_MAPPER.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String path = Config.CHAT_RECORD_PATH + seq;
		FileUtil.writeFile(path, Tools.getSysDate() + ".txt", jsonStr);
		avatarBadge(sender.getUserName(), jsonStr);
	}

	/**
	 * 处理群聊消息
	 * 
	 * @param sender
	 * @param owner
	 * @param message
	 * @param msg
	 */
	public void receiveGroupMessage(Contact chatRoom, Contact sender, WxMessage msg) {
		String seq = chatRoom.getSeq();
		String timestamp = Tools.getTimestamp();
		msg.setChatType(ChatType.GROUPCHAT.getCode());
		msg.setTimestamp(timestamp);
		msg.setTo(chatRoom.getNickName());
		msg.setFrom(sender.getNickName());
		msg.setDirection(Direction.RECEIVE.getCode());
		
//		if(sender.getSeq() == null){
//			Contact member = wechatService.getChatRoomMemberInfo(chatRoom.getUserName(), sender.getUserName());
//			if(member != null)
//				sender.setHeadImgUrl(member.getHeadImgUrl());
//		}
//		File avatar = new File(Config.IMG_PATH + sender.getSeq()+".jpg");
//		msg.setAvatar(avatar.getPath());
//		if(!avatar.exists() && sender.getHeadImgUrl() != null && !sender.getHeadImgUrl().isEmpty()) {
//			wechatService.download(cacheService.getHostUrl() + sender.getHeadImgUrl(), sender.getSeq()+".jpg", MessageType.IMAGE);
//		}
		if(sender.getHeadImgUrl() == null){
			Contact member = wechatService.getChatRoomMemberInfo(chatRoom.getUserName(), sender.getUserName());
			if(member != null)
				sender.setHeadImgUrl(member.getHeadImgUrl());
		}
		msg.setAvatar(cacheService.getHostUrl() + sender.getHeadImgUrl());
		String jsonStr = "";
		try {
			jsonStr = JSON_MAPPER.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String path = Config.CHAT_RECORD_PATH + seq;
		FileUtil.writeFile(path, Tools.getSysDate() + ".txt", jsonStr);
		avatarBadge(chatRoom.getUserName(), jsonStr);
	}

	/**
	 * 处理新消息，头像加消息提醒
	 * 
	 * @param seq
	 */
	public void avatarBadge(String userName, String jsonMsg) {
		JSObject app = WxbotView.getInstance().getBrowser().executeJavaScriptAndReturnValue("app").asObject();
		JSValue newMessage = app.getProperty("newMessage");
		newMessage.asFunction().invokeAsync(app, userName, new JSONString(jsonMsg));
	}

	
	  
	/**  
	* @Title: syncSeq  
	* @Description: 同步seq变更
	* @param @param seqMap    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void syncSeq(Map<String, String> seqMap){
		WxbotView wxbotView = WxbotView.getInstance();
		JSObject app = wxbotView.getBrowser().executeJavaScriptAndReturnValue("app").asObject();
		JSValue syncSeq = app.getProperty("syncSeq");
		try {
			syncSeq.asFunction().invokeAsync(app, new JSONString(JSON_MAPPER.writeValueAsString(seqMap)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		seqMap.forEach((k, v) -> {
			//同步关键词seq
			if(KeywordFunction.KEY_MAP != null){
				ConcurrentHashMap<String, Msg> map = KeywordFunction.KEY_MAP.remove(k);
				if(map != null)
					KeywordFunction.KEY_MAP.put(v, map);
			}
			
			//同步定时消息seq
			if(TimerFunction.TIMER_MAP != null){
				ConcurrentLinkedQueue<ScheduleMsg> queue = TimerFunction.TIMER_MAP.remove(k);
				if(queue != null)
					TimerFunction.TIMER_MAP.put(v, queue);
			}
			
			//同步群列表seq
			if(SettingFunction.SETTING != null && SettingFunction.SETTING.getTuring() != null){
				if(SettingFunction.SETTING.getTuring().remove(k))
					SettingFunction.SETTING.getTuring().add(v);
			}
			
			if(SettingFunction.SETTING != null && SettingFunction.SETTING.getKeywords() != null){
				if(SettingFunction.SETTING.getKeywords().remove(k))
					SettingFunction.SETTING.getKeywords().add(v);
			}
			
			if(SettingFunction.SETTING != null && SettingFunction.SETTING.getTimers() != null){
				if(SettingFunction.SETTING.getTimers().remove(k))
					SettingFunction.SETTING.getTimers().add(v);
			}
			
			if(SettingFunction.SETTING != null && SettingFunction.SETTING.getForwards() != null){
				if(SettingFunction.SETTING.getForwards().remove(k))
					SettingFunction.SETTING.getForwards().add(v);
			}
			
			//seq变动，重命名聊天记录文件夹
			String oldPath = Config.CHAT_RECORD_PATH + k;
			String newPath = Config.CHAT_RECORD_PATH + v;
			FileUtil.renameFile(oldPath, newPath);
		});
	}

	/**
	 * 联系人变化
	 * 
	 * @param msg
	 */
	public void execContactsChanged(Set<Contact> contacts, int type) {
		JSObject app = WxbotView.getInstance().getBrowser().executeJavaScriptAndReturnValue("app").asObject();
		String json = "[]";
		try {
			json = JSON_MAPPER.writeValueAsString(contacts);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (type == 1) {
			JSValue addContact = app.getProperty("addContact");
			addContact.asFunction().invokeAsync(app, new JSONString(json));
		} else if (type == 2) {
			JSValue delContact = app.getProperty("delContact");
			delContact.asFunction().invokeAsync(app, new JSONString(json));
		} else {
			JSValue delContact = app.getProperty("modContact");
			delContact.asFunction().invokeAsync(app, new JSONString(json));
		}
	}
}
