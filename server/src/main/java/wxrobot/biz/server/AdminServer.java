package wxrobot.biz.server;

import java.util.regex.Pattern;

import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.crap.jrain.core.validate.security.component.Coder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import wxrobot.dao.entity.AdminUser;
import wxrobot.dao.entity.User;
import wxrobot.server.utils.Tools;

@Service
public class AdminServer extends BaseServer {
	
	/**
	 * 新增管理员帐号
	 * @param user
	 * @return
	 */
	public AdminUser addAdmin(AdminUser user) {
		return mongoTemplate.insert(user);
	}

	/**
	 * 获取用户
	 * 
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
	 * 获取用户列表
	 * 
	 * @param params
	 * @return
	 */
	public DataResult getUserList(JSONObject params) {
		Page page = new Page((Integer)params.getOrDefault("curPage", 1), (Integer)params.getOrDefault("pageSize", 10));
		Query query = new Query();
		if (!Tools.isStrEmpty(params.getString("userName"))) {
			Pattern pattern = Pattern.compile("^.*" + params.getString("userName") + ".*$", Pattern.CASE_INSENSITIVE);
			query.addCriteria(Criteria.where("userInfo.userName").regex(pattern));
		}
		if (!Tools.isStrEmpty(params.getString("phoneState"))) {
			query.addCriteria(Criteria.where("userInfo.phoneState").is(params.getBoolean("phoneState")));
		}
		if (!Tools.isStrEmpty(params.getString("serverState"))) {
			query.addCriteria(Criteria.where("userInfo.serverState").is(params.getBoolean("serverState")));
		}
		if (!Tools.isStrEmpty(params.getString("destroy"))) {
			query.addCriteria(Criteria.where("userInfo.destroy").is(params.getBoolean("destroy")));
		}
		return findPage(page, query, User.class);
	}

	/**
	 * 更新软件服务时间
	 * 
	 * @param params
	 * @return
	 */
	public long extension(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.getString("id")));
		Update update = Update.update("userInfo.serverEnd", Tools.dateToStamp(params.getString("server_end"), "yyyy-MM-dd HH:mm"));
		return mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}

	/**
	 * 充值密码 888888
	 * 
	 * @param params
	 * @return
	 */
	public long resetPwd(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.getString("id")));
		Update update = Update.update("userInfo.userPwd", Coder.encryptMD5("888888"));
		return mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}

	/**
	 * 注销用户
	 * 
	 * @param params
	 * @return
	 */
	public long destroy(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.getString("id")));
		Update update = Update.update("userInfo.destroy", params.getBoolean("destroy"));
		return mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}

	/**
	 * 锁定用户
	 * 
	 * @param params
	 * @return
	 */
	public long lockUser(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.getString("id")));
		Update update = Update.update("userInfo.serverState", params.getBoolean("server_state"));
		return mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}
}
