package client.view.function;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
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

import client.enums.Direction;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Arith;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.Tools;
import client.utils.WxMessageTool;
import client.view.WxbotView;
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
			
	        // 获取文件绝对路径
			String fileUrl = lastSendFile.getAbsolutePath();
			// 获取文件Content-Type(Mime-Type)
			System.out.println(fileUrl);
			sendApp(seq, nickName, userName, fileUrl);
		});
	}
	/**
	 * @Title: sendApp 
	 * @Description: 发送文件消息 调用后会打开文件选择器 可以选择任何类型文件 
	 * @param userName用户ID 
	 * @return void 返回类型
	 * @throws
	 */

	public void sendApp(String seq, String nickName, String userName, String fileUrl) {
        String contentType = null;  
        try {  
            contentType = Files.probeContentType(new File(fileUrl).toPath());
	        System.out.println("File content type is : " + contentType);  
	        
	        WxMessage message = new WxMessage();
	        // 发送表情
	        if (contentType != null && contentType.contains(PREFIX_GIF)) {
	        	wechatService.sendEmoticon(userName == null ? cacheService.getOwner().getUserName() : userName, fileUrl);
	        	message.setMsgType(MessageType.EMOTICON.getCode());
	        	message.setBody(new WxMessageBody(fileUrl, getImgHeightOrWidth(fileUrl, "height"), getImgHeightOrWidth(fileUrl, "width")));
	        }
	        // 发送图片
	        else if (contentType != null && contentType.contains(PREFIX_IMG)) {
	        	SendMsgResponse response = wechatService.sendImage(userName == null ? cacheService.getOwner().getUserName() : userName, fileUrl);
	        	String thumbImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), response.getMsgID(), cacheService.getsKey()) + "&type=slave";
	        	String fullImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), response.getMsgID(), cacheService.getsKey());
	        	fullImageUrl = wechatService.download(fullImageUrl, response.getMsgID()+".jpg", MessageType.VIDEO);
        		thumbImageUrl = wechatService.download(thumbImageUrl, response.getMsgID()+"_thumb.jpg", MessageType.VIDEO);
        		
	        	message.setMsgType(MessageType.IMAGE.getCode());
	        	message.setBody(new WxMessageBody(fullImageUrl, thumbImageUrl, getImgHeightOrWidth(thumbImageUrl, "height"), getImgHeightOrWidth(thumbImageUrl, "width")));
	        }
	        // 发送视频
	        else if (contentType != null && contentType.contains(PREFIX_VIDEO)) {
	        	// 文件超限 25MB
				if (FileUtil.getFileSize(fileUrl) > MAX_VIDEO_SIZE) {
					WxbotView wxbotView = WxbotView.getInstance();
					String msg = "发送的视频文件不能大于25M";
					String script = "Chat.methods.alertUtil('WARNING','" + msg + "')";
					wxbotView.executeScript(script);
					return;
				}
	        	
	        	SendMsgResponse response = wechatService.sendVideo(userName == null ? cacheService.getOwner().getUserName() : userName, fileUrl);
	        	// 下载发送视频 缩略图
        		String thumbImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), response.getMsgID(), cacheService.getsKey()) + "&type=slave";
        		thumbImageUrl = wechatService.download(thumbImageUrl, response.getMsgID()+".jpg", MessageType.VIDEO);
	        	
	        	message.setMsgType(MessageType.VIDEO.getCode());
	        	message.setBody(new WxMessageBody(fileUrl, thumbImageUrl));
	        } else {
	        	// 文件超限 100MB
				if (FileUtil.getFileSize(fileUrl) > MAX_FILE_SIZE) {
					WxbotView wxbotView = WxbotView.getInstance();
					String msg = "发送的文件不能大于100M";
					String script = "Chat.methods.alertUtil('WARNING','" + msg + "')";
					wxbotView.executeScript(script);
					return;
				}
				
	        	wechatService.sendApp(userName == null ? cacheService.getOwner().getUserName() : userName, fileUrl);
	        	message.setMsgType(MessageType.APP.getCode());
	        	message.setBody(new WxMessageBody(MessageType.APP, fileUrl, lastSendFile.getName(), FileUtil.getFileSizeString(fileUrl)));
	        }

			String timestamp = Tools.getTimestamp();
			message.setTimestamp(timestamp);
			message.setTo(nickName);
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setChatType(userName.startsWith("@@")?2:1);
			String str = jsonMapper.writeValueAsString(message);
			String filePath = Config.CHAT_RECORD_PATH + seq;
			FileUtil.writeFile(filePath, Tools.getSysDate() + ".txt", str);
			WxMessageTool.avatarBadge(seq);
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
	public JSONString chatRecord(String seq, JSFunction function) {
		new Thread(() -> {
			try {
				String path = Config.CHAT_RECORD_PATH + seq + "/" + Tools.getSysDate() + ".txt";
				List<String> l = FileUtil.readFile(path);
				List<WxMessage> records = new LinkedList<WxMessage>();
				if (l.size() > 0 && l != null) {
					for (String str : l) {
						WxMessage msg = jsonMapper.readValue(str, WxMessage.class);
						records.add(msg);
					}
				}
				// 全部设置已读消息
				WxMessageTool.haveRead(seq);
//				return new JSONString(jsonMapper.writeValueAsString(records));
				function.invokeAsync(function, new JSONString(jsonMapper.writeValueAsString(records)));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		return new JSONString("{}");
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
			String str = jsonMapper.writeValueAsString(message);
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
	public String getRealUrl(String path) {
		File file = new File(path);
		if (!file.exists()) {
			logger.error("getRealUrl:目录[" + path + "]不存在");
			return "";
		}
		return file.getAbsolutePath().replace("\\", "/");
	}
	
	/**
	 * 切割图片高度、宽度
	 * @param width
	 * @param height
	 * @param type
	 * @return
	 */
	public int cutImg(int width, int height, String type) {
		// 如果宽度大于最大宽度，则实际高度=原始高度/（原始宽度/最大宽度）
		if (width > Config.MAX_IMG_WIDTH) {
			double mult = Arith.div(width, Config.MAX_IMG_WIDTH, 2);
			height = Arith.getInt(Arith.div(height, mult));
			width = Config.MAX_IMG_WIDTH;
		}
		if ("width".equals(type) || "width" == type) {
			return width;
		} else {
			return height;
		}
	}

	/**
	 * 获取图片高度、宽度
	 * 
	 * @param type width宽度
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 */
	public int getImgHeightOrWidth(String path, String type) throws FileNotFoundException, IOException {
		File picture = new File(path);
		if (!picture.exists()) {
			logger.error("getImgHeightOrWidth:目录文件[" + path + "]不存在");
			return 0;
		}
		BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
		int width = sourceImg.getWidth(); // 源图宽度
		int height = sourceImg.getHeight(); // 源图高度
		return cutImg(width, height, type);
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
