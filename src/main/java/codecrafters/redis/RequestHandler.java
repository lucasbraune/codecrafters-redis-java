package codecrafters.redis;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;
    private final CacheService service;

    public RequestHandler(Socket clientSocket, CacheService service) {
        this.clientSocket = clientSocket;
        this.service = service;
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
            for (RespArray request = RespArray.decode(in);
                 request != null;
                 request = RespArray.decode(in)
            ) {
                RespData response = handleRequest(request);
                out.write(response.encode().getBytes());
            }
        } catch (InputMismatchException e) {
            System.out.println("Unable to parse request: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private RespData handleRequest(RespArray request) {
        if (request.getElements().isEmpty()) {
            return new RespError("Empty request");
        }
        String command = request.getElements().get(0).getValue();
        if (command == null) {
            return new RespError("Null bulk string as command");
        }
        switch (command) {
            case "ping":
                return service.ping();
            case "echo":
                if (request.getElements().size() < 2) {
                    return new RespError("Missing echo message");
                }
                return service.echo(request.getElements().get(1));
            default:
                return new RespError("Unknown command: " + command);
        }
    }
}
