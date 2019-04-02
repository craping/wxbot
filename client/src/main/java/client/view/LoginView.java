package client.view;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.Notification;
import com.teamdev.jxbrowser.chromium.NotificationHandler;
import com.teamdev.jxbrowser.chromium.NotificationService;
import com.teamdev.jxbrowser.chromium.PermissionHandler;
import com.teamdev.jxbrowser.chromium.PermissionRequest;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.PermissionType;
import com.teamdev.jxbrowser.chromium.ProtocolHandler;
import com.teamdev.jxbrowser.chromium.ProtocolService;
import com.teamdev.jxbrowser.chromium.URLRequest;
import com.teamdev.jxbrowser.chromium.URLResponse;
import com.teamdev.jxbrowser.chromium.events.NotificationEvent;
import com.teamdev.jxbrowser.chromium.events.NotificationListener;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import com.teamdev.jxbrowser.chromium.javafx.DefaultDialogHandler;

import client.Launch;
import client.utils.FileUtil;
import client.view.function.Wxbot;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
  
/**  
* @ClassName: LoginView  
* @Description: Chromium外壳登录窗口类，单例模式
* @author Crap  
* @date 2019年1月26日  
*    
*/  
    
public final class LoginView extends AnchorPane  {
	
	private static final Logger logger = LogManager.getLogger(LoginView.class);
	
	private static LoginView INSTANCE;
	
	private Browser browser;
	
	private BrowserView browserView;
	
	private Stage viewStage;
	
	private Scene viewScene;
	
	public static LoginView getInstance() {
		if(INSTANCE == null)
			INSTANCE = new LoginView();
		return INSTANCE;
	}
	
	private LoginView() {
		getChildren().remove(browserView);
		browser = new Browser(Launch.CACHE_CONTEXT);
	    browserView = new BrowserView(browser);
	    BrowserContext context = browser.getContext();
	    ProtocolService protocolService = context.getProtocolService();
	    protocolService.setProtocolHandler("jar", new ProtocolHandler() {
	        @Override
	        public URLResponse onRequest(URLRequest request) {
	            try {
	                URLResponse response = new URLResponse();
	                URL path = new URL(request.getURL().split("[?]")[0]);
	                InputStream inputStream = path.openStream();
	                DataInputStream stream = new DataInputStream(inputStream);
	                byte[] data = new byte[stream.available()];
	                stream.readFully(data);
	                response.setData(data);
	                String mimeType = FileUtil.getMimeType(path.toString());
	                response.getHeaders().setHeader("Content-Type", mimeType);
	                return response;
	            } catch (Exception ignored) {}
	            return null;
	        }
	    });
	    
        NotificationService notificationService = context.getNotificationService();
        notificationService.setNotificationHandler(new NotificationHandler() {
            @Override
            public void onShowNotification(NotificationEvent event) {
                final Notification notification = event.getNotification();
                Platform.runLater(() -> {
                    final Stage stage = new Stage();
                    stage.setTitle(notification.getTitle());
                    Label label = new Label(notification.getMessage());
                    label.setPadding(new Insets(15, 15, 15, 15));
                    stage.setScene(new Scene(label, 300, 100));
                    stage.setAlwaysOnTop(true);
                    stage.show();

                    notification.addNotificationListener(new NotificationListener() {
                        @Override
                        public void onClose(NotificationEvent event) {
                            if (event.getNotification().isClosed()) {
                                Platform.runLater(() -> {
                                    stage.close();
                                });
                            }
                        }
                    });

                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                                event.consume();
                                notification.close();
                            }
                        }
                    });
                });
            }
        });

        // Grant notification permission if it's necessary
        browser.setPermissionHandler(new PermissionHandler() {
            @Override
            public PermissionStatus onRequestPermission(PermissionRequest request) {
                if (request.getPermissionType() == PermissionType.NOTIFICATIONS) {
                    return PermissionStatus.GRANTED;
                }
                return PermissionStatus.DENIED;
            }
        });
        browser.setDialogHandler(new DefaultDialogHandler(browserView));
        
        browser.addScriptContextListener(new ScriptContextAdapter() {
            @Override
            public void onScriptContextCreated(ScriptContextEvent event) {
            	Browser browser = event.getBrowser();
                JSValue value = browser.executeJavaScriptAndReturnValue("window");
            	value.asObject().setProperty("wxbot", Launch.CONTEXT.getBean(Wxbot.class));
            }
        });
        browser.addDisposeListener(event -> {
        	System.out.println("disposed event = "+event);
        	System.exit(0);
        });
        browser.loadURL(getClass().getClassLoader().getResource("view/login.html").toExternalForm());
        
        browser.addConsoleListener(e -> {
        	String level = e.getLevel().name();
        	level = level.equalsIgnoreCase("log")?"trace":level;
            logger.log(Level.toLevel(level), e.getMessage() + " [" + e.getSource() + ":" + e.getLineNumber() + "] ");
        });
		
		setTopAnchor(browserView, 0.0);
		setRightAnchor(browserView, 0.0);
		setBottomAnchor(browserView, 0.0);
		setLeftAnchor(browserView, 0.0);
		getChildren().add(browserView);
		
		viewScene = new Scene(this, 400, 300);
		if(Launch.DEBUG)
			viewScene.setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.F12) {
					debug();
				}
			});
		viewStage = new Stage();
		viewStage.setTitle("微信机器人");
		viewStage.setResizable(false);
		viewStage.setIconified(false);
		viewStage.setScene(viewScene);
		viewStage.setOnCloseRequest(e -> {
			new Thread(() -> {
				System.out.println("loginView is disposed = " + browser.dispose(true));
			}).start();
		});
	}
	/**  
	* @Title: load  
	* @Description: 启动网页主界面视图
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/   
	public void show() {
		viewStage.show();
	}
	
	/**  
	* @Title: debug  
	* @Description: 打开调试窗口 
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	public void debug() {
		if(Launch.DEBUG) {
			Browser debugBrowser = new Browser(Launch.CACHE_CONTEXT);
			BrowserView debugBrowserView = new BrowserView(debugBrowser);
	        debugBrowser.loadURL(browser.getRemoteDebuggingURL());
	        Scene debugScene = new Scene(debugBrowserView, 790, 790);
	        Stage debugStage = new Stage();
			debugStage.setTitle("调试");
			debugStage.setResizable(true);
			debugStage.setIconified(false);
			debugStage.setScene(debugScene);
			debugStage.show();
			debugStage.setOnCloseRequest(e -> {
				new Thread(() -> {
					System.out.println("debugView is disposed = " + debugBrowser.dispose(true));
				}).start();
			});
		}
	}
	
	/**  
	* @Title: close  
	* @Description: 关闭视图
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	public void close() {
		viewStage.close();
		new Thread(() -> {
			System.out.println("loginView is disposed = " + browser.dispose(true));
		}).start();
	}
	
	public static void exit(){
		if(INSTANCE != null)
			INSTANCE.close();
	}
	  
	/**  
	* @Title: hide  
	* @Description: 隐藏登录窗口
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void hide() {
		viewStage.hide();
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public BrowserView getBrowserView() {
		return browserView;
	}

	public void setBrowserView(BrowserView browserView) {
		this.browserView = browserView;
	}

	public Stage getViewStage() {
		return viewStage;
	}

	public void setViewStage(Stage viewStage) {
		this.viewStage = viewStage;
	}

}
