package org.multithreadedserver;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;



public class Main {
    public static void main(String[] args) {
        try (ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, 4,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2));
             ServerSocket serverSocket = new ServerSocket(1729)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try{
                    executorService.execute(new ClientHandler(clientSocket));
                }catch(RejectedExecutionException e){
                    rejectConnection(clientSocket);
                }

            }
        } catch (Exception e) {
            System.err.println("Error in setup"+e.getMessage());
        }
    }
    private static void rejectConnection(Socket socket)  {
        try{
            System.out.println("Queue is full");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("HTTP/1.1 503 Service Unavailable");
            out.println("Content-Type: text/plain");
            out.println("");
            out.println("Server is busy please try again later.");
            out.close();
            socket.close();
        }catch (IOException e){
            System.err.println("Could not send 503 error:"+e.getMessage());
        }
    }
}



