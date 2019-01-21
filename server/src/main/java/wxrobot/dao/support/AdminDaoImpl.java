package wxrobot.dao.support;

import org.springframework.stereotype.Repository;

import wxrobot.dao.AdminDao;
import wxrobot.dao.entity.AdminUser;

@Repository
public class AdminDaoImpl extends BaseDaoImpl<AdminUser> implements AdminDao {
}
