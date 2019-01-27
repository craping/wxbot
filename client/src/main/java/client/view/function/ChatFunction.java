package client.view.function;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import org.springframework.stereotype.Component;

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
	* @param @param userName    用户ID
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
	
	  
	/**  
	* @Title: sendText  
	* @Description: 发送文本消息
	* @param @param userName
	* @param @param text    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void sendText(String userName, String text) {
		try {
			wechatService.sendText(userName == null?cacheService.getOwner().getUserName():userName, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
