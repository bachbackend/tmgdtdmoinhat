package com.example.service.serviceImpl;//package com.example.TMG_DTD_NEW.service.serviceImpl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//
//@Service
//public class DistributedLockService {
//    private static final String LOCK_KEY = "user_lock";
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    public boolean acquireLock() {
//        return Objects.requireNonNull(redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, "locked"));
//    }
//
//    public void releaseLock() {
//        redisTemplate.delete(LOCK_KEY);
//    }
//}




























