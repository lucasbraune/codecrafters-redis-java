package codecrafters.redis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting redis server.");
        ServerSocket serverSocket;
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
        } catch (InputMismatchException e) {
            System.out.println("Bad request: " + e.getMessage());
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

    private static void handleRequests(InputStream in, OutputStream out) throws IOException, InputMismatchException {
        String PONG = (new RespSimpleString("PONG")).encode();
        InputStream bufferedInput = new BufferedInputStream(in);

        for (RespArray request = RespArray.decode(bufferedInput);
             request != null;
             request = RespArray.decode(bufferedInput)
        ) {
            if (request.getElements().get(0).getValue().equals("ping")) {
                out.write(PONG.getBytes());
            } else {
                System.out.println("Unexpected request");
            }
        }
    }
}
