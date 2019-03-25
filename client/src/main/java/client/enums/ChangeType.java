package client.enums;

public enum ChangeType {
	/** 添加 */
	ADD(1),
	/** 删除 */
	DEL(2),
	/** 修改 */
	MOD(3);
	
	private final int code;

	ChangeType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
