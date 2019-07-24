package wxrobot.dao.entity.field;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
public class ScheduleMsg extends Msg{
	
	@Field
	private String uuid;
	
	@Field
	private String schedule;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	
}
