package client.view.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.response.SendMsgResponse;
import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.enums.MessageType;

import client.enums.Direction;
import client.pojo.Msg;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.Tools;
import client.utils.WxMessageTool;

@Component
public class ChatServer extends BaseServer {
	
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
	/**  
	* @Title: writeSendTextRecord  
	* @Description: 发送文本聊天记录
	* @param @param contact
	* @param @param content    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void writeSendTextRecord(Contact contact, String content){
		try {
	        WxMessage message = new WxMessage();
	        message.setBody(new WxMessageBody(content));
	        message.setMsgType(MessageType.TEXT.getCode());
			message.setTo(contact.getUserName());
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setTimestamp(Tools.getTimestamp());
			message.setChatType(contact.getUserName().startsWith("@@")?2:1);
			String json = JSON_MAPPER.writeValueAsString(message);
			FileUtil.writeFile(Config.CHAT_RECORD_PATH + contact.getSeq(), Tools.getSysDate() + ".txt", json);
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
	    
	public void writeSendAppRecord(Contact contact, String localFileUrl, String msgId){
		try {
	        WxMessage message = new WxMessage();
        	File file = new File(localFileUrl);
        	String contentType = Files.probeContentType(file.toPath());
        	//表情
	        if (contentType != null && contentType.contains(PREFIX_GIF)) {
	        	message.setMsgType(MessageType.EMOTICON.getCode());
	        	message.setBody(new WxMessageBody(localFileUrl));
	        }
	        //图片
	        else if (contentType != null && contentType.contains(PREFIX_IMG)) {
	        	message.setMsgType(MessageType.IMAGE.getCode());
	        	message.setBody(new WxMessageBody(localFileUrl, localFileUrl));
	        }
	        //视频
	        else if (contentType != null && contentType.contains(PREFIX_VIDEO)) {
	        	//缩略图本地路径
        		String thumbImageName = "/img/"+msgId+".jpg";
        		String thumbImageUrl = null;
        		//缩略图不存在则下载
        		if(!new File(thumbImageName).exists()){
        			thumbImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), msgId, cacheService.getsKey()) + "&type=slave";
        			thumbImageUrl = wechatService.download(thumbImageUrl, msgId+".jpg", MessageType.IMAGE);
        		}
        		message.setMsgType(MessageType.IMAGE.getCode());
        		message.setBody(new WxMessageBody(localFileUrl, thumbImageUrl));
	        }
	        //附件
	        else {
	        	message.setBody(new WxMessageBody(localFileUrl, file.getName(), FileUtil.getFileSizeString(file.length())));
	        }
			message.setTo(contact.getUserName());
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setTimestamp(Tools.getTimestamp());
			message.setChatType(contact.getUserName().startsWith("@@")?2:1);
			String json = JSON_MAPPER.writeValueAsString(message);
			FileUtil.writeFile(Config.CHAT_RECORD_PATH + contact.getSeq(), Tools.getSysDate() + ".txt", json);
			msgTool.avatarBadge(contact.getUserName(), json);
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
		MessageType msgType;
		switch (msg.getType()) {
		case 1:
			msgType = MessageType.TEXT;
			break;
		case 2:
			msgType = MessageType.IMAGE;
			break;
		case 3:
			msgType = MessageType.EMOTICON;
			break;
		case 4:
			msgType = MessageType.VIDEO;
			break;
		default:
			msgType = MessageType.APP;
			break;
		}
		
		String msgId = null;
		for (Contact chatRoom : contacts) {
			if (msgType == MessageType.TEXT) {
				try {
					wechatService.sendText(chatRoom.getUserName(), msg.getContent());
					writeSendTextRecord(chatRoom, msg.getContent());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					if(msg.getMediaCache() == null){
						msg.setMediaCache(wechatService.uploadMedia(chatRoom.getUserName(), Config.ATTCH_PATH+msg.getContent()));
					}
					SendMsgResponse response = wechatService.forwardAttachMsg(chatRoom.getNickName(), msg.getMediaCache(), msgType);
					if(msgId == null)
						msgId = response.getMsgID();
					//写转发消息聊天记录
					writeSendAppRecord(chatRoom, Config.ATTCH_PATH+msg.getContent(), msgId);
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
						link.addFirst(BaseServer.JSON_MAPPER.readValue(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"), WxMessage.class));
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
					link.addFirst(BaseServer.JSON_MAPPER.readValue(new String(line.toString().getBytes("ISO-8859-1"), "utf-8"), WxMessage.class));
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
