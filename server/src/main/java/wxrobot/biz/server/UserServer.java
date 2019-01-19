package wxrobot.biz.server;

import wxrobot.dao.entity.User;

public interface UserServer {

	User insert(User user);
	User find(String id);
}
