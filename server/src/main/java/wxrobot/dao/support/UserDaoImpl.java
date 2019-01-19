package wxrobot.dao.support;

import org.springframework.stereotype.Repository;

import wxrobot.dao.UserDao;
import wxrobot.dao.entity.User;

@Repository
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {
}
