package client.errors;

public enum Errors {

	HTTP_CONNECT_ERR(100, "系统错误，请联系管理员"), 
	UNKNOWEXCEPTION(101, "未知错误");

	/** 错误码 */
	private final int errCode;
	/** 错误描述 */
	private final String errMsg;
	private Object args[];

	private Errors(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public int getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		if (args != null && args.length > 0) {
			String errMsg = this.errMsg;
			for (Object arg : args) {
				if(errMsg.indexOf("{}") != -1) {
					errMsg = errMsg.substring(0, errMsg.indexOf("{}")).concat(arg.toString()).concat(errMsg.substring(errMsg.indexOf("{}") + 2));
				}
			}
			return errMsg;
		} else {
			return this.errMsg;
		}
	}
	
	public Object[] getArgs() {
		return args;
	}
	
	public Errors setArgs(Object... args) {
		this.args = args;
		return this;
	}

	public static String getErrCodeMsg(Errors e) {
		return "错误码：" + e.getErrCode();
	}
}
