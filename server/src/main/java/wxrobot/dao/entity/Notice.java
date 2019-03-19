package wxrobot.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import wxrobot.server.utils.Tools;

/**
 * 系统公告
 *
 */
@Data
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
}
