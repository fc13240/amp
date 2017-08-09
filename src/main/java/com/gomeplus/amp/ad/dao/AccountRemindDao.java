package com.gomeplus.amp.ad.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.AccountRemind;

/**
 * 账户余额提醒Dao
 *
 * @author baishen
 */
@Repository
@Transactional(value = "launchTransaction")
public class AccountRemindDao extends HibernateDao<AccountRemind, Integer> {
	
	/**
	 * 初始化 设置sessionFactory
	 * 
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
}
