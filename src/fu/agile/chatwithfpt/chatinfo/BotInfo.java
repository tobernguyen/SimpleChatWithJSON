package fu.agile.chatwithfpt.chatinfo;

public class BotInfo {
	String botName;
	String botId;
	String botLanguage;

	public BotInfo(String botId, String botName, String botLanguage) {
		super();
		this.botName = botName;
		this.botId = botId;
		this.botLanguage = botLanguage;
	}

	public String getBotName() {
		return botName;
	}

	public void setBotName(String botName) {
		this.botName = botName;
	}

	public String getBotId() {
		return botId;
	}

	public void setBotId(String botId) {
		this.botId = botId;
	}

	public String getBotLanguage() {
		return botLanguage;
	}

	public void setBotLanguage(String botLanguage) {
		this.botLanguage = botLanguage;
	}
}
