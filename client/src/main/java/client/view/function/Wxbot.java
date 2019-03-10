package client.view.function;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import com.cherry.jeeves.domain.shared.Contact;
import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;

import client.pojo.WxUser;
import client.utils.Config;
import client.view.WxbotView;
import client.view.server.ChatServer;

/**
 * @ClassName: Wxbot
 * @Description: Java与Chromium交互函数类
 * @author Crap
 * @date 2019年1月26日
 * 
 */

@Component
@EnableScheduling
public class Wxbot extends KeywordFunction implements SchedulingConfigurer {
	
	@Autowired
	protected ChatServer chatServer;
	
	public Wxbot() {
		super();
	}

	/**
	 * 获取用户token
	 * 
	 * @return
	 */
	public String getToken() {
		return user.getToken();
	}

	/**
	 * 获取用户信息
	 * 
	 * @param token
	 * @return
	 */
	public JSONString getUserInfo() {
		try {
			return new JSONString(jsonMapper.writeValueAsString(user));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return new JSONString("{}");
	}
	
	  
	/**  
	* @Title: syncUserInfo  
	* @Description: 同步用户信息
	* @param @param syncUserInfo    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	    
	public void syncUserInfo(JSObject syncUserInfo){
		if(syncUserInfo != null && syncUserInfo.toJSONString() != null && !syncUserInfo.toJSONString().isEmpty()){
			try {
				this.user = jsonMapper.readValue(syncUserInfo.toJSONString(), WxUser.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Title: start 
	 * @Description: 启动Jeeves机器人线程 
	 * @param 参数 
	 * @return void 返回类型 
	 * @throws
	 */
	public void start(JSObject syncUserInfo) {
		if (syncUserInfo != null && syncUserInfo.toJSONString() != null && !syncUserInfo.toJSONString().isEmpty()) {
			try {
				this.user = jsonMapper.readValue(syncUserInfo.toJSONString(), WxUser.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	 * @throws
	 */
	public void stop() {
		jeeves.stop();
		if (wxbotThread != null)
			wxbotThread.interrupt();
	}

	@Scheduled(fixedRate=1000)
    private void work() {
		//判断定时消息是否开启
		if(SettingFunction.SETTING.getSwitchs().isGlobalTimer() && TIMER_MAP != null){
			Date now = new Date();
			
			TIMER_MAP.forEach((seq, msgs) -> {
				msgs.forEach(msg -> {
					String[] schedule = msg.getSchedule().split("[|]");
					String scheduleType = schedule[0];
					MessageType msgType;
					switch (msg.getType()) {
					case 1:
						msgType = MessageType.TEXT;
						break;
					case 2:
						msgType = MessageType.IMAGE;
						break;
					case 3:
						msgType = MessageType.EMOTICON;
						break;
					case 4:
						msgType = MessageType.VIDEO;
						break;
					default:
						msgType = MessageType.APP;
						break;
					}
					
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
								chatServer.sendGloba(cacheService.getChatRooms(), msg.getContent(), msgType);
							} else {
								Contact chatRoom = cacheService.getChatRoom(seq);
								if(chatRoom != null){
									if (msg.getType() == 1) {
										sendText(seq, chatRoom.getNickName(), chatRoom.getUserName(), msg.getContent());
									} else {
										sendApp(seq, chatRoom.getNickName(), chatRoom.getUserName(), Config.ATTCH_PATH+msg.getContent());
									}
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
								chatServer.sendGloba(cacheService.getChatRooms(), msg.getContent(), msgType);
							} else {
								Contact chatRoom = cacheService.getChatRoom(seq);
								if(chatRoom != null){
									if (msg.getType() == 1) {
										sendText(seq, chatRoom.getNickName(), chatRoom.getUserName(), msg.getContent());
									} else {
										sendApp(seq, chatRoom.getNickName(), chatRoom.getUserName(), Config.ATTCH_PATH+msg.getContent());
									}
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