package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.Flight;

/**
 * 广告 Dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional(value = "adTransaction")
public class AdvertisementDao extends HibernateDao<Advertisement, Integer> {

	/**
	 * 初始化 设置sessionFactory
	 * 
	 * @param sessionFactory
	 */
	@Autowired
	public void init(@Qualifier("adSessionFactory") SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	/**
	 * 获取全部在线的广告
	 * 
	 * @return 广告集合
	 */
	public List<Advertisement> getOnlineAdvertisements() {
		Criteria criteria = this.getSession().createCriteria(Advertisement.class);
		criteria.add(Restrictions.eq("status", Advertisement.Status.ONLINE.getValue()));
		return criteria.list();
	}

	/**
	 * 根据advertisementIds获取多条广告信息
	 * 
	 * @param advertisementIds
	 * @return
	 */
	public List<Advertisement> getAdvertisementsByAdvertisementIds(List<Integer> advertisementIds) {
		if (advertisementIds.isEmpty()) {
			return new ArrayList<Advertisement>();
		}

		Criteria criteria = this.getSession().createCriteria(Advertisement.class);
		criteria.add(Restrictions.in("advertisementId", advertisementIds));
		return criteria.list();
	}

	/**
	 * 查询多个广告位对应的有效
	 * 
	 * @param publisherIds
	 * @return
	 */
	public List<Advertisement> getAdvertisementsBySlotIds(List<Integer> slotIds) {
		Criteria criteria = this.getSession().createCriteria(Advertisement.class);
		criteria.add(Restrictions.in("slotId", slotIds)).add(Restrictions.eq("status", Advertisement.Status.ONLINE.getValue()));
		return criteria.list();
	}


	/**
	 * 广告列表
	 * 
	 * @param pagination
	 * @param keyword
	 * @param slotIds
	 * @param width
	 * @param height
	 * @return
	 */
	public List<Advertisement> getAdvertisementsByPagination(Pagination pagination, String keyword,
									List<Integer> slotIds, Integer width, Integer height, Integer saleMode) {

		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.gt("status", Flight.Status.DELETE.getValue()));
		criterions.add(Restrictions.eq("saleMode", saleMode));

		if (!StringUtils.isEmpty(keyword)) {
			criterions.add(Restrictions.like("name", keyword, MatchMode.ANYWHERE));
		}
		if (0 != width) {
			criterions.add(Restrictions.eq("width", width));
		}
		if (0 != height) {
			criterions.add(Restrictions.eq("height", height));
		}
		if (!CollectionUtils.isEmpty(slotIds)) {
			criterions.add(Restrictions.in("slotId", slotIds));
		}

		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
		Projection projection = criteriaImpl.getProjection();

		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;

		pagination.setTotalCount(totalCount);

		criteria.setProjection(projection);

		criteria.setFirstResult(pagination.getOffset());
		criteria.setMaxResults(pagination.getNumber());
		criteria.addOrder(Order.desc("advertisementId"));

		return criteria.list();
	}

	public Advertisement getAdvertisementByAdvertisementId(Integer advertisementId) {
		Criteria criteria = this.getSession().createCriteria(Advertisement.class);
		criteria.add(Restrictions.eq("advertisementId", advertisementId));
		return (Advertisement) criteria.uniqueResult();
	}
}
