package wxrobot.admin.server.impl;

import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.crap.jrain.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import wxrobot.admin.server.AdminServer;
import wxrobot.dao.AdminDao;
import wxrobot.dao.UserDao;
import wxrobot.dao.entity.AdminUser;

@Service
public class AdminServerImpl implements AdminServer {

	@Autowired
	private AdminDao adminDao;
	@Autowired
	private UserDao userDao;

	@Override
	public AdminUser getUser(String userName, String userPwd) {
		return adminDao.findOne(new Query()
				.addCriteria(Criteria.where("userName").is(userName).and("userPwd").is(userPwd)));
	}

	@Override
	public DataResult getUserList(JSONObject params) {
		Page page = new Page(params.optInt("curPage", 1), params.optInt("pageSize", 10));
		return userDao.findPage(page, new Query());
	}

	@Override
	public int extension(JSONObject params) {
		Query query = new Query();
	    query.addCriteria(Criteria.where("_id").is(params.optString("id")));
	    Update update = Update.update("userInfo.serverEnd", 
	    		DateUtil.parseDate(params.getString("server_end"), "yyyy-MM-dd HH:mm:ss"));
		return userDao.update(query, update);
	}

	@Override
	public int lockUser(JSONObject params) {
		Query query = new Query();
	    query.addCriteria(Criteria.where("_id").is(params.optString("id")));
	    Update update = Update.update("userInfo.serverState", params.optBoolean("server_state"));
		return userDao.update(query, update);
	}
}
