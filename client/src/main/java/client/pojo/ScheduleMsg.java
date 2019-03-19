package client.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ScheduleMsg extends Msg {
	@JsonProperty
	private String uuid;
	@JsonProperty
	private String schedule;
	@JsonProperty
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Date getLastSendTime() {
		return lastSendTime;
	}

	public void setLastSendTime(Date lastSendTime) {
		this.lastSendTime = lastSendTime;
	}
    
    
}
