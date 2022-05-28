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
        try {
            String command = getCommand(request);
            List<String> arguments = getArguments(request);
            switch (command) {
                case "ping":
                    String pingResult = cacheService.ping();
                    return new RespSimpleString(pingResult);
                case "echo":
                    String echoResult = cacheService.echo(getMessage(arguments));
                    return new RespBulkString(echoResult);
                case "get":
                    String getResult = cacheService.get(getKey(arguments));
                    return new RespBulkString(getResult);
                case "set":
                    Optional<Long> px = getPx(arguments);
                    String setResult = px.isPresent() ?
                            cacheService.set(getKey(arguments), getValue(arguments), px.get()) :
                            cacheService.set(getKey(arguments), getValue(arguments));
                    return new RespSimpleString(setResult);
                default:
                    return new RespError("Unknown command: " + command);
            }
        } catch (BadRequestException e) {
            return new RespError(e.getMessage());
        }
    }

    public static class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }

    private static String getCommand(RespArray request) throws BadRequestException {
        List<RespBulkString> elements = request.getElements();
        if (elements.isEmpty()) {
            throw new BadRequestException("Empty request");
        }
        if (elements.get(0).equals(RespBulkString.NULL)) {
            throw new BadRequestException("Command is null");
        }
        return elements.get(0).getValue();
    }

    private static List<String> getArguments(RespArray request) throws BadRequestException {
        if (request.getElements().isEmpty()) {
            throw new BadRequestException("Empty request");
        }
        return request.getElements().stream()
                .skip(1)
                .map(RespBulkString::getValue)
                .collect(Collectors.toList());
    }

    private static String getMessage(List<String> arguments) throws BadRequestException {
        if (arguments.isEmpty()) {
            throw new BadRequestException("Empty request");
        }
        return arguments.get(0);
    }

    private static String getKey(List<String> arguments) throws BadRequestException {
        if (arguments.isEmpty()) {
            throw new BadRequestException("Key missing");
        }
        String key = arguments.get(0);
        if (key == null) {
            throw new BadRequestException("Null key");
        }
        return key;
    }

    private static String getValue(List<String> arguments) throws BadRequestException {
        if (arguments.size() < 2) {
            throw new BadRequestException("Value missing");
        }
        String value = arguments.get(1);
        if (value == null) {
            throw new BadRequestException("Null value");
        }
        return value;
    }

    private static Optional<Long> getPx(List<String> arguments) throws BadRequestException {
        boolean pxPresent = arguments.size() >= 4 && arguments.get(2).equals("px");
        if (!pxPresent) {
            return Optional.empty();
        }
        String pxString = arguments.get(3);
        if (pxString == null) {
            throw new BadRequestException("Null PX");
        }
        try {
            long px = Long.parseUnsignedLong(arguments.get(3));
            return Optional.of(px);
        } catch (NumberFormatException e) {
            throw new BadRequestException("PX not unsigned integer");
        }
    }
}
