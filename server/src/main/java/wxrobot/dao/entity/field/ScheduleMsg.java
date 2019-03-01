package wxrobot.dao.entity.field;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class ScheduleMsg {
	
	@Field
	private String uuid;
	
	@Field
	private Integer type;
	
	@Field
	private String content;
	
	@Field
	private String schedule;
}
