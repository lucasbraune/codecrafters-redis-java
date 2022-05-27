package codecrafters.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private Map<RespBulkString, CacheItem> map = new ConcurrentHashMap<>();

    RespData ping(List<RespBulkString> ignoredArguments) {
        return new RespSimpleString("PONG");
    }

    RespData echo(List<RespBulkString> arguments) {
        if (arguments.isEmpty()) {
            return new RespError("Missing echo message");
        }
        return arguments.get(0);
    }

    RespData set(List<RespBulkString> arguments) {
        switch (arguments.size()) {
            case 2:
                map.put(arguments.get(0), new CacheItem(arguments.get(1)));
                return new RespSimpleString("OK");
            case 4:
                String option = arguments.get(2).getValue();
                if (option == null || !option.equalsIgnoreCase("px")) {
                    return new RespError("Expected PX option, got " + option);
                }
                try {
                    int expiration = Integer.parseInt(arguments.get(3).getValue()); // milliseconds
                    map.put(arguments.get(0), new CacheItem(arguments.get(1), expiration));
                    return new RespSimpleString("OK");
                } catch (NumberFormatException e) {
                    return new RespError("Expiration either null or not integer");
                }
            default:
                return new RespError("Unexpected argument count: " + arguments.size());
        }
    }

    RespData get(List<RespBulkString> arguments) {
        if (arguments.isEmpty()) {
            return new RespError("Missing key");
        }
        RespBulkString key = arguments.get(0);
        CacheItem item = map.get(key);
        if (item == null) {
            return RespBulkString.NULL;
        }
        if (!item.isValid()) {
            map.remove(key);
            return RespBulkString.NULL;
        }
        return item.getValue();
    }
}
