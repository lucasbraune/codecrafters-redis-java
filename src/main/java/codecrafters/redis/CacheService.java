package codecrafters.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private Map<RespBulkString, RespBulkString> map = new ConcurrentHashMap<>();

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
        if (arguments.size() < 2) {
            return new RespError("Missing key or value or both");
        }
        map.put(arguments.get(0), arguments.get(1));
        return new RespSimpleString("OK");
    }

    RespData get(List<RespBulkString> arguments) {
        if (arguments.size() < 1) {
            return new RespError("Missing key");
        }
        return map.get(arguments.get(0));
    }
}
