package com.gomeplus.amp.ad.security.shiro.session;

import java.util.Set;

/**
 * redis连接客户端连接接口
 *
 * @author baishen
 */
public interface RedisClient {	
	
	public void init();
	
	public byte[] get(byte[] key);
	
	public byte[] set(byte[] key, byte[] value);

	public byte[] set(byte[] key, byte[] value, int expire);
	
	public void del(byte[] key);

	public void flushDB();
	
	public Long dbSize();

	public Set<byte[]> keys(String pattern);
	
	public String[] getServers();

	public void setServers(String[] servers);

	public int getExpire();

	public void setExpire(int expire);

	public int getTimeout();

	public void setTimeout(int timeout);

	public String getPassword();

	public void setPassword(String password);
}
