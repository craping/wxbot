package client.pojo;

import com.cherry.jeeves.domain.response.UploadMediaResponse;
import com.cherry.jeeves.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Msg {
	
	@JsonProperty
	protected int type;
	
	@JsonProperty
	protected String content;
	
	protected UploadMediaResponse mediaCache;
	
	public Msg() {
	}
	
	public Msg(MessageType type, String content){
		switch (type) {
		case TEXT:
			this.type = 1;
			break;
		case IMAGE:
			this.type = 2;
			break;
		case EMOTICON:
			this.type = 3;
			break;
		case VIDEO:
		case MICROVIDEO:
			this.type = 4;
			break;
		default:
			this.type = 5;
			break;
		}
		this.content = content;
	}
	
	public Msg(int type, String content){
		this.type = type;
		this.content = content;
	}
	
	public int getType() {
		return type;
	}
	
	public MessageType getMsgType(){
		switch (this.type) {
		case 1:
			return MessageType.TEXT;
		case 2:
			return MessageType.IMAGE;
		case 3:
			return MessageType.EMOTICON;
		case 4:
			return MessageType.VIDEO;
		default:
			return MessageType.APP;
		}
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UploadMediaResponse getMediaCache() {
		return mediaCache;
	}

	public void setMediaCache(UploadMediaResponse mediaCache) {
		this.mediaCache = mediaCache;
	}
	
	
}
