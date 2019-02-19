package client.view.function;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;
import javax.tools.Tool;

import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.pojo.WxMessage;
import client.utils.FileUtil;
import client.utils.Tools;
import client.view.WxbotView;
import javafx.application.Platform;
import javafx.stage.FileChooser;

@Component
public abstract class ChatFunction extends ContactsFunction {
	
	protected final FileChooser sendChooser = new FileChooser();
	
	protected File lastSendFile;
	
	public ChatFunction() {
		super();
		sendChooser.setTitle("选择文件");
		sendChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
	}
	/**  
	* @Title: sendApp  
	* @Description: 发送文件消息
	* 调用后会打开文件选择器 可以选择任何类型文件
	* @param  userName    用户ID
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void sendApp(String userName) {
		Platform.runLater(() -> {
			if(lastSendFile != null && lastSendFile.isFile())
				sendChooser.setInitialDirectory(lastSendFile.getParentFile());
			
			lastSendFile = sendChooser.showOpenDialog(WxbotView.getInstance().getViewStage());
			if(lastSendFile == null)
				return;
			System.out.println(lastSendFile.getAbsolutePath());
			
			try {
				wechatService.sendApp(userName == null?cacheService.getOwner().getUserName():userName, lastSendFile.getAbsolutePath());
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
				for(String str : l) {
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
	* @param @param text    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void sendText(String seq, String nickName, String userName, String chatType, String text) {
		try {
			WxMessage message = new WxMessage();
			String timestamp = Tools.getTimestamp();
			message.setMsgId(timestamp);
			message.setTimestamp(timestamp);
			message.setTo(nickName);
			message.setFrom(cacheService.getOwner().getNickName());
			message.setChatType(chatType);
			message.setMsgType("txt");
			message.setBody(text);
			String content = jsonMapper.writeValueAsString(message);
			String path = "d:/chat/" + seq;
			FileUtil.writeFile(path, Tools.getSysDate() + ".txt", content);
			wechatService.sendText(userName == null?cacheService.getOwner().getUserName():userName, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
