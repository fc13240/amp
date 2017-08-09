package com.gomeplus.amp.ad.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import java.util.HashSet;
import java.util.Set;

/**
 * Redis 工具类
 * @author DèngBīn
 */
public class RedisUtil {

	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

	private static Jedis jedis = null;

	private static JedisCluster jedisCluster = null;

	public static final String CAPTCHA_KEY = "ck@";

	public static final String FAIL_LOGIN_KEY = "fl@";

	private synchronized static Jedis getJedis() {
		try {
			if (jedis == null) {
				return new Jedis("bj01-ops-rds.dev.gomeplus.com", 8005);
			} else {
				return jedis;
			}
		} catch (Exception e) {
			logger.error("获取Jedis失败! 原因如下:" + e);
			e.printStackTrace();
			return null;
		}
	}

	private synchronized static JedisCluster getJedisCluster() {
		try {
			if (jedisCluster == null) {
				Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
				// Jedis Cluster will attempt to discover cluster nodes automatically
				jedisClusterNodes.add(new HostAndPort("bj02-ops-rdsc01.pro.gomeplus.com", 7007));
				jedisClusterNodes.add(new HostAndPort("bj02-ops-rdsc02.pro.gomeplus.com", 7007));
				jedisClusterNodes.add(new HostAndPort("bj02-ops-rdsc03.pro.gomeplus.com", 7007));
				jedisClusterNodes.add(new HostAndPort("bj02-ops-rdsc04.pro.gomeplus.com", 7007));
				jedisClusterNodes.add(new HostAndPort("bj02-ops-rdsc05.pro.gomeplus.com", 7007));
				jedisClusterNodes.add(new HostAndPort("bj02-ops-rdsc06.pro.gomeplus.com", 7007));
				return new JedisCluster(jedisClusterNodes);
			} else {
				return jedisCluster;
			}
		} catch (Exception e) {
			logger.error("获取jedisCluster失败! 原因如下:" + e);
			e.printStackTrace();
			return null;
		}
	}

	public static void set(String key, String value) {
		String environment = getEnvironment();

		if (environment.equals("development") || environment.equals("testing") || environment.equals("preproduction")) {
			getJedis().set(key, value);
		} else if (environment.equals("production")) {
			getJedisCluster().set(key, value);
		}
	}

    public static void setex(String key, Integer seconds, String value) {
        String environment = getEnvironment();

        if (environment.equals("development") || environment.equals("testing") || environment.equals("preproduction")) {
            getJedis().setex(key, seconds, value);
        } else if (environment.equals("production")) {
            getJedisCluster().set(key, value);
        }
    }

	public static String get(String key) {
		String environment = getEnvironment();

		if (environment.equals("development") || environment.equals("testing") || environment.equals("preproduction")) {
			return getJedis().get(key);
		} else if (environment.equals("production")) {
			return getJedisCluster().get(key);
		}

		return null;
	}

	public static void del(String key) {
		String environment = getEnvironment();

		if (environment.equals("development") || environment.equals("testing") || environment.equals("preproduction")) {
			getJedis().del(key);
		} else if (environment.equals("production")) {
			getJedisCluster().del(key);
		}
	}

	private static String getEnvironment() {
		String environment = System.getenv().get("ENVIRONMENT");
		if (environment == null) {
			environment = "development";
		}
		return environment;
	}
}
