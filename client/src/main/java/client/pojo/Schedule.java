package client.pojo;

import java.util.List;

import lombok.Data;

@Data
public class Schedule {
	
	private String seq;
	
	private List<ScheduleMsg> msgs;
}
