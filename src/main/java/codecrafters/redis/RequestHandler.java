package codecrafters.redis;

import codecrafters.redis.protocol.*;

import java.util.List;

public class RequestHandler {
    private final CacheService cacheService;

    public RequestHandler(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public RespData handle(RespArray request) {
        List<RespBulkString> elements = request.getElements();
        if (elements.isEmpty()) {
            return new RespError("Empty request");
        }
        String command = elements.get(0).getValue();
        if (command == null) {
            return new RespError("Null command");
        }
        List<RespBulkString> arguments = elements.subList(1, elements.size());
        switch (command) {
            case "ping":
                String pingResult = cacheService.ping();
                return new RespSimpleString(pingResult);
            case "echo":
                String echoResult = cacheService.echo("TODO");
                return new RespBulkString(echoResult);
            default:
                return new RespError("Unknown command: " + command);
        }
    }


}
