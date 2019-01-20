package wxrobot.admin.server.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wxrobot.admin.server.AdminServer;
import wxrobot.dao.AdminDao;
import wxrobot.dao.entity.AdminUser;

@Service
public class AdminServerImpl implements AdminServer {

	@Autowired
	private AdminDao adminDao;

	public AdminDao getAdminDao() {
		return adminDao;
	}

	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

	@Override
	public AdminUser getUser(String userName, String userPwd) {
		// TODO Auto-generated method stub
		return null;
	}
}
