package client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import client.utils.Arith;

/**
 * 消息体
 * 
 * @author wr
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class WxMessageBody {
	
	/** 文本消息或微信地址 */
	public String content;

	/** 缩略图或者本地路径 */
	public String thumbImageUrl;
	/** 文件名或本地路径 */
	public String fileName;
	/** 文件大小 */
	public String fileSize;
	/** 是否绝对路径 */
	public boolean absolute;

	public WxMessageBody() {
	};

	/**
	 * 文本消息
	 * 表情消息
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
	public WxMessageBody(String url, long voiceLength) {
		this(url);
		this.fileSize = Arith.ceilInt(Arith.div(voiceLength, 1000, 2))+"";
	}

	/**
	 * 视频消息
	 * 图片消息
	 * 
	 * @param videoUrl
	 * @param thumbImageUrl
	 */
	public WxMessageBody(String url, String thumbImageUrl) {
		this(url);
		this.thumbImageUrl = thumbImageUrl;
	}
	
	/**
	 * 通用文件
	 * 
	 * @param type
	 * @param param
	 */
	public WxMessageBody(String url, String fileName, String fileSize) {
		this(url);
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getThumbImageUrl() {
		return thumbImageUrl;
	}

	public void setThumbImageUrl(String thumbImageUrl) {
		this.thumbImageUrl = thumbImageUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}
	
	
}
