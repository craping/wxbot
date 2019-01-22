package client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import client.controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Configuration
@PropertySource("application.properties")
@ComponentScan({"com.cherry.jeeves", "client"})
public class Launch extends Application {
	
	public static ApplicationContext context = new AnnotationConfigApplicationContext(Launch.class);
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml")));
			scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
			primaryStage.setIconified(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("微信机器人");
			primaryStage.setOnCloseRequest(e -> {
				System.exit(0);
			});
			primaryStage.show();
			Platform.setImplicitExit(false);
			LoginController.LOGIN_STAGE = primaryStage;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
