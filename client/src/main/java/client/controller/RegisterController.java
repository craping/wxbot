package client.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class RegisterController implements Initializable {

	private final String REGISTER_URL = "http://127.0.0.1:9527/user/register?format=json";

	public static Stage LOGIN_STAGE;
	@FXML
	private TextField loginNameText;
	@FXML
	private PasswordField loginPwdText;
	@FXML
	private Button registerBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
	
	public void register() {
		
	}

}
