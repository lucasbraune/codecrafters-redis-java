package codecrafters.redis;

import codecrafters.redis.protocol.*;
import codecrafters.redis.protocol.Error;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestHandler {
    public static final SimpleString OK = new SimpleString("OK");
    public static final SimpleString PONG = new SimpleString("PONG");

    private final CacheService cacheService;

    public RequestHandler(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public RedisSerializable handle(BulkStringArray request) {
        try {
            String command = getCommand(request);
            switch (command) {
                case "ping":
                    return handlePing(request);
                case "echo":
                    return handleEcho(request);
                case "get":
                    return handleGet(request);
                case "set":
                    return handleSet(request);
                default:
                    return new Error("Unknown command: " + command);
            }
        } catch (BadRequestException e) {
            return new Error(e.getMessage());
        } catch (RuntimeException e) {
            return new Error("Internal server error");
        }
    }

    public static class BadRequestException extends Exception {
        public BadRequestException(String message) {
            super(message);
        }
    }

    private static String getCommand(BulkStringArray request) throws BadRequestException {
        List<BulkString> bulkStrings = request.asList();
        if (bulkStrings.isEmpty()) {
            throw new BadRequestException("Empty request");
        }
        Optional<String> command = bulkStrings.get(0).getContent();
        if (!command.isPresent()) {
            throw new BadRequestException("Command is null");
        }
        return command.get();
    }

    private SimpleString handlePing(BulkStringArray request) {
        return PONG;
    }

    private BulkString handleEcho(BulkStringArray request) throws BadRequestException {
        if (request.asList().size() < 2) {
            throw new BadRequestException("Message missing");
        }
        return request.asList().get(1);
    }

    private BulkString handleGet(BulkStringArray request) throws BadRequestException {
        List<String> arguments = getArguments(request);
        Optional<String> getResult = cacheService.get(getKey(arguments));
        return getResult.map(BulkString::of).orElse(BulkString.NULL);
    }

    private SimpleString handleSet(BulkStringArray request) throws BadRequestException {
        List<String> arguments = getArguments(request);
        Optional<Long> px = getPx(arguments);
        if (px.isPresent()) {
            cacheService.set(getKey(arguments), getValue(arguments), px.get());
        } else {
            cacheService.set(getKey(arguments), getValue(arguments));
        }
        return OK;
    }

    private static List<String> getArguments(BulkStringArray request) throws BadRequestException {
        if (request.asList().isEmpty()) {
            throw new BadRequestException("Empty request");
        }
        return request.asList().stream()
                .skip(1)
                .map(BulkString::getContent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
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
