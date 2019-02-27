package client.pojo;

import lombok.Data;

@Data
public class ScheduleMsg {
	
	private String uuid;
	
	private int type;
	
	private String content;
	
	private String schedule;
}
