package client.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class ScheduleMsg {
	
	private String uuid;
	
	private int type;
	
	private String content;
	
	private String schedule;
	
	private Date lastSendTime;
}
