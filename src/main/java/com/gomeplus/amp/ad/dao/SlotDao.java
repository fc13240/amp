package com.gomeplus.amp.ad.dao;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Publisher;
import com.gomeplus.amp.ad.model.Slot;

/**
 * 广告位dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional("adTransaction")
public class SlotDao extends HibernateDao<Slot, Integer> {

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
	 * 查询多个媒体对应的所有有效广告位
	 */
	public List<Slot> getSlotsByPublisherIds(List<Integer> publisherIds) {
		Criteria criteria = this.getSession().createCriteria(Slot.class);
		criteria.add(Restrictions.in("publisherId", publisherIds)).add(Restrictions.eq("status", Slot.Status.ONLINE.getValue()));
		return criteria.list();
	}

	/**
	 * 查询某媒体的所有有效广告位
	 * @param publisherId
	 * @return
	 */
	public List<Slot> getSlotsByPublisherId(Integer publisherId) {
		Criteria criteria = this.getSession().createCriteria(Slot.class);
		criteria.add(Restrictions.eq("publisherId", publisherId)).add(Restrictions.eq("status", Slot.Status.ONLINE.getValue()));
		return criteria.list();
	}

	/**
	 * 批量查询广告位
	 * @param slotIds
	 * @return
	 */
	public List<Slot> getSlotsBySlotIds(List<Integer> slotIds) {
		if (slotIds.isEmpty()) {
			return new ArrayList<Slot>();
		}
		Criteria criteria = this.getSession().createCriteria(Slot.class);
		criteria.add(Restrictions.in("slotId", slotIds)).add(Restrictions.eq("status", Slot.Status.ONLINE.getValue()));
		return criteria.list();
	}
	
	/**
	 * 查询某媒体的所有有效广告位
	 * @param publisherId
	 * @return
	 */
	public Slot getSlotBySlotId(Integer slotId) {
		Criteria criteria = this.getSession().createCriteria(Slot.class);
		criteria.add(Restrictions.eq("slotId", slotId));
		return (Slot)criteria.uniqueResult();
	}

}
