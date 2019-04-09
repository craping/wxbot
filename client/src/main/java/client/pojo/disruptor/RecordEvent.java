package client.pojo.disruptor;

import client.utils.Config;

public class RecordEvent {
	
	private int hash;
	
	private String seq;
	
	private String fileName;
	
	private String content;
	

	public RecordEvent() {
	}
	
	public RecordEvent(String seq, String fileName, String content) {
		this.seq = seq;
		this.fileName = fileName;
		this.content = content;
		this.hash = seq.hashCode() & Integer.MAX_VALUE;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPath(){
		return Config.CHAT_RECORD_PATH + this.seq;
	}

	public int getHash() {
		return hash;
	}

	public void clear(){
		this.seq = null;
		this.fileName = null;
		this.content = null;
	}
}
