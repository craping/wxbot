package wxrobot.dao.entity.field;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ScheduleMsg extends Msg{
	
	@Field
	private String uuid;
	
	@Field
	private String schedule;
}
