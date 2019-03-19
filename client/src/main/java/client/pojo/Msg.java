package client.pojo;

import com.cherry.jeeves.domain.response.UploadMediaResponse;
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
	
	public Msg(int type, String content){
		this.type = type;
		this.content = content;
	}
	
	public int getType() {
		return type;
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
