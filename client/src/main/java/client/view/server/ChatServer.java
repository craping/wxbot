package client.view.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.response.SendMsgResponse;
import com.cherry.jeeves.domain.response.UploadMediaResponse;
import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.enums.MessageType;

import client.enums.Direction;
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
			FileUtil.writeFile(Config.CHAT_RECORD_PATH + contact.getSeq(), Tools.getSysDate() + ".txt", jsonMapper.writeValueAsString(message));
			WxMessageTool.avatarBadge(contact.getSeq());
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
	        	message.setBody(new WxMessageBody(localFileUrl, 0, 0));
	        }
	        //图片
	        else if (contentType != null && contentType.contains(PREFIX_IMG)) {
	        	message.setMsgType(MessageType.IMAGE.getCode());
	        	message.setBody(new WxMessageBody(localFileUrl, localFileUrl, 0, 0));
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
	        	message.setBody(new WxMessageBody(MessageType.APP, localFileUrl, new File(localFileUrl).getName(), FileUtil.getFileSizeString(file.length())));
	        }
			message.setTo(contact.getUserName());
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setTimestamp(Tools.getTimestamp());
			message.setChatType(contact.getUserName().startsWith("@@")?2:1);
			FileUtil.writeFile(Config.CHAT_RECORD_PATH + contact.getSeq(), Tools.getSysDate() + ".txt", jsonMapper.writeValueAsString(message));
			WxMessageTool.avatarBadge(contact.getSeq());
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
	    
	public void sendGloba(Collection<Contact> contacts, String content, MessageType msgType) {
		//第一个消息的媒体数据
		UploadMediaResponse media = null;
		String msgId = null;
		for (Contact chatRoom : contacts) {
			if (msgType == MessageType.TEXT) {
				try {
					wechatService.sendText(chatRoom.getUserName(), content);
					writeSendTextRecord(chatRoom, content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					if(media == null){
						media = wechatService.uploadMedia(chatRoom.getUserName(), Config.ATTCH_PATH+content);
					}
					SendMsgResponse response = wechatService.forwardAttachMsg(chatRoom.getNickName(), media, msgType);
					if(msgId == null)
						msgId = response.getMsgID();
					//写转发消息聊天记录
					writeSendAppRecord(chatRoom, Config.ATTCH_PATH+content, msgId);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
