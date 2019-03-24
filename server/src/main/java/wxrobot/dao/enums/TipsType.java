package wxrobot.dao.enums;

public enum TipsType {
	CHATROOMFOUND("chatRoomFoundTip"), 
	MEMBERJOIN("memberJoinTip"), 
	MEMBERLEFT("memberLeftTip");

	private final String type;

	TipsType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public static TipsType getTipsType(String type) {
		for (TipsType tip : TipsType.values()) {
			if (tip.getType().equals(type))
				return tip;
		}
		return null;
	}
}
