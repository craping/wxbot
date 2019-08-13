package wxrobot.biz.server;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import wxrobot.dao.entity.Contact;
import wxrobot.dao.entity.field.ContactInfo;

@Service
public class ContactServer extends BaseServer {

	public long syncContacts(String uid, JSONObject params) {
		Query query = new Query(Criteria.where("uid").is(uid));

		Update update = new Update();
//		update.set("individuals", (List<ContactInfo>) params.optJSONArray("idis"));
		update.set("chatRooms", params.getJSONArray("crs").toJavaList(ContactInfo.class));

		return mongoTemplate.upsert(query, update, Contact.class).getModifiedCount();
	}
	
	public long addContact(String uid, List<ContactInfo> crs) {
		Query query = new Query(Criteria.where("uid").is(uid));

		Update update = new Update();
//		update.set("individuals", (List<ContactInfo>) params.optJSONArray("idis"));
		update.set("chatRooms", crs);

		return mongoTemplate.upsert(query, update, Contact.class).getModifiedCount();
	}

	/**
	 * 获取用户联系人
	 * 
	 * @param uid
	 * @return
	 */
	public Contact getUserContact(String uid) {
		return mongoTemplate.findOne(Query.query(Criteria.where("uid").is(uid)), Contact.class);
	}
}
