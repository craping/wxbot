package wxrobot.dao.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import wxrobot.dao.entity.field.KeywordMap;

@Data
@Document(collection = "robot_keyword")
@JsonInclude(Include.NON_NULL)
public class Keyword {
	
	@Id
	@Field(value = "_id")
	private String id;
	
	@Field
	private String userName;
	
	@Field
	private List<KeywordMap> keyMaps;
}
