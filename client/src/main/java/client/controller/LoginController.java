package client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.cherry.jeeves.utils.Coder;

import client.Launch;
import client.alert.AlertUtil;
import client.errors.Errors;
import client.utils.Config;
import client.utils.HttpUtil;
import client.utils.Tools;
import client.view.RegisterView;
import client.view.WxbotView;
import client.view.function.Wxbot;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	public static Stage LOGIN_STAGE;
	@FXML
	private TextField loginNameText;
	@FXML
	private PasswordField loginPwdText;
	@FXML
	private Button registerBtn;
	private Wxbot wxbot;
	
	private Map<String, Object> result = new HashMap<String, Object>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		wxbot = Launch.context.getBean(Wxbot.class);
	}

	public void login(ActionEvent event) throws IOException {
		String userName = loginNameText.getText().trim();
		String userPwd = loginPwdText.getText().trim();
		if (Tools.isStrEmpty(userName)) {
			AlertUtil.informationDialog(LOGIN_STAGE, "请输入登录名", null);
			return;
		}
		if (Tools.isStrEmpty(userPwd)) {
			AlertUtil.informationDialog(LOGIN_STAGE, "请输入密码", null);
			return;
		}

		// 组织请求参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login_name", userName);
		params.put("login_pwd", Coder.encryptMD5(userPwd));
		result = HttpUtil.sendRequest(Config.LOGIN_URL, params);
		if (result.isEmpty()) {
			AlertUtil.errorDialog(LOGIN_STAGE, Errors.HTTP_CONNECT_ERR.getErrMsg(), Errors.getErrCodeMsg(Errors.HTTP_CONNECT_ERR));
			return;
		}
		
		if (!result.get("result").toString().equals("0")) {
			AlertUtil.errorDialog(LOGIN_STAGE, result.get("msg").toString(), "错误码：" + result.get("errcode"));
			return;
		}
		
		// 用户token
		String token = (((Map<?, ?>)((Map<?, ?>)result.get("data")).get("info")).get("token")).toString();
		
		// 开启微信机器人
		wxbot.start(token);
	}
	
	/**
	 * 退出登录 清空缓存
	 * @param token
	 */
	public void logout(String token) {
		// 组织请求参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", wxbot.userToken);
		result = HttpUtil.sendRequest(Config.LOGOUT_URL, params);
	}
	
	public void register() {
		WxbotView.getInstance().load();
		Platform.runLater(() -> {
			try {
				new RegisterView().open();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}