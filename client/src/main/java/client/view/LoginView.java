package client.view;

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
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.NotificationEvent;
import com.teamdev.jxbrowser.chromium.events.NotificationListener;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import client.Launch;
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
	
	private boolean debug;
	
	public static LoginView getInstance() {
		if(INSTANCE == null)
			INSTANCE = new LoginView(true);
		return INSTANCE;
	}
	
	private LoginView() {
		this(false);
	}
	
	private LoginView(boolean debug) {
		this.debug = debug;
		viewScene = new Scene(this, 400, 300);
		viewScene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.F12) {
				debug();
			}
		});
	}
	
	  
	/**  
	* @Title: load  
	* @Description: 启动网页主界面视图
	* @param     参数  
	* @return void    返回类型  
	* @throws  
	*/   
	public void load() {
		getChildren().remove(browserView);
		browser = new Browser();
	    browserView = new BrowserView(browser);
	    BrowserContext context = browser.getContext();
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
		
		viewStage = new Stage();
		viewStage.setTitle("微信机器人");
		viewStage.setResizable(false);
		viewStage.setIconified(false);
		viewStage.setScene(viewScene);
		viewStage.setOnCloseRequest(e -> {
			new Thread(() -> {
				System.out.println("wxbotView is disposed = " + browser.dispose(true));
			}).start();
		});
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
		if(this.debug) {
			Browser debugBrowser = new Browser();
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

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	
}
