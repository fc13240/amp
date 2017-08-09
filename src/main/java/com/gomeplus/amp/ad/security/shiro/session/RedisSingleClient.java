package com.gomeplus.amp.ad.security.shiro.session;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * redis连接类
 *
 * @author baishen
 */
public class RedisSingleClient implements RedisClient {	
	
	//redis 服务器地址:端口
	private String[] servers = null;
	//失效时间
	private int expire = 0;
	//连接超时时间
	private int timeout = 0;
	//密码
	private String password = "";
	//redids 连接池
	private static JedisPool jedisPool = null;
	
	public RedisSingleClient() {
		
	}
	
	/**
	 * 初始化方法
	 */
	public void init() {
		if (jedisPool == null) {
			String server = servers[0];
			String[] hostPort = server.split(":");
			String host = hostPort[0];
			Integer port = Integer.parseInt(hostPort[1]);

			if (password != null && !"".equals(password)) {
				jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
			} else if (timeout != 0) {
				jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout);
			} else {
				jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
			}
		}
	}
	
	/**
	 *根据key从redis中获取value
	 * @param key
	 * @return value
	 */
	public byte[] get(byte[] key) {
		byte[] value = null;
		Jedis jedis = jedisPool.getResource();
		try {
			value = jedis.get(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 根据key、value设置信息到redis中
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] set(byte[] key,byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key,value);
			if (this.expire != 0) {
				jedis.expire(key, this.expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 根据key、value、expire失效时间设置信息到redis中 
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	public byte[] set(byte[] key,byte[] value,int expire) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key,value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
		return value;
	}
	
	/**
	 * 根据key删除redis中的信息
	 * @param key
	 */
	public void del(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 清空所有db：
	 */
	public void flushDB() {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.flushDB();
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 返回当前数据库中key的数目。
	 */
	public Long dbSize() {
		Long dbSize = 0L;
		Jedis jedis = jedisPool.getResource();
		try {
			dbSize = jedis.dbSize();
		} finally {
			jedisPool.returnResource(jedis);
		}
		return dbSize;
	}

	/**
	 * keys
	 * @return
	 */
	public Set<byte[]> keys(String pattern) {
		Set<byte[]> keys = null;
		Jedis jedis = jedisPool.getResource();
		try {
			keys = jedis.keys(pattern.getBytes(StandardCharsets.UTF_8));
		} finally {
			jedisPool.returnResource(jedis);
		}
		return keys;
	}
	
	public String[] getServers() {
		return servers;
	}

	public void setServers(String[] servers) {
		this.servers = servers;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
