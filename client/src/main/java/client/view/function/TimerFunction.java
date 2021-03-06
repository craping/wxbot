package client.view.function;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSONString;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.ScheduleMsg;
import client.utils.Config;
import client.view.WxbotView;
import client.view.server.BaseServer;

@Component
public class TimerFunction extends ChatFunction {

	public static ConcurrentHashMap<String, ConcurrentLinkedQueue<ScheduleMsg>> TIMER_MAP = new ConcurrentHashMap<>();
	
	public TimerFunction() {
		super();

	}
	
	public JSONString getMsgs(String seq){
		ConcurrentLinkedQueue<ScheduleMsg> msgs = TIMER_MAP.get(seq);
		if(msgs == null)
			return new JSONString("[]");
		try {
			return new JSONString(BaseServer.JSON_MAPPER.writeValueAsString(msgs));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new JSONString("[]");
		}
	}
	
	public void syncTimers(JSObject syncTimerMap) {
		new Thread(() -> {
			try {
				TIMER_MAP.clear();
				ConcurrentHashMap<String, ConcurrentLinkedQueue<ScheduleMsg>> timerMap = 
						BaseServer.JSON_MAPPER.readValue(syncTimerMap.toJSONString(), new TypeReference<ConcurrentHashMap<String, ConcurrentLinkedQueue<ScheduleMsg>>>() {});
				timerMap.forEach((k, v) -> {
					v.forEach(msg -> {
						if(msg.getType() != 1){
							System.out.printf("定时文件消息[%s]\n", msg.getContent());
							downloadAttach(msg.getContent());
						}
					});
				});
				TIMER_MAP.putAll(timerMap);
				WxbotView.getInstance().executeSettingScript("app.getMsgs()");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	public void addMsg(String seq, JSObject addMsg){
		try {
			ScheduleMsg msg = BaseServer.JSON_MAPPER.readValue(addMsg.toJSONString(), ScheduleMsg.class);
			if(msg.getType() != 1){
				System.out.printf("定时文件消息[%s]\n", msg.getContent());
				downloadAttach(msg.getContent());
			}
			
			ConcurrentLinkedQueue<ScheduleMsg> msgs = TIMER_MAP.get(seq);
			if (msgs == null) {
				msgs = new ConcurrentLinkedQueue<>();
				TIMER_MAP.put(seq, msgs);
			}
			msgs.add(msg);
			if(Config.GLOBA_SEQ.equals(seq))
				WxbotView.getInstance().executeSettingScript("app.getMsgs()");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delMsg(String seq, String uuid){
		ConcurrentLinkedQueue<ScheduleMsg> msgs = TIMER_MAP.get(seq);
		if (msgs != null)
			msgs.removeIf(e -> e.getUuid().equals(uuid));
		
		if(Config.GLOBA_SEQ.equals(seq))
			WxbotView.getInstance().executeSettingScript("app.getMsgs()");
	}
}
