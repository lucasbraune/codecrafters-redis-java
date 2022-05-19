import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true) {
                clientSocket = serverSocket.accept();
                handleRequests(clientSocket.getInputStream(), clientSocket.getOutputStream());
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    private static void handleRequests(InputStream in, OutputStream out) throws IOException {
        final String PONG = (new RespSimpleString("PONG")).toString();
        Scanner scanner = new Scanner(in);
        while (scanner.findWithinHorizon("PING", 0) != null) {
            System.out.println("Received: " + scanner.match().group() + "\nSending: " + PONG);
            out.write(PONG.getBytes());
        }
    }
}
