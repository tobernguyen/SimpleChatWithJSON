package fu.agile.chatwithfpt.chatinfo;

public class UserMessage implements IMessage {
	private String message;
	private long timeStamp;

	public UserMessage(String message, long timeStamp) {
		super();
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
