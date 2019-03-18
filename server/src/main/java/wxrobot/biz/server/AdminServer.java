package wxrobot.biz.server;

import java.util.regex.Pattern;

import org.crap.jrain.core.bean.result.criteria.DataResult;
import org.crap.jrain.core.bean.result.criteria.Page;
import org.crap.jrain.core.util.DateUtil;
import org.crap.jrain.core.validate.security.component.Coder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import wxrobot.dao.entity.AdminUser;
import wxrobot.dao.entity.User;
import wxrobot.server.utils.Tools;

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
		Query query = new Query();
		if (!Tools.isStrEmpty(params.optString("userName"))) {
			Pattern pattern=Pattern.compile("^.*" + params.optString("userName") + ".*$", Pattern.CASE_INSENSITIVE);
			query.addCriteria(Criteria.where("userInfo.userName").regex(pattern));
		}
		if (!Tools.isStrEmpty(params.optString("phoneState"))) {
			query.addCriteria(Criteria.where("userInfo.phoneState").is(params.optBoolean("phoneState")));
		}
		if (!Tools.isStrEmpty(params.optString("serverState"))) {
			query.addCriteria(Criteria.where("userInfo.serverState").is(params.optBoolean("serverState")));
		}
		return findPage(page, query, User.class);
	}

	/**
	  *  更新软件服务时间
	 * @param params
	 * @return
	 */
	public int extension(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.optString("id")));
		String timestamp = String.valueOf(DateUtil.parseDate(params.getString("server_end"), "yyyy-MM-dd").getTime());
		Update update = Update.update("userInfo.serverEnd", timestamp);
		return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}

	/**
	  *  充值密码 888888
	 * @param params
	 * @return
	 */
	public int resetPwd(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.optString("id")));
		Update update = Update.update("userInfo.userPwd", Coder.encryptMD5("888888"));
		return (int) mongoTemplate.updateFirst(query, update, User.class).getModifiedCount();
	}
	
	/**
	  *  注销用户
	 * @param params
	 * @return
	 */
	public int destroy(JSONObject params) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(params.optString("id")));
		Update update = Update.update("userInfo.destroy", params.optBoolean("destroy"));
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
