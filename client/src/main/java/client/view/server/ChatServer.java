package client.view.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.cherry.jeeves.domain.response.SendMsgResponse;
import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.domain.shared.Message;
import com.cherry.jeeves.enums.AppMessageType;
import com.cherry.jeeves.enums.MessageType;
import com.cherry.jeeves.utils.MessageUtils;

import client.enums.ChatType;
import client.enums.Direction;
import client.pojo.Msg;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.pojo.disruptor.RecordEvent;
import client.utils.Config;
import client.utils.EmojiUtil;
import client.utils.FileUtil;
import client.utils.Tools;
import client.utils.WxMessageTool;

@Component
public class ChatServer extends BaseServer {
	private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
	
	@Value("${wechat.url.get_msg_img}")
    private String WECHAT_URL_GET_MSG_IMG;
    @Value("${wechat.url.get_voice}")
    private String WECHAT_URL_GET_VOICE;
    @Value("${wechat.url.get_video}")
    private String WECHAT_URL_GET_VIDEO;
    @Value("${wechat.url.get_media}")
    private String WECHAT_URL_GET_MEDIA;
    private final static String PREFIX_IMG = "image/";
	private final static String PREFIX_VIDEO = "video/";
	private final static String PREFIX_GIF = "image/gif";
	@Autowired
	private WxMessageTool msgTool;
	
	public static Long POINT = null;
	
