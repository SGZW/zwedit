package com.upcacm.zwedit.util;

import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisPool {

    private static String ADDR = "127.0.0.1";

    private static Integer PORT = 6379;

    private static String AUTH = "233";
    
    private static Integer MAX_TOTAL = 100;

    private static Integer MAX_IDLE = 50;

    private static Integer MAX_WAIT_MILLIS = 10000;

    private static Integer TIMEOUT = 10000;

    private static Boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    public static void init(Properties config) throws Exception {
        ADDR = config.getProperty("redis_host", "127.0.0.1");
        PORT = new Integer(config.getProperty("redis_port", "6379"));
        AUTH = config.getProperty("redis_auth", "233");
        MAX_TOTAL = new Integer(config.getProperty("redis_pool_size", "100"));
        MAX_IDLE = new Integer(config.getProperty("redis_max_idle", "50"));
        MAX_WAIT_MILLIS = new Integer(config.getProperty("redis_wait", "10000"));
        TIMEOUT = new Integer(config.getProperty("redis_timeout", "10000"));
        TEST_ON_BORROW = new Boolean(config.getProperty("redis_test_on_borrow", "true"));
    
        JedisPoolConfig redisConfig = new JedisPoolConfig();
        redisConfig.setMaxTotal(MAX_TOTAL);
        redisConfig.setMaxIdle(MAX_IDLE);
        redisConfig.setMaxWaitMillis(MAX_WAIT_MILLIS);
        redisConfig.setTestOnBorrow(TEST_ON_BORROW);

        jedisPool = new JedisPool(redisConfig, ADDR, PORT, TIMEOUT, AUTH);
    }

    public static Jedis getJedis() throws Exception {
        if (jedisPool == null) throw new Exception("jedisPool not exist!");
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
    
    public static void returnResource(final Jedis jedis){
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }
}
