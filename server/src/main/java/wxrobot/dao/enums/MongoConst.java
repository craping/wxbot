package wxrobot.dao.enums;

public enum MongoConst {
	GT("$gt"), 		// 大于
	LT("$lt"), 		// 小于
	GTE("$gte"), 	// 大于等于
	LTE("$lte"), 	// 小于等于
	AND("and"), 
	OR("or"), 
	NOT("not");
	
	private String compareIdentify;

	MongoConst(String compareIdentify) {
		this.compareIdentify = compareIdentify;
	}

	public String getCompareIdentify() {
		return compareIdentify;
	}
}
