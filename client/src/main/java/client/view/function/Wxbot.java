package client.view.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.Jeeves;
import com.cherry.jeeves.domain.shared.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.utils.Config;
import client.utils.HttpUtil;
import client.utils.Tools;
import client.view.WxbotView;

/**
 * @ClassName: Wxbot
 * @Description: Java与Chromium交互函数类
 * @author Crap
 * @date 2019年1月26日
 * 
 */

@Component
public class Wxbot extends KeywordFunction {

	@Autowired
	private Jeeves jeeves;

	public Thread wxbotThread;

	public String userToken;

	public Wxbot() {
		super();
	}

	/**
	 * 获取用户token
	 * 
	 * @return
	 */
	public String getToken() {
		return userToken;
	}
	
	/**
	 * 获取用户信息
	 * @param token
	 * @return
	 */
	public JSONString getUserInfo() {
		// 组织请求参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", userToken);
		Map<String, Object> result = HttpUtil.sendRequest(Config.USERINFO_URL, params);
		try {
			System.out.println(result);
			return new JSONString(jsonMapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}

	/**
	 * 根据用户名userName 获取用户contact
	 * 
	 * @param contacts 微信
	 * @param userName
	 * @return
	 */
	public static Contact getSender(Set<Contact> contacts, String userName) {
		return contacts.stream().filter(individual -> userName.equals(individual.getUserName())).findFirst().orElse(null);
	}
	
	/**
	 * 获取群聊详情
	 * @param chatRooms
	 * @param chatRoomName
	 * @return
	 */
	public Contact getChatRoom(Set<Contact> chatRooms, String chatRoomName) {
		Contact chatRoom = chatRooms.stream().filter(x -> chatRoomName.equals(x.getUserName())).findFirst().orElse(null);
		try {
			// 再次获取群详情，并获取群成员详情
			chatRoom = wechatService.getChatRoomInfo(chatRoom.getUserName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chatRoom;
	}

	/**
	 * @Title: start 
	 * @Description: 启动Jeeves机器人线程 
	 * @param 参数 
	 * @return void 返回类型 
	 * @throws
	 */
	public void start(String token) {
		if (Tools.isStrEmpty(userToken)) {
			userToken = token;
		}
		wxbotThread = new Thread(() -> {
			try {
				jeeves.start();
			} catch (Exception e) {
			}
			wxbotThread = null;
		});
		wxbotThread.setDaemon(true);
		wxbotThread.start();
	}

	/**
	 * @Title: stop 
	 * @Description: 停止Jeeves机器人线程， 
	 * @param 参数 
	 * @return void 返回类型
	 *  @throws
	 */
	public void stop() {
		jeeves.stop();
		if (wxbotThread != null)
			wxbotThread.interrupt();
	}

	public JSONString test() {
		Set<Contact> sets = new HashSet<>();
		Contact c = new Contact();
		c.setUserName("123");
		sets.add(c);
		Contact c1 = new Contact();
		c1.setUserName("123456");
		sets.add(c1);
		try {
			return new JSONString(jsonMapper.writeValueAsString(sets));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}

	public void execute() {
		Map<String, String> seqMap = new HashMap<>();
		seqMap.put("123", "321");
		WxbotView wxbotView = WxbotView.getInstance();
		JSObject app = wxbotView.getBrowser().executeJavaScriptAndReturnValue("app").asObject();
		JSValue onMembersSeqChanged = app.getProperty("onMembersSeqChanged");
		try {
			onMembersSeqChanged.asFunction().invoke(app, new JSONString(jsonMapper.writeValueAsString(seqMap)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}