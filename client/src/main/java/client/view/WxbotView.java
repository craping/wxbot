package client.view;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.javafx.webkit.WebConsoleListener;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.bb;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import client.Launch;
import client.view.function.Wxbot;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class WxbotView extends AnchorPane  {
	
	private static final Logger logger = LogManager.getLogger(WxbotView.class);
	
	private Browser browser;
	
	private BrowserView browserView;
	
	private Stage viewStage;
    
	private Browser debugBrowser;
	
	private BrowserView debugBrowserview;
	
	private Stage debugStage;
	
	private boolean debug;
	
	private EventHandler<Event> close;
    
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
	    BrowserPreferences.setChromiumSwitches("--disable-web-security", "--user-data-dir", "--allow-file-access-from-files", "--remote-debugging-port=9222");
	}
	
	public WxbotView() {
		this(false);
	}
	
	public WxbotView(boolean debug) {
		this.debug = debug;
	}
	
	public void load() {
		getChildren().remove(browserView);
		browser = new Browser();
	    browserView = new BrowserView(browser);
		browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Browser browser = event.getBrowser();
                    JSValue value = browser.executeJavaScriptAndReturnValue("window");
                    value.asObject().setProperty("wxbot", Launch.context.getBean(Wxbot.class));
                }
            }
        });
        browser.addDisposeListener(event -> {
        	System.out.println("disposed event = "+event);
        });
        browser.loadURL(getClass().getClassLoader().getResource("view/main.html").toExternalForm());
		
		WebConsoleListener.setDefaultListener((WebView webView, String message, int lineNumber, String sourceId) -> {
			logger.info(message + " [" + sourceId + ":" + lineNumber + "] ");
		});
		
		setTopAnchor(browserView, 0.0);
		setRightAnchor(browserView, 0.0);
		setBottomAnchor(browserView, 0.0);
		setLeftAnchor(browserView, 0.0);
		getChildren().add(browserView);
		
		viewStage = new Stage();
		Scene scene = new Scene(this);
		viewStage.setTitle("调试");
		viewStage.setResizable(true);
		viewStage.setIconified(false);
		viewStage.setScene(scene);
		viewStage.setOnCloseRequest(e -> {
			new Thread(() -> {
				System.out.println("wxbotView is disposed = " + browser.dispose(true));
			}).start();
			if(close !=null)
				close.handle(e);
		});
		scene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.F12) {
				debug();
			}
		});
		viewStage.show();
	}
	
	public void debug() {
		if(this.debug) {
			debugBrowser = new Browser();
		    debugBrowserview = new BrowserView(debugBrowser);
	        debugBrowser.loadURL(browser.getRemoteDebuggingURL());
	        debugStage = new Stage();
			Scene scene = new Scene(debugBrowserview);
			debugStage.setTitle("调试");
			debugStage.setResizable(true);
			debugStage.setIconified(false);
			debugStage.setScene(scene);
			debugStage.show();
			debugStage.setOnCloseRequest(e -> {
				new Thread(() -> {
					System.out.println("debugView is disposed = " + debugBrowser.dispose(true));
				}).start();
			});
		}
	}
	
	public void close() {
		if(viewStage != null)
			viewStage.close();
		if(viewStage != null)
			viewStage.close();
	}
	
	public void onClose(EventHandler<Event> event) {
		close = event;
	}
	
	public JSValue executeScript(String javaScript) {
		return browser.executeJavaScriptAndReturnValue(javaScript);
	}
}
