package client.view.function;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.FriendInvitationContent;
import com.cherry.jeeves.domain.shared.Member;
import com.cherry.jeeves.domain.shared.Message;
import com.cherry.jeeves.domain.shared.RecommendInfo;
import com.cherry.jeeves.enums.MessageType;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.MessageHandler;
import com.cherry.jeeves.service.WechatHttpService;
import com.cherry.jeeves.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.enums.ChatType;
import client.pojo.Msg;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Config;
import client.utils.EmojiUtil;
import client.utils.FileUtil;
import client.utils.WxMessageTool;
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
			wechatHttpService.getCookies().forEach((k, v) -> {
				cookieStorage.setSessionCookie("https://wx2.qq.com", k, v, ".qq.com", "/", false, false);
				cookieStorage.setSessionCookie("https://wx.qq.com", k, v, ".qq.com", "/", false, false);
			});
			cookieStorage.save();
			WxbotView.getInstance().load();
		});
		
	}

	@Override
	public void onLogout(Member member) {
		logger.debug("用户退出");
		logger.debug("用ID：" + member.getUserName());
		logger.debug("用户名：" + member.getNickName());
		Platform.runLater(() -> {
			WxbotView.getInstance().close();
		});
	}
	
	@Override
	public void onContactCompleted() {
		WxbotView.getInstance().executeScript("app.loadContacts()");
	}
	
	@Override
	public void onReceivingChatRoomTextMessage(Message message) {
		String content = EmojiUtil.formatFace(MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		String userName = MessageUtils.getSenderOfChatRoomTextMessage(message.getContent());
		logger.debug("群聊文本消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + userName);
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + content);
		
		WxMessage record = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody(content));
		final Contact chatRoom;
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			msgTool.sendMessage(chatRoom, record, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), userName);
			msgTool.receiveGroupMessage(chatRoom, sender, record);
		}
		
		try {
			System.out.println(BaseServer.JSON_MAPPER.writeValueAsString(message));
			System.out.println(BaseServer.JSON_MAPPER.writeValueAsString(chatRoom));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		//判断关键词是否开启
		if (SettingFunction.SETTING.getSwitchs().isGlobalKeyword() && chatRoom != null) {
			
			Map<String, Msg> keyMap = null;
			
			//判断全群关键词权限
			if(SettingFunction.SETTING.getPermissions().isGlobalKeyword()){
				// 全群关键词自动回复
				keyMap = KeywordFunction.KEY_MAP.get(Config.GLOBA_SEQ);
				if (keyMap != null) {
					keyMap.forEach((k, v) -> {
						if (content.contains(k)) {
							chatServer.sendGloba(cacheService.getChatRooms(), v);
						}
					});
				}
			}
			
			//判断分群关键词权限
			if(SettingFunction.SETTING.getPermissions().isKeyword()){
				// 分群关键词自动回复
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
	}

	@Override
	public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.debug("群聊图片消息");
		logger.debug("thumbImageUrl:" + thumbImageUrl);
		logger.debug("fullImageUrl:" + fullImageUrl);
		
		// 下载图片文件 到本地
		fullImageUrl = wechatHttpService.download(fullImageUrl, message.getMsgId() + ".jpg", MessageType.IMAGE);
		thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + "_thumb.jpg", MessageType.IMAGE);
		// 处理消息
		WxMessage msg = new WxMessage(MessageType.IMAGE.getCode(), new WxMessageBody(fullImageUrl, thumbImageUrl)); 
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			msgTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			msgTool.receiveGroupMessage(chatRoom, sender, msg);
		}
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
		
		if(content.trim().isEmpty()) {
			WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody("[发送了一个表情，请在手机上查看]"));
			Contact chatRoom = new Contact();
			if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
				chatRoom = cacheService.getChatRoom(message.getToUserName());
				msgTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
			} else {
				chatRoom = cacheService.getChatRoom(message.getFromUserName());
				Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
				msgTool.receiveGroupMessage(chatRoom, sender, msg);
			}
		} else {
			// 下载表情文件 到本地
			emoticonUrl = wechatHttpService.download(emoticonUrl, message.getMsgId() + ".gif", MessageType.EMOTICON);
			// 处理消息
			WxMessage msg = new WxMessage(MessageType.EMOTICON.getCode(), new WxMessageBody(emoticonUrl));
			Contact chatRoom = new Contact();
			if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
				chatRoom = cacheService.getChatRoom(message.getToUserName());
				msgTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
			} else {
				chatRoom = cacheService.getChatRoom(message.getFromUserName());
				Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
				msgTool.receiveGroupMessage(chatRoom, sender, msg);
			}
		}
	}

	@Override
	public void onReceivingChatRoomVoiceMessage(Message message, String voiceUrl) {
		logger.debug("群聊语音消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.debug("voiceUrl:" + voiceUrl);
		
		// 下载语音文件 到本地
		voiceUrl = wechatHttpService.download(voiceUrl, message.getMsgId() + ".mp3", MessageType.VOICE);
		// 处理语音消息
		WxMessage msg = new WxMessage(MessageType.VOICE.getCode(), new WxMessageBody(voiceUrl, message.getVoiceLength()));
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			msgTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			msgTool.receiveGroupMessage(chatRoom, sender, msg);
		}
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
		
		// 下载视频文件、缩略图文件 到本地
		videoUrl = wechatHttpService.download(videoUrl, message.getMsgId() + ".mp4", MessageType.VIDEO);
		thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + ".jpg", MessageType.VIDEO);
		// 处理视频消息
		WxMessage msg = new WxMessage(MessageType.VIDEO.getCode(), new WxMessageBody(videoUrl, thumbImageUrl));
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			msgTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			msgTool.receiveGroupMessage(chatRoom, sender, msg);
		}
	}

	@Override
	public void onReceivingChatRoomMediaMessage(Message message, String mediaUrl) {
		logger.debug("群聊多媒体消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.debug("mediaUrl:" + mediaUrl);
		
		// 下载文件
		mediaUrl = wechatHttpService.download(mediaUrl, message.getMsgId() + "_" + message.getFileName(), MessageType.APP);
		// 处理通用文件消息
		WxMessage msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(mediaUrl, message.getFileName(), FileUtil.getFileSizeString(Long.valueOf(message.getFileSize()))));		
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			msgTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			msgTool.receiveGroupMessage(chatRoom, sender, msg);
		}
	}

	@Override
	public void onReceivingPrivateTextMessage(Message message) {
		String content = EmojiUtil.formatFace(message.getContent());
		logger.debug("私聊文本消息");
		logger.debug("from: " + message.getFromUserName());
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + content);
		
		WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody(content));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					chatServer.writeSendTextRecord(c, message.getContent());
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
		// 下载图片文件 到本地
		final String fullImagePath = wechatHttpService.download(fullImageUrl, message.getMsgId() + ".jpg", MessageType.IMAGE);
		final String  thumbImagePath = wechatHttpService.download(thumbImageUrl, message.getMsgId() + "_thumb.jpg", MessageType.IMAGE);
		// 处理图片消息
		WxMessage msg = new WxMessage(MessageType.IMAGE.getCode(), new WxMessageBody(fullImagePath, thumbImagePath));
		// 处理消息
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
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
		
		if(message.getContent() == null || message.getContent().isEmpty()) {
			WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody("[发送了一个表情，请在手机上查看]"));
			if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
				Contact recipient = cacheService.getContact(message.getToUserName());
				msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
			} else {
				Contact sender = cacheService.getContact(message.getFromUserName());
				msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
			}
		} else {
			// 下载表情文件 到本地
			emoticonUrl = wechatHttpService.download(emoticonUrl, message.getMsgId() + ".gif", MessageType.EMOTICON);
			// 处理表情消息
			WxMessage msg = new WxMessage(MessageType.EMOTICON.getCode(), new WxMessageBody(emoticonUrl));
			if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
				Contact recipient = cacheService.getContact(message.getToUserName());
				msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
			} else {
				Contact sender = cacheService.getContact(message.getFromUserName());
				msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
			}
			
			//转发给群
			if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
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
		// 下载语音文件 到本地
		voiceUrl = wechatHttpService.download(voiceUrl, message.getMsgId() + ".mp3", MessageType.VOICE);
		WxMessage msg = new WxMessage(MessageType.VOICE.getCode(), new WxMessageBody(voiceUrl, message.getVoiceLength()));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
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
		// 下载视频文件、缩略图文件 到本地
		videoUrl = wechatHttpService.download(videoUrl, message.getMsgId() + ".mp4", MessageType.VIDEO);
		thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + ".jpg", MessageType.VIDEO);
		// 处理视频消息
		WxMessage msg = new WxMessage(MessageType.VIDEO.getCode(), new WxMessageBody(videoUrl, thumbImageUrl));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
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
	public void onReceivingPrivateMediaMessage(Message message, String mediaUrl) {
		logger.debug("私聊多媒体消息");
		logger.debug("mediaUrl:" + mediaUrl);
		// 下载文件
		mediaUrl = wechatHttpService.download(mediaUrl, message.getMsgId() + "_" + message.getFileName(), MessageType.APP);
		// 处理私聊通用文件消息
		WxMessage msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(mediaUrl, message.getFileName(), FileUtil.getFileSizeString(Long.valueOf(message.getFileSize()))));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			msgTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			msgTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
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
		ObjectMapper xmlMapper = new XmlMapper();
		FriendInvitationContent friendInvitationContent = xmlMapper.readValue(content, FriendInvitationContent.class);
		wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), message.getRecommendInfo().getNickName());
		wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), friendInvitationContent.getFromusername());
	}

	@Override
	public void onChatRoomMembersChanged(Contact chatRoom, Set<Contact> membersJoined, Set<Contact> membersLeft) {
		logger.debug("群成员变动消息");
		logger.debug("群ID:" + chatRoom.getUserName());
		if (membersJoined != null && membersJoined.size() > 0) {
			logger.debug("新加入成员:" + String.join(",", membersJoined.stream().map(Contact::getNickName).collect(Collectors.toList())));
		}
		if (membersLeft != null && membersLeft.size() > 0) {
			logger.debug("离开成员:" + String.join(",", membersLeft.stream().map(Contact::getNickName).collect(Collectors.toList())));
		}
	}

	@Override
	public void onNewChatRoomsFound(Set<Contact> chatRooms) {
		logger.debug("发现新群消息");
		chatRooms.forEach(x -> logger.debug(x.getUserName()));
		msgTool.execContactsChanged(chatRooms, 2);
	}

	@Override
	public void onChatRoomsDeleted(Set<Contact> chatRooms) {
		logger.debug("群被删除消息");
		chatRooms.forEach(x -> logger.debug(x.getUserName()));
		msgTool.execContactsChanged(chatRooms, 2);
	}

	@Override
	public void onNewFriendsFound(Set<Contact> contacts) {
		logger.debug("发现新好友消息");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		msgTool.execContactsChanged(contacts, 1);
	}

	@Override
	public void onFriendsDeleted(Set<Contact> contacts) {
		logger.debug("好友被删除消息");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		msgTool.execContactsChanged(contacts, 1);
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
		WxbotView.getInstance().executeScript("app.loadChatRooms()");
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
			deleteEvt.asFunction().invoke(app, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(contactMap)));
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
			blacklistEvt.asFunction().invoke(app, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(contactMap)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
