package com.gomeplus.amp.ad.security.shiro.session;

import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

/**
 * redis客户端集群连接类
 * 
 * @author baishen
 */
public class RedisClusterClient implements RedisClient {	
	

	//redis 服务器地址:端口
	private String[] servers = null;
	//失效时间
	private int expire = 0;
	//连接超时时间
	private int timeout = 0;
	//密码
	private String password = "";
	//redids 集群
	private static BinaryJedisCluster jedisCluster = null;

	
	public RedisClusterClient() {
		
	}

	/**
	 * 初始化方法
	 */
	public void init() {
		if (jedisCluster == null) {
			Set<HostAndPort> nodes = new HashSet<HostAndPort>();  
			String[] hostPorts = servers;
			for(int i = 0; i < hostPorts.length; i++) {  
				String hostPort[] = hostPorts[i].split(":");  
				nodes.add(new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1])));  
			}

			if (timeout != 0) {
				jedisCluster = new BinaryJedisCluster(nodes, timeout);
			} else {
				jedisCluster = new BinaryJedisCluster(nodes);  
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
		try {
			value = jedisCluster.get(key);
		} catch (Exception exception) {
		}
		return value;
	}
	
	/**
	 * 根据key、value设置信息到redis中
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] set(byte[] key, byte[] value) {
		try {
			jedisCluster.set(key, value);
			if (this.expire != 0) {
				jedisCluster.expire(key, this.expire);
			}
		} catch (Exception exception) {
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
	public byte[] set(byte[] key, byte[] value, int expire) {
		try {
			jedisCluster.set(key, value);
			if (expire != 0) {
				jedisCluster.expire(key, expire);
			}
		} catch (Exception exception) {
		}
		return value;
	}
	
	/**
	 * 根据key删除redis中的信息
	 * @param key
	 */
	public void del(byte[] key) {
		try {
			jedisCluster.del(key);
		} finally {
		}
	}
	
	/**
	 * 清空所有db：
	 */
	public void flushDB() {
	}
	
	/**
	 * 返回当前数据库中key的数目。
	 */
	public Long dbSize() {
		Long dbSize = 0L;
		return dbSize;
	}

	/**
	 * keys
	 * @param regex
	 * @return
	 */
	public Set<byte[]> keys(String pattern) {
		Set<byte[]> keys = null;
		//@todo 获取集群keys
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
