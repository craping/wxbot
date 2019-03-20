package wxrobot.server.param.enums;

import org.crap.jrain.core.validate.support.param.EnumParam;

import wxrobot.dao.enums.SettingModule;

public class SettingModuleEParam extends EnumParam {
	
	public SettingModuleEParam() {
		super(SettingModule.values());
	}
}
