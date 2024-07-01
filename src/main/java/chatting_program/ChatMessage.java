package chatting_program;

import java.io.*;

public class ChatMessage implements Serializable {
	public static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, BROADCAST = 3;
	private int type;

	private String message;
	
	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public int getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
}
