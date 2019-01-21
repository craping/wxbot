package client.view.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;

import javafx.application.Platform;

@Component
public class Wxbot {
	
	@Autowired
	private Jeeves jeeves;
	
	public static Thread wxbotThread;
	
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
	
	public void exit() {
		jeeves.stop();
		if(wxbotThread != null)
			wxbotThread.interrupt();
		Platform.exit();
    }
}
