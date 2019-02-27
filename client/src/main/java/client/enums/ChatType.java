package client.enums;

public enum ChatType {
	/** 私聊 */
	CHAT,
	/** 群聊 */
	GROUPCHAT;

	static ChatType getType(String name) {
		if (name == "CHAT" || name.equals("CHAT")) {
			return CHAT;
		} else {
			return GROUPCHAT;
		}
	}
}
