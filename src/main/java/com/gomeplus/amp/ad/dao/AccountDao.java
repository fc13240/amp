package com.gomeplus.amp.ad.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Account;
import com.gomeplus.amp.ad.model.Account.Status;
/**
 * 账户Dao
 * @author suna01
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class AccountDao extends HibernateDao<Account, Integer> {
	
	/**
	 * 初始化 设置sessionFactory
	 * 
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	/**
	 * 获取账号信息
	 * @param userId
	 * @return
	 */
	public List<Account> getAccountByAccountId(Integer userId) {
		Criteria criteria = this.getSession().createCriteria(Account.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("status", Account.Status.NORMAL.getValue()));
		return criteria.list();
	} 
	
	/**
	 * 根据userId、type查询相应类型的账户信息
	 * @param userId
	 * @param type
	 * @return
	 */
	public Account getAccountByType(Integer userId, Integer type){
		Criteria criteria = this.getSession().createCriteria(Account.class);
		criteria.add(Restrictions.eq("userId", userId));
		criteria.add(Restrictions.eq("type", type));
		criteria.add(Restrictions.eq("status", Account.Status.NORMAL.getValue()));
		return (Account)criteria.uniqueResult();
	}
	
}
