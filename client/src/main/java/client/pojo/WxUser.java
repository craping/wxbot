package client.pojo;

import com.teamdev.jxbrowser.chromium.JSObject;

public class WxUser {

	private String id;
	private String userName;
	private String userPwd;
	/** 昵称 */
	private String nickName;
	private String regTime;
	/** 软件到期时间 */
	private String serverEnd;
	/** 软件服务状态 */
	private Boolean serverState;
	private String phoneNum;
	/** 手机认证状态 */
	private Boolean phoneState;
	/** 帐号来源0管理员添加，1注册 */
	private int comefrom = 0;
	private String regIp;
	/** 用户token */
	private String token;

	public WxUser(JSObject jsObject) {
		this.id = jsObject.getProperty("id").getStringValue();
		this.token = jsObject.getProperty("token").toString();
		JSObject userInfo = jsObject.getProperty("userInfo").asObject();
		this.userName = userInfo.getProperty("userName").getStringValue();
		this.userPwd = userInfo.getProperty("userPwd").toString();
		this.nickName = userInfo.getProperty("nickName").toString();
		this.regTime = userInfo.getProperty("regTime").toString();
		this.serverEnd = userInfo.getProperty("serverEnd").toString();
		this.serverState = userInfo.getProperty("serverState").getBooleanValue();
		this.phoneNum = userInfo.getProperty("phoneNum").toString();
		this.phoneState = userInfo.getProperty("phoneState").getBooleanValue();
		this.comefrom = (int)userInfo.getProperty("comefrom").getNumberValue();
		this.regIp = userInfo.getProperty("regIp").toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
