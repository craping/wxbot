package client.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.javafx.webkit.WebConsoleListener;

import client.view.function.Wxbot;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

@SuppressWarnings("restriction")
public class AppView extends AnchorPane {
	
	private static final Logger logger = LogManager.getLogger(AppView.class);
	
	public static WebView webView = new WebView();

	public static WebEngine webEngine = webView.getEngine();
	
	public AppView() {
		
		webEngine.getLoadWorker().stateProperty()
		.addListener((ov, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				JSObject win = (JSObject) webEngine.executeScript("window");
				win.setMember("wxbot", new Wxbot());
			}
		});
		webEngine.setOnAlert(e -> {
			System.out.println("Alert Event  -  Message:  " + e.getData());
		});
		webEngine.load(getClass().getClassLoader().getResource("view/index.html").toExternalForm());
		
		WebConsoleListener.setDefaultListener((WebView webView, String message, int lineNumber, String sourceId) -> {
			System.out.println(message + " [" + sourceId + ":" + lineNumber + "] ");
		});
//		webView.setContextMenuEnabled(false);
		
		setTopAnchor(webView, 0.0);
		setRightAnchor(webView, 0.0);
		setBottomAnchor(webView, 0.0);
		setLeftAnchor(webView, 0.0);
		getChildren().add(webView);
	}
	
	public static Object executeScript(String script) {
		return webEngine.executeScript(script);
	}
}