package wxrobot.dao.entity.field;

import wxrobot.server.utils.Tools;

/**
 * 用户基本信息
 * 
 * @author wr
 */
public class UserInfo {

	public UserInfo() {
	}

	public UserInfo(String userName, String userPwd, String phoneNum) {
		this.userName = userName;
		this.userPwd = userPwd;
		this.phoneNum = phoneNum;
		this.serverState = true;
		this.phoneState = false;
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
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRegTime() {
		return regTime;
	}

	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}

	public String getServerEnd() {
		return serverEnd;
	}

	public void setServerEnd(String serverEnd) {
		this.serverEnd = serverEnd;
	}

	public Boolean getServerState() {
		return serverState;
	}

	public void setServerState(Boolean serverState) {
		this.serverState = serverState;
	}

	public Boolean getDestroy() {
		return destroy;
	}

	public void setDestroy(Boolean destroy) {
		this.destroy = destroy;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public Boolean getPhoneState() {
		return phoneState;
	}

	public void setPhoneState(Boolean phoneState) {
		this.phoneState = phoneState;
	}

	public int getComefrom() {
		return comefrom;
	}

	public void setComefrom(int comefrom) {
		this.comefrom = comefrom;
	}

	public String getRegIp() {
		return regIp;
	}

	public void setRegIp(String regIp) {
		this.regIp = regIp;
	}
	
	
}
