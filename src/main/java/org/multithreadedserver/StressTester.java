package org.multithreadedserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class StressTester {
    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            final int requestId = i;
            new Thread(() -> {
                try {
                    System.out.println("Client " + requestId + ": Attempting connection...");
                    try (Socket socket = new Socket("localhost", 1729);
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        System.out.println("Client " + requestId + ": Connected!");

                        // Read the response from the server (if any)
                        String response = in.readLine();
                        System.out.println("Client " + requestId + ": Received -> " + response);
                    }
                } catch (IOException e) {
                    System.err.println("Client " + requestId + ": Connection failed or rejected. " + e.getMessage());
                }
            }).start();
        }
    }
}
