package wxrobot.server.enums;

import org.crap.jrain.core.bean.result.Errcode;


public enum CustomErrors implements Errcode {
	
	USER_OPR_ERR(1,100, "操作失败"),
	PLAN_OPR_ERR(-9,100, "后台计划方案为空！"),

	USER_TOKEN_NULL(1,500, "请求错误，缺少参数token"),
	USER_ACC_ERR(1,501, "用户帐号或密码错误"),
	USER_PWD_ERR(1,502, "用户密码错误"),
	USER_CHANGE_PWD_ERR(1,503, "确认密码不一致"),
	USER_LOCKED_ERR(1,504, "用户状态为锁定"),
	USER_LOGIN_ERR_EX(1,505, "登录失败，请联系管理员"),
	USER_NOT_LOGIN(1,506, "用户未登录"),
	USER_SERVER_END(1,507, "服务已过期，请联系管理员"),
	USER_LOCKED(1,508, "状态异常，请联系管理员");
	
	public int result;
	public int errCode;
	public String errMsg;

	private CustomErrors(int result, int errCode, String errMsg) {
		this.result = result;
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	@Override
	public int getResult() {
		return this.result;
	}

	@Override
	public int getErrcode() {
		return this.errCode;
	}

	@Override
	public String getMsg() {
		return this.errMsg;
	}
}
