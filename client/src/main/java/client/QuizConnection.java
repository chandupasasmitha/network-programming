package client;

import models.Question;

import java.io.*;
import java.net.Socket;

public class QuizConnection {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private QuestionController controller;
    private String playerName;

    public QuizConnection(String host, int port, String playerName, QuestionController controller) {
        this.playerName = playerName;
        this.controller = controller;
        this.controller.setConnection(this);

        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send player name first
            out.writeObject(playerName);
            out.flush();

            System.out.println("✅ Connected to quiz server as " + playerName);

            // Start listening for questions
            new Thread(this::listenForQuestions).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForQuestions() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Question q) {
                    System.out.println("📩 New question: " + q.getText());
                    controller.setQuestion(q);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Disconnected from server.");
        }
    }

    public void sendAnswer(String answer) {
        try {
            out.writeObject(answer);
            out.flush();
        } catch (IOException e) {
            System.out.println("⚠️ Failed to send answer: " + e.getMessage());
        }
    }
}
