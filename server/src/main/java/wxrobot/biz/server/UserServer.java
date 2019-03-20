package wxrobot.biz.server;

import org.crap.jrain.core.ErrcodeException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

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
		Query query = new Query(Criteria.where("userInfo.userName").is(userName));
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
		return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), User.class);
	}
	
	/**
	 * 判断用户是否存在 不存在则报错
	 * @param id
	 * @return User.class
	 * @throws ErrcodeException
	 */
	public User checkUserExist(String id) throws ErrcodeException {
		// 判断用户名是否已存在
		User user = find(id);
		if (user == null)
			throw new ErrcodeException(CustomErrors.USER_NOT_EXIST);
		return user;
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
			Query query = new Query(Criteria.where("id").is(user.getId()));
			Update update = Update.update("token", user.getToken());
			return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
