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
import client.utils.HttpUtil;

@Component
public class TimerFunction extends ChatFunction {

	public static Map<String, LinkedList<ScheduleMsg>> timerMap;
	
	public final static String GLOBA_SEQ = "global";
	
	public final String ATTACH_URL = "http://127.0.0.1:8888/";
	
	public final String ATTCH_PATH = "resource/attach/";

	public TimerFunction() {
		super();

	}
	
	public void syncTimers(JSObject syncTimerMap) {
		try {
			timerMap = jsonMapper.readValue(syncTimerMap.toJSONString(), new TypeReference<Map<String, LinkedList<ScheduleMsg>>>() {});
			timerMap.forEach((k, v) -> {
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
			if(timerMap == null){
				timerMap = new HashMap<>();
			}
			if (!timerMap.containsKey(seq)) {
				timerMap.put(seq, new LinkedList<>());
			}
			timerMap.get(seq).add(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delMsg(String seq, String uuid){
		if(timerMap != null && timerMap.containsKey(seq))
			timerMap.get(seq).removeIf(e -> e.getUuid().equals(uuid));
	}
	
	public void downloadAttach(String fileName){
		File attach = new File(ATTCH_PATH+fileName);
		if(!attach.exists()){
			System.out.printf("文件[%s]不存在 从云端获取...\n", fileName);
			HttpUtil.download(ATTACH_URL + user.getUserName() + "/" + fileName, attach.getPath());
			System.out.printf("文件[%s]下载完毕\n", fileName);
		}
	}
}
