package client.view;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.ContextMenuHandler;
import com.teamdev.jxbrowser.chromium.ContextMenuParams;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.Notification;
import com.teamdev.jxbrowser.chromium.NotificationHandler;
import com.teamdev.jxbrowser.chromium.NotificationService;
import com.teamdev.jxbrowser.chromium.PermissionHandler;
import com.teamdev.jxbrowser.chromium.PermissionRequest;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.PermissionType;
import com.teamdev.jxbrowser.chromium.bb;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.NotificationEvent;
import com.teamdev.jxbrowser.chromium.events.NotificationListener;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import client.Launch;
import client.view.function.Wxbot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
  
/**  
* @ClassName: WxbotView  
* @Description: Chromium外壳窗口类，单例模式
* @author Crap  
* @date 2019年1月26日  
*    
*/  
    
public final class WxbotView extends AnchorPane  {
	
	private static final Logger logger = LogManager.getLogger(WxbotView.class);
	
	private static WxbotView INSTANCE;
	
	private Browser browser;
	
	private BrowserView browserView;
	
	private Stage viewStage;
	
	private Scene viewScene;
	
	
//	private Browser debugBrowser;
//	
//	private BrowserView debugBrowserView;
//	
//	private Stage debugStage;
	
	
	
	private Browser settingBrowser;
	
	private BrowserView settingBrowserView;
	
	private Stage settingStage;
	
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
	
	public static WxbotView getInstance() {
		if(INSTANCE == null)
			INSTANCE = new WxbotView(true);
		return INSTANCE;
	}
	
	private WxbotView() {
		this(false);
	}
	
	private WxbotView(boolean debug) {
		this.debug = debug;
		viewScene = new Scene(this, 900, 652);
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
		if(this.debug)
			browser.setContextMenuHandler(new MyContextMenuHandler(browserView));
        browser.addDisposeListener(event -> {
        	System.out.println("disposed event = "+event);
        });
        browser.loadURL(getClass().getClassLoader().getResource("view/main.html").toExternalForm());
        
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
			if(close !=null)
				close.handle(e);
		});
		viewStage.show();
		setting();
	}
	
	public void openSetting(){
		if(settingStage != null){
			settingBrowser.executeJavaScript("app.syncSetting()");
			settingStage.show();
		}
	}
	
	public void setting() {
		settingBrowser = new Browser();
		settingBrowserView = new BrowserView(settingBrowser);
		settingBrowser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Browser browser = event.getBrowser();
                    JSValue value = browser.executeJavaScriptAndReturnValue("window");
                    value.asObject().setProperty("wxbot", Launch.context.getBean(Wxbot.class));
                }
            }
        });
		settingBrowser.loadURL(getClass().getClassLoader().getResource("view/setting.html").toExternalForm());
		
		AnchorPane pane = new AnchorPane(settingBrowserView);
		AnchorPane.setTopAnchor(settingBrowserView, 0.0);
		AnchorPane.setRightAnchor(settingBrowserView, 0.0);
		AnchorPane.setBottomAnchor(settingBrowserView, 0.0);
		AnchorPane.setLeftAnchor(settingBrowserView, 0.0);
        Scene settingScene = new Scene(pane, 700, 500);
        settingScene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.F12) {
				debugSetting();
			}
		});
        settingStage = new Stage();
        settingStage.setTitle("设置");
        settingStage.setResizable(false);
        settingStage.setIconified(false);
        settingStage.setScene(settingScene);
//        settingStage.show();
        settingStage.onCloseRequestProperty().bind(settingStage.onHiddenProperty());
//        settingStage.setOnCloseRequest(e -> {
//			new Thread(() -> {
//				System.out.println("settingView is disposed = " + settingBrowser.dispose(true));
//			}).start();
//		});
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
	
	public void debugSetting() {
		if(this.debug) {
			Browser debugBrowser = new Browser();
			BrowserView debugBrowserView = new BrowserView(debugBrowser);
	        debugBrowser.loadURL(settingBrowser.getRemoteDebuggingURL());
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
		if(viewStage != null)
			viewStage.close();
		if(settingStage != null)
			settingStage.close();
	}
	
	  
	/**  
	* @Title: onClose  
	* @Description: 关闭事件处理
	* @param @param event    
	* @return void    返回类型  
	* @throws  
	*/  
	public void onClose(EventHandler<Event> event) {
		close = event;
	}
	
	  
	/**  
	* @Title: executeScript  
	* @Description: 执行javascript
	* @param @param javaScript
	* @param @return    参数  
	* @return JSValue    返回类型  
	* @throws  
	*/  
	public JSValue executeScript(String javaScript) {
		return browser.executeJavaScriptAndReturnValue(javaScript);
	}
	
	private static class MyContextMenuHandler implements ContextMenuHandler {

        private final Pane pane;

        private MyContextMenuHandler(Pane paren) {
            this.pane = paren;
        }

        private static MenuItem createMenuItem(String title, final Runnable action) {
            MenuItem menuItem = new MenuItem(title);
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    action.run();
                }
            });
            return menuItem;
        }

        public void showContextMenu(final ContextMenuParams params) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    createAndDisplayContextMenu(params);
                }
            });
        }

        private void createAndDisplayContextMenu(final ContextMenuParams params) {
            final ContextMenu contextMenu = new ContextMenu();

            // Since context menu doesn't auto hide, listen mouse scroll events
            // on BrowserView and hide context menu on mouse click
            pane.setOnScroll(new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    contextMenu.hide();
                }
            });

            // If there's link under mouse pointer, create and add
            // the "Open link in new window" menu item to our context menu
            if (!params.getLinkText().isEmpty()) {
                contextMenu.getItems().add(createMenuItem(
                        "Open link in new window", new Runnable() {
                            public void run() {
                                String linkURL = params.getLinkURL();
                                System.out.println("linkURL = " + linkURL);
                            }
                        }));
            }

            // Create and add "Reload" menu item to our context menu
            contextMenu.getItems().add(createMenuItem("重新加载", new Runnable() {
                public void run() {
                    params.getBrowser().reload();
                }
            }));

            // Display context menu at required location on screen
            Point location = params.getLocation();
            Point2D screenLocation = pane.localToScreen(location.x, location.y);
            contextMenu.show(pane, screenLocation.getX(), screenLocation.getY());
        }
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
