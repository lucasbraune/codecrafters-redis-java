import java.io.IOException;
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
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                while (scanner.hasNextLine()) {
                    String inputLine = scanner.nextLine();
                    System.out.println("Input line: " + inputLine);
                    clientSocket.getOutputStream()
                            .write(Resp.encode("PONG"));
                }
                clientSocket.close();
                System.out.println("Client socket closed.");
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
}
