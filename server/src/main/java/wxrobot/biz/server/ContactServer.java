package wxrobot.biz.server;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import wxrobot.dao.entity.Contact;
import wxrobot.dao.entity.field.ContactInfo;

@Service
public class ContactServer extends BaseServer{

	@SuppressWarnings("unchecked")
	public long syncContacts(String uid, JSONObject params) {
		Query query = new Query(Criteria.where("uid").is(uid));
		
		Update update = new Update();
//		update.set("individuals", (List<ContactInfo>) params.optJSONArray("idis"));
		update.set("chatRooms", (List<ContactInfo>) params.optJSONArray("crs"));
		
		return mongoTemplate.upsert(query, update, Contact.class).getModifiedCount();
	}
}
