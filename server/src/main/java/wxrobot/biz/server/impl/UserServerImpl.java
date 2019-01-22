package wxrobot.biz.server.impl;

import org.crap.jrain.core.ErrcodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import wxrobot.biz.server.UserServer;
import wxrobot.dao.UserDao;
import wxrobot.dao.entity.User;
import wxrobot.server.enums.CustomErrors;

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
	public User insert(User user) throws ErrcodeException {
		String userName = user.getUserInfo().getUserName();
		// 判断用户名是否已存在
		if (userDao.findCount(new Query().addCriteria(Criteria.where("userInfo.userName").is(userName))) > 0)
			throw new ErrcodeException(CustomErrors.USER_EXIST_ERR.setArgs(userName));
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
