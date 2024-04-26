package com.study.dawheen.notification.repository.impl;

import com.study.dawheen.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {

    private final RedisTemplate<String, SseEmitter> emitterRedisTemplate;
    private final RedisTemplate<String, Object> cacheRedisTemplate;


    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitterRedisTemplate.opsForValue().set(emitterId, sseEmitter, 1, TimeUnit.HOURS);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        cacheRedisTemplate.opsForValue().set(eventCacheId, event, 7, TimeUnit.DAYS);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        Set<String> keys = emitterRedisTemplate.keys(memberId + "*");
        Map<String, SseEmitter> userEmitters = new HashMap<>();

        assert keys != null;
        for (String key : keys) {
            SseEmitter emitter = emitterRedisTemplate.opsForValue().get(key);
            userEmitters.put(key, emitter);
        }
        return userEmitters;
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
        Set<String> keys = emitterRedisTemplate.keys(memberId + "*");

        assert keys != null;
        for (String key : keys) {
            emitterRedisTemplate.delete(key);
        }


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
