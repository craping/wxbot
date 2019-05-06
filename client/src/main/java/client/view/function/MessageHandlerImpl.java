package client.view.function;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.FriendInvitationContent;
import com.cherry.jeeves.domain.shared.Member;
import com.cherry.jeeves.domain.shared.Message;
import com.cherry.jeeves.domain.shared.RecommendInfo;
import com.cherry.jeeves.enums.AppMessageType;
import com.cherry.jeeves.enums.MessageType;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.MessageHandler;
import com.cherry.jeeves.service.WechatHttpService;
import com.cherry.jeeves.utils.Coder;
import com.cherry.jeeves.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.enums.ChangeType;
import client.enums.ChatType;
import client.enums.TipsType;
import client.pojo.Msg;
import client.pojo.WxMessage;
import client.utils.Config;
import client.utils.EmojiUtil;
import client.utils.HttpUtil;
import client.utils.WxMessageTool;
import client.view.LoginView;
import client.view.QRView;
import client.view.WxbotView;
import client.view.server.BaseServer;
import client.view.server.ChatServer;
import javafx.application.Platform;

@Component
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerImpl.class);
	
	@Autowired
	private WechatHttpService wechatHttpService;
	@Autowired
	private CacheService cacheService;
	@Autowired
	private ChatServer chatServer;
	@Autowired
	private WxMessageTool msgTool;
	
	private QRView qrView;

	@Override
	public void onQR(byte[] qrData) {
		logger.debug("获取登录二维码");
		Platform.runLater(() -> {
			try {
				if (qrView == null) {
					qrView = new QRView();
				}
				qrView.open(qrData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void onScanning(String headImgBase64) {
		logger.debug("用户已扫码");
		logger.debug("头像：" + headImgBase64);
	}

	@Override
	public void onExpired() {
		logger.debug("二维码过期");
		Platform.runLater(() -> {
			qrView.expired();
		});
	}

	@Override
	public void onConfirmation() {
		logger.debug("确认登录");
		Platform.runLater(() -> {
			qrView.close();
			WxbotView.getInstance().show();
		});
	}

	@Override
	public void onLogin(Member member) {
		logger.debug("用户登录");
		logger.debug("用ID：" + member.getUserName());
		logger.debug("用户名：" + member.getNickName());
		Platform.runLater(() -> {
			CookieStorage cookieStorage = WxbotView.getInstance().getBrowser().getCookieStorage();
//			wechatHttpService.getCookies().forEach((k, v) -> {
//				cookieStorage.setSessionCookie("https://wx2.qq.com", k, v, ".qq.com", "/", false, false);
//				cookieStorage.setSessionCookie("https://wx.qq.com", k, v, ".qq.com", "/", false, false);
//			});
			com.cherry.jeeves.utils.rest.HttpUtil.cookieStore.getCookies().forEach(cookie -> {
				cookieStorage.setSessionCookie("https://wx2.qq.com", cookie.getName(), cookie.getValue(), ".qq.com", "/", false, false);
				cookieStorage.setSessionCookie("https://wx.qq.com", cookie.getName(), cookie.getValue(), ".qq.com", "/", false, false);
			});
			cookieStorage.save();
			WxbotView.getInstance().executeScript("app.init()");
			WxbotView.getInstance().executeSettingScript("app.init()");
		});
		
	}

	@Override
	public void onLogout(Member member) {
		logger.debug("用户退出");
		logger.debug("用ID：" + member.getUserName());
		logger.debug("用户名：" + member.getNickName());
		Platform.runLater(() -> {
			WxbotView.exit();
			LoginView.exit();
		});
	}
	
	@Override
	public void onContactCompleted() {
		WxbotView.getInstance().executeScript("app.loadIndividuals()");
	}
	
	@Override
	public void onReceivingChatRoomTextMessage(Message message) {
		String content = MessageUtils.getChatRoomTextMessageContent(message.getContent());
		String userName = MessageUtils.getSenderOfChatRoomTextMessage(message.getContent());
		userName = message.getFromUserName().contains("@@")?userName:message.getFromUserName();
		logger.debug("群聊文本消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + userName);
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + content);
		
		chatServer.writeReceiveRecord(message, MessageType.TEXT, null, null);
		
		//判断账户状态
		if(SettingFunction.isWorking()){
			
			//关键词功能是否开启
			final Contact chatRoom = cacheService.getChatRoom(message.getFromUserName().contains("@@")?message.getFromUserName():message.getToUserName());
			if (SettingFunction.SETTING.getSwitchs().isGlobalKeyword()) {
				
				Map<String, Msg> keyMap = null;
				
				//是否有“全群”关键词权限 并且 “全群”关键词开关开启
				if(SettingFunction.SETTING.getPermissions().isGlobalKeyword() && SettingFunction.SETTING.getKeywords().contains(Config.GLOBA_SEQ)){
					// “全群”关键词自动回复
					keyMap = KeywordFunction.KEY_MAP.get(Config.GLOBA_SEQ);
					if (keyMap != null) {
						keyMap.forEach((k, v) -> {
							if (content.contains(k)) {
								chatServer.sendGloba(Arrays.asList(chatRoom), v);
							}
						});
					}
				}
				
				//是否有“分群”关键词权限 并且 “分群”关键词开关开启
				if(SettingFunction.SETTING.getPermissions().isKeyword() && SettingFunction.SETTING.getKeywords().contains(chatRoom.getSeq())){
					// “分群”关键词自动回复
					keyMap = KeywordFunction.KEY_MAP.get(chatRoom.getSeq());
					if (keyMap != null) {
						keyMap.forEach((k, v) -> {
							if (content.contains(k)) {
								chatServer.sendGloba(Arrays.asList(chatRoom), v);
							}
						});
					}
				}
			}
			
			//图灵机器人自动回复
			if(SettingFunction.SETTING.getPermissions().isTuring() && SettingFunction.SETTING.getTuring().contains(chatRoom.getSeq()) 
					&& SettingFunction.TURING_KEY !=  null && !SettingFunction.TURING_KEY.isEmpty() && content.contains("@"+cacheService.getOwner().getNickName())
			){
				String userId = Coder.encryptMD5(userName);
				Contact member = cacheService.searchContact(chatRoom.getMemberList(), userName);
				String nickName = member == null?"":member.getNickName();
				
				String json = "{'reqType':0,'perception': {'inputText': {'text': '"+content+"'}},'userInfo': {'apiKey': '"+SettingFunction.TURING_KEY+"','userId': '"+userId.replace("@", "")+"','groupId':'"+chatRoom.getUserName()+"'}}";
				String result = HttpUtil.doPost("http://openapi.tuling123.com/openapi/api/v2", json);
				JSONObject jsonResult = JSONObject.parseObject(result);
				if(jsonResult.containsKey("results")) {
					JSONArray results = jsonResult.getJSONArray("results");
					String type;
					String value;
					for (int i = 0; i < results.size(); i++) {
						JSONObject r = results.getJSONObject(i);
						type = r.getString("resultType");
						value = r.getJSONObject("values").getString(type);
						switch (type) {
						case "text":
						case "url":
							chatServer.sendGloba(Arrays.asList(chatRoom), new Msg(MessageType.TEXT, "@"+nickName+" "+ value));
							break;
	
						default:
							
							break;
						}
					} 
				}
			}
		}
	}

	@Override
	public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.debug("群聊图片消息");
		logger.debug("thumbImageUrl:" + thumbImageUrl);
		logger.debug("fullImageUrl:" + fullImageUrl);
		
		chatServer.writeReceiveRecord(message, MessageType.IMAGE, fullImageUrl, thumbImageUrl);
	}

	@Override
	public void onReceivingChatRoomEmoticonMessage(Message message, String emoticonUrl) {
		String content = MessageUtils.getChatRoomTextMessageContent(message.getContent());
		logger.debug("群聊表情消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + content);
		logger.debug("emoticonUrl:" + emoticonUrl);
		
		chatServer.writeReceiveRecord(message, MessageType.EMOTICON, emoticonUrl, emoticonUrl);
	}

	@Override
	public void onReceivingChatRoomVoiceMessage(Message message, String voiceUrl) {
		logger.debug("群聊语音消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.debug("voiceUrl:" + voiceUrl);
		
		chatServer.writeReceiveRecord(message, MessageType.VOICE, voiceUrl, voiceUrl);
	}

	@Override
	public void onReceivingChatRoomVideoMessage(Message message, String thumbImageUrl, String videoUrl) {
		logger.debug("群聊视频消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.debug("url:" + thumbImageUrl);
		logger.debug("videoUrl" + videoUrl);
		
		chatServer.writeReceiveRecord(message, MessageType.VIDEO, videoUrl, thumbImageUrl);
	}

	@Override
	public void onReceivingChatRoomMediaMessage(Message message, String thumbImageUrl, String mediaUrl) {
		logger.debug("群聊多媒体消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.debug("mediaUrl:" + mediaUrl);
		
		chatServer.writeReceiveRecord(message, MessageType.APP, mediaUrl, thumbImageUrl);
	}

	@Override
	public void onReceivingPrivateTextMessage(Message message) {
		String content = EmojiUtil.formatFace(message.getContent()).replace("&amp;", "&");
		logger.debug("私聊文本消息");
		logger.debug("from: " + message.getFromUserName());
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + content);
		
		chatServer.writeReceiveRecord(message, MessageType.TEXT, null, null);
		
		//转发给群
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isForward() && cacheService.getOwner().getUserName().equals(message.getFromUserName()) && cacheService.getOwner().getUserName().equals(message.getToUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					chatServer.writeSendTextRecord(c, content, wechatHttpService.sendText(c.getUserName(), content).getMsgID());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	@Override
	public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.debug("私聊图片消息");
		logger.debug("thumbImageUrl:" + thumbImageUrl);
		logger.debug("fullImageUrl:" + fullImageUrl);
		
		WxMessage msg = chatServer.createReceiveMsg(message, MessageType.IMAGE, fullImageUrl, thumbImageUrl);
		chatServer.writeReceiveRecord(message, msg);
		
		//转发给群
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isForward() && cacheService.getOwner().getUserName().equals(message.getFromUserName()) && cacheService.getOwner().getUserName().equals(message.getToUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					msgTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	@Override
	public void onReceivingPrivateEmoticonMessage(Message message, String emoticonUrl) {
		logger.debug("私聊表情消息");
		logger.debug("emoticonUrl:" + emoticonUrl);
		
		WxMessage msg = chatServer.createReceiveMsg(message, MessageType.EMOTICON, emoticonUrl, emoticonUrl);
		chatServer.writeReceiveRecord(message, msg);
		
		//转发给群
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isForward() && message.getContent() != null && !message.getContent().isEmpty()) {
			if(cacheService.getOwner().getUserName().equals(message.getFromUserName()) && cacheService.getOwner().getUserName().equals(message.getToUserName())){
				cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
					try {
						wechatHttpService.forwardMsg(c.getUserName(), message);
						msgTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
	}

	@Override
	public void onReceivingPrivateVoiceMessage(Message message, String voiceUrl) {
		logger.debug("私聊语音消息");
		logger.debug("voiceUrl:" + voiceUrl);
		
		WxMessage msg = chatServer.createReceiveMsg(message, MessageType.VOICE, voiceUrl, voiceUrl);
		chatServer.writeReceiveRecord(message, msg);
		
		//转发给群
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isForward() && cacheService.getOwner().getUserName().equals(message.getFromUserName()) && cacheService.getOwner().getUserName().equals(message.getToUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					msgTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	@Override
	public void onReceivingPrivateVideoMessage(Message message, String thumbImageUrl, String videoUrl) {
		logger.debug("私聊视频消息");
		logger.debug("thumbImageUrl:" + thumbImageUrl);
		logger.debug("videoUrl:" + videoUrl);
		
		WxMessage msg = chatServer.createReceiveMsg(message, MessageType.VIDEO, videoUrl, thumbImageUrl);
		chatServer.writeReceiveRecord(message, msg);
		
		//转发给群
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isForward() && cacheService.getOwner().getUserName().equals(message.getFromUserName()) && cacheService.getOwner().getUserName().equals(message.getToUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					msgTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	@Override
	public void onReceivingPrivateMediaMessage(Message message, String thumbImageUrl, String mediaUrl) {
		logger.debug("私聊多媒体消息");
		logger.debug("mediaUrl:" + mediaUrl);
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		
		WxMessage msg = chatServer.createReceiveMsg(message, MessageType.APP, mediaUrl, thumbImageUrl);
		chatServer.writeReceiveRecord(message, msg);
		
		//转发给群
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isForward() && cacheService.getOwner().getUserName().equals(message.getFromUserName()) && cacheService.getOwner().getUserName().equals(message.getToUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					if(message.getAppMsgType() == AppMessageType.URL.getCode()){
						chatServer.writeSendTextRecord(c, mediaUrl, wechatHttpService.sendText(c.getUserName(), mediaUrl).getMsgID());
					}else{
						wechatHttpService.forwardMsg(c.getUserName(), message);
						msgTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	@Override
	public void onMembersSeqChanged(Map<String, String> seqMap) {
		logger.debug("收到联系人seq变动消息");
		//变更关键词seq
		msgTool.syncSeq(seqMap);
	}

	@Override
	public boolean onReceivingFriendInvitation(RecommendInfo info) {
		logger.debug("收到好友请求消息");
		logger.debug("recommendinfo content:" + info.getContent());
		// 判断全选和开关
		return SettingFunction.SETTING.getPermissions().isAcceptFriend() && SettingFunction.SETTING.getSwitchs().isAutoAcceptFriend();
	}

	@Override
	public void postAcceptFriendInvitation(Message message) throws IOException {
		logger.debug("接受好友请求消息");
//        将该用户的微信号设置成他的昵称
		String content = StringEscapeUtils.unescapeXml(message.getContent());
		FriendInvitationContent friendInvitationContent = BaseServer.XML_MAPPER.readValue(content, FriendInvitationContent.class);
		wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), message.getRecommendInfo().getNickName());
		wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), friendInvitationContent.getFromusername());
	}

	@Override
	public void onChatRoomMembersChanged(Contact chatRoom, Set<Contact> membersJoined, Set<Contact> membersLeft) {
		logger.debug("群成员变动消息");
		logger.debug("群ID:" + chatRoom.getUserName());
		if(SettingFunction.isWorking()){
			Map<String, Msg> tips = TipFunction.TIP_MAP.get(chatRoom.getSeq());
			
			if(SettingFunction.SETTING.getPermissions().isMemberJoinTip() && tips != null && tips.containsKey(TipsType.MEMBERJOIN.getType())){
				
				Msg joinTip =  tips.get(TipsType.MEMBERJOIN.getType());
				if(membersJoined != null && membersJoined.size() > 0 && joinTip != null){
					StringBuffer members = new StringBuffer();
					membersJoined.forEach(c -> {
						members.append("@").append(c.getNickName()).append(" ");
					});
					if (joinTip.getMsgType() == MessageType.TEXT) {
						chatServer.sendGloba(Arrays.asList(chatRoom), new Msg(MessageType.TEXT, joinTip.getContent().replace("[user]", members.toString())));
					} else {
						chatServer.sendGloba(Arrays.asList(chatRoom), new Msg(MessageType.TEXT, members.toString()));
						chatServer.sendGloba(Arrays.asList(chatRoom), joinTip);
					}
				}
			}
			
			if(SettingFunction.SETTING.getPermissions().isMemberLeftTip() && tips != null && tips.containsKey(TipsType.MEMBERLEFT.getType())) {
				
				Msg letfTip = tips.get(TipsType.MEMBERLEFT.getType());
				if(membersLeft != null && membersLeft.size() > 0 && letfTip != null){
					StringBuffer members = new StringBuffer();
					membersLeft.forEach(c -> {
						members.append("@").append(c.getNickName()).append(" ");
					});
					if (letfTip.getMsgType() == MessageType.TEXT) {
						chatServer.sendGloba(Arrays.asList(chatRoom), new Msg(MessageType.TEXT, letfTip.getContent().replace("[user]", members.toString())));
					} else {
						chatServer.sendGloba(Arrays.asList(chatRoom), new Msg(MessageType.TEXT, members.toString()));
						chatServer.sendGloba(Arrays.asList(chatRoom), letfTip);
					}
				}
			}
		}
	}

	@Override
	public void onNewChatRoomsFound(Set<Contact> chatRooms) {
		logger.debug("发现新群消息");
		chatRooms.forEach(x -> logger.debug(x.getUserName()));
		msgTool.execContactsChanged(chatRooms, ChangeType.ADD.getCode());
		WxbotView.getInstance().executeSettingScript("app.notifyChatRooms()");
		
//		if(SettingFunction.isWorking() && SettingFunction.SETTING.getPermissions().isChatRoomFoundTip() && chatRooms != null && chatRooms.size() >0){ 
//			chatRooms.forEach(chatRoom -> {
//				Map<String, Msg> tips = TipFunction.TIP_MAP.get(chatRoom.getSeq());
//				if(tips != null && tips.containsKey(TipsType.CHATROOMFOUND.getType()))
//					chatServer.sendGloba(Arrays.asList(chatRoom), tips.get(TipsType.CHATROOMFOUND.getType()));
//			});
//		}
	}
	
	@Override
	public void onChatRoomsModify(Set<Contact> chatRooms) {
		logger.debug("群信息变动");
		chatRooms.forEach(x -> logger.debug(x.getUserName()));
		msgTool.execContactsChanged(chatRooms, ChangeType.MOD.getCode());
		WxbotView.getInstance().executeSettingScript("app.notifyChatRooms()");
	}
	
	@Override
	public void onChatRoomsDeleted(Set<Contact> chatRooms) {
		logger.debug("群被删除消息");
		chatRooms.forEach(x -> logger.debug(x.getUserName()));
		msgTool.execContactsChanged(chatRooms, ChangeType.DEL.getCode());
		WxbotView.getInstance().executeSettingScript("app.notifyChatRooms()");
	}

	@Override
	public void onNewFriendsFound(Set<Contact> contacts) {
		logger.debug("发现新好友消息");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		msgTool.execContactsChanged(contacts, ChangeType.ADD.getCode());
	}
	
	@Override
	public void onFriendsModify(Set<Contact> contacts) {
		logger.debug("好友信息变动");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		msgTool.execContactsChanged(contacts, ChangeType.MOD.getCode());
	}

	@Override
	public void onFriendsDeleted(Set<Contact> contacts) {
		logger.debug("好友被删除消息");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		msgTool.execContactsChanged(contacts, ChangeType.DEL.getCode());
	}

	@Override
	public void onNewMediaPlatformsFound(Set<Contact> mps) {
		logger.debug("发现新公众号消息");
	}

	@Override
	public void onMediaPlatformsDeleted(Set<Contact> mps) {
		logger.debug("公众号被删除消息");
	}

	@Override
	public void onRedPacketReceived(Contact contact) {
		logger.debug("红包消息");
		if (contact != null) {
			logger.debug("红包来自： " + contact.getNickName());
		}
	}

	@Override
	public void onStatusNotifyReaded(Message message) {
		logger.debug("onStatusNotifyReaded 消息");
		try {
			logger.debug(BaseServer.JSON_MAPPER.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusNotifyEnterSession(Message message) {
		logger.debug("onStatusNotifyEnterSession 消息");
		WxbotView.getInstance().executeScript(String.format("app.setCount('%s', 0)", message.getStatusNotifyUserName()));
		try {
			logger.debug(BaseServer.JSON_MAPPER.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusNotifyInited(Message message) {
		logger.debug("onStatusNotifyInited 消息");
		try {
			logger.debug(BaseServer.JSON_MAPPER.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusNotifyQuitSession(Message message) {
		logger.debug("onStatusNotifyQuitSession 消息");
		
		try {
			logger.debug(BaseServer.JSON_MAPPER.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStatusNotifySyncConv(Message message) {
		WxbotView.getInstance().executeScript("app.updateChatRooms()");
		WxbotView.getInstance().executeSettingScript("app.notifyChatRooms()");
	}

	@Override
	public void onFriendVerify(Contact contact) {
		logger.debug("onFriendVerify 消息");
		
		Map<String, Contact> contactMap = new HashMap<String, Contact>();
		WxbotView wxbotView = WxbotView.getInstance();
		JSObject app = wxbotView.getBrowser().executeJavaScriptAndReturnValue("app").asObject();
		JSValue deleteEvt = app.getProperty("deleteEvt");
		try {
			contactMap.put(contact.getSeq(), contact);
			deleteEvt.asFunction().invokeAsync(app, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(contactMap)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendBlacklist(Contact contact) {
		logger.debug("onFriendBlacklist 消息");
		
		Map<String, Contact> contactMap = new HashMap<String, Contact>();
		WxbotView wxbotView = WxbotView.getInstance();
		JSObject app = wxbotView.getBrowser().executeJavaScriptAndReturnValue("app").asObject();
		JSValue blacklistEvt = app.getProperty("blacklistEvt");
		try {
			contactMap.put(contact.getSeq(), contact);
			blacklistEvt.asFunction().invokeAsync(app, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(contactMap)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
