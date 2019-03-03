package wxrobot.dao.entity.field;

import lombok.Data;

/**
 * 联系人详情
 * 
 * @author wr
 *
 */
@Data
public class ContactInfo {

	public ContactInfo() {
	}

	public ContactInfo(String seq, String nickName, String headImgUrl) {
		this.seq = seq;
		this.nickName = nickName;
		this.headImgUrl = headImgUrl;
	}

	private String seq;
	private String nickName;
	private String headImgUrl;
}
