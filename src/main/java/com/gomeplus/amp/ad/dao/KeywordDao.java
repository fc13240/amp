package com.gomeplus.amp.ad.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Keyword;

@Repository
@Transactional(value = "launchTransaction")
public class KeywordDao extends HibernateDao<Keyword, Integer> {
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
	 * 根据flightId获取对应的关键词信息
	 * 
	 * @param flightId
	 * @return
	 */
	public List<Keyword> getKeywordsByFlightId(Integer flightId) {
		Criteria criteria = this.getSession().createCriteria(Keyword.class);
		criteria.add(Restrictions.eq("flightId", flightId));
		criteria.add(Restrictions.eq("status", Keyword.Status.NORMAL.getValue()));
		criteria.addOrder(Order.asc("pcBid"));
		return criteria.list();
	}
	
	/**
	 * 批量添加关键词
	 * @param keywords
	 * @throws Exception
	 */
	public void batchSaveKeywords(List<Keyword> keywords) throws Exception {
		Session session = this.getSession();
		Integer i = 1;
		for (Keyword keyword : keywords) {
			Date currentTime = new Date();
			keyword.setCreateTime(currentTime);
			keyword.setUpdateTime(currentTime);
			session.save(keyword);
			if (++i % 10 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
	}
	
	/**
	 * 批量删除关键词
	 * @param materials
	 * @throws Exception
	 */
	public void batchDeleteKeywords(List<Keyword> keywords) throws Exception {
		Session session = this.getSession();
		Integer i = 1;
		for (Keyword keyword : keywords) {
			keyword.setStatus(Keyword.Status.DELETE.getValue());
			session.update(keyword);
			if (++i % 10 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
	}
}
