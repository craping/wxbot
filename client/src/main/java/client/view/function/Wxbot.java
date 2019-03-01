package client.view.function;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
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
public class Wxbot extends KeywordFunction implements SchedulingConfigurer {

	@Autowired
	private Jeeves jeeves;

	public Thread wxbotThread;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("M,d,H,m,s");
	
	public String userToken = "e7c1cdfcf411481b97bba2ddf6bc4611";

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

	@Scheduled(fixedRate=1000)
    private void work() {
		if(timerMap != null && timerMap.size() > 0){
			String[] dateTime = dateFormat.format(new Date()).split(",");
			timerMap.forEach((k, v) -> {
				v.forEach(s -> {
					String[] schedule = s.getSchedule().split(",");
					if("*".equals(schedule[0]) || dateTime[0].equals(schedule[0]) 
						&& "*".equals(schedule[1]) || dateTime[1].equals(schedule[1])
						&& "*".equals(schedule[2]) || dateTime[2].equals(schedule[2])
						&& "*".equals(schedule[3]) || dateTime[3].equals(schedule[3])
						&& "*".equals(schedule[4]) || dateTime[4].equals(schedule[4])
					){
						System.out.println("定时消息匹配："+s.getSchedule());
					}
				});
			});
		}
    }
	
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}
	
	@Bean(destroyMethod="shutdown")
	private Executor taskExecutor() {
        return Executors.newScheduledThreadPool(60);
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