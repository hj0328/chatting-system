package chatting_program;

import chatting_program.server.Server;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatIntegrationTest {
    private static final int SERVER_TEST_PORT = 7777;
    private static Server server;
    private static Thread serverThread;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    static void setServer() throws InterruptedException {
        // 테스트 전용 서버를 별도 스레드에서 실행
        server = new Server(SERVER_TEST_PORT);
        serverThread = new Thread(() -> server.start());
        serverThread.start();
        // 서버가 완전히 시작될 시간을 기다림
        TimeUnit.SECONDS.sleep(1);
    }

    @BeforeEach
    public void setUpStreams() {
        // System.out을 outContent로 리다이렉션
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        // 테스트 후 원래의 System.out 복원
        System.setOut(originalOut);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.shutdown();
        serverThread.interrupt();
    }

    @Test
    public void 브로드캐스트_테스트() throws Exception {
        // 클라이언트 A에서 브로드캐스트 메시지 전송
        Thread thread = new Thread(() -> {
            Client clientA = new Client("localhost", SERVER_TEST_PORT, "clientA");
            clientA.makeConnection();
            String testMessage = "Hello Client B!\n";
            clientA.sendMessage(new ChatMessageDto(ChatMessageDto.BROADCAST, testMessage, "clientA", ""));
//            clientA.disconnect();
        });
        thread.start();

        // 클라이언트 B에서 브로드캐스트 메시지 전송
        Thread thread2 = new Thread(() -> {
            Client clientB = new Client("localhost", SERVER_TEST_PORT, "clientB");
            clientB.makeConnection();
            String testMessage = "Hello Client A!\n";
            clientB.sendMessage(new ChatMessageDto(ChatMessageDto.BROADCAST, testMessage, "clientB", ""));
//            clientB.disconnect();
        });
        thread2.start();

        thread.join();
        thread2.join();

        // 일정 시간이 지난 후 출력을 확인
        TimeUnit.SECONDS.sleep(1);
        System.setOut(originalOut);
        String output = outContent.toString();
        System.out.println("Captured output: " + output);

        // 클라이언트 B의 마지막 수신 메시지가 testMessage를 포함하는지 확인
        assertTrue(output.contains("Hello Client A"),
                "클라이언트 B가 메시지를 받지 못했습니다.");
        assertTrue(output.contains("Hello Client B"),
                "클라이언트 A가 메시지를 받지 못했습니다.");

    }

    @Test
    public void 귓속말_테스트() throws Exception {

        // 클라이언트 A 생성
        Thread thread = new Thread(() -> {
            Client clientA = new Client("localhost", SERVER_TEST_PORT, "clientA");
            clientA.makeConnection();
        });
        thread.start();

        // client B에서 client A에 메시지 전송
        Thread thread2 = new Thread(() -> {
            Client clientB = new Client("localhost", SERVER_TEST_PORT, "clientB");
            clientB.makeConnection();
            String testMessage = "@clientA 귓속말 Client A!\n";

            clientB.sendMessage(new ChatMessageDto(ChatMessageDto.MESSAGE, testMessage.split("clientA")[1], "", "clientA"));
        });
        thread2.start();

        // 일정 시간이 지난 후 출력을 확인
        TimeUnit.SECONDS.sleep(2);
        System.setOut(originalOut);
        String output = outContent.toString();
        System.out.println("Captured output: " + output);

        // 클라이언트 B의 마지막 수신 메시지가 testMessage를 포함하는지 확인

        assertTrue(output.contains("귓속말 Client A"),
                "클라이언트 A가 메시지를 받지 못했습니다.");

    }
}
