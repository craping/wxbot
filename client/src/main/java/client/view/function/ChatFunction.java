package client.view.function;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.enums.Direction;
import client.pojo.WxMessage;
import client.pojo.WxMessageBody;
import client.utils.Arith;
import client.utils.Config;
import client.utils.FileUtil;
import client.utils.Tools;
import client.view.WxbotView;
import javafx.application.Platform;
import javafx.stage.FileChooser;

@Component
public abstract class ChatFunction extends ContactsFunction {

	private static final Logger logger = LoggerFactory.getLogger(ChatFunction.class);

	protected final FileChooser sendChooser = new FileChooser();

	protected File lastSendFile;

	public ChatFunction() {
		super();
		sendChooser.setTitle("选择文件");
		sendChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
	}

	/**
	 * @Title: sendApp 
	 * @Description: 发送文件消息 调用后会打开文件选择器 可以选择任何类型文件 
	 * @param userName用户ID 
	 * @return void 返回类型
	 * @throws
	 */

	public void sendApp(String userName) {
		Platform.runLater(() -> {
			if (lastSendFile != null && lastSendFile.isFile())
				sendChooser.setInitialDirectory(lastSendFile.getParentFile());

			lastSendFile = sendChooser.showOpenDialog(WxbotView.getInstance().getViewStage());
			if (lastSendFile == null)
				return;
			System.out.println(lastSendFile.getAbsolutePath());

			try {
				wechatService.sendApp(userName == null ? cacheService.getOwner().getUserName() : userName,
						lastSendFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public JSONString chatRecord(String seq) {
		try {
			String path = "d:/chat/" + seq + "/" + Tools.getSysDate() + ".txt";
			List<String> l = FileUtil.readFile(path);
			List<WxMessage> records = new ArrayList<WxMessage>();
			if (l.size() > 0 && l != null) {
				for (String str : l) {
					WxMessage msg = jsonMapper.readValue(str, WxMessage.class);
					records.add(msg);
				}
			}
			return new JSONString(jsonMapper.writeValueAsString(records));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void sendText(String seq, String nickName, String userName, String chatType, String content) {
		try {
			WxMessage message = new WxMessage();
			String timestamp = Tools.getTimestamp();
			message.setTimestamp(timestamp);
			message.setTo(nickName);
			message.setFrom(cacheService.getOwner().getNickName());
			message.setDirection(Direction.SEND.getCode());
			message.setMsgType(MessageType.TEXT.getCode());
			message.setBody(new WxMessageBody(content));
			String str = jsonMapper.writeValueAsString(message);
			String path = "d:/chat/" + seq;
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
			logger.error("目录[" + path + "]不存在");
			return "";
		}
		return file.getAbsolutePath().replace("\\", "/");
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
			logger.error("目录文件[" + path + "]不存在");
			return 0;
		}
		BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
		int width = sourceImg.getWidth(); // 源图宽度
		int height = sourceImg.getHeight(); // 源图高度
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
	 * 视频播放
	 * 
	 * @param path
	 */
	public void mediaPlay(String path) {
		File file = new File(path);
		if (!file.exists()) {
			logger.error("目录[" + path + "]不存在");
			return;
		}
		Tools.openFileByOs(file.toURI().toString());
	}
}
