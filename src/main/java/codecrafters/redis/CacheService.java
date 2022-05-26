package codecrafters.redis;

public class CacheService {
    RespSimpleString ping() {
        return new RespSimpleString("PONG");
    }

    RespBulkString echo(RespBulkString message) {
        return message;
    }
}
