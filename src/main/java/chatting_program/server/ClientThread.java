package chatting_program.server;

import chatting_program.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

// client 요청 당 ClientThread 인스턴스 하나 생성하여 connection 맺는다.
public class ClientThread extends Thread {
    private Socket socket;

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private String username;
    private ChatMessage chatMessage;
    private String timestamp;
    private String notif = " *** ";

    ClientThread(Socket socket) {
        this.socket = socket;

        //Creating both Data Stream
        display("Thread trying to create Object Input/Output Streams");
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

    public void run() {
        boolean keepListening = true;
        while (keepListening) {
            try {
                chatMessage = (ChatMessage) sInput.readObject();
            } catch (IOException e) {
                display(username + " Exception reading Streams: " + e);
                break;
            } catch (ClassNotFoundException e2) {
                display(username + " Exception deserializing: " + e2);
                break;
            }

            int type = chatMessage.getType();
            if (type == ChatMessage.MESSAGE) {
                String[] split = chatMessage.getMessage().split(" ");
                String receiveClient = split[0].split("@")[1];
                String message = split[1];

                boolean isSent = forwardPersonalMessage(message, receiveClient);
                if (!isSent) {
                    writeMsg(notif + "Sorry. No such user exists." + notif);
                }
            } else if (type == ChatMessage.LOGOUT) {
                display(username + " disconnected with a LOGOUT message.");
                keepListening = false;
            } else if (type == ChatMessage.WHOISIN) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                writeMsg("List of the users connected at " + simpleDateFormat.format(new Date()));

                int number = 1;
                for (ClientThread clientThread : Server.clientThreads) {
                    writeMsg(number++ + ") " + clientThread.getUsername() + " since " + clientThread.getTimestamp());
                }
            } else {
                // broadcast
                display("broadcast worked by " + this.username);
                broadcast(chatMessage.getMessage());
            }
        }
        // if out of the loop then disconnected and remove from client list
        remove();
        close();
    }

    private synchronized void remove() {
        String disconnectedClient = this.username;
        Server.clientThreads.remove(this);

        broadcast(notif + disconnectedClient + " has left the chat room." + notif);
    }

    // 모든 client 에 메시지 전달
    public synchronized void broadcast(String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        String newMessage = time + " " + this.username + " " + message;
        display(newMessage);

        for(ClientThread clientThread : Server.clientThreads) {
            boolean isMessageSent = clientThread.writeMsg(newMessage);
            if (!isMessageSent) {
                display("Disconnected Client " + clientThread.getUsername() + " removed from list.");
            }
        }
    }

    private boolean forwardPersonalMessage(String message, String receiver) {

        boolean found = false;
        for (ClientThread clientThread : Server.clientThreads) {
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

    // close everything
    private void close() {
        try {
            if (sOutput != null) sOutput.close();
            if (sInput != null) sInput.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
        }
    }

    // write a String to the Client output stream
    boolean writeMsg(String msg) {
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
        System.out.println(msg + "\n");
    }
}
