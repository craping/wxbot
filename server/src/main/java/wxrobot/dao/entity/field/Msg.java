package wxrobot.dao.entity.field;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
@Data
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
}
