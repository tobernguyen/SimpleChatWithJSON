package fu.agile.chatwithfpt.chatinfo;

public interface IMessage {
	void setMessage(String msg);

	void setTimeStamp(long timeStamp);

	String getMessage();

	long getTimeStamp();
}
