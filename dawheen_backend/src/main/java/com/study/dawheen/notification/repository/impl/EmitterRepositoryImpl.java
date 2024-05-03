package com.study.dawheen.notification.repository.impl;

import com.study.dawheen.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> emitterRedisTemplate;
    private final RedisTemplate<String, Object> cacheRedisTemplate;


    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        sseEmitterMap.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        cacheRedisTemplate.opsForValue().set(eventCacheId, event, 7, TimeUnit.DAYS);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return sseEmitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        Set<String> keys = cacheRedisTemplate.keys(memberId + "*");
        Map<String, Object> valueMap = new HashMap<>();

        assert keys != null;
        for (String key : keys) {
            Object value = cacheRedisTemplate.opsForValue().get(key);
            valueMap.put(key, value);
        }

        return valueMap;
    }

    @Override
    public void deleteById(String id) {
        emitterRedisTemplate.delete(id);
    }

    @Override
    public void deleteAllEmitterStartWithId(String memberId) {
        sseEmitterMap.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        sseEmitterMap.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartWithId(String memberId) {
        Set<String> keys = cacheRedisTemplate.keys(memberId + "*");

        assert keys != null;
        for (String key : keys) {
            cacheRedisTemplate.delete(key);
        }

    }
}
