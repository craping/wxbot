package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import wxrobot.server.utils.Tools;

/**
 * 系统公告
 *
 */
@Document(collection = "robot_notice")
@JsonInclude(Include.NON_NULL)
public class Notice {

	@Id
	@Field(value = "_id")
	private String id;
	@Field
	private String title;
	@Field
	private String content;
	@Field
	private String oprTime = Tools.getTimestamp(); // 操作时间
	@Field
	private String sendTime; // 设定的发布时间
	@Field
	private Boolean state = false; // true 发布，false 取消发布
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOprTime() {
		return oprTime;
	}
	public void setOprTime(String oprTime) {
		this.oprTime = oprTime;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}
	
	
}
