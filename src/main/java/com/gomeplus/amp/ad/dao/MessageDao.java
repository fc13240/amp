package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.model.Message;

/**
 * 消息Dao
 * 
 * @author lifei01
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class MessageDao extends HibernateDao<Message, Integer> {
	
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
	 * 站内信分页查询
	 * @param pagination 分页类
	 * @param userId 用户id
	 * @param keyword 关键字
	 * @param type 类型1系统公告 2资金变动 3账单
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	public List<Message> getMessagesByUserId(Pagination pagination, Integer userId, Integer type,
		String keyword, Date startTime, Date endTime) {

		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("type", type));
		criterions.add(Restrictions.eq("userId", userId));
		if (!StringUtils.isEmpty(keyword)) {
			criterions.add(Restrictions.like("title", keyword, MatchMode.ANYWHERE));
		}
		Date zeroTime = new Date(0);
		if (startTime.after(zeroTime) && endTime.after(zeroTime) && !startTime.equals(endTime)) {
			criterions.add(Restrictions.ge("createTime", startTime));
			criterions.add(Restrictions.le("createTime", endTime));
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
		criteria.addOrder(Order.desc("messageId"));
		return criteria.list();
	}

	/**
	 * 获取最新的站内信
	 * @param userId 用户id
	 * @param number 数量
	 * @return
	 */
	public List<Message> getLatestMessagesByUserId(Integer userId, Integer number) {

		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("userId", userId));
		Criteria criteria = createCriteria(criterions.toArray(new Criterion[criterions.size()]));
		criteria.setMaxResults(number);
		criteria.addOrder(Order.desc("messageId"));

		return criteria.list();
	}
}
