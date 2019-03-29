package client;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.bb;
import com.teamdev.jxbrowser.chromium.internal.Environment;

import client.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

@Configuration
@PropertySource("application.properties")
@ComponentScan({"com.cherry.jeeves", "client"})
public class Launch extends Application {
	
	public static ApplicationContext context = new AnnotationConfigApplicationContext(Launch.class);
	
	static {
	    try {
	        Field e = bb.class.getDeclaredField("e");
	        e.setAccessible(true);
	        Field f = bb.class.getDeclaredField("f");
	        f.setAccessible(true);
	        Field modifersField = Field.class.getDeclaredField("modifiers");
	        modifersField.setAccessible(true);
	        modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
	        modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
	        e.set(null, new BigInteger("1"));
	        f.set(null, new BigInteger("1"));
	        modifersField.setAccessible(false);
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    BrowserPreferences.setChromiumSwitches("--disable-google-traffic", "--disable-web-security", "--user-data-dir", "--allow-file-access-from-files", "--remote-debugging-port=9222");
	}
	
	@Override
    public void init() throws Exception {
        // On Mac OS X Chromium engine must be initialized in non-UI thread.
        if (Environment.isMac()) {
            BrowserCore.initialize();
        }
    }
	
	@Override
	public void start(Stage primaryStage) {
		try {
			LoginView.getInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
