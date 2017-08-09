package com.gomeplus.amp.ad.security.shiro.session;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 重写shiro中关于cache操作的基础类
 * @author lifei01
 */
public class RedisCacheManager implements CacheManager {

	private static final Logger logger = LoggerFactory
			.getLogger(RedisCacheManager.class);

	//快速查询表用于存储cache
	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
	//redis基础信息配置
	private RedisClient redisClient;

	/**
	 * caches 存储在redis中的key
	 */
	private String keyPrefix = "amp_cache:";
	
	/**
	 * 返回Redis session keys
	 * @return The prefix
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * 设置Redis sessions key 
	 * @param keyPrefix 
	 */
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	
	/**
	 * 按照名称获取RedisCache 
	 * @param name 
	 */
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		logger.debug("获取名称为: " + name + " 的RedisCache实例");	
		Cache cache = caches.get(name);
		if (cache == null) {
			// 初始化redisClient
			redisClient.init();	
			// 创建一个新的 cache实例
			cache = new RedisCache<K, V>(redisClient, keyPrefix);
			caches.put(name, cache);
		}
		return cache;
	}

	public RedisClient getRedisClient() {
		return redisClient;
	}

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}
}
