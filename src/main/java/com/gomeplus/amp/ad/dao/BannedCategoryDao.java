package com.gomeplus.amp.ad.dao;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.BannedCategory;
import com.gomeplus.amp.ad.model.Keyword;

@Repository
@Transactional(value = "launchTransaction")
public class BannedCategoryDao extends HibernateDao<Keyword, Integer> {
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
	 * 获取禁投类目列表
	 * @return
	 */
	public List<BannedCategory> getAllBannedCategories() {
		Criteria criteria = this.getSession().createCriteria(BannedCategory.class);
		criteria.add(Restrictions.eq("status", BannedCategory.Status.NORMAL.getValue()));
		return criteria.list();
	}
}
