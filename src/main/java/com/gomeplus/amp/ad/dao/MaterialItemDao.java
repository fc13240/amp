package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Keyword;
import com.gomeplus.amp.ad.model.MaterialItem;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;


@Repository
@Transactional("launchTransaction")
public class MaterialItemDao extends HibernateDao<MaterialItem, Integer> {

	private Logger logger = LoggerFactory.getLogger(MaterialItemDao.class);

	/**
	 * 初始化 设置sessionFactory
	 *
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public List<MaterialItem> getMaterialItemsByMaterialId(Integer materialId) {
		Criteria criteria = this.getSession().createCriteria(MaterialItem.class);
		criteria.add(Restrictions.eq("materialId", materialId)).add(Restrictions.gt("status", MaterialItem.Status.DELETE.getValue()));
		return criteria.list();
	}
	
	public void batchDeleteMaterialItems(List<MaterialItem> materialItems) throws Exception {
		Session session = this.getSession();
		Integer i = 1;
		for (MaterialItem materialItem : materialItems) {
			materialItem.setStatus(MaterialItem.Status.DELETE.getValue());
			session.update(materialItem);
			if (++i % 10 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
	}

	public void batchSaveMaterialItems(List<MaterialItem> materialItems) throws Exception {
		Session session = this.getSession();
		Integer i = 1;
		for (MaterialItem materialItem : materialItems) {
			Date currentTime = new Date();
			materialItem.setCreateTime(currentTime);
			materialItem.setUpdateTime(currentTime);
			session.save(materialItem);
			if (++i % 10 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
	}
}
