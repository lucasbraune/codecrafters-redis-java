package codecrafters.redis.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BulkStringArray implements RedisSerializable {
    final private List<BulkString> bulkStrings;

    /**
     * @throws NullPointerException if elements is null
     */
    public BulkStringArray(List<BulkString> elements) {
        this.bulkStrings = new ArrayList<>(elements);
    }

    public BulkStringArray(BulkString... elements) {
        this(Arrays.asList(elements));
    }

    public List<BulkString> asList() {
        return Collections.unmodifiableList(bulkStrings);
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(bulkStrings.size()).append("\r\n");
        for (BulkString bulkString : bulkStrings) {
            sb.append(bulkString.serialize());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BulkStringArray bulkStringArray = (BulkStringArray) o;

        return bulkStrings.equals(bulkStringArray.bulkStrings);
    }

    @Override
    public int hashCode() {
        return bulkStrings.hashCode();
    }
}
