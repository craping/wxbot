package wxrobot.dao.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import wxrobot.dao.entity.field.ContactInfo;

/**
 * 
 * 微信联系人
 * 
 * @author wr
 *
 */
@Data
@Document(collection = "robot_contact")
public class Contact {

	@Id
	@Field(value = "_id")
	private String id;
	@Field
	private String uid;
	@Field
	private List<ContactInfo> individuals;
	@Field
	private List<ContactInfo> chatRooms;
}
