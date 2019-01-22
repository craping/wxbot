package client.controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import client.view.WxbotView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {
	
	public static Stage LOGIN_STAGE;
	@FXML
	private Button loginBtn;
	@FXML
	private Button regBtn;
	@FXML
	private TextField loginNameText;
	@FXML
	private TextField loginPwdText;
	@Autowired
	private RestTemplate restTemplate;
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void login(ActionEvent event) {
		System.out.println("Button Clicked!");

		Date now = new Date();

		DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
		String dateTimeString = df.format(now);
		// Show in VIEW
//		myTextField.setText(dateTimeString);

	}
	
	private String url = "http://127.0.0.1:9527/user/login?format=json";
	public void login1() {
		HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        
        Map<String, String> params = new HashMap<>();
        params.put("login_name", "111111");
		try {
			HttpEntity<String> formEntity = new HttpEntity<String>(jsonMapper.writeValueAsString(params), headers);
			String result = restTemplate.postForObject(url, formEntity, String.class);
			//ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(customHeader), String.class);
			System.out.println(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
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
