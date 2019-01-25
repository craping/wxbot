package client.view.function;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import com.teamdev.jxbrowser.chromium.JSArray;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.view.WxbotView;
import javafx.application.Platform;
import javafx.stage.FileChooser;

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
	
	public void stop() {
		jeeves.stop();
		if(wxbotThread != null)
			wxbotThread.interrupt();
    }
	
	public Set<Contact> getContact() {
		return cacheService.getIndividuals();
	}
	
	public JSONString test() {
		Set<Contact> sets = new HashSet<>();
		Contact c = new Contact();
		c.setUserName("123");
		sets.add(c);
		Contact c1 = new Contact();
		c1.setUserName("123");
		sets.add(c1);
		try {
			return new JSONString(jsonMapper.writeValueAsString(sets));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
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
	
	public void sendText(String userName, String text) {
		try {
			wechatService.sendText(userName == null?cacheService.getOwner().getUserName():userName, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
