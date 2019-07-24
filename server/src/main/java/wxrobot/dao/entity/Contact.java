package wxrobot.dao.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import wxrobot.dao.entity.field.ContactInfo;

/**
 * 
 * 微信联系人
 * 
 * @author wr
 *
 */
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<ContactInfo> getIndividuals() {
		return individuals;
	}
	public void setIndividuals(List<ContactInfo> individuals) {
		this.individuals = individuals;
	}
	public List<ContactInfo> getChatRooms() {
		return chatRooms;
	}
	public void setChatRooms(List<ContactInfo> chatRooms) {
		this.chatRooms = chatRooms;
	}
	
	
}
