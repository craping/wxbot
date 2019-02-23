package client.pojo;

import com.cherry.jeeves.enums.MessageType;

import lombok.Data;

/**
 * 消息体
 * 
 * @author wr
 *
 */
@Data
public class WxMessageBody {

	/** 文本消息 */
	public String content = "";

	/** 缩略图 -> 图片、视频 */
	public String thumbImageUrl = "";

	/** 图片长宽 -> 图片、表情 */
	public String imgHeight = "";
	public String imgWidth = "";

	/** 图片消息 */
	public String fullImageUrl = "";

	/** 表情消息 */
	public String emoticonUrl = "";

	/** 语音消息 */
	public String voiceUrl = "";

	/** 视频消息 */
	public String videoUrl = "";

	/** 多媒体消息 */
	public String mediaUrl = "";

	public WxMessageBody() {
	};

	public WxMessageBody(String content) {
		this.content = content;
	}

	public WxMessageBody(MessageType type, String param, String param2, String imgHeight, String imgWidth) {
		switch (type) {
		case IMAGE:
			this.fullImageUrl = param;
			this.thumbImageUrl = param2;
			this.imgHeight = imgHeight;
			this.imgWidth = imgWidth;
			break;
		case EMOTICON:
			this.emoticonUrl = param;
			this.imgHeight = imgHeight;
			this.imgWidth = imgWidth;
			break;
		case VOICE:
			this.voiceUrl = param;
			break;
		case VIDEO:
			this.videoUrl = param;
			this.thumbImageUrl = param2;
			break;
		default:
			this.content = param;
			break;
		}
	}
}
