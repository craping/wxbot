package client.view.function;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.utils.Config;
import client.utils.FileUtil;
import client.view.LoginView;
import client.view.WxbotView;
import client.view.server.BaseServer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * @ClassName: Wxbot
 * @Description: Java与Chromium交互函数类
 * @author Crap
 * @date 2019年1月26日
 * 
 */

@Component
@EnableScheduling
public class Wxbot extends TipFunction implements SchedulingConfigurer {
	
	public Wxbot() {
		super();
	}

	public String getDomain(){
		return Config.DOMAIN;
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @param token
	 * @return
	 */
	public JSONString getUserInfo() {
		return new JSONString(USER.toJSONString());
	}
	
	/**  
	 * @Title: syncUserInfo  
	 * @Description: 同步用户信息
	 * @param @param syncUserInfo    参数  
	 * @return void    返回类型  
	 * @throws  
	 */  
	
	public void syncUserInfo(JSObject syncUserInfo){
		new Thread(() -> {
			USER = syncUserInfo;
		}).start();
	}
	
	public void syncServerTime(String serverEnd){
		new Thread(() -> {
			USER.getProperty("userInfo").asObject().setProperty("serverEnd", serverEnd);
			JSObject app = WxbotView.getInstance().getSettingBrowser().executeJavaScriptAndReturnValue("app").asObject();
			JSValue notifyUser = app.getProperty("notifyUser");
			notifyUser.asFunction().invokeAsync(app, new JSONString(USER.toJSONString()));
		}).start();
	}
	
	public void syncServerState(boolean serverState){
		new Thread(() -> {
			USER.getProperty("userInfo").asObject().setProperty("serverState", serverState);
			JSObject app = WxbotView.getInstance().getSettingBrowser().executeJavaScriptAndReturnValue("app").asObject();
			JSValue notifyUser = app.getProperty("notifyUser");
			notifyUser.asFunction().invokeAsync(app, new JSONString(USER.toJSONString()));
		}).start();
	}

	/**
	 * @Title: start 
	 * @Description: 启动Jeeves机器人线程 
	 * @param 参数 
	 * @return void 返回类型 
	 * @throws
	 */
	public void start(JSObject syncUserInfo) {
		USER = syncUserInfo;
		wxbotThread = new Thread(() -> {
			try {
				FileUtil.start();
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
	 * @throws
	 */
	public void stop() {
		jeeves.stop();
		if (wxbotThread != null){
			wxbotThread.interrupt();
		}
		Platform.runLater(() -> {
			WxbotView.exit();
			LoginView.exit();
		});
	}

	public void exit(String title, String msg) {
		jeeves.stop();
		if (wxbotThread != null){
			wxbotThread.interrupt();
		}
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(msg);
			Optional<ButtonType> result = alert.showAndWait();
			if(result.isPresent() && result.get() == ButtonType.OK){
				WxbotView.exit();
				LoginView.exit();
			}
		});
	}
	
	
	@Scheduled(fixedRate=1000)
    private void work() {
		//定时消息功能是否开启
		if(SettingFunction.isWorking() && SettingFunction.SETTING.getSwitchs().isGlobalTimer() && TIMER_MAP != null){
			Date now = new Date();
			
			TIMER_MAP.forEach((seq, msgs) -> {
				//如果是“全群”定时消息 并且 (无“全群”定时消息权限 或者 “全群”定时消息开关关闭) 则跳出
				if(Config.GLOBA_SEQ.equals(seq) && (!SettingFunction.SETTING.getPermissions().isGlobalTimer() || !SettingFunction.SETTING.getTimers().contains(Config.GLOBA_SEQ)))
					return;
				
				//如果是“分群”定时消息 并且 (无“分群”定时消息权限 或者 “分群”定时消息开关关闭) 则跳出
				if(!Config.GLOBA_SEQ.equals(seq) && (!SettingFunction.SETTING.getPermissions().isTimer() || !SettingFunction.SETTING.getTimers().contains(seq)))
					return;
				
				msgs.forEach(msg -> {
					String[] schedule = msg.getSchedule().split("[|]");
					String scheduleType = schedule[0];
					
					//固定时间发送
					if ("1".equals(scheduleType)) {
						String[] cron = schedule[1].split(",");
						String[] dateTime = dateFormat.format(now).split(",");
						if(("*".equals(cron[0]) || dateTime[0].equals(cron[0]))
							&& ("*".equals(cron[1]) || dateTime[1].equals(cron[1]))
							&& ("*".equals(cron[2]) || dateTime[2].equals(cron[2]))
							&& ("*".equals(cron[3]) || dateTime[3].equals(cron[3]))
							&& ("*".equals(cron[4]) || dateTime[4].equals(cron[4]))
						){
							System.out.println("固定时间消息匹配："+msg.getSchedule());
							//全局群消息走转发
							if (Config.GLOBA_SEQ.equals(seq)) {
								chatServer.sendGloba(cacheService.getChatRooms(), msg);
							} else {
								Contact chatRoom = cacheService.getChatRoom(seq);
								if(chatRoom != null){
									chatServer.sendGloba(Arrays.asList(chatRoom), msg);
								}
							}
						}
					} 
					//间隔时间发送
					else {
						long interval = Long.parseLong(schedule[1])*1000;
						msg.setLastSendTime(msg.getLastSendTime() == null?new Date():msg.getLastSendTime());
						if(now.getTime() - msg.getLastSendTime().getTime() >= interval){
							
							System.out.println("间隔时间消息匹配："+msg.getSchedule());
							//全局群消息走转发
							if (Config.GLOBA_SEQ.equals(seq)) {
								chatServer.sendGloba(cacheService.getChatRooms(), msg);
							} else {
								Contact chatRoom = cacheService.getChatRoom(seq);
								if(chatRoom != null){
									chatServer.sendGloba(Arrays.asList(chatRoom), msg);
								}
							}
							msg.setLastSendTime(now);
						}
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
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(sets));
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
			onMembersSeqChanged.asFunction().invokeAsync(app, new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(seqMap)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}