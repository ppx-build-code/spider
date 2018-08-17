package com.links86.spider.cache;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisCache {

    private static final String REDIS_HOST = "47.98.50.18";
    private static final String REDIS_PWD = "86links#pwd1234";
    private static final int REDIS_PORT = 6379;

    private static RedisTemplate redisTemplate;


    public static RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            return new RedisCache().redisTemplate();
        }
        return redisTemplate;
    }

    private RedisTemplate<String, Object> redisTemplate() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(REDIS_HOST);
        factory.setPassword(REDIS_PWD);
        factory.setPort(REDIS_PORT);
        factory.setDatabase(0);
        factory.afterPropertiesSet();

        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
