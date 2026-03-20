package com.xushu.campus.order.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 简单的内存RedisTemplate，避免连接真实的Redis服务器
 */
public class SimpleRedisTemplate extends RedisTemplate<String, Object> {

    private final Map<String, Object> memoryStore = new ConcurrentHashMap<>();

    public SimpleRedisTemplate() {
        // 设置序列化器
        setKeySerializer(new StringRedisSerializer());
        setValueSerializer(new StringRedisSerializer());
        setHashKeySerializer(new StringRedisSerializer());
        setHashValueSerializer(new StringRedisSerializer());

        // 禁用连接工厂
        setConnectionFactory(null);
    }

    @Override
    public void afterPropertiesSet() {
        // 跳过父类的初始化，避免连接Redis服务器
        // 不调用super.afterPropertiesSet()
        // 只设置序列化器
        if (getKeySerializer() == null) {
            setKeySerializer(new StringRedisSerializer());
        }
        if (getValueSerializer() == null) {
            setValueSerializer(new StringRedisSerializer());
        }
    }

    @Override
    public RedisConnectionFactory getConnectionFactory() {
        return null; // 返回null避免连接尝试
    }

    @Override
    public Boolean delete(String key) {
        return memoryStore.remove(key) != null;
    }

    @Override
    public Boolean hasKey(String key) {
        return memoryStore.containsKey(key);
    }

    @Override
    public ValueOperations<String, Object> opsForValue() {
        return new SimpleValueOperations(memoryStore);
    }

    /**
     * 简单的ValueOperations实现
     */
    private static class SimpleValueOperations implements ValueOperations<String, Object> {

        private final Map<String, Object> store;

        public SimpleValueOperations(Map<String, Object> store) {
            this.store = store;
        }

        @Override
        public void set(String key, Object value) {
            store.put(key, value);
        }

        @Override
        public void set(String key, Object value, long timeout, TimeUnit unit) {
            store.put(key, value);
            // 忽略过期时间
        }

        @Override
        public Object get(Object key) {
            if (key instanceof String) {
                return store.get(key);
            }
            return null;
        }

        @Override
        public Boolean setIfAbsent(String key, Object value) {
            return store.putIfAbsent(key, value) == null;
        }

        @Override
        public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
            return setIfAbsent(key, value);
        }

        @Override
        public Boolean setIfPresent(String key, Object value) {
            return store.replace(key, value) != null;
        }

        @Override
        public Boolean setIfPresent(String key, Object value, long timeout, TimeUnit unit) {
            return setIfPresent(key, value);
        }

        @Override
        public Object getAndDelete(String key) {
            return store.remove(key);
        }

        @Override
        public Object getAndExpire(String key, long timeout, TimeUnit unit) {
            return store.get(key);
        }

        @Override
        public Object getAndPersist(String key) {
            return store.get(key);
        }

        @Override
        public Object getAndSet(String key, Object value) {
            return store.put(key, value);
        }

        @Override
        public Long increment(String key) {
            Object current = store.get(key);
            long value = current instanceof Number ? ((Number) current).longValue() : 0L;
            value++;
            store.put(key, value);
            return value;
        }

        @Override
        public Long increment(String key, long delta) {
            Object current = store.get(key);
            long value = current instanceof Number ? ((Number) current).longValue() : 0L;
            value += delta;
            store.put(key, value);
            return value;
        }

        @Override
        public Double increment(String key, double delta) {
            Object current = store.get(key);
            double value = current instanceof Number ? ((Number) current).doubleValue() : 0.0;
            value += delta;
            store.put(key, value);
            return value;
        }

        @Override
        public Long decrement(String key) {
            return increment(key, -1L);
        }

        @Override
        public Long decrement(String key, long delta) {
            return increment(key, -delta);
        }

        // 以下方法OrderServiceImpl未使用，提供默认实现
        @Override
        public Integer append(String key, String value) {
            return 0;
        }

        @Override
        public String get(String key, long start, long end) {
            return null;
        }

        @Override
        public void set(String key, Object value, long offset) {
            // 空实现
        }

        @Override
        public Long size(String key) {
            return 0L;
        }

        @Override
        public Boolean getBit(String key, long offset) {
            return false;
        }

        @Override
        public Boolean setBit(String key, long offset, boolean value) {
            return false;
        }

        @Override
        public java.util.List<Long> bitField(String key, org.springframework.data.redis.connection.BitFieldSubCommands subCommands) {
            return java.util.Collections.emptyList();
        }

        @Override
        public org.springframework.data.redis.core.RedisOperations<String, Object> getOperations() {
            return null;
        }

        @Override
        public java.util.List<Object> multiGet(java.util.Collection<String> keys) {
            return java.util.Collections.emptyList();
        }

        @Override
        public void multiSet(java.util.Map<? extends String, ? extends Object> map) {
            // 空实现
        }

        @Override
        public Boolean multiSetIfAbsent(java.util.Map<? extends String, ? extends Object> map) {
            return false;
        }

        @Override
        public Object getAndExpire(String key, java.time.Duration timeout) {
            return null;
        }
    }
}