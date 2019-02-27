package client.pojo;

import com.cherry.jeeves.enums.MessageType;

import client.utils.Arith;
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
	public int imgHeight;
	public int imgWidth;

	/** 图片消息 */
	public String fullImageUrl = "";

	/** 表情消息 */
	public String emoticonUrl = "";

	/** 语音消息 */
	public String voiceUrl = "";
	public int voiceLength = 1;

	/** 视频消息 */
	public String videoUrl = "";

	/** 多媒体消息 */
	public String mediaUrl = "";
	
	/** 文件名 */
	public String fileName = "";
	/** 文件大小 */
	public String fileSize = "";

	public WxMessageBody() {
	};

	/**
	 * 文本消息
	 * 
	 * @param content
	 */
	public WxMessageBody(String content) {
		this.content = content;
	}

	/**
	 * 语音消息
	 * 
	 * @param voiceUrl
	 * @param voiceLength
	 */
	public WxMessageBody(String voiceUrl, long voiceLength) {
		this.voiceUrl = voiceUrl;
		this.voiceLength = Arith.ceilInt(Arith.div(voiceLength, 1000, 2));
	}

	/**
	 * 视频消息
	 * 
	 * @param videoUrl
	 * @param thumbImageUrl
	 */
	public WxMessageBody(String videoUrl, String thumbImageUrl) {
		this.videoUrl = videoUrl;
		this.thumbImageUrl = thumbImageUrl;
	}

	/**
	 * 表情消息
	 * 
	 * @param emoticonUrl
	 * @param imgHeight
	 * @param imgWidth
	 */
	public WxMessageBody(String emoticonUrl, int imgHeight, int imgWidth) {
		this.emoticonUrl = emoticonUrl;
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
	}

	/**
	 * 图片消息
	 * 
	 * @param fullImageUrl
	 * @param thumbImageUrl
	 * @param imgHeight
	 * @param imgWidth
	 */
	public WxMessageBody(String fullImageUrl, String thumbImageUrl, int imgHeight, int imgWidth) {
		this.fullImageUrl = fullImageUrl;
		this.thumbImageUrl = thumbImageUrl;
		this.imgHeight = imgHeight;
		this.imgWidth = imgWidth;
	}
	
	/**
	 * 通用文件
	 * 
	 * @param type
	 * @param param
	 */
	public WxMessageBody(MessageType type, String fileUrl, String fileName, String fileSize) {
		if (type.equals(MessageType.APP)) {
			this.mediaUrl = fileUrl;
			this.fileName = fileName;
			this.fileSize = fileSize;
		}
	}
}
