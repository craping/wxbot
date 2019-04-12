package wxrobot.server.param;

import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.bean.result.Result;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.exception.ValidationException;
import org.crap.jrain.core.validate.support.param.SingleParam;
import org.crap.jrain.core.validate.support.param.StringParam;

import wxrobot.dao.enums.TipsType;
import wxrobot.server.enums.CustomErrors;

public class TipsTypeParam extends StringParam implements SingleParam {

	public TipsTypeParam() {
		this.desc = "提示语类型";
		this.value = "type";
	}

	@Override
	protected Errcode validateValue(Object param) throws ValidationException {
		if (TipsType.getTipsType(param.toString()) == null)
			return new Result(CustomErrors.USER_OPR_ERR);
		return Errors.OK;
	}

	@Override
	protected String cast0(Object param) {
		return param.toString();
	}
}
