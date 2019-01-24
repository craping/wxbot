package wxrobot.biz.server;

import org.crap.jrain.core.ErrcodeException;

import wxrobot.dao.entity.User;

public interface UserServer {

	User insert(User user) throws ErrcodeException;

	User getUser(String userName, String userPwd);

	User find(String id);

	int updateToken(User user);
}
