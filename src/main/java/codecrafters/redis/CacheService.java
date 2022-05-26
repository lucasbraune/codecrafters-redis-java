package codecrafters.redis;

import java.util.List;

public class CacheService {
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
        return new RespError("Unimplemented");
    }

    RespData get(List<RespBulkString> arguments) {
        if (arguments.size() < 1) {
            return new RespError("Missing key");
        }
        return new RespError("Unimplemented");
    }
}
