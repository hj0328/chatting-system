package chatting_program.server;

import chatting_program.ChatMessageDto;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	public static ConcurrentLinkedQueue<ClientEntity> clientThreads;

	private AtomicInteger uniqueConnectionId;
	private SimpleDateFormat simpleDateFormat;

	private int port;
	private String notif = " *** ";

	// ExecutorService로 쓰레드 풀 생성 (고정 크기 100)
	private ExecutorService threadPool;

	// 비동기 브로드캐스트 메시지 큐
	private BlockingQueue<ChatMessageDto> broadcastQueue;

	public Server(int port) {
		this.port = port;
		simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		clientThreads = new ConcurrentLinkedQueue<>();
		uniqueConnectionId = new AtomicInteger();
		threadPool = Executors.newFixedThreadPool(100);
		broadcastQueue = new LinkedBlockingQueue<>();

		// 브로드캐스트 메시지를 처리할 소비자 스레드 시작
		threadPool.execute(new BroadcastConsumer());
	}
	
	public void start() {
		// 서버 소켓 생성 후 connection request 대기
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				display("Server waiting for Clients on port " + port + ".");

				// 새로운 client connection 맺을 때까지 block
				Socket socket = serverSocket.accept();

				int clientSessionId = uniqueConnectionId.incrementAndGet();
				ClientEntity clientThread = new ClientEntity(socket, clientSessionId, this);
				clientThreads.add(clientThread);

				// 쓰레드 풀을 이용하여 ClientThread 실행
				threadPool.execute(clientThread);

				clientThread.writeMsg("id: " + uniqueConnectionId.get());
				clientThread.broadcast(notif + clientThread.getUsername() + " has joined the chat room." + notif);
			}

		} catch (IOException e) {
			String msg = simpleDateFormat.format(new Date()) + " Exception on new ServerSocket: " + e ;
			display(msg);
		}
	}

	// 클라이언트가 BROADCAST 메시지를 보낼 때 호출하여 큐에 메시지를 넣음.
	public void enqueueBroadcast(ChatMessageDto msg) {
		try {
			broadcastQueue.put(msg);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	// 브로드캐스트 메시지를 처리할 소비자 스레드
	private class BroadcastConsumer implements Runnable {
		public void run() {
			while (true) {
				try {
					// 큐에서 메시지를 꺼냄 (큐가 비어있으면 대기)
					ChatMessageDto msg = broadcastQueue.take();
					// 메시지 형식 (발신자 정보 포함)
					String message = msg.getFrom() + ": " + msg.getMessage();
					display(message);
					// 모든 클라이언트에게 메시지 전송 (자신을 제외할 필요가 있으면 추가 조건 처리)
					for (ClientEntity client : clientThreads) {
						client.writeMsg(message);
					}
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}

	public void shutdown() {
		display("Server shutting down...");

		// 스레드 풀에 더 이상 작업을 받지 않도록 종료 요청
		threadPool.shutdown();
		try {
			// 최대 10초까지 작업 완료 기다림
			if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
				// 아직 종료되지 않은 작업이 있다면 강제 종료
				threadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			threadPool.shutdownNow();
			// 현재 스레드 인터럽트 상태 복원
			Thread.currentThread().interrupt();
		}

		display("Server shutdown complete.");
	}


	private void display(String msg) {
		String time = "[Server] " + simpleDateFormat.format(new Date()) + " " + msg + "\n";
		System.out.println(time);
	}
}

