package client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterController implements Initializable {

	public static Stage REGISTER_STAGE;
	@FXML
	private TextField phoneNumTxt;
	@FXML
	private TextField codeTxt;
	@FXML
	private PasswordField pwdTxt;
	@FXML
	private PasswordField confirmPwdTxt;
	@FXML
	private Button sendCodeBtn;
	@FXML
	private Button cancelBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void register() {
		
	}
	
	public void cancel() {
		cancelBtn.getScene().getWindow().hide();
		LoginController.LOGIN_STAGE.show();
	}
	
	private Timeline animation;
    private String S = "";
    private int tmp = 5;
    
	public void sendCode() {
		tmp = 5;
		animation = new Timeline(new KeyFrame(Duration.millis(1000), e -> timelabel()));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
	}
	
	public void timelabel() {
        tmp--;
        S = "重新发送(" + tmp + ")";
        sendCodeBtn.setDisable(true);
        sendCodeBtn.setText(S);
        if (tmp <= 0) {
        	sendCodeBtn.setDisable(false);
        	sendCodeBtn.setText("重新发送");
        	sendCodeBtn.setOnAction((ActionEvent e) -> {
        		sendCode();
        	});
        	animation.stop();
        }
    }
}
