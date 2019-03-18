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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.response.SendMsgResponse;
import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSFunction;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.enums.Direction;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.Tools;
import client.utils.WxMessageTool;
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

	@Autowired
	private WxMessageTool msgTool;
	
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
	    
	public void openAppFile(String seq, String nickName, String userName){
		Platform.runLater(() -> {
			if (lastSendFile != null && lastSendFile.isFile())
				sendChooser.setInitialDirectory(lastSendFile.getParentFile());
		
			lastSendFile = sendChooser.showOpenDialog(WxbotView.getInstance().getViewStage());
			if (lastSendFile == null)
				return;
			sendApp(seq, nickName, userName, lastSendFile);
		});
	}
	/**
	 * @Title: sendApp 
	 * @Description: 发送文件消息 调用后会打开文件选择器 可以选择任何类型文件 
	 * @param userName用户ID 
	 * @return void 返回类型
	 * @throws
	 */

	public void sendApp(String seq, String nickName, String userName, File file) {
        String contentType = null;  
        try {  
            contentType = Files.probeContentType(file.toPath());
	        
	        WxMessage message = new WxMessage();
	        // 发送表情
	        if (contentType != null && contentType.contains(PREFIX_GIF)) {
	        	wechatService.sendEmoticon(userName == null ? cacheService.getOwner().getUserName() : userName, file.getPath());
	        	message.setMsgType(MessageType.EMOTICON.getCode());
	        	message.setBody(new WxMessageBody(file.getPath()));
	        }
	        // 发送图片
	        else if (contentType != null && contentType.contains(PREFIX_IMG)) {
	        	SendMsgResponse response = wechatService.sendImage(userName == null ? cacheService.getOwner().getUserName() : userName, file.getPath());
	        	String thumbImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), response.getMsgID(), cacheService.getsKey()) + "&type=slave";
	        	String fullImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), response.getMsgID(), cacheService.getsKey());
	        	fullImageUrl = wechatService.download(fullImageUrl, response.getMsgID()+".jpg", MessageType.VIDEO);
        		thumbImageUrl = wechatService.download(thumbImageUrl, response.getMsgID()+"_thumb.jpg", MessageType.VIDEO);
        		
	        	message.setMsgType(MessageType.IMAGE.getCode());
	        	message.setBody(new WxMessageBody(fullImageUrl, thumbImageUrl));
	        }
	        // 发送视频
	        else if (contentType != null && contentType.contains(PREFIX_VIDEO)) {
	        	// 文件超限 25MB
				if (file.length() > MAX_VIDEO_SIZE) {
					WxbotView.getInstance().executeScript("app.$Message.error('发送的视频文件不能大于25M');");
					return;
				}
	        	
	        	SendMsgResponse response = wechatService.sendVideo(userName == null ? cacheService.getOwner().getUserName() : userName, file.getPath());
	        	// 下载发送视频 缩略图
        		String thumbImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), response.getMsgID(), cacheService.getsKey()) + "&type=slave";
        		thumbImageUrl = wechatService.download(thumbImageUrl, response.getMsgID()+".jpg", MessageType.VIDEO);
	        	
	        	message.setMsgType(MessageType.VIDEO.getCode());
	        	message.setBody(new WxMessageBody(file.getPath(), thumbImageUrl));
	        } else {
	        	// 文件超限 100MB
				if (file.length() > MAX_FILE_SIZE) {
					WxbotView.getInstance().executeScript("app.$Message.error('发送的文件不能大于100M');");
					return;
				}
				
	        	wechatService.sendApp(userName == null ? cacheService.getOwner().getUserName() : userName, file.getPath());
	        	message.setMsgType(MessageType.APP.getCode());
	        	message.setBody(new WxMessageBody(file.getPath(), file.getName(), FileUtil.getFileSizeString(file.length())));
	        }

			String timestamp = Tools.getTimestamp();
			message.setTimestamp(timestamp);
			message.setTo(nickName);
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setChatType(userName.startsWith("@@")?2:1);
			String json = BaseServer.JSON_MAPPER.writeValueAsString(message);
			
			String filePath = Config.CHAT_RECORD_PATH + seq;
			FileUtil.writeFile(filePath, Tools.getSysDate() + ".txt", json);
			msgTool.avatarBadge(userName, json);
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
	public void mediaPlay(String path) {
		File file = new File(path);
		if (!file.exists()) {
			logger.error("mediaPlay:目录[" + path + "]不存在");
			return;
		}
		Tools.openFileByOs(file.toURI().toString());
	}
	
	/**
	 * 语音播放
	 * @param path
	 */
	public void voicePlay(String path) {
		File file = new File(path);
		if (!file.exists()) {
			logger.error("voicePlay:目录[" + path + "]不存在");
			return;
		}
		Media media = new Media(file.toURI().toString());
		MediaPlayer mplayer = new MediaPlayer(media);
		mplayer.play();
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
