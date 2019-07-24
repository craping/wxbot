package wxrobot.dao.entity;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import wxrobot.dao.entity.field.ScheduleMsg;

@Document(collection = "robot_timer")
@JsonInclude(Include.NON_NULL)
public class Timer {
	
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field
	private String userName;
	
	@Field
	private Map<String, List<ScheduleMsg>> timerMap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Map<String, List<ScheduleMsg>> getTimerMap() {
		return timerMap;
	}

	public void setTimerMap(Map<String, List<ScheduleMsg>> timerMap) {
		this.timerMap = timerMap;
	}
	
	
}
