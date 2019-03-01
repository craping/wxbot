package client.view.function;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.ScheduleMsg;
import client.utils.HttpUtil;

@Component
@EnableScheduling
public class TimerFunction extends ChatFunction {

	public static Map<String, List<ScheduleMsg>> timerMap;
	
	public final static String GLOBA_SEQ = "globa";
	
	private final String ATTACH_URL = "http://127.0.0.1:8888/";

	public TimerFunction() {
		super();

	}
	
	public void syncTimers(JSObject syncTimerMap) {
		try {
			timerMap = jsonMapper.readValue(syncTimerMap.toJSONString(), new TypeReference<Map<String, List<ScheduleMsg>>>() {});
			timerMap.forEach((k, v) -> {
				v.forEach(msg -> {
					if(msg.getType() != 1){
						System.out.printf("定时文件消息[%s]\n", msg.getContent());
						File attach = new File("resource/attach/"+msg.getContent());
						if(!attach.exists()){
							System.out.printf("文件[%s]不存在 从云端获取...\n", msg.getContent());
							HttpUtil.download(ATTACH_URL+msg.getContent(), attach.getPath());
						}
					}
				});
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addMsg(String seq, JSObject addMsg){
		try {
			ScheduleMsg msg = jsonMapper.readValue(addMsg.toJSONString(), ScheduleMsg.class);
			if(timerMap != null && timerMap.containsKey(seq))
				timerMap.get(seq).add(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delMsg(String seq, String uuid){
		if(timerMap != null && timerMap.containsKey(seq))
			timerMap.get(seq).removeIf(e -> e.getUuid().equals(uuid));
	}
}
