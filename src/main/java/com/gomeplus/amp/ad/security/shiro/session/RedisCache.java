package com.gomeplus.amp.ad.security.shiro.session;

import com.gomeplus.adm.common.util.SerializeUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 把cache保存到redis基础控制器
 * @author lifei01
 */
public class RedisCache<K, V> implements Cache<K, V> {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
		
	/**
	 * 封装的redis实例
	 */
	private RedisClient cache;
	
	/**
	 *redis key用于保存sesison
	 */
	private String keyPrefix = "amp_cache:";
	
	/**
	 * 返回redis session的key.
	 * @return  prefix
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * 设置redis session中的key
	 * @param keyPrefix
	 */
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
	
	/**
	 * 通过一个JedisManager实例构造RedisCache
	 */
	public RedisCache(RedisClient cache) {
		if (cache == null) {
			throw new IllegalArgumentException("cache不能为空");
		}
		this.cache = cache;
	}
	
	/**
	 * 构造方法
	 * @param cache
	 * @param prefix
	 * @return  RedisCache实例
	 */
	public RedisCache(RedisClient cache, String prefix) {	 
		this(cache);
		this.keyPrefix = prefix;
	}
	
	/**
	 * 获得byte[]型的key
	 * @param key
	 * @return byte[]型的key
	 */
	private byte[] getByteKey(K key) {
		if(key instanceof String){
			String preKey = this.keyPrefix + key;
			return preKey.getBytes(StandardCharsets.UTF_8);
		} else {
			return SerializeUtil.serialize(key);
		}
	}
 	
	/**
	 * 根据key从Redis中获取value
	 * @return byte[]型的value
	 */
	@Override
	public V get(K key) throws CacheException {
		logger.debug("根据key从Redis中获取对象 key [" + key + "]");
		try {
			if (key == null) {
				return null;
			} else {
				byte[] rawValue = cache.get(getByteKey(key));
				@SuppressWarnings("unchecked")
				V value = (V)SerializeUtil.deserialize(rawValue);
				return value;
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * 把cache放入redis中
	 * @param key
	 * @param value
	 * @return value
	 */
	@Override
	public V put(K key, V value) throws CacheException {
		logger.debug("根据key从存储 key [" + key + "]");
		try {
			cache.set(getByteKey(key), SerializeUtil.serialize(value),cache.getExpire());
			return value;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * 根据key删除redis的该条信息
	 * @param key
	 * @return value
	 */
	@Override
	public V remove(K key) throws CacheException {
		logger.debug("从redis中删除 key [" + key + "]");
		try {
			V previous = get(key);
			cache.del(getByteKey(key));
			return previous;
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public void clear() throws CacheException {
		logger.debug("从redis中删除所有元素");
		try {
			cache.flushDB();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	@Override
	public int size() {
		try {
			return cache.dbSize().intValue();
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 *获取所有的key
	 **@return key集合
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keys() {
		try {
			Set<byte[]> keys = cache.keys(this.keyPrefix + "*");
			if (CollectionUtils.isEmpty(keys)) {
				return Collections.emptySet();
			} else {
				Set<K> newKeys = new HashSet<K>();
				for (byte[] key : keys ) {
					newKeys.add((K)key);
				}
				return newKeys;
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}

	/**
	 * 获取所有的value
	 * @return values集合
	 */
	@Override
	public Collection<V> values() {
		try {
			Set<byte[]> keys = cache.keys(this.keyPrefix + "*");
			if (!CollectionUtils.isEmpty(keys)) {
				List<V> values = new ArrayList<V>(keys.size());
				for (byte[] key : keys) {
					@SuppressWarnings("unchecked")
					V value = get((K)key);
					if (value != null) {
						values.add(value);
					}
				}
				return Collections.unmodifiableList(values);
			} else {
				return Collections.emptyList();
			}
		} catch (Throwable t) {
			throw new CacheException(t);
		}
	}
}
