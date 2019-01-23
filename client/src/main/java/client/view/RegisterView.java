package client.view;

import java.io.IOException;

import client.controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegisterView extends AnchorPane {

	public Stage REGISTER_STAGE;

	public RegisterView() {
		REGISTER_STAGE = new Stage();
	}

	public void open() throws IOException {

		LoginController.LOGIN_STAGE.hide();

		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Register.fxml"));
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
		REGISTER_STAGE.setIconified(false);
		REGISTER_STAGE.setScene(scene);
		REGISTER_STAGE.setTitle("用户注册");
		REGISTER_STAGE.setResizable(false);
		REGISTER_STAGE.setOnCloseRequest(e -> {
			REGISTER_STAGE.close();
			LoginController.LOGIN_STAGE.show();
		});
		REGISTER_STAGE.show();
	}
}
