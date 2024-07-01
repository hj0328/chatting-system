package chatting_program.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	private AtomicInteger uniqueConnectionId;
	public static ArrayList<ClientThread> clientThreads;
	private SimpleDateFormat simpleDateFormat;

	private int port;
	private String notif = " *** ";
	
	public Server(int port) {
		this.port = port;
		simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		clientThreads = new ArrayList<>();
		uniqueConnectionId = new AtomicInteger();
	}
	
	public void start() {
		// 서버 소켓 생성 후 connection request 대기
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				display("Server waiting for Clients on port " + port + ".");

				// 새로운 client connection 맺을 때까지 block
				Socket socket = serverSocket.accept();

				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
				clientThread.writeMsg("id: " + uniqueConnectionId);
				uniqueConnectionId.incrementAndGet();
				clientThreads.add(clientThread);

				clientThread.broadcast(notif + clientThread.getUsername() + " has joined the chat room." + notif);
			}

		} catch (IOException e) {
			String msg = simpleDateFormat.format(new Date()) + " Exception on new ServerSocket: " + e ;
			display(msg);
		}
	}

	private void display(String msg) {
		String time = simpleDateFormat.format(new Date()) + " " + msg + "\n";
		System.out.println(time);
	}
}

