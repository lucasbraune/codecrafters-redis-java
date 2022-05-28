package codecrafters.redis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) {
        int port = 6379;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            CacheService service = new CacheService();
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(service);
                Runnable worker = new ConnectionHandler(clientSocket, requestHandler);
                executor.execute(worker);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
