package wxrobot.dao.entity.field;

import lombok.Data;
import wxrobot.server.utils.Tools;

/**
 * 用户基本信息
 * 
 * @author wr
 */
@Data
public class UserInfo {

	public UserInfo() {
	}

	public UserInfo(String userName, String userPwd, String phoneNum) {
		this.userName = userName;
		this.userPwd = userPwd;
		this.phoneNum = phoneNum;
		this.serverState = true;
		this.phoneState = true;
		this.comefrom = 1;
	}

	private String userName;
	private String userPwd;
	/** 昵称 */
	private String nickName;
	private String regTime = Tools.getTimestamp();
	/** 软件到期时间 */
	private String serverEnd = Tools.getTimestamp();
	/** 软件服务状态 */
	private Boolean serverState;
	/** 注销标识 */
	private Boolean destroy = false;
	private String phoneNum;
	/** 手机认证状态 */
	private Boolean phoneState = false;
	/** 帐号来源0管理员添加，1注册 */
	private int comefrom = 0;
	private String regIp;
}
