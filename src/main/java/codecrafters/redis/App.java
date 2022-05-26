package codecrafters.redis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting redis server.");
        int port = 6379;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable worker = new RequestHandler(clientSocket);
                worker.run();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
