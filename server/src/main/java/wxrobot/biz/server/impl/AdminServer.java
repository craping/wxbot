package wxrobot.biz.server.impl;

import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.crap.jrain.core.util.DateUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import wxrobot.dao.entity.AdminUser;
import wxrobot.dao.entity.User;

@Service
public class AdminServer extends BaseServer {

	/**
	  *  获取admin用户
	 * @param userName
	 * @param userPwd
	 * @return
	 */
	public AdminUser getUser(String userName, String userPwd) {
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is(userName).and("userPwd").is(userPwd));
		return mongoTemplate.findOne(query, AdminUser.class);
	}

	/**
	  *  获取用户列表
	 * @param params
	 * @return
	 */
	public DataResult getUserList(JSONObject params) {
		Page page = new Page(params.optInt("curPage", 1), params.optInt("pageSize", 10));
		return findPage(page, new Query(), User.class);
	}

	/**
	  *  更新软件服务时间
	 * @param params
	 * @return
	 */
	public int extension(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.optString("id")));
		Update update = Update.update("userInfo.serverEnd", DateUtil.parseDate(params.getString("server_end"), "yyyy-MM-dd HH:mm:ss"));
		return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}

	/**
	  *  锁定用户
	 * @param params
	 * @return
	 */
	public int lockUser(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.optString("id")));
		Update update = Update.update("userInfo.serverState", params.optBoolean("server_state"));
		return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}
}
