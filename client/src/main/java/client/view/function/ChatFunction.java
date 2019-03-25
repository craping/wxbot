package client.view.function;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.response.SendMsgResponse;
import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSFunction;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.enums.Direction;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.Tools;
import client.view.WxbotView;
import client.view.server.BaseServer;
import client.view.server.ChatServer;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

@Component
public abstract class ChatFunction extends ContactsFunction {

	private static final Logger logger = LoggerFactory.getLogger(ChatFunction.class);

	protected final FileChooser sendChooser = new FileChooser();

	protected File lastSendFile;
	
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
	private final static long MAX_FILE_SIZE = 104857600;
	private final static long MAX_VIDEO_SIZE = 26214400;
	
	private MediaPlayer mplayer;
	
	public ChatFunction() {
		super();
		sendChooser.setTitle("选择文件");
		sendChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
	}

	
	  
	/**  
	* @Title: openAppFile  
	* @Description: 打开文件选择对话框
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void openAppFile(String userName){
		Platform.runLater(() -> {
			if (lastSendFile != null && lastSendFile.isFile())
				sendChooser.setInitialDirectory(lastSendFile.getParentFile());
		
			lastSendFile = sendChooser.showOpenDialog(WxbotView.getInstance().getViewStage());
			if (lastSendFile == null)
				return;
			sendApp(userName, lastSendFile);
		});
	}
	/**
	 * @Title: sendApp 
	 * @Description: 发送文件消息 调用后会打开文件选择器 可以选择任何类型文件 
	 * @param userName用户ID 
	 * @return void 返回类型
	 * @throws
	 */

	public void sendApp(String userName, File file) {
        String contentType = null;  
        try {  
            contentType = Files.probeContentType(file.toPath());
	        
	        SendMsgResponse response;
	        // 发送表情
	        if (contentType != null && contentType.contains(PREFIX_GIF)) {
	        	response = wechatService.sendEmoticon(userName, file.getPath());
	        }
	        // 发送图片
	        else if (contentType != null && contentType.contains(PREFIX_IMG)) {
	        	response = wechatService.sendImage(userName, file.getPath());
	        }
	        // 发送视频
	        else if (contentType != null && contentType.contains(PREFIX_VIDEO)) {
	        	// 文件超限 25MB
				if (file.length() > MAX_VIDEO_SIZE) {
					WxbotView.getInstance().executeScript("app.$Message.error('发送的视频文件不能大于25M');");
					return;
				}
				response = wechatService.sendVideo(userName, file.getPath());
	        } else {
	        	// 文件超限 100MB
				if (file.length() > MAX_FILE_SIZE) {
					WxbotView.getInstance().executeScript("app.$Message.error('发送的文件不能大于100M');");
					return;
				}
				response = wechatService.sendApp(userName, file.getPath());
	        }
	        
	        chatServer.writeSendAppRecord(userName.contains("@@")?cacheService.getChatRoom(userName):cacheService.getContact(userName), file.getAbsolutePath(), response.getMsgID(), true);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取聊天记录
	 * @param seq
	 * @return
	 */
	public void chatRecord(String seq, String date, JSFunction function) {
		new Thread(() -> {
			try {
				String path = Config.CHAT_RECORD_PATH + seq + "/" + date.replace("-", "") + ".txt";
				List<WxMessage> link = chatServer.readRecord(path);
				function.invokeAsync(function, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(link)));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void resetChatRecord(String seq, String date, JSFunction function) {
		new Thread(() -> {
			try {
				String path = Config.CHAT_RECORD_PATH + seq + "/" + date.replace("-", "") + ".txt";
				ChatServer.POINT = null;
				List<WxMessage> link = chatServer.readRecord(path);
				function.invokeAsync(function, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(link)));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	/**
	 * @Title: sendText 
	 * @Description: 发送文本消息 
	 * @param @param userName 
	 * @param text 参数 
	 * @return void 返回类型 
	 * @throws
	 */

	public void sendText(String seq, String nickName, String userName, String content) {
		try {
			WxMessage message = new WxMessage();
			String timestamp = Tools.getTimestamp();
			message.setTimestamp(timestamp);
			message.setTo(nickName);
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setMsgType(MessageType.TEXT.getCode());
			message.setChatType(userName.startsWith("@@")?2:1);
			message.setBody(new WxMessageBody(content));
			String str = BaseServer.JSON_MAPPER.writeValueAsString(message);
			String path = Config.CHAT_RECORD_PATH + seq;
			FileUtil.writeFile(path, Tools.getSysDate() + ".txt", str);
			wechatService.sendText(userName == null ? cacheService.getOwner().getUserName() : userName, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取本地文件绝对路径
	 * 
	 * @param path 相对路径
	 * @return
	 */
	public String getRootPath() {
		return System.getProperty("user.dir")+"/";
	}

	/**
	 * 视频播放
	 * 
	 * @param path
	 */
	public void mediaPlay(JSObject jsonMsg) {
		try {
			WxMessage msg = BaseServer.JSON_MAPPER.readValue(jsonMsg.toJSONString(), WxMessage.class);
			File file;
			if(msg.msgType == MessageType.VIDEO.getCode()){
				file = new File(msg.getBody().getFileName());
				if (!file.exists()) {
					logger.debug("视频[" + file + "]不存在 下载");
					wechatService.download(msg.getBody().getContent(), msg.getMsgId()+".mp4", MessageType.VIDEO);
				}
				Tools.openFileByOs(file.toURI().toString());
			} else if(msg.msgType == MessageType.VOICE.getCode()){
				file = new File(msg.getBody().getFileName());
				if (!file.exists()) {
					logger.debug("音频[" + file + "]不存在 下载");
					wechatService.download(msg.getBody().getContent(), msg.getMsgId()+".mp3", MessageType.VOICE);
				}
				Media media = new Media(file.toURI().toString());
				if(mplayer != null)
					mplayer.stop();
				mplayer = new MediaPlayer(media);
				mplayer.play();
			} else {
				file = new File(msg.getBody().getThumbImageUrl());
				if (!file.exists()) {
					logger.debug("文件[" + file + "]不存在 下载");
					wechatService.download(msg.getBody().getContent(), msg.getMsgId()+"_"+msg.getBody().getFileName(), MessageType.APP);
				}
				Tools.openFileByOs(file.toURI().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws FileNotFoundException, IOException {
		String path = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\6366123229188429104.jpg";
		File picture = new File(path);
		if (!picture.exists()) {
			logger.error("目录文件[" + path + "]不存在");
		}
		BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
		int width = sourceImg.getWidth(); // 源图宽度
		int height = sourceImg.getHeight(); // 源图高度
		System.out.println(width + ":" + height);
	}
}
