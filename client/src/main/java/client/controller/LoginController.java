package client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.cherry.jeeves.Jeeves;

import client.Launch;
import client.alert.AlertUtil;
import client.utils.HttpUtil;
import client.utils.Tools;
import client.view.QRView;
import client.view.WxbotView;
import client.view.function.Wxbot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	private final String LOGIN_URL = "http://127.0.0.1:9527/user/login?format=json";

	public static Stage LOGIN_STAGE;
	@FXML
	private Button loginBtn;
	@FXML
	private Button regBtn;
	@FXML
	private TextField loginNameText;
	@FXML
	private TextField loginPwdText;
	
	private Map<String, Object> result = new HashMap<String, Object>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void login(ActionEvent event) throws IOException {
		String userName = loginNameText.getText().trim();
		String userPwd = loginPwdText.getText().trim();
		if (Tools.isStrEmpty(userName)) {
			AlertUtil.errorDialog(LOGIN_STAGE, "请输入登录名", null);
			return;
		}
		if (Tools.isStrEmpty(userPwd)) {
			AlertUtil.errorDialog(LOGIN_STAGE, "请输入密码", null);
			return;
		}

		// 组织请求参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login_name", userName);
		params.put("login_pwd", userPwd);
		result = HttpUtil.sendRequest(LOGIN_URL, params);
		
		Wxbot bot = Launch.context.getBean(Wxbot.class);
		bot.start();
		
		LOGIN_STAGE.close();
		QRView qrView = new QRView();
		qrView.open();
	}

	public void testWxbotView() {
		LOGIN_STAGE.hide();
		WxbotView wxbotView = new WxbotView(true);
		wxbotView.onClose(e -> {
			LOGIN_STAGE.show();
		});
		wxbotView.load();
	}

}
