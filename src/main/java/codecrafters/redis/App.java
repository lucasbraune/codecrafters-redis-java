package codecrafters.redis;

import java.io.*;
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
                handleRequests(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /**
     * Handles requests from a socket, then closes it.
     */
    private static void handleRequests(Socket clientSocket) throws IOException {
        // Closing a socket's input or output stream closes the socket.
        try (InputStream in = new BufferedInputStream(clientSocket.getInputStream());
             OutputStream out = clientSocket.getOutputStream()
        ) {
            for (RespArray request = RespArray.decode(in);
                 request != null;
                 request = RespArray.decode(in)
            ) {
                RespData response = handleRequest(request);
                out.write(response.encode().getBytes());
            }
        } catch (InputMismatchException e) {
            System.out.println("Unable to parse request: " + e.getMessage());
        }
    }

    private static RespData handleRequest(RespArray request) {
        String command = request.getElements().get(0).getValue();
        if (command.equals("ping")) {
            return new RespSimpleString("PONG");
        } else {
            return new RespSimpleString("Unknown command: " + command);
        }
    }
}
