package wxrobot.server.param;

import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.exception.ValidationException;
import org.crap.jrain.core.validate.support.param.StringParam;

public class MobileCodeParam extends StringParam {
	public MobileCodeParam() {
		this.desc = "手机验证码";
		this.required = true;
	}

	@Override
	protected String cast0(Object param) {
		return param.toString();
	}

	@Override
	protected Errcode validateValue(Object param) throws ValidationException {
		/*
		 * if (request.getSession(false) == null) return Errors.PARAM_FORMAT_ERROR;
		 * 
		 * boolean validated =
		 * paramString.equals(request.getSession().getAttribute("MOBILE_CODE"));
		 * if(!validated) return CustomErrors.USER_ERROR_SMS_CHECK;
		 * request.getSession().getAttribute("MOBILE_CODE_TIME");
		 * 
		 * long
		 * oldTime=request.getSession().getAttribute("MOBILE_CODE_TIME")==null?0:(long)
		 * request.getSession().getAttribute("MOBILE_CODE_TIME");
		 * if((System.currentTimeMillis()-10 * 60 * 1000)>oldTime){ return
		 * CustomErrors.USER_ERROR_SMS_EXPIRE; }
		 */
		return Errors.OK;
	}
}
