package client.pojo;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Setting {
	/**  
	* @Fields 图灵机器人数组[seq]
	*/  
	@JsonProperty
	private ConcurrentLinkedQueue<String> turing = new ConcurrentLinkedQueue<>();
	/**  
	* @Fields 关键词数组[seq]
	*/  
	@JsonProperty
	private ConcurrentLinkedQueue<String> keywords = new ConcurrentLinkedQueue<>();
	
	/**  
	* @Fields 定时消息数组[seq]
	*/  
	@JsonProperty
	private ConcurrentLinkedQueue<String> timers = new ConcurrentLinkedQueue<>();
	
	/**  
	* @Fields 群转发数组[seq]
	*/  
	@JsonProperty
	private ConcurrentLinkedQueue<String> forwards = new ConcurrentLinkedQueue<>();
	
	/**  
	* @Fields 提示语设置
	*/  
	@JsonProperty
	private Tips tips = new Tips();
	
	/**  
	* @Fields 功能开关设置
	*/  
	@JsonProperty
	private Switchs switchs = new Switchs();
	
	/**  
	* @Fields 用户权限
	*/  
	@JsonProperty
	private Permissions permissions = new Permissions();
	
	
	public ConcurrentLinkedQueue<String> getTuring() {
		return turing;
	}

	public void setTuring(ConcurrentLinkedQueue<String> turing) {
		this.turing = turing;
	}

	public ConcurrentLinkedQueue<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ConcurrentLinkedQueue<String> keywords) {
		this.keywords = keywords;
	}

	public ConcurrentLinkedQueue<String> getTimers() {
		return timers;
	}

	public void setTimers(ConcurrentLinkedQueue<String> timers) {
		this.timers = timers;
	}

	public ConcurrentLinkedQueue<String> getForwards() {
		return forwards;
	}

	public void setForwards(ConcurrentLinkedQueue<String> forwards) {
		this.forwards = forwards;
	}

	public Tips getTips() {
		return tips;
	}

	public void setTips(Tips tips) {
		this.tips = tips;
	}

	public Switchs getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switchs switchs) {
		this.switchs = switchs;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
	
}
