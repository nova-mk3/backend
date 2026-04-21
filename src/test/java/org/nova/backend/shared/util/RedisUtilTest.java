package org.nova.backend.shared.util;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nova.backend.annotation.FastTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;

@FastTest
@ExtendWith(MockitoExtension.class)
class RedisUtilTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisUtil redisUtil;

    @Test
    @DisplayName("문자열 값을 TTL과 함께 저장한다")
    void 문자열_값을_TTL과_함께_저장한다() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisUtil.set("upload:test", "done", 10, TimeUnit.MINUTES);

        verify(valueOperations).set("upload:test", "done", 10, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("문자열 값을 조회한다")
    void 문자열_값을_조회한다() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("image:meta:test")).thenReturn("100x200");

        String value = redisUtil.get("image:meta:test");

        assertThat(value).isEqualTo("100x200");
    }

    @Test
    @DisplayName("키 삭제를 위임한다")
    void 키_삭제를_위임한다() {
        redisUtil.delete("upload:test");

        verify(redisTemplate).delete("upload:test");
    }

    @Test
    @DisplayName("키 존재 여부를 반환한다")
    void 키_존재_여부를_반환한다() {
        when(redisTemplate.hasKey("upload:test")).thenReturn(true);

        boolean exists = redisUtil.hasKey("upload:test");

        assertThat(exists).isTrue();
    }
}