package codecrafters.redis;

import codecrafters.redis.protocol.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestHandler {
    private final CacheService cacheService;

    public RequestHandler(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public RespData handle(RespArray request) {
        if (isEmpty(request)) {
            return new RespError("Empty request");
        }
        if (containsNull(request)) {
            return new RespError("Request contains null bulk string");
        }
        String command = getCommand(request);
        List<String> arguments = getArguments(request);
        switch (command.toLowerCase()) {
            case "ping":
                String pingResult = cacheService.ping();
                return new RespSimpleString(pingResult);
            case "echo":
                if (arguments.size() < 1) {
                    return new RespError("Message missing");
                }
                String echoResult = cacheService.echo(arguments.get(0));
                return new RespBulkString(echoResult);
            case "get":
                if (arguments.size() < 1) {
                    return new RespError("Key missing");
                }
                String getResult = cacheService.get(arguments.get(0));
                return new RespBulkString(getResult);
            case "set":
                if (arguments.size() < 2) {
                    return new RespError("Key or value missing");
                }
                String key = arguments.get(0);
                String value = arguments.get(1);
                Optional<Long> px;
                try {
                    px = getPx(arguments);
                } catch (NumberFormatException e) {
                    return new RespError("Bad PX value");
                }
                String setResult = px.isPresent() ?
                        cacheService.set(key, value, px.get()) :
                        cacheService.set(key, value);
                return new RespSimpleString(setResult);
            default:
                return new RespError("Unknown command: " + command);
        }
    }

    private static boolean isEmpty(RespArray request) {
        return request.getElements().isEmpty();
    }

    private static boolean containsNull(RespArray request) {
        return request.getElements().stream()
                .noneMatch(element -> element.equals(RespBulkString.NULL));
    }

    private static String getCommand(RespArray request) {
        return request.getElements().get(0).getValue();
    }

    private static List<String> getArguments(RespArray request) {
        return request.getElements().stream()
                .skip(1)
                .map(RespBulkString::getValue)
                .collect(Collectors.toList());
    }

    /**
     * @throws NumberFormatException if the third argument is "px" and the fourth argument is not an integer.
     */
    private static Optional<Long> getPx(List<String> arguments) {
        boolean pxPresent = arguments.size() >= 4 &&
                arguments.get(2).equalsIgnoreCase("PX");
        if (pxPresent) {
            long px = Long.parseLong(arguments.get(3));
            return Optional.of(px);
        } else {
            return Optional.empty();
        }
    }
}
