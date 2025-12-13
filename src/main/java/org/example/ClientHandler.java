package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements  Runnable {
    Socket clientSocket;
    private static class HTTPRequest {
        String method;
        String path;
        java.util.Map<String, String> headers = new java.util.HashMap<>();
    }
    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            HTTPRequest request = new HTTPRequest();

            String line = in.readLine();
            if (line == null || line.isEmpty()) return;

            String[] parts = line.split(" ");
            if (parts.length >= 2) {
                request.method = parts[0];
                request.path = parts[1];
            } else {

                return;
            }

            while ((line = in.readLine()) != null && !line.isEmpty()) {
                int colonIndex = line.indexOf(":");
                if (colonIndex != -1) {
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    request.headers.put(key, value);
                }
            }

            System.out.println("Processing: " + request.method + " " + request.path);


            if ("GET".equals(request.method)) {
                if ("/".equals(request.path)) {
                    sendResponse(out, 200, "Hello Home");
                } else if ("/sleep".equals(request.path)) {
                    Thread.sleep(5000); // Simulate slow DB query
                    sendResponse(out, 200, "Awake now");
                } else {
                    sendResponse(out, 404, "Not Found");
                }
            } else {
                sendResponse(out, 501, "Not Implemented");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (Exception e) { }
        }
    }

    private void sendResponse(PrintWriter out, int statusCode, String body) {
        out.println("HTTP/1.1 " + statusCode + " OK");
        out.println("Content-Type: text/plain");
        out.println("Content-Length: " + body.length());
        out.println("");
        out.println(body);
    }
}
