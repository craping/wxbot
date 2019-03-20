package wxrobot.dao.enums;

public enum SettingModule {
	TURING("turing"),
	KEYWORDS("keywords"),
	TIMERS("timers"),
	FORWARDS("forwards");
	
	private final String module;
	
	SettingModule(String module){
		this.module = module;
	}
	
	public String getModule() {
		return module;
	}
}
