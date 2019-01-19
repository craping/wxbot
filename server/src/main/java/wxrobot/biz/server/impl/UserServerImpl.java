package wxrobot.biz.server.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import wxrobot.biz.server.UserServer;
import wxrobot.dao.UserDao;
import wxrobot.dao.entity.User;

@Service
public class UserServerImpl implements UserServer {

	@Autowired
	private UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public User insert(User user) {
		return userDao.insert(user);
	}

	@Override
	public User find(String id) {
		BasicDBObject fieldsObject = new BasicDBObject();
		fieldsObject.put("_id", true);
		fieldsObject.put("userInfo.userName", true);

		BasicDBObject dbObject = new BasicDBObject();
		fieldsObject.put("_id", "5c43609a76785a40bca4aa5e");

		Query query = new BasicQuery(dbObject.toJson(), fieldsObject.toJson());

		return userDao.findOne(query);
	}

}
