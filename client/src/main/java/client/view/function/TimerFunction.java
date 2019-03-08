package client.view.function;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSObject;

import client.pojo.ScheduleMsg;
import client.utils.Config;
import client.utils.HttpUtil;

@Component
public class TimerFunction extends ChatFunction {

	public static Map<String, LinkedList<ScheduleMsg>> TIMER_MAP;
	
	public TimerFunction() {
		super();

	}
	
	public void syncTimers(JSObject syncTimerMap) {
		try {
			TIMER_MAP = jsonMapper.readValue(syncTimerMap.toJSONString(), new TypeReference<Map<String, LinkedList<ScheduleMsg>>>() {});
			TIMER_MAP.forEach((k, v) -> {
				v.forEach(msg -> {
					if(msg.getType() != 1){
						System.out.printf("定时文件消息[%s]\n", msg.getContent());
						downloadAttach(msg.getContent());
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
			if(msg.getType() == 2)
				downloadAttach(msg.getContent());
			if(TIMER_MAP == null){
				TIMER_MAP = new HashMap<>();
			}
			if (!TIMER_MAP.containsKey(seq)) {
				TIMER_MAP.put(seq, new LinkedList<>());
			}
			TIMER_MAP.get(seq).add(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delMsg(String seq, String uuid){
		if(TIMER_MAP != null && TIMER_MAP.containsKey(seq))
			TIMER_MAP.get(seq).removeIf(e -> e.getUuid().equals(uuid));
	}
	
	public void downloadAttach(String fileName){
		File attach = new File(Config.ATTCH_PATH+fileName);
		if(!attach.exists()){
			System.out.printf("文件[%s]不存在 从云端获取...\n", fileName);
			HttpUtil.download(Config.ATTACH_URL + user.getUserName() + "/" + fileName, attach.getPath());
			System.out.printf("文件[%s]下载完毕\n", fileName);
		}
	}
}
