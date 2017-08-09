package com.gomeplus.amp.ad.dao;


import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户 dao
 * @author DèngBīn
 */
@Repository
@Transactional(value = "launchTransaction")
public class UserDao extends HibernateDao<User, Integer> {

	/**
	 * 初始化 设置sessionFactory
	 *
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}
}
