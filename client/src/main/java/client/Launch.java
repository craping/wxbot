package client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import client.view.WxbotView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@Configuration
@PropertySource("application.properties")
@ComponentScan({"com.cherry.jeeves", "client"})
public class Launch extends Application {
	
	public static ApplicationContext context = new AnnotationConfigApplicationContext(Launch.class);
	
	@Override
	public void start(Stage primaryStage) {
		try {
	        Scene scene = new Scene(new WxbotView(), 900, 600);
//			Scene scene = new Scene(new AppView(), 900, 600);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
			primaryStage.setIconified(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("微信机器人");
			primaryStage.show();
			primaryStage.setOnCloseRequest(e -> {
				new Thread(() -> {
					System.out.println("wxbotView is disposed = " + WxbotView.browser.dispose(true));
				}).start();
			});
			scene.setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.F12) {
					WxbotView.debug();
				}
			});
			Platform.setImplicitExit(false);
			String url = getClass().getClassLoader().getResource("view/main.html").toExternalForm();
			System.out.println(url);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
