package chatting_program;

import java.io.*;

public class ChatMessageDto implements Serializable {
	public static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, BROADCAST = 3;
	private int type;
	private String to;
	private String from;
	private String message;

	public ChatMessageDto(int type, String message, String from, String to) {
		this.type = type;
		this.message = message;
		this.from = from;
		this.to = to;
	}

	public int getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
	public String getTo() {
		return to;
	}
	public String getFrom() {
		return from;
	}
}
