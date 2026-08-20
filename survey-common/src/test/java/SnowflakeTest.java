import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import snowflake.SnowFlake;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

class SnowflakeTest {

    @Test
    @DisplayName("유효한 Node ID로 Snowflake를 생성하고 ID를 발급한다.")
    void generateId_Success() {
        //given
        SnowFlake snowFlake = new SnowFlake(1L);

        //when
        long id = snowFlake.nextId();

        //then
        assertThat(id).isPositive();
    }

    @Test
    @DisplayName("허용 범위 밖의 Node ID(음수 또는 1023 초과)를 전달하면 예외가 발생한다")
    void invalidNodeId_ThrowException() {
        //음수 노드 ID
        assertThatThrownBy(() -> new SnowFlake(-1))
                .isInstanceOf(IllegalArgumentException.class);

        // 1023 초과 노드 ID (10비트 최대값 초과)
        assertThatThrownBy(() -> new SnowFlake(1024L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("순차적으로 생성된  ID는 항상 이전 ID보다 크다. (시간순 정렬성)")
    void idsAreIncreasing() {
        //given
        SnowFlake snowFlake = new SnowFlake(1L);

        //when
        long firstId = snowFlake.nextId();
        long secondId = snowFlake.nextId();
        long thirdId = snowFlake.nextId();


        //then
        assertThat(secondId).isGreaterThan(firstId);
        assertThat(thirdId).isGreaterThan(secondId);
    }

    @Test
    @DisplayName("단일 스레드에서 대량 생성 시 중복이 발생하지 않는다.")
    void generateMultipleIds_NoDuplicates() {
        //given
        SnowFlake snowFlake = new SnowFlake(1L);
        int count = 100_100;
        Set<Long> idSet = new HashSet<>(count);

        //when
        for (int i = 0; i < count; i++) {
            idSet.add(snowFlake.nextId());
        }

        //then
        assertThat(idSet).hasSize(count);
    }

    @Test
    @DisplayName("멀티스레드 동시 요청 환경에서도 ID 중복이 발생하지 안흔다. (동시성 테스트)")
    void concurrentGenerate_NoDuplicates() throws InterruptedException {
        //given
        SnowFlake snowFlake = new SnowFlake(1L);
        int threadCount = 20;
        int idsPerThread = 5_000;
        int totalIds = threadCount * idsPerThread;

        Set<Long> concurrentSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < idsPerThread; j++) {
                        concurrentSet.add(snowFlake.nextId());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

            startLatch.countDown();
            endLatch.await();
            executorService.shutdown();

            //then
            assertThat(concurrentSet).hasSize(totalIds);
    }
}
