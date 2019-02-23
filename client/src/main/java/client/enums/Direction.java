package client.enums;

public enum Direction {

	SEND(1), // 發送
	RECEIVE(2); // 接收

	private final int code;

	Direction(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
