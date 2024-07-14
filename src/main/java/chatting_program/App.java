package chatting_program;

import chatting_program.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) {
        System.out.println("> Enter Server or Client");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String role = br.readLine();
            if (role.equals("Server")) {
                startServer();
            } else if (role.equals("Client")) {
                startClient();
            } else {
                System.out.println("> Wrong request");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startClient() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("> Enter the username (default: Anonymous): ");
            String userName = br.readLine();
            if (userName.isEmpty()) {
                userName = "Anonymous";
            }

            // default number 7000
            System.out.println("> Enter Server Port Number (default: 7001): ");
            int portNumber = 7001;
            String nextLine = br.readLine();
            if (!nextLine.isEmpty()) {
                portNumber = Integer.parseInt(nextLine);
            }

            System.out.println("> Enter the Server Address (default: localhost): ");
            String serverAddress = br.readLine();
            if (serverAddress.isEmpty()) {
                serverAddress = "localhost";
            }

            Client client = new Client(serverAddress, portNumber, userName);
            client.start();
        } catch (IOException e) {
            System.out.println("> Invalid port number.");
        }
    }

    private static void startServer() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // default number 7001
        int portNumber = 7001;
        System.out.println("> Enter Port Number (default: 7001): ");
        String nextLine = null;
        try {
            nextLine = br.readLine();

        } catch (IOException e) {
            System.out.println("> Invalid port number.");
        }
        if (!nextLine.isEmpty()) {
            portNumber = Integer.parseInt(nextLine);
        }

        Server server = new Server(portNumber);
        server.start();
    }
}
