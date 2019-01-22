package wxrobot.server.param;

import org.crap.jrain.core.validate.support.param.RegExpParam;

public class UserNameParam extends RegExpParam {
	/**
	 * 
	 * @Description:登录名规则检查 必须是数字、字母
	 * @return boolean:
	 */
	public UserNameParam() {
		this.regex = "(?=.*[A-Za-z])(?=.*[0-9])[a-zA-Z0-9]+$";
	}
}
