package wxrobot.dao.entity;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import wxrobot.dao.entity.field.ScheduleMsg;

@Data
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
}
