package wxrobot.dao.entity.field;

import org.springframework.data.mongodb.core.mapping.Field;
public class Msg {
	@Field
	protected Integer type;
	
	@Field
	protected String content;
	
	public Msg() {
	}
	
	public Msg(int type, String content){
		this.type = type;
		this.content = content;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
