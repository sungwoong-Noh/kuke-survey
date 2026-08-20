package snowflake;

public class SnowFlake {

    private static final long UNUSED_BITS = 1L;
    private static final long EPOCH_BITS = 41L;
    private static final long NODE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // 기준 시간 (Custom Epoch: 예 - 2024-01-01T00:00:00Z = 1704067200000L)
    private static final long CUSTOM_EPOCH = 1704067200000L;

    private final long nodeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;


    public SnowFlake(long nodeId) {

        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("Node ID must be between %d and %d", 0, MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }


    public synchronized long nextId() {
        long currentTimestamp = timestamp();
        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - currentTimestamp)
            );
        }

        // 현재 요청과 이전 요청 시간이 같다면 sequence를 올린다. (중복 방지)
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - CUSTOM_EPOCH) << (NODE_ID_BITS + SEQUENCE_BITS))
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }
}
