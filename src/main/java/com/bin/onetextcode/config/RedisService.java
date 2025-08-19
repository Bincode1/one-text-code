package com.bin.onetextcode.config;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value, DEFAULT_TTL);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}