	public WxMessage createReceiveMsg(Message message, MessageType msgType, String fullImageUrl, String thumbImageUrl){
		boolean isChatRoom = (message.getFromUserName() != null && message.getFromUserName().startsWith("@@"))
				|| (message.getToUserName() != null && message.getToUserName().startsWith("@@"));
		WxMessage msg;
		String content;
		if (isChatRoom) {
			content = MessageUtils.getChatRoomTextMessageContent(message.getContent());
		} else {
			content = message.getContent();
		}
		switch (msgType) {
		// 文本
		case TEXT:
			msg = new WxMessage(msgType.getCode(), new WxMessageBody(EmojiUtil.formatFace(content)));
			break;
		// 图片
		case IMAGE:
			// 下载图片文件 到本地
//			fullImageUrl = wechatHttpService.download(fullImageUrl, message.getMsgId() + ".jpg", MessageType.IMAGE);
//			thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + "_thumb.jpg", MessageType.IMAGE);
			msg = new WxMessage(msgType.getCode(), new WxMessageBody(fullImageUrl, thumbImageUrl));
			msg.getBody().setFileName(Config.IMG_PATH + message.getMsgId() + ".jpg");
			break;
		// 表情
		case EMOTICON:
			// 下载表情文件 到本地
//			emoticonUrl = wechatHttpService.download(emoticonUrl, message.getMsgId() + ".gif", MessageType.EMOTICON);
			if (content == null || content.trim().isEmpty()) {
				msg = new WxMessage(MessageType.TEXT.getCode(), new WxMessageBody("[发送了一个表情，请在手机上查看]"));
			} else {
				msg = new WxMessage(msgType.getCode(), new WxMessageBody(fullImageUrl));
				msg.getBody().setFileName(Config.IMG_PATH + message.getMsgId() + ".gif");
			}
			break;
		// 语音
		case VOICE:
			// 下载语音文件 到本地
//			voiceUrl = wechatHttpService.download(voiceUrl, message.getMsgId() + ".mp3", MessageType.VOICE);
			msg = new WxMessage(msgType.getCode(), new WxMessageBody(fullImageUrl, message.getVoiceLength()));
			msg.getBody().setFileName(Config.VOICE + message.getMsgId() + ".mp3");
			break;
		// 视频
		case VIDEO:
//			videoUrl = wechatHttpService.download(videoUrl, message.getMsgId() + ".mp4", MessageType.VIDEO);
//			thumbImageUrl = wechatHttpService.download(thumbImageUrl, message.getMsgId() + ".jpg", MessageType.VIDEO);
			msg = new WxMessage(msgType.getCode(), new WxMessageBody(fullImageUrl, thumbImageUrl));
			msg.getBody().setFileName(Config.FILE_PATH + message.getMsgId() + ".mp4");
			break;
		// 多媒体
		default:
			// 下载文件
//			mediaUrl = wechatHttpService.download(mediaUrl, message.getMsgId() + "_" + message.getFileName(), MessageType.APP);
			if(message.getAppMsgType() == AppMessageType.URL.getCode()){
				msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(fullImageUrl, thumbImageUrl));
				msg.getBody().setFileName(message.getFileName());
				String xml = StringEscapeUtils.unescapeXml(content).replace("<br/>", "");
				try {
					SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
					parser.parse(new InputSource(new StringReader(xml)), new DefaultHandler() {
						
						private String currentTag = null;
						@Override
						public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
							super.startElement(uri, localName, qName, attributes);
							currentTag = qName;
						}
						@Override
						public void endElement(String uri, String localName, String qName) throws SAXException {
							super.endElement(uri, localName, qName);
							currentTag = "";
						}
						
						@Override
						public void characters(char[] ch, int start, int length) throws SAXException {
							super.characters(ch, start, length);
							String value = new String(ch, start, length);
							switch (currentTag) {
								case "des":
									msg.getBody().setFileSize(value);
									break;
							}
						}
					});
				} catch (IOException | ParserConfigurationException | SAXException e) {
					e.printStackTrace();
				}
				
			}else{
				long fileSize = message.getFileSize() == null || message.getFileSize().isEmpty()?0:Long.valueOf(message.getFileSize());
				msg = new WxMessage(MessageType.APP.getCode(), new WxMessageBody(fullImageUrl, message.getFileName(), FileUtil.getFileSizeString(fileSize)));
				msg.getBody().setThumbImageUrl(Config.FILE_PATH +message.getMsgId() + "_" + message.getFileName());
			}
			msg.setAppMsgType(message.getAppMsgType());
			break;
		}
		msg.setMsgId(message.getMsgId());
		
		return msg;
	}
	
	public void writeReceiveRecord(Message message, WxMessage msg){
		boolean isChatRoom = (message.getFromUserName() != null && message.getFromUserName().startsWith("@@"))
				|| (message.getToUserName() != null && message.getToUserName().startsWith("@@"));
		
		Contact contact;
		logger.debug("[writeReceiveRecord]");
		if(isChatRoom){
			if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
				contact = cacheService.getChatRoom(message.getToUserName());
				msg.getBody().setAbsolute(true);
				if(contact != null)
					msgTool.sendMessage(contact, msg, cacheService.getOwner().getNickName(), ChatType.GROUPCHAT.getCode());
			} else {
				contact = cacheService.getChatRoom(message.getFromUserName());
				if(contact != null){
					Contact sender = cacheService.searchContact(contact.getMemberList(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent()));
					msgTool.receiveGroupMessage(contact, sender, msg);
				}
			}
		} else {
			if (message.getFromUserName().equals(cacheService.getOwner().getUserName())) {
				contact = cacheService.getContact(message.getToUserName());
				msg.getBody().setAbsolute(true);
				if(contact != null)
					msgTool.sendMessage(contact, msg, cacheService.getOwner().getNickName(), ChatType.CHAT.getCode());
			} else {
				contact = cacheService.getContact(message.getFromUserName());
				if(contact != null)
					msgTool.receiveMessage(contact, cacheService.getOwner(), msg);
			}
		}
		logger.debug("[writeReceiveRecord done]");
	}
	/**  
	* @Title: writeReceiveRecord  
	* @Description: 通用写接收聊天记录
	* @param @param message
	* @param @param msgType
	* @param @param fullImageUrl
	* @param @param thumbImageUrl    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void writeReceiveRecord(Message message, MessageType msgType, String fullImageUrl, String thumbImageUrl){
		writeReceiveRecord(message, createReceiveMsg(message, msgType, fullImageUrl, thumbImageUrl));
	}
	
	/**  
	* @Title: writeSendTextRecord  
	* @Description: 发送文本聊天记录
	* @param @param contact
	* @param @param content    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void writeSendTextRecord(Contact contact, String content, String msgId){
		try {
	        WxMessage message = new WxMessage();
	        message.setMsgId(msgId);
	        message.setBody(new WxMessageBody(content));
	        message.setMsgType(MessageType.TEXT.getCode());
			message.setTo(contact.getUserName());
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setTimestamp(Tools.getTimestamp());
			message.setChatType(contact.getUserName().startsWith("@@")?2:1);
			String json = JSON_MAPPER.writeValueAsString(message);
			FileUtil.writeFile(new RecordEvent(contact.getSeq(), Tools.getSysDate() + ".txt", json));
			msgTool.avatarBadge(contact.getUserName(), json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	  
	/**  
	* @Title: writeSendAppRecord  
	* @Description: 发送文件聊天记录
	* @param @param contact
	* @param @param localFileUrl
	* @param @param response    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void writeSendAppRecord(Contact contact, String localFileUrl, String msgId, boolean absolute){
		try {
	        WxMessage message = new WxMessage();
	        message.setMsgId(msgId);
        	File file = new File(localFileUrl);
        	String contentType = Files.probeContentType(file.toPath());
        	//表情
	        if (contentType != null && contentType.contains(PREFIX_GIF)) {
	        	message.setMsgType(MessageType.EMOTICON.getCode());
	        	message.setBody(new WxMessageBody(localFileUrl, localFileUrl));
	        	message.getBody().setFileName(localFileUrl);
	        }
	        //图片
	        else if (contentType != null && contentType.contains(PREFIX_IMG)) {
	        	message.setMsgType(MessageType.IMAGE.getCode());
	        	message.setBody(new WxMessageBody(localFileUrl, localFileUrl));
	        	message.getBody().setFileName(localFileUrl);
	        }
	        //视频
	        else if (contentType != null && contentType.contains(PREFIX_VIDEO)) {
	        	//缩略图本地路径
        		String thumbImageName = Config.IMG_PATH + msgId + ".jpg";
        		String thumbImageUrl = null;
        		//缩略图不存在则下载
        		if(!new File(thumbImageName).exists()){
        			thumbImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), msgId, cacheService.getsKey()) + "&type=slave";
        			thumbImageUrl = wechatService.download(thumbImageUrl, msgId+".jpg", MessageType.IMAGE);
        		}
        		message.setMsgType(MessageType.VIDEO.getCode());
        		message.setBody(new WxMessageBody(localFileUrl, thumbImageUrl));
        		message.getBody().setFileName(localFileUrl);
	        }
	        //附件
	        else {
	        	message.setMsgType(MessageType.APP.getCode());
	        	message.setBody(new WxMessageBody(localFileUrl, file.getName(), FileUtil.getFileSizeString(file.length())));
	        	message.getBody().setThumbImageUrl(localFileUrl);
	        }
	        message.getBody().setAbsolute(absolute);
			message.setTo(contact.getNickName());
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setTimestamp(Tools.getTimestamp());
			message.setChatType(contact.getUserName().startsWith("@@")?2:1);
			String json = JSON_MAPPER.writeValueAsString(message);
			FileUtil.writeFile(new RecordEvent(contact.getSeq(), Tools.getSysDate() + ".txt", json));
			if(!absolute){
				message.getBody().setContent(Config.ROOT + localFileUrl);
				message.getBody().setThumbImageUrl(Config.ROOT + message.getBody().getThumbImageUrl());
				if(message.msgType != MessageType.APP.getCode())
					message.getBody().setFileName(Config.ROOT + localFileUrl);
				json = JSON_MAPPER.writeValueAsString(message);
			}
			msgTool.avatarBadge(contact.getUserName(), json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发图片消息  
	 * @param contact
	 * @param msg
	 */
	public void sendImg(Contact contact, Msg msg) {
		MessageType msgType = msg.getMsgType();
		String msgId = null;
		SendMsgResponse response = null;
		try {
			if(msg.getMediaCache() == null){
				msg.setMediaCache(wechatService.uploadMedia(contact.getUserName(), msg.getContent()));
			}
			response = wechatService.forwardAttachMsg(contact.getUserName(), msg.getMediaCache(), msgType);
			if(msgId == null)
				msgId = response.getMsgID();
			//写转发消息聊天记录
			writeSendAppRecord(contact, msg.getContent(), msgId, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  
	/**  
	* @Title: sendGloba  
	* @Description: 发送消息到群列表
	* @param @param content
	* @param @param msgType
	* @param @throws IOException    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void sendGloba(Collection<Contact> contacts, Msg msg) {
		MessageType msgType = msg.getMsgType();
		String msgId = null;
		SendMsgResponse response = null;
		for (Contact chatRoom : contacts) {
			if (msgType == MessageType.TEXT) {
				try {
					response = wechatService.sendText(chatRoom.getUserName(), msg.getContent());
					writeSendTextRecord(chatRoom, msg.getContent(), response.getMsgID());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					if(msg.getMediaCache() == null){
						msg.setMediaCache(wechatService.uploadMedia(chatRoom.getUserName(), Config.ATTCH_PATH+msg.getContent()));
					}
					response = wechatService.forwardAttachMsg(chatRoom.getUserName(), msg.getMediaCache(), msgType);
					if(msgId == null)
						msgId = response.getMsgID();
					//写转发消息聊天记录
					writeSendAppRecord(chatRoom, Config.ATTCH_PATH+msg.getContent(), msgId, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public List<WxMessage> readRecord(String path) throws IOException {
		// 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
		LinkedList<WxMessage> link = new LinkedList<>();

		// 目录是否存在
		if (!(new File(path)).exists()) {
			return link;
		}

		RandomAccessFile rf = null;
		WxMessage msg;
		try {
			rf = new RandomAccessFile(path, "r");
			long len = rf.length();
			long start = rf.getFilePointer();
			POINT = POINT == null?start + len - 1:POINT;
			StringBuffer line = new StringBuffer();
			int count = 0;
			rf.seek(POINT);
			int c = -1;
			
			while (POINT > start) {
				c = rf.read();
				if (c == '\n' || c == '\r') {
					if(line.length() > 0) {
						msg = BaseServer.JSON_MAPPER.readValue(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"), WxMessage.class);
						if(msg.direction == Direction.SEND.getCode() && msg.msgType != MessageType.TEXT.getCode() && !msg.getBody().isAbsolute()){
							msg.getBody().setContent(Config.ROOT + msg.getBody().getContent());
							msg.getBody().setThumbImageUrl(Config.ROOT + msg.getBody().getThumbImageUrl());
							if(msg.msgType != MessageType.APP.getCode())
								msg.getBody().setFileName(Config.ROOT + msg.getBody().getContent());
						}
						link.addFirst(msg);
						line.setLength(0);
						count++;
					}
					POINT--;
				} else {
					line.insert(0, (char)c);
				}
				POINT--;
				rf.seek(POINT);
				if (POINT == 0) {// 当文件指针退至文件开始处，输出第一行
					line.insert(0, (char)rf.read());
					msg = BaseServer.JSON_MAPPER.readValue(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"), WxMessage.class);
					if(msg.direction == Direction.SEND.getCode() && msg.msgType != MessageType.TEXT.getCode() && !msg.getBody().isAbsolute()){
						msg.getBody().setContent(Config.ROOT + msg.getBody().getContent());
						msg.getBody().setThumbImageUrl(Config.ROOT + msg.getBody().getThumbImageUrl());
						if(msg.msgType != MessageType.APP.getCode())
							msg.getBody().setFileName(Config.ROOT + msg.getBody().getContent());
					}
					link.addFirst(msg);
				}
				if(count == 30)
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rf != null)
					rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return link;
	}
	
	public static void main(String[] args) throws Exception {
		List<WxMessage> list = new ChatServer().readRecord("resource/674210262/20190316.txt");
		list.forEach(e -> {
			System.out.println(e);
		});
		System.out.println("\n\n\n\n\n\n");
		list = new ChatServer().readRecord("resource/674210262/20190316.txt");
		list.forEach(e -> {
			System.out.println(e);
		});
	}
}
