package codecrafters.redis;

import codecrafters.redis.protocol.RespArray;
import codecrafters.redis.protocol.RespBulkString;
import codecrafters.redis.protocol.RespData;
import codecrafters.redis.protocol.RespError;

import java.util.List;

public class RequestHandler {
    private final CacheService service;

    public RequestHandler(CacheService service) {
        this.service = service;
    }

    public RespData handle(RespArray request) {
        List<RespBulkString> elements = request.getElements();
        if (elements.isEmpty()) {
            return new RespError("Empty request");
        }
        String command = elements.get(0).getValue();
        if (command == null) {
            return new RespError("Null bulk string as command");
        }
        List<RespBulkString> arguments = elements.subList(1, elements.size());
        switch (command) {
            case "ping":
                return service.ping(arguments);
            case "echo":
                return service.echo(arguments);
            case "get":
                return service.get(arguments);
            case "set":
                return service.set(arguments);
            default:
                return new RespError("Unknown command: " + command);
        }
    }
}
