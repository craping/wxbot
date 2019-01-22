package wxrobot.admin.server;

import org.crap.jrain.core.bean.result.criteria.DataResult;

import net.sf.json.JSONObject;
import wxrobot.dao.entity.AdminUser;

public interface AdminServer {

	AdminUser getUser(String userName, String userPwd);

	DataResult getUserList(JSONObject params);

	/** 修改服务时间 */
	int extension(JSONObject params);

	/** 修改服务状态 */
	int lockUser(JSONObject params);
}
