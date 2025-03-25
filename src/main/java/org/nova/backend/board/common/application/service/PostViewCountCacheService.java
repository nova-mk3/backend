package org.nova.backend.board.common.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewCountCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "post:viewcount:";

    public void increaseViewCount(UUID postId) {
        String key = KEY_PREFIX + postId;
        redisTemplate.opsForValue().increment(key);
    }

    public int getViewCount(UUID postId) {
        String key = KEY_PREFIX + postId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value.toString()) : 0;
    }

    public void resetViewCount(UUID postId) {
        String key = KEY_PREFIX + postId;
        redisTemplate.delete(key);
    }

    public Map<UUID, Integer> getAllViewCounts(List<UUID> postIds) {
        Map<UUID, Integer> result = new HashMap<>();
        for (UUID postId : postIds) {
            result.put(postId, getViewCount(postId));
        }
        return result;
    }
}

