package client.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class ScheduleMsg {
	
	private String uuid;
	
	private int type;
	
	private String content;
	
	private String schedule;
	
	private Date lastSendTime;
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleMsg msg = (ScheduleMsg) o;
        return this.getUuid().equals(msg.getUuid());
    }

    @Override
    public int hashCode() {
        return this.getUuid().hashCode();
    }
}
