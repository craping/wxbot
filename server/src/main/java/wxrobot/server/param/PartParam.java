package wxrobot.server.param;

import org.crap.jrain.core.bean.result.Errcode;
import org.crap.jrain.core.error.support.Errors;
import org.crap.jrain.core.validate.exception.ParamFormatException;
import org.crap.jrain.core.validate.exception.ValidationException;
import org.crap.jrain.core.validate.support.param.ValidateParam;
import org.crap.jrain.mvc.netty.decoder.Part;

public class PartParam extends ValidateParam<Object> {

	@Override
	protected Errcode validateValue(Object param) throws ValidationException {
		if(param instanceof Part)
			return Errors.OK;
		if(param instanceof String)
			return Errors.OK;
		
		throw new ParamFormatException(this);
	}

	@Override
	protected Object cast0(Object param) {
		return param;
	}
}
