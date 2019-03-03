package wxrobot.biz.server;

import org.crap.jrain.core.ErrcodeException;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import wxrobot.biz.server.impl.BaseServer;
import wxrobot.dao.entity.User;
import wxrobot.server.enums.CustomErrors;

@Service
public class UserServer extends BaseServer{

	/**
	 * 新增用户
	 * @param user
	 * @return
	 * @throws ErrcodeException
	 */
	public User insert(User user) throws ErrcodeException {
		String userName = user.getUserInfo().getUserName();
		// 判断用户名是否已存在
		Query query = new Query();
		query.addCriteria(Criteria.where("userInfo.userName").is(userName));
		if (mongoTemplate.count(query, User.class) > 0)
			throw new ErrcodeException(CustomErrors.USER_EXIST_ERR.setArgs(userName));
		return mongoTemplate.insert(user);
	}

	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User find(String id) {
		BasicDBObject fieldsObject = new BasicDBObject();
		fieldsObject.put("_id", true);
		fieldsObject.put("userInfo.userName", true);

		BasicDBObject dbObject = new BasicDBObject();
		fieldsObject.put("_id", "5c43609a76785a40bca4aa5e");

		Query query = new BasicQuery(dbObject.toJson(), fieldsObject.toJson());
		return mongoTemplate.findOne(query, User.class);
	}

	/**
	 * 获取用户
	 * @param userName
	 * @param userPwd
	 * @return
	 */
	public User getUser(String userName, String userPwd) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userInfo.userName").is(userName).and("userInfo.userPwd").is(userPwd));
		return mongoTemplate.findOne(query, User.class);
	}

	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateToken(User user) {		
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(user.getId()));
			Update update = Update.update("token", user.getToken());
			return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
