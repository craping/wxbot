package wxrobot.server.param;

import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.exception.ValidationException;
import org.crap.jrain.core.validate.support.param.SingleParam;
import org.crap.jrain.core.validate.support.param.StringParam;

import wxrobot.server.enums.CustomErrors;
import wxrobot.server.utils.RedisUtil;
import wxrobot.server.utils.Tools;

public class AdminTokenParam extends StringParam implements SingleParam {
	
	public AdminTokenParam() {
		this.desc="管理员用户Token";
		this.value = "token";
	}

	@Override
	protected Errcode validateValue(Object param) throws ValidationException {
		String token = param.toString();
		if (Tools.isStrEmpty(token))
			return new Result(CustomErrors.USER_NOT_LOGIN);
		
		String key = "admin_user_" + token;
		if (!(RedisUtil.exists(key))) {
			return new Result(CustomErrors.USER_NOT_LOGIN);
		}
		return Errors.OK;
	}

	@Override
	protected String cast0(Object param) {
		return param.toString();
	}
}
