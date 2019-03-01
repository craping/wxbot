package client.enums;

public enum ChatType {
	/** 私聊 */
	CHAT(1),
	/** 群聊 */
	GROUPCHAT(2);
	
	private final int code;

	ChatType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
