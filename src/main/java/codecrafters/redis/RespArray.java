package codecrafters.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RespArray {
    final private List<RespBulkString> elements;

    public RespArray(List<RespBulkString> elements) {
        this.elements = new ArrayList<>(elements);
    }

    public RespArray(RespBulkString... elements) {
        this(Arrays.asList(elements));
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(elements.size());
        for (RespBulkString element : elements) {
            sb.append(element.toString());
        }
        return sb.toString();
    }
}