package wxrobot.dao.support;

import org.springframework.stereotype.Repository;

import wxrobot.dao.UserLogDao;
import wxrobot.dao.entity.UserLog;

@Repository
public class UserLogDaoImpl extends BaseDaoImpl<UserLog> implements UserLogDao {
}
