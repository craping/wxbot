package client.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.cherry.jeeves.utils.Coder;

import client.alert.AlertUtil;
import client.errors.Errors;
import client.utils.Config;
import client.utils.HttpUtil;
import client.utils.RegexUtil;
import client.utils.Tools;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		String phoneNum = phoneNumTxt.getText().trim();
		if (!RegexUtil.validate(phoneNum, RegexUtil.PHONE_NUM_REGEX)) {
			AlertUtil.informationDialog(REGISTER_STAGE, "请输入正确的手机号", null);
			return;
		}
		String pwd = pwdTxt.getText().trim();
		if (Tools.isStrEmpty(pwd)) {
			AlertUtil.informationDialog(REGISTER_STAGE, "请输入密码", null);
			return;
		}
		String confirmPwd = confirmPwdTxt.getText().trim();
		if (Tools.isStrEmpty(confirmPwd)) {
			AlertUtil.informationDialog(REGISTER_STAGE, "请输入确认密码", null);
			return;
		}
		if (!pwd.equals(confirmPwd)) {
			AlertUtil.informationDialog(REGISTER_STAGE, "两次密码输入不一致", null);
			return;
		}

		// 组织请求参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_name", phoneNum);
		params.put("user_pwd", Coder.encryptMD5(pwd));
		params.put("confirm_pwd", Coder.encryptMD5(confirmPwd));
		params.put("phone_num", phoneNum);
		params.put("phone_code", codeTxt.getText());
		Map<String, Object> result = HttpUtil.sendRequest(Config.REGISTER_URL, params);
		if (result.isEmpty()) {
			AlertUtil.errorDialog(REGISTER_STAGE, Errors.HTTP_CONNECT_ERR.getErrMsg(), Errors.getErrCodeMsg(Errors.HTTP_CONNECT_ERR));
			return;
		}
		if (!result.get("result").toString().equals("0")) {
			AlertUtil.errorDialog(REGISTER_STAGE, result.get("msg").toString(), "错误码：" + result.get("errcode"));
			return;
		}

		Alert alert = new Alert(Alert.AlertType.NONE);
		// 设置窗口的标题
		alert.setTitle("信息");
		alert.setHeaderText("注册成功");
		// 设置对话框的 icon 图标，参数是主窗口的 stage
		alert.initOwner(REGISTER_STAGE);
		ButtonType buttonPlayAgain = new ButtonType("确定");
		alert.getButtonTypes().setAll(buttonPlayAgain);
		alert.setOnCloseRequest(e -> {
			System.out.println("3");
			ButtonType btnResult = alert.getResult();
			if (btnResult != null && btnResult == buttonPlayAgain) {
				cancelBtn.getScene().getWindow().hide();
				LoginController.LOGIN_STAGE.show();
			}
		});
		alert.show();
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
