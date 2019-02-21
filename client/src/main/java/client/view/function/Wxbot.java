package client.view.function;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.domain.shared.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;

import client.utils.Tools;

  
/**  
* @ClassName: Wxbot  
* @Description: Java与Chromium交互函数类
* @author Crap  
* @date 2019年1月26日  
*    
*/  
    
@Component
public class Wxbot extends KeywordFunction {
	
	@Autowired
	private Jeeves jeeves;
	
	public Thread wxbotThread;
	
	public String userToken;
	
	public Wxbot() {
		super();
	}
	 
	/**  
	* @Title: start  
	* @Description: 启动Jeeves机器人线程
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void start(String token) {
		if (Tools.isStrEmpty(userToken)) {
			userToken = token;
		}
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
	
	public String getToken() {
		return userToken;
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

}
