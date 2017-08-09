package com.gomeplus.amp.ad.security.shiro.session;

import com.gomeplus.adm.common.util.SerializeUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * redis对session的操作基础类
 * @author lifei01
 */
public class RedisSessionDAO extends AbstractSessionDAO {

	private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
	/**
	 * shiro-redis的session对象前缀
	 */
	private RedisClient redisClient;
	
	/**
	 * keyPrefix 用于session
	 */
	private String keyPrefix = "amp_session:";
	
	/**
	 * 保存session到redis中
	 * @param session
	 * @throws UnknownSessionException
	 */
	@Override
	public void update(Session session) throws UnknownSessionException {
		this.saveSession(session);
	}
	
	/**
	 * 保存session到redis中
	 * @param session
	 * @throws UnknownSessionException
	 */
	private void saveSession(Session session) throws UnknownSessionException {
		if (session == null || session.getId() == null) {
			logger.error("session或者sessionId为空");
			return;
		}
		byte[] key = getByteKey(session.getId());
		byte[] value = SerializeUtil.serialize(session);
		session.setTimeout(redisClient.getExpire() * 1000);
		this.redisClient.set(key, value, redisClient.getExpire());
	}
	
	/**
	 * 根据session删除redis中的session
	 * @param session
	 */
	@Override
	public void delete(Session session) {
		if (session == null || session.getId() == null) {
			logger.error("session或者sessionId为空");
			return;
		}
		redisClient.del(this.getByteKey(session.getId()));
	}

	/**
	 * 获取redis中session集合
	 * @return session集合
	 */
	@Override
	public Collection<Session> getActiveSessions() {
		Set<Session> sessions = new HashSet<Session>();
		Set<byte[]> keys = redisClient.keys(this.keyPrefix + "*");
		if (keys != null && keys.size()>0) {
			for (byte[] key : keys) {
				Session session = (Session)SerializeUtil.deserialize(redisClient.get(key));
				sessions.add(session);
			}
		}
		return sessions;
	}

	/**
	 * 根据session获取一个新的标示
	 * @param session
	 * @return session集合
	 */
	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = this.generateSessionId(session);  
		this.assignSessionId(session, sessionId);
		this.saveSession(session);
		return sessionId;
	}

	/**
	 * 根据序列化的sessionId从redis中获取反序列化之后的session对象
	 * @param sessionId
	 * @return session
	 */
	@Override
	protected Session doReadSession(Serializable sessionId) {
		if(sessionId == null){
			logger.error("ssessionId为空");
			return null;
		}

		Session session = (Session)SerializeUtil.deserialize(redisClient.get(this.getByteKey(sessionId)));

		return session;
	}
	
	/**
	 * 获得byte[]型的key
	 * @param sessionId
	 * @return 
	 */
	private byte[] getByteKey(Serializable sessionId) {
		String preKey = this.keyPrefix + sessionId;
		return preKey.getBytes(StandardCharsets.UTF_8);
	}

	public RedisClient getRedisClient() {
		return redisClient;
	}

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
		//初始化redisClient
		this.redisClient.init();
	}

	/**
	 *返回keyFrefix
	 * @return prefix
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}

	/**
	 * 设置redis中的session的key
	 * @param keyPrefix 
	 */
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}
}
