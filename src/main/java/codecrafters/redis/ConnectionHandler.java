package codecrafters.redis;

import codecrafters.redis.protocol.BulkStringArray;
import codecrafters.redis.protocol.RedisSerializable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private final Socket clientSocket;
    private final RequestHandler requestHandler;

    public ConnectionHandler(Socket clientSocket, RequestHandler requestHandler) {
        this.clientSocket = clientSocket;
        this.requestHandler = requestHandler;
    }

    /**
     * Handles requests from this handler's socket, then closes the socket.
     */
    @Override
    public void run() {
        System.out.println("Handling requests in thread " + Thread.currentThread().getName());
        // Closing a socket's input or output stream closes the socket.
        try (InputStream in = new BufferedInputStream(clientSocket.getInputStream());
             OutputStream out = clientSocket.getOutputStream()
        ) {
            for (BulkStringArray request = BulkStringArray.readFrom(in);
                 request != null;
                 request = BulkStringArray.readFrom(in)
            ) {
                RedisSerializable response = requestHandler.handle(request);
                out.write(response.serialize().getBytes());
            }
        } catch (InputMismatchException e) {
            System.out.println("Unable to parse request: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
