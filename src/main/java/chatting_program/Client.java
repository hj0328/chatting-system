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
	private int id;	// 서버에서 전달하는 id 값

	Client(String serverAddress, int serverPort, String username) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.username = username;
	}

	// 서버 연결을 시도하고, 스트림 생성 및 사용자 이름 전송
	public boolean makeConnection() {
		try {
			// stream 소켓 생성 후 connect 시도 -> 3 way handshake 발생
			socket = new Socket(serverAddress, serverPort);
			display("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
		} catch (Exception ec) {
			display("Error connecting to server:" + ec);
			return false;
		}

		try {
			// 양방향 통신을 위한 Object 스트림 생성
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput = new ObjectInputStream(socket.getInputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		try {
			// 첫 연결 시 사용자 이름을 서버에 전달
			sOutput.writeObject(username);

			// 서버는 첫 연결 시 사용자 id를 Client에 전달
			String msg = (String) sInput.readObject();
			if (msg.startsWith("id:")) {
				id = Integer.valueOf(msg.split(" ")[1]);
			}
			display("My Id="+id);
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
		}

		// 서버 메시지듣기 위한 스레드 시작
		new ListenFromServer().start();

        return true;
	}

	private void display(String msg) {
		System.out.println(msg);
		System.out.println();

	}

	public void sendMessage(ChatMessageDto msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	public void disconnect() {
		try {
			if (sInput != null) sInput.close();
			if (sOutput != null) sOutput.close();
			if (socket != null) socket.close();
		} catch (Exception e) {
			System.out.println("disconnect failed.");
		}

	}

	public void start() {
		if (!makeConnection())
			return;

		StringBuilder sb = new StringBuilder();
		sb.append("Welcome to the chatroom.")
				.append("설명서: \n")
				.append("1. 활성화된 모든 클라이언트에게 브로드캐스트 메시지를 보내려면, 메시지를 입력하세요.\n")
				.append("2. 원하는 클라이언트에게 귓속말를 보내려면 @username 메시지 형식으로 입력하세요.\n")
				.append("3. 활성화된 클라이언트 목록을 보려면 따옴표 없이 WHOISIN을 입력하세요.\n")
				.append("4. LOGOUT을 입력하면 서버에서 로그아웃됩니다.\n\n");
		System.out.println(sb);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");

			// read message from user
			try {
				String msg = br.readLine();
				if (msg.equalsIgnoreCase("LOGOUT")) {
					sendMessage(new ChatMessageDto(ChatMessageDto.LOGOUT, null, null, null));
					break;
				} else if (msg.equalsIgnoreCase("WHOISIN")) {
					sendMessage(new ChatMessageDto(ChatMessageDto.WHOISIN, null, null, null));
				} else if (msg.startsWith("@")) {
					String to = msg.split("@")[0].split(" ")[0];
					String toMsg = msg.split(to)[1];
					sendMessage(new ChatMessageDto(ChatMessageDto.MESSAGE, toMsg, this.username, to));
				} else {
					sendMessage(new ChatMessageDto(ChatMessageDto.BROADCAST, msg, this.username, null));
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

	public int getId() {
		return this.id;
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

