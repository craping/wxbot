package client.view.function;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileSystemView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.service.CacheService;
import com.cherry.jeeves.service.WechatHttpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.view.WxbotView;
import javafx.application.Platform;
import javafx.stage.FileChooser;

  
/**  
* @ClassName: Wxbot  
* @Description: Java与Chromium交互函数类
* @author Crap  
* @date 2019年1月26日  
*    
*/  
    
@Component
public class Wxbot {
	
	@Autowired
	private Jeeves jeeves;
	
	@Autowired
	private WechatHttpService wechatService;
	
	@Autowired
	private CacheService cacheService;
	
	public final FileChooser sendChooser = new FileChooser();
	
	private File lastSendFile;
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	public Thread wxbotThread;
	
	public Wxbot() {
		sendChooser.setTitle("选择文件");
		sendChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
	}
	
	
	  
	/**  
	* @Title: start  
	* @Description: 启动Jeeves机器人线程
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void start() {
		wxbotThread = new Thread(() -> {
        	try {
        		jeeves.start();
        	}catch (Exception e) {
			}
        	wxbotThread = null;
        });
		wxbotThread.setDaemon(true);
		wxbotThread.start();
    	
	}
	
	  
	/**  
	* @Title: stop  
	* @Description: 停止Jeeves机器人线程，
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void stop() {
		jeeves.stop();
		if(wxbotThread != null)
			wxbotThread.interrupt();
    }
	
	  
	/**  
	* @Title: getIndividuals  
	* @Description: 获取联系人列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	    
	public JSONString getIndividuals() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getIndividuals()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	  
	/**  
	* @Title: getChatRooms  
	* @Description: 获取群列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	    
	public JSONString getChatRooms() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getChatRooms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	  
	/**  
	* @Title: getMediaPlatforms  
	* @Description: 获取公众号列表
	* @param @return    参数  
	* @return JSONString    返回类型  
	* @throws  
	*/  
	    
	public JSONString getMediaPlatforms() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(cacheService.getMediaPlatforms()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	public JSONString test() {
		Set<Contact> sets = new HashSet<>();
		Contact c = new Contact();
		c.setUserName("123");
		sets.add(c);
		Contact c1 = new Contact();
		c1.setUserName("123456");
		sets.add(c1);
		try {
			return new JSONString(jsonMapper.writeValueAsString(sets));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
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
