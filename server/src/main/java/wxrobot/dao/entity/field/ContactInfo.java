package wxrobot.dao.entity.field;

/**
 * 联系人详情
 * 
 * @author wr
 *
 */
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
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	
	
}
