package client.pojo;

import org.json.JSONObject;

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

	public WxUser(JSONObject params) {
		this.id = params.getString("id");
		this.token = params.getString("token");
		JSONObject userInfo = params.getJSONObject("userInfo");
		this.userName = userInfo.optString("userName");
		this.userPwd = userInfo.optString("userPwd");
		this.nickName = userInfo.optString("nickName");
		this.regTime = userInfo.optString("regTime");
		this.serverEnd = userInfo.optString("serverEnd");
		this.serverState = userInfo.optBoolean("serverState");
		this.phoneNum = userInfo.optString("phoneNum");
		this.phoneState = userInfo.optBoolean("phoneState");
		this.comefrom = userInfo.optInt("comefrom");
		this.regIp = userInfo.optString("regIp");
	}
}
