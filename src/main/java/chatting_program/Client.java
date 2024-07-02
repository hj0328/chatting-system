package chatting_program;

import java.io.*;
import java.net.Socket;

public class Client {

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;

	private Socket socket;
	private String serverAddress;
	private String username;
	private int serverPort;
	private static int id;

	Client(String serverAddress, int serverPort, String username) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.username = username;
	}

	private boolean makeConnection() {
		try {
			// stream 소켓 생성 후 connect 시도 -> 3 way handshake 발생
			socket = new Socket(serverAddress, serverPort);
			display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		} catch (Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}

		try {
			// 양방향 통신 준비
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// 서버 메시지 대기
		new ListenFromServer().start();

		try {
			// 첫 연결 시 사용자 이름 전달
			sOutput.writeObject(username);
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	private void display(String msg) {
		System.out.println(msg);
		System.out.println();

	}

	private void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	private void disconnect() {
		try {
			if (sInput != null) sInput.close();
			if (sOutput != null) sOutput.close();
			if (socket != null) socket.close();
		} catch (Exception e) {
			System.out.println("disconnect failed.");
		}

	}

	public void start() {
		// socket connection 생성
		if (!makeConnection())
			return;

		StringBuilder sb = new StringBuilder();
		sb.append("Welcome to the chatroom.")
				.append("Instructions: \n")
				.append("1. Simply type the message to send broadcast to all active clients\n")
				.append("2. Type '@username message' without quotes to send message to desired client\n")
				.append("3. Type 'WHOISIN' without quotes to see list of active clients\n")
				.append("4. Type 'LOGOUT' without quotes to logoff from server\n\n");
		System.out.println(sb);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");

			// read message from user
			try {
				String msg = br.readLine();
				if (msg.equalsIgnoreCase("LOGOUT")) {
					sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
					break;
				} else if (msg.equalsIgnoreCase("WHOISIN")) {
					sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
				} else if (msg.startsWith("@")) {
					sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
				} else {
					sendMessage(new ChatMessage(ChatMessage.BROADCAST, msg));
				}
			} catch (IOException e) {
				System.out.println("Invalid value");
				break;
			}
		}

		// close resource
		try {
			br.close();
			disconnect();
		} catch (IOException e) {
		}
	}

	/*
	 * 서버 메시지 대기 스레드
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while (true) {
				try {
					// server msg 올때까지 block
					String msg = (String) sInput.readObject();
					if (msg.startsWith("id:")) {
						Client.id = Integer.valueOf(msg.split(" ")[1]);
					}

					System.out.println(msg);
					System.out.print("> ");
				} catch (IOException e) {
					display("*** Server has closed the connection: " + e);
					break;
				} catch (ClassNotFoundException e2) {
				}
			}
		}
	}
}

