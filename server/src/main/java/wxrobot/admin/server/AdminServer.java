package wxrobot.admin.server;

import wxrobot.dao.entity.AdminUser;

public interface AdminServer {
	
	AdminUser getUser(String userName, String userPwd);
	
}
