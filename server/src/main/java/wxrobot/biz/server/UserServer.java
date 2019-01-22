package wxrobot.biz.server;

import org.crap.jrain.core.ErrcodeException;

import wxrobot.dao.entity.User;

public interface UserServer {

	User insert(User user) throws ErrcodeException;

	User find(String id);
}
