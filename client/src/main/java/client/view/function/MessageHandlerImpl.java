package client.view.function;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.ChatRoomMember;
import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.Member;
import com.cherry.jeeves.domain.shared.Message;
import com.cherry.jeeves.domain.shared.RecommendInfo;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.MessageHandler;
import com.cherry.jeeves.service.WechatHttpService;
import com.cherry.jeeves.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.controller.LoginController;
import client.view.QRView;
import client.view.WxbotView;
import javafx.application.Platform;
import netscape.javascript.JSObject;

@Component
public class MessageHandlerImpl implements MessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerImpl.class);
	@Autowired
	private WechatHttpService wechatHttpService;
	@Autowired
	private CacheService cacheService;
	
	private ObjectMapper jasonMapper = new ObjectMapper();
	
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
		Platform.runLater(() -> {
			qrView.close();
			WxbotView wxbotView = WxbotView.getInstance();
			wxbotView.onClose(e -> {
				LoginController.LOGIN_STAGE.show();
			});
			wxbotView.load();
		});
		
	}

	@Override
	public void onLogin(Member member) {
		logger.info("用户登录");
		logger.info("用ID：" + member.getUserName());
		logger.info("用户名：" + member.getNickName());
		
		try {
			logger.info("individuals：" );
			System.out.println(jasonMapper.writeValueAsString(cacheService.getIndividuals()));
			logger.info("mediaPlatforms：");
			System.out.println(jasonMapper.writeValueAsString(cacheService.getMediaPlatforms()));
			logger.info("chatRooms：");
			System.out.println(jasonMapper.writeValueAsString(cacheService.getChatRooms()));
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
		logger.info("群聊文本消息");
		logger.info("from chatroom: " + message.getFromUserName());
		logger.info("from person: " + MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
		logger.info("to: " + message.getToUserName());
		logger.info("content:" + MessageUtils.getChatRoomTextMessageContent(message.getContent()));
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

		if (message.getFromUserName().equals(cacheService.getOwner().getUserName()))
			return;
		// 将原文回复给对方
		try {
			wechatHttpService.sendText(message.getFromUserName(), message.getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
		logger.info("私聊图片消息");
		logger.info("thumbImageUrl:" + thumbImageUrl);
		logger.info("fullImageUrl:" + fullImageUrl);

		if (message.getFromUserName().equals(cacheService.getOwner().getUserName()))
			return;
		// 将原文回复给对方
		try {
			wechatHttpService.forwardMsg(message.getFromUserName(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceivingPrivateEmoticonMessage(Message message, String emoticonUrl) {
		logger.info("私聊表情消息");
		logger.info("emoticonUrl:" + emoticonUrl);

		if (message.getFromUserName().equals(cacheService.getOwner().getUserName()))
			return;
		// 将原文回复给对方
		try {
			wechatHttpService.forwardMsg(message.getFromUserName(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceivingPrivateVoiceMessage(Message message, String voiceUrl) {
		logger.info("私聊语音消息");
		logger.info("voiceUrl:" + voiceUrl);

		if (message.getFromUserName().equals(cacheService.getOwner().getUserName()))
			return;
		// 将原文回复给对方
		try {
			wechatHttpService.forwardMsg(message.getFromUserName(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceivingPrivateVideoMessage(Message message, String thumbImageUrl, String videoUrl) {
		logger.info("私聊视频消息");
		logger.info("videoUrl:" + videoUrl);

		if (message.getFromUserName().equals(cacheService.getOwner().getUserName()))
			return;
		// 将原文回复给对方
		try {
			wechatHttpService.forwardMsg(message.getFromUserName(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReceivingPrivateMediaMessage(Message message, String mediaUrl) {
		logger.info("私聊多媒体消息");
		logger.info("mediaUrl:" + mediaUrl);

		if (message.getFromUserName().equals(cacheService.getOwner().getUserName()))
			return;
		// 将原文回复给对方
		try {
			wechatHttpService.forwardMsg(message.getFromUserName(), message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onReceivingFriendInvitation(RecommendInfo info) {
		logger.info("收到好友请求消息");
		logger.info("recommendinfo content:" + info.getContent());
//        默认接收所有的邀请
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
	public void onChatRoomMembersChanged(Contact chatRoom, Set<ChatRoomMember> membersJoined,
			Set<ChatRoomMember> membersLeft) {
		logger.info("群成员变动消息");
		logger.info("群ID:" + chatRoom.getUserName());
		if (membersJoined != null && membersJoined.size() > 0) {
			logger.info("新加入成员:" + String.join(",",
					membersJoined.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
		}
		if (membersLeft != null && membersLeft.size() > 0) {
			logger.info("离开成员:" + String.join(",",
					membersLeft.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
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
}
