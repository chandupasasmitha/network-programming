package client;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class SocketClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listenerThread;

    // Connect to server and start listening
    public void connect(String host, int port, String playerName, Consumer<String> onMessageReceived) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Server asks for name
        String prompt = in.readLine();
        System.out.println("Server says: " + prompt);
        out.println(playerName);

        // Background thread to listen for messages
        listenerThread = new Thread(() -> {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    onMessageReceived.accept(message);
                }
            } catch (IOException ignored) {
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    // Send message to server
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    // Close connection
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
