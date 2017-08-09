package com.gomeplus.amp.ad.security.shiro.util;

import com.gomeplus.amp.ad.security.shiro.model.Principal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class PrincipalUtil {

	/**
	 * 获取授权对象
	 * @return Subject
	 */
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取当前登录者的session
	 * @return Session
	 */
	public static Session getSession() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null) {
				session = subject.getSession();
			}
			if (session != null) {
				return session;
			}
		} catch (InvalidSessionException exception) {

		}
		return null;
	}
	
	/**
	 * 获取当前登录对象
	 * @return Principal
	 */
	public static Principal getPrincipal() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null) {
				return principal;
			}
		} catch (UnavailableSecurityManagerException exception) {
			
		} catch (InvalidSessionException exception) {
			
		}
		return null;
	}

	/**
	 * 获取当前登录者的账号ID
	 * @return userId
	 */
	public static Integer getUserId() {
		return PrincipalUtil.getPrincipal().getUserId();
	}

	/**
	 * 获取当前登录者的账号名
	 * @return name
	 */
	public static String getName() {
		return PrincipalUtil.getPrincipal().getName();
	}

	/**
	 * 获取当前登录者的广告主id
	 * @return dspAdvertiserId
	 */
	public static Integer getDspAdvertiserId() {
		return PrincipalUtil.getPrincipal().getDspAdvertiserId();
	}

	/**
	 * 获取当前登录者的广告账户id
	 * @return adAccountId
	 */
	public static Integer getDspAdAccountId() {
		return PrincipalUtil.getPrincipal().getDspAdAccountId();
	}

	/**
	 * 获取当前登录者的返利账户id
	 * @return rebateAccountId
	 */
	public static Integer getDspRebateAccountId() {
		return PrincipalUtil.getPrincipal().getDspRebateAccountId();
	}

	/**
	 * 
	 * 
	 */
	public static void setDspAdvertiserId(Integer dspAdvertiserId) {
		PrincipalUtil.getPrincipal().setDspAdvertiserId(dspAdvertiserId);
	}

}