package client;

import client.view.AppView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launch extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Scene scene = new Scene(new AppView(), 900, 600);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
			primaryStage.setIconified(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("微信机器人");
			primaryStage.show();
			primaryStage.setOnCloseRequest(e -> {
				System.exit(0);
			});
			Platform.setImplicitExit(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
