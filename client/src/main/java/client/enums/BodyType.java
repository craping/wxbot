package client.enums;

public enum BodyType {

	SEND(1), // 發送
	RECEIVE(2); // 接收

	private final int code;

	BodyType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
