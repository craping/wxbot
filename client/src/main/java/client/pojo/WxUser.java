package client.pojo;

import com.teamdev.jxbrowser.chromium.JSObject;

import lombok.Data;

@Data
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
}
