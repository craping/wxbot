package client.view.function;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.teamdev.jxbrowser.chromium.JSArray;

import client.pojo.Schedule;

@Component
public class TimerFunction extends ChatFunction {

	public static List<Schedule> timers;
	
	public final static String GLOBA_SEQ = "globa";

	public TimerFunction() {
		super();

	}
	
	public void syncTimers(JSArray timers) {
		System.out.println(timers);
		try {
			TimerFunction.timers = jsonMapper.readValue(timers.toJSONString(), new TypeReference<List<Schedule>>() {});
			System.out.println(TimerFunction.timers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
