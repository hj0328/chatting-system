package chatting_program.server;

import chatting_program.ChatMessageDto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

// client 요청 당 ClientThread 인스턴스 하나 생성하여 connection 맺는다.
public class ClientEntity extends Thread {
    private Socket socket;

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private String username;
    private int sessionId;
    private ChatMessageDto chatMessage;
    private String timestamp;
    private String notif = " *** ";
    private Server server;

    ClientEntity(Socket socket, int clientSessionId, Server server) {
        this.socket = socket;
        this.sessionId = clientSessionId;
        this.server = server;

        //Creating both Data Stream
        display("ClientSession trying to create Object Input/Output Streams");
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());

            username = (String) sInput.readObject();
        } catch (IOException e) {
            display("Exception creating new Input/output Streams: " + e);
        } catch (ClassNotFoundException e) {
            display("Deserialization ClassNotFoundException: " + e);
        }
        timestamp = new Date().toString();
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void run() {
        boolean keepListening = true;
        while (keepListening) {
            try {
                chatMessage = (ChatMessageDto) sInput.readObject();
            } catch (IOException e) {
                display(username + " Exception reading Streams: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                display(username + " Exception deserializing: " + e2);
                break;
            }

            int type = chatMessage.getType();
            if (type == ChatMessageDto.MESSAGE) {
                String receiveClient = chatMessage.getTo();
                String message = chatMessage.getMessage();

                boolean isSent = forwardPersonalMessage(message, receiveClient);
                if (isSent) {
                    display("귓속말 From: " + this.username + ", To: " + chatMessage.getTo() + ", msg:" + chatMessage.getMessage());
                } else {
                    writeMsg(notif + "Sorry. No such user exists." + notif);
                }
            } else if (type == ChatMessageDto.LOGOUT) {
                display(username + " disconnected with a LOGOUT message.");
                keepListening = false;
            } else if (type == ChatMessageDto.WHOISIN) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                writeMsg("List of the users connected at " + simpleDateFormat.format(new Date()));

                int number = 1;
                for (ClientEntity clientThread : Server.clientThreads) {
                    writeMsg(number++ + ") " + clientThread.getUsername() + " since " + clientThread.getTimestamp());
                }
            } else {
                display("broadcast worked by " + this.username + ", msg: " + chatMessage.getMessage());

                ChatMessageDto broadcastMsg = new ChatMessageDto(ChatMessageDto.BROADCAST, chatMessage.getMessage(), this.username, null);
                server.enqueueBroadcast(broadcastMsg);
//                broadcast(chatMessage.getMessage());
            }
        }
        remove();
        close();
    }

    private void remove() {
        String disconnectedClient = this.username;
        Server.clientThreads.remove(this);
        broadcast(notif + disconnectedClient + " has left the chat room." + notif);
    }

    // 모든 client 에 메시지 전달
    public void broadcast(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String newMessage = time + " " + this.username + " " + message;

        for (ClientEntity clientThread : Server.clientThreads) {
            if (clientThread.getSessionId() == this.sessionId) {
                continue;
            }

            boolean isMessageSent = clientThread.writeMsg(newMessage);
            if (!isMessageSent) {
                display("Disconnected Client " + clientThread.getUsername() + " removed from list.");
            }
        }
    }

    private boolean forwardPersonalMessage(String message, String receiver) {
        boolean found = false;
        for (ClientEntity clientThread : Server.clientThreads) {
            if (!clientThread.getUsername().equals(receiver)) {
                continue;
            }

            found = true;
            boolean isSuccess = clientThread.writeMsg(message);
            if (!isSuccess) {
                Server.clientThreads.remove(clientThread);
                display("Disconnected Client " + clientThread.getUsername() + " removed from list.");
            }
            break;
        }
        return found;
    }

    private void close() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
        }
    }

    public boolean writeMsg(String msg) {
        if (!socket.isConnected()) {
            close();
            return false;
        }

        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display(notif + "Error sending message to " + username + notif);
            display(e.toString());
        }
        return true;
    }

    private void display(String msg) {
        System.out.println("[Server] " + msg + "\n");
    }
}
