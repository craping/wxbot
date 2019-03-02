package client.view.function;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
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
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.controller.LoginController;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.WxMessageTool;
import client.view.QRView;
import client.view.WxbotView;
import javafx.application.Platform;

@Component
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerImpl.class);
	@Autowired
	private WechatHttpService wechatHttpService;
	@Autowired
	private CacheService cacheService;

	private ObjectMapper jsonMapper = new ObjectMapper();
	{
		jsonMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
	}
	private QRView qrView;

	@Override
	public void onQR(byte[] qrData) {
		logger.info("获取登录二维码");
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
		logger.info("用户已扫码");
		logger.info("头像：" + headImgBase64);
	}

	@Override
	public void onExpired() {
		logger.info("二维码过期");
		Platform.runLater(() -> {
			qrView.expired();
		});
	}

	@Override
	public void onConfirmation() {
		logger.info("确认登录");

	}

	@Override
	public void onLogin(Member member) {
		logger.info("用户登录");
		logger.info("用ID：" + member.getUserName());
		logger.info("用户名：" + member.getNickName());
		Platform.runLater(() -> {
			qrView.close();
			WxbotView wxbotView = WxbotView.getInstance();
			wxbotView.onClose(e -> {
				LoginController.LOGIN_STAGE.show();
			});
			wxbotView.load();
			CookieStorage cookieStorage = wxbotView.getBrowser().getCookieStorage();
			wechatHttpService.getCookies().forEach((k, v) -> {
				cookieStorage.setSessionCookie("https://wx2.qq.com", k, v, ".qq.com", "/", false, false);
				cookieStorage.setSessionCookie("https://wx.qq.com", k, v, ".qq.com", "/", false, false);
			});
			cookieStorage.save();
		});
		try {
			logger.info("individuals：");
			System.out.println(jsonMapper.writeValueAsString(cacheService.getIndividuals()));
			logger.info("mediaPlatforms：");
			System.out.println(jsonMapper.writeValueAsString(cacheService.getMediaPlatforms()));
			logger.info("chatRooms：");
			System.out.println(jsonMapper.writeValueAsString(cacheService.getChatRooms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLogout(Member member) {
		logger.info("用户退出");
		logger.info("用ID：" + member.getUserName());
		logger.info("用户名：" + member.getNickName());
	}

	@Override
	public void onReceivingChatRoomTextMessage(Message message) {
		String content = MessageUtils.getChatRoomTextMessageContent(message.getContent());
		String userName = MessageUtils.getSenderOfChatRoomTextMessage(message.getContent());
		logger.info("群聊文本消息");
		logger.info("from chatroom: " + message.getFromUserName());
		logger.info("from person: " + userName);
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + content);

		Contact chatRoom = cacheService.getChatRoom(message.getFromUserName());
		Contact sender = cacheService.searchContact(chatRoom.getMemberList(), userName);
		WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody(content));
		WxMessageTool.receiveGroupMessage(chatRoom, sender, msg);
		
		try {
			System.out.println(jsonMapper.writeValueAsString(message));
			System.out.println(jsonMapper.writeValueAsString(chatRoom));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		if (chatRoom != null) {
			// 全域关键词自动回复
			Map<String, String> keyMap = KeywordFunction.keyMap.get(KeywordFunction.GLOBA_SEQ);
			if (keyMap != null) {
				for (Map.Entry<String, String> entry : keyMap.entrySet()) {
					if (content.contains(entry.getKey())) {
						try {
							wechatHttpService.sendText(message.getFromUserName(), entry.getValue());
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}

			// 分群关键词自动回复
			keyMap = KeywordFunction.keyMap.get(chatRoom.getSeq());
			if (keyMap != null) {
				for (Map.Entry<String, String> entry : keyMap.entrySet()) {
					if (content.contains(entry.getKey())) {
						try {
							wechatHttpService.sendText(message.getFromUserName(), entry.getValue());
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.info("群聊图片消息");
		logger.info("thumbImageUrl:" + thumbImageUrl);
		logger.info("fullImageUrl:" + fullImageUrl);
	}

	@Override
	public void onReceivingChatRoomEmoticonMessage(Message message, String emoticonUrl) {
		logger.info("群聊表情消息");
		logger.info("from chatroom: " + message.getFromUserName());
		logger.info("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.info("emoticonUrl:" + emoticonUrl);
	}

	@Override
	public void onReceivingChatRoomVoiceMessage(Message message, String voiceUrl) {
		logger.info("群聊语音消息");
		logger.info("from chatroom: " + message.getFromUserName());
		logger.info("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.info("voiceUrl:" + voiceUrl);
	}

	@Override
	public void onReceivingChatRoomVideoMessage(Message message, String thumbImageUrl, String videoUrl) {
		logger.info("群聊视频消息");
		logger.info("from chatroom: " + message.getFromUserName());
		logger.info("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.info("url:" + thumbImageUrl);
		logger.info("videoUrl" + videoUrl);
	}

	@Override
	public void onReceivingChatRoomMediaMessage(Message message, String mediaUrl) {
		logger.info("群聊多媒体消息");
		logger.info("from chatroom: " + message.getFromUserName());
		logger.info("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
		logger.info("mediaUrl:" + mediaUrl);
	}

	@Override
	public void onReceivingPrivateTextMessage(Message message) {
		logger.info("私聊文本消息");
		logger.info("from: " + message.getFromUserName());
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + message.getContent());

		Contact sender = cacheService.getContact(message.getFromUserName());
		WxMessage msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody(message.getContent()));
		WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
	}

	@Override
	public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.info("私聊图片消息");
		logger.info("thumbImageUrl:" + thumbImageUrl);
		logger.info("fullImageUrl:" + fullImageUrl);

		// 下载图片文件 到本地
		fullImageUrl = wechatHttpService.download(fullImageUrl, message.getMsgId() + ".jpg", MessageType.IMAGE);
		thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + "_thumb.jpg", MessageType.IMAGE);

		// 处理消息
		Contact sender = cacheService.getContact(message.getFromUserName());
		WxMessage msg = new WxMessage(MessageType.IMAGE.getCode(), new WxMessageBody(fullImageUrl, thumbImageUrl, message.getImgHeight(), message.getImgWidth()));
		WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
	}

	@Override
	public void onReceivingPrivateEmoticonMessage(Message message, String emoticonUrl) {
		logger.info("私聊表情消息");
		logger.info("emoticonUrl:" + emoticonUrl);

		// 下载表情文件 到本地
		emoticonUrl = wechatHttpService.download(emoticonUrl, message.getMsgId() + ".gif", MessageType.EMOTICON);
		// 处理消息
		Contact sender = cacheService.getContact(message.getFromUserName());
		WxMessage msg = new WxMessage(MessageType.EMOTICON.getCode(), new WxMessageBody(emoticonUrl, message.getImgHeight(), message.getImgWidth()));
		WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
	}

	@Override
	public void onReceivingPrivateVoiceMessage(Message message, String voiceUrl) {
		logger.info("私聊语音消息");
		logger.info("voiceUrl:" + voiceUrl);

		// 下载语音文件 到本地
		voiceUrl = wechatHttpService.download(voiceUrl, message.getMsgId() + ".mp3", MessageType.VOICE);

		Contact sender = cacheService.getContact(message.getFromUserName());
		WxMessage msg = new WxMessage(MessageType.VOICE.getCode(), new WxMessageBody(voiceUrl, message.getVoiceLength()));
		WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
	}

	@Override
	public void onReceivingPrivateVideoMessage(Message message, String thumbImageUrl, String videoUrl) {
		logger.info("私聊视频消息");
		logger.info("thumbImageUrl:" + thumbImageUrl);
		logger.info("videoUrl:" + videoUrl);

		// 下载视频文件、缩略图文件 到本地
		videoUrl = wechatHttpService.download(videoUrl, message.getMsgId() + ".mp4", MessageType.VIDEO);
		thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + ".jpg", MessageType.VIDEO);

		Contact sender = cacheService.getContact(message.getFromUserName());
		WxMessage msg = new WxMessage(MessageType.VIDEO.getCode(), new WxMessageBody(videoUrl, thumbImageUrl));
		WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
	}

	@Override
	public void onReceivingPrivateMediaMessage(Message message, String mediaUrl) {
		logger.info("私聊多媒体消息");
		logger.info("mediaUrl:" + mediaUrl);

		try {
			System.out.println(jsonMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		// 下载文件
		mediaUrl = wechatHttpService.download(mediaUrl, message.getMsgId() + "_" + message.getFileName(), MessageType.APP);
		
		Contact sender = cacheService.getContact(message.getFromUserName());
		WxMessage msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(MessageType.APP, mediaUrl, message.getFileName(), FileUtil.getFileSizeString(Long.valueOf(message.getFileSize()))));
		WxMessageTool.receiveMessage(sender, cacheService.getOwner(), msg);
	}

	@Override
	public void onMembersSeqChanged(Map<String, String> seqMap) {
		logger.info("收到联系人seq变动消息");
		System.out.println(seqMap);
		Platform.runLater(() -> {
			WxbotView wxbotView = WxbotView.getInstance();
			JSObject app = wxbotView.getBrowser().executeJavaScriptAndReturnValue("app").asObject();
			JSValue onMembersSeqChanged = app.getProperty("onMembersSeqChanged");
			try {
				onMembersSeqChanged.asFunction().invoke(app, new JSONString(jsonMapper.writeValueAsString(seqMap)));
				// seq变动，重命名聊天记录文件夹
				seqMap.forEach((k, v) -> {
					String oldPath = Config.CHAT_RECORD_PATH + k;
					String newPath = Config.CHAT_RECORD_PATH + v;
					FileUtil.renameFile(oldPath, newPath);
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public boolean onReceivingFriendInvitation(RecommendInfo info) {
		logger.info("收到好友请求消息");
		logger.info("recommendinfo content:" + info.getContent());
		// 默认接收所有的邀请
		return true;
	}

	@Override
	public void postAcceptFriendInvitation(Message message) throws IOException {
		logger.info("接受好友请求消息");
//        将该用户的微信号设置成他的昵称
//		String content = StringEscapeUtils.unescapeXml(message.getContent());
//		ObjectMapper xmlMapper = new XmlMapper();
//		FriendInvitationContent friendInvitationContent = xmlMapper.readValue(content, FriendInvitationContent.class);
		wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), message.getRecommendInfo().getNickName());
	}

	@Override
	public void onChatRoomMembersChanged(Contact chatRoom, Set<Contact> membersJoined, Set<Contact> membersLeft) {
		logger.info("群成员变动消息");
		logger.info("群ID:" + chatRoom.getUserName());
		if (membersJoined != null && membersJoined.size() > 0) {
			logger.info("新加入成员:" + String.join(",", membersJoined.stream().map(Contact::getNickName).collect(Collectors.toList())));
		}
		if (membersLeft != null && membersLeft.size() > 0) {
			logger.info("离开成员:" + String.join(",", membersLeft.stream().map(Contact::getNickName).collect(Collectors.toList())));
		}
	}

	@Override
	public void onNewChatRoomsFound(Set<Contact> chatRooms) {
		logger.info("发现新群消息");
		chatRooms.forEach(x -> logger.info(x.getUserName()));
	}

	@Override
	public void onChatRoomsDeleted(Set<Contact> chatRooms) {
		logger.info("群被删除消息");
		chatRooms.forEach(x -> logger.info(x.getUserName()));
	}

	@Override
	public void onNewFriendsFound(Set<Contact> contacts) {
		logger.info("发现新好友消息");
		contacts.forEach(x -> {
			logger.info(x.getUserName());
			logger.info(x.getNickName());
		});
	}

	@Override
	public void onFriendsDeleted(Set<Contact> contacts) {
		logger.info("好友被删除消息");
		contacts.forEach(x -> {
			logger.info(x.getUserName());
			logger.info(x.getNickName());
		});
	}

	@Override
	public void onNewMediaPlatformsFound(Set<Contact> mps) {
		logger.info("发现新公众号消息");
	}

	@Override
	public void onMediaPlatformsDeleted(Set<Contact> mps) {
		logger.info("公众号被删除消息");
	}

	@Override
	public void onRedPacketReceived(Contact contact) {
		logger.info("红包消息");
		if (contact != null) {
			logger.info("红包来自： " + contact.getNickName());
		}
	}

	@Override
	public void onStatusNotifyReaded(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusNotifyEnterSession(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusNotifyInited(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusNotifyQuitSession(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusNotifySyncConv(Message message) {
		// TODO Auto-generated method stub

	}
}
