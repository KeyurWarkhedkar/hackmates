package com.keyur.hackmates.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TempCodeStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TTL = Duration.ofSeconds(60);

    public void save(String code, String jwt) {
        redisTemplate.opsForValue().set(code, jwt, TTL);
    }

    public String getAndDelete(String code) {
        String jwt = (String) redisTemplate.opsForValue().get(code);
        if (jwt != null) {
            redisTemplate.delete(code);
        }
        return jwt;
    }
}

