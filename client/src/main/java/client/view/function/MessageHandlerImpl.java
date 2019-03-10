package client.view.function;

import java.io.IOException;
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
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.enums.ChatType;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.WxMessageTool;
import client.view.QRView;
import client.view.WxbotView;
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
	protected ChatServer chatServer;

	private ObjectMapper jsonMapper = new ObjectMapper();
	{
		jsonMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
		jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
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

	}

	@Override
	public void onLogin(Member member) {
		logger.debug("用户登录");
		logger.debug("用ID：" + member.getUserName());
		logger.debug("用户名：" + member.getNickName());
		Platform.runLater(() -> {
			qrView.close();
			WxbotView wxbotView = WxbotView.getInstance();
			wxbotView.onClose(e -> {
				
			});
			CookieStorage cookieStorage = wxbotView.getBrowser().getCookieStorage();
			wechatHttpService.getCookies().forEach((k, v) -> {
				cookieStorage.setSessionCookie("https://wx2.qq.com", k, v, ".qq.com", "/", false, false);
				cookieStorage.setSessionCookie("https://wx.qq.com", k, v, ".qq.com", "/", false, false);
			});
			cookieStorage.save();
			wxbotView.load();
		});
		try {
			logger.debug("individuals：");
			System.out.println(jsonMapper.writeValueAsString(cacheService.getIndividuals()));
			logger.debug("mediaPlatforms：");
			System.out.println(jsonMapper.writeValueAsString(cacheService.getMediaPlatforms()));
			logger.debug("chatRooms：");
			System.out.println(jsonMapper.writeValueAsString(cacheService.getChatRooms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLogout(Member member) {
		logger.debug("用户退出");
		logger.debug("用ID：" + member.getUserName());
		logger.debug("用户名：" + member.getNickName());
	}

	@Override
	public void onReceivingChatRoomTextMessage(Message message) {
		String content = MessageUtils.getChatRoomTextMessageContent(message.getContent());
		String userName = MessageUtils.getSenderOfChatRoomTextMessage(message.getContent());
		logger.debug("群聊文本消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + userName);
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + content);
		
		WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody(content));
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			WxMessageTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), userName);
			WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
		}
		
		try {
			System.out.println(jsonMapper.writeValueAsString(message));
			System.out.println(jsonMapper.writeValueAsString(chatRoom));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		//判断关键词是否开启
		if (SettingFunction.SETTING.getSwitchs().isGlobalKeyword() && chatRoom != null) {
			
			// 全域关键词自动回复
			Map<String, String> keyMap = KeywordFunction.KEY_MAP.get(Config.GLOBA_SEQ);
			if (keyMap != null) {
				for (Map.Entry<String, String> entry : keyMap.entrySet()) {
					if (content.contains(entry.getKey())) {
						try {
							//回复关键词并写聊天记录
							wechatHttpService.sendText(chatRoom.getUserName(), entry.getValue());
							chatServer.writeSendTextRecord(chatRoom, entry.getValue());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			// 分群关键词自动回复
			keyMap = KeywordFunction.KEY_MAP.get(chatRoom.getSeq());
			if (keyMap != null) {
				for (Map.Entry<String, String> entry : keyMap.entrySet()) {
					if (content.contains(entry.getKey())) {
						try {
							//回复关键词并写聊天记录
							wechatHttpService.sendText(message.getFromUserName(), entry.getValue());
							chatServer.writeSendTextRecord(chatRoom, entry.getValue());
						} catch (IOException e) {
							e.printStackTrace();
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
		
		// 下载图片文件 到本地
		fullImageUrl = wechatHttpService.download(fullImageUrl, message.getMsgId() + ".jpg", MessageType.IMAGE);
		thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + "_thumb.jpg", MessageType.IMAGE);
		// 处理消息
		WxMessage msg = new WxMessage(MessageType.IMAGE.getCode(), new WxMessageBody(fullImageUrl, thumbImageUrl, message.getImgHeight(), message.getImgWidth())); 
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			WxMessageTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
		}
	}

	@Override
	public void onReceivingChatRoomEmoticonMessage(Message message, String emoticonUrl) {
		logger.debug("群聊表情消息");
		logger.debug("from chatroom: " + message.getFromUserName());
		logger.debug("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.debug("emoticonUrl:" + emoticonUrl);
		
		// 下载表情文件 到本地
		emoticonUrl = wechatHttpService.download(emoticonUrl, message.getMsgId() + ".gif", MessageType.EMOTICON);
		// 处理消息
		WxMessage msg = new WxMessage(MessageType.EMOTICON.getCode(), new WxMessageBody(emoticonUrl, message.getImgHeight(), message.getImgWidth()));
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			WxMessageTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
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
			WxMessageTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
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
			WxMessageTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
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
		WxMessage msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(MessageType.APP, mediaUrl, message.getFileName(), FileUtil.getFileSizeString(Long.valueOf(message.getFileSize()))));		
		Contact chatRoom = new Contact();
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			chatRoom = cacheService.getChatRoom(message.getToUserName());
			WxMessageTool.sendMessage(chatRoom, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
		} else {
			chatRoom = cacheService.getChatRoom(message.getFromUserName());
			Contact sender = cacheService.searchContact(chatRoom.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
			WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
		}
	}

	@Override
	public void onReceivingPrivateTextMessage(Message message) {
		logger.debug("私聊文本消息");
		logger.debug("from: " + message.getFromUserName());
		logger.debug("to: " + message.getToUserName());
		logger.debug("content:" + message.getContent());
		
		WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody(message.getContent()));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			WxMessageTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
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
		WxMessage msg = new WxMessage(MessageType.IMAGE.getCode(), new WxMessageBody(fullImagePath, thumbImagePath, message.getImgHeight(), message.getImgWidth()));
		// 处理消息
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			WxMessageTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					WxMessageTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
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
		// 下载表情文件 到本地
		emoticonUrl = wechatHttpService.download(emoticonUrl, message.getMsgId() + ".gif", MessageType.EMOTICON);
		// 处理表情消息
		WxMessage msg = new WxMessage(MessageType.EMOTICON.getCode(), new WxMessageBody(emoticonUrl, message.getImgHeight(), message.getImgWidth()));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			WxMessageTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					WxMessageTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
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
			WxMessageTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					WxMessageTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
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
			WxMessageTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					WxMessageTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
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
		WxMessage msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(MessageType.APP, mediaUrl, message.getFileName(), FileUtil.getFileSizeString(Long.valueOf(message.getFileSize()))));
		if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
			Contact recipient = cacheService.getContact(message.getToUserName());
			WxMessageTool.sendMessage(recipient, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
		} else {
			Contact sender = cacheService.getContact(message.getFromUserName());
			WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
		}
		
		//转发给群
		if(cacheService.getOwner().getUserName().equals(message.getFromUserName())){
			cacheService.getChatRooms().stream().filter(c -> SettingFunction.SETTING.getForwards().contains(c.getSeq())).forEach(c -> {
				try {
					wechatHttpService.forwardMsg(c.getUserName(), message);
					WxMessageTool.sendMessage(c, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	@Override
	public void onMembersSeqChanged(Map<String, String> seqMap) {
		logger.debug("收到联系人seq变动消息");
		System.out.println(seqMap);
		Platform.runLater(() -> {
			WxbotView wxbotView = WxbotView.getInstance();
			JSObject app = wxbotView.getBrowser().executeJavaScriptAndReturnValue("app").asObject();
			JSValue syncSeq = app.getProperty("syncSeq");
			try {
				syncSeq.asFunction().invokeAsync(app, new JSONString(jsonMapper.writeValueAsString(seqMap)));
				WxMessageTool.execContactsChanged("您有好友更新消息，已成功刷新列表");
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
		// seq变动，重命名聊天记录文件夹
		seqMap.forEach((k, v) -> {
			String oldPath = Config.CHAT_RECORD_PATH + k;
			String newPath = Config.CHAT_RECORD_PATH + v;
			FileUtil.renameFile(oldPath, newPath);
		});
		//变更关键词seq
		WxMessageTool.syncSeq(seqMap);
		WxMessageTool.execContactsChanged("您有好友更新消息，已成功刷新列表");
	}

	@Override
	public boolean onReceivingFriendInvitation(RecommendInfo info) {
		logger.debug("收到好友请求消息");
		logger.debug("recommendinfo content:" + info.getContent());
		// 默认接收所有的邀请
		return SettingFunction.SETTING.getSwitchs().isAutoAcceptFriend();
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
		WxMessageTool.execContactsChanged("发现新群消息，已成功刷新列表");
	}

	@Override
	public void onChatRoomsDeleted(Set<Contact> chatRooms) {
		logger.debug("群被删除消息");
		chatRooms.forEach(x -> logger.debug(x.getUserName()));
		WxMessageTool.execContactsChanged("群被删除，已成功刷新列表");
	}

	@Override
	public void onNewFriendsFound(Set<Contact> contacts) {
		logger.debug("发现新好友消息");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		WxMessageTool.execContactsChanged("发现新好友消息，已成功刷新列表");
	}

	@Override
	public void onFriendsDeleted(Set<Contact> contacts) {
		logger.debug("好友被删除消息");
		contacts.forEach(x -> {
			logger.debug(x.getUserName());
			logger.debug(x.getNickName());
		});
		WxMessageTool.execContactsChanged("好友被删除消息，已成功刷新列表");
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
	}

	@Override
	public void onStatusNotifyEnterSession(Message message) {
	}

	@Override
	public void onStatusNotifyInited(Message message) {
	}

	@Override
	public void onStatusNotifyQuitSession(Message message) {
	}

	@Override
	public void onStatusNotifySyncConv(Message message) {
	}

	@Override
	public void onFriendVerify(Contact contact) {
		logger.debug("onFriendVerify 消息");
		try {
			System.out.println(jsonMapper.writeValueAsString(contact));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFriendBlacklist(Contact contact) {
		// TODO Auto-generated method stub
		
	}
}
