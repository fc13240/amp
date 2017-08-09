package com.gomeplus.amp.ad.dao;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.PageTemplate;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面模板 Dao
 * @author DèngBīn
 */
@Repository
@Transactional(value="launchTransaction")
public class PageTemplateDao extends HibernateDao<PageTemplate, Integer> {
	/**
	 * 初始化 设置sessionFactory
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	/**
	 * 获取页面模板列表
	 * @param platform 平台(设置类型)
	 * @return
	 */
	public List<PageTemplate> getListOfPageTemplate(Integer platform) {

		List<Criterion> criterions = new ArrayList<Criterion>();

		criterions.add(Restrictions.eq("platform", platform));
		criterions.add(Restrictions.eq("status", PageTemplate.Status.ONLINE.getValue()));

		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		criteria.setProjection(projection);

		return criteria.list();
	}

	/**
	 * 查询所有有效自建页模板
	 * 
	 * @return
	 */
	public List<PageTemplate> getWebpageTemplates() {
		Criteria criteria = this.getSession().createCriteria(PageTemplate.class);
		criteria.add(Restrictions.gt("status", PageTemplate.Status.DELETE.getValue()));
		return criteria.list();
	}

	/**
	 * 查询所有有效自建页模板
	 * 
	 * key: WebpageTemplateId 
	 * value: WebpageTemplate
	 * 
	 * @return
	 */
	// TODO 迁移到manager
	public Map<Integer, PageTemplate> getWebpageTemplateMap() {
		Map<Integer, PageTemplate> webpageTemplateMap = new LinkedHashMap<Integer, PageTemplate>();
		List<PageTemplate> webpageTemplates = getWebpageTemplates();
		if (CollectionUtils.isEmpty(webpageTemplates)) {
			return webpageTemplateMap;
		}
		for (PageTemplate webpageTemplate : webpageTemplates) {
			webpageTemplateMap.put(webpageTemplate.getPageTemplateId(), webpageTemplate);
		}
		return webpageTemplateMap;
	}

}
