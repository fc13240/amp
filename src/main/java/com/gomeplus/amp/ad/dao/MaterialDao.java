package com.gomeplus.amp.ad.dao;

import java.util.ArrayList;
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
import com.gomeplus.amp.ad.model.Material;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;

/**
 * 素材 Dao
 * 
 * @author wangwei01
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class MaterialDao extends HibernateDao<Material, Integer> {
	
	private Logger logger = LoggerFactory.getLogger(MaterialDao.class);

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
	 * 查询素材分页列表
	 * 
	 * @param pagination
	 * @param flightId
	 * @param keyword
	 * @param status
	 * @return
	 */
	public List<Material> getMaterialsByPagination(Pagination pagination, Integer flightId, String keyword,
			Integer state) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.gt("status", Material.Status.DELETE.getValue()));
		criterions.add(Restrictions.eq("flightId", flightId));
		criterions.add(Restrictions.eq("userId", PrincipalUtil.getUserId()));

		if (!StringUtils.isEmpty(keyword)) {
			criterions.add(Restrictions.like("name", keyword, MatchMode.ANYWHERE));
		}

		if (state != 0) {
			if (state == Material.State.APPROVING.getValue()) {
				criterions.add(Restrictions.eq("approveStatus", Material.ApproveStatus.APPROVING.getValue()));
			} else if (state == Material.State.REJECT.getValue()) {
				criterions.add(Restrictions.eq("approveStatus", Material.ApproveStatus.REJECT.getValue()));
			} else {
				criterions.add(Restrictions.eq("approveStatus", Material.ApproveStatus.APPROVED.getValue()));
				if (state == Material.State.NORMAL.getValue()) {
					criterions.add(Restrictions.eq("status", Material.Status.NORMAL.getValue()));
				} else if (state == Material.State.SUSPEND.getValue()) {
					criterions.add(Restrictions.eq("status", Material.Status.SUSPEND.getValue()));
				}
			}
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
		criteria.addOrder(Order.desc("createTime"));

		return criteria.list();
	}

	/**
	 * 根据投放单Id集合查询所有的创意
	 * @param flightIds
	 * @return
	 */
	public List<Material> getMaterialsByFlightIds(List<Integer> flightIds) {
		Criteria criteria = this.getSession().createCriteria(Material.class);
		criteria.add(Restrictions.gt("status", Material.Status.DELETE.getValue()));
		criteria.add(Restrictions.in("flightId", flightIds));
		return criteria.list();
	}
	
	/**
	 * 查询某投放单元下有效创意个数
	 * @param flightId
	 * @return
	 */
	public Long getMaterialTotalByFlightId(Integer flightId) {
		Criteria criteria = this.getSession().createCriteria(Material.class);
		criteria.add(Restrictions.gt("status", Material.Status.DELETE.getValue()));
		criteria.add(Restrictions.eq("flightId", flightId));
		Long totalCountObject = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
		Long totalCount = (totalCountObject != null) ? totalCountObject : 0;
		return totalCount;
	}

	/**
	 * 批量删除创意 （纯操作数据库，不关心素材状态）
	 * 已经将投放计划、投放单元的删除状态同步给ads,约定ads也做级联删除
	 * 
	 * @param materials
	 * @throws Exception
	 */
	public void batchDeleteMaterials(List<Material> materials) throws Exception {
		Session session = this.getSession();
		Integer i = 1;
		for (Material material : materials) {
			session.update(material);
			if (++i % 10 == 0) {
				session.flush();
				session.clear();
			}
		}
		session.flush();
		session.clear();
	}
	
	/**
	 * 根据flightId查询素材是否存在
	 * @param flightId
	 * @return
	 */
	public List<Material> getMaterialBySkuId(Integer flightId) {
		Criteria criteria = this.getSession().createCriteria(Material.class);
		criteria.add(Restrictions.in("flightId", flightId));
		criteria.add(Restrictions.gt("status", Material.Status.DELETE.getValue()));
		return criteria.list();
	}
	
	/**
	 * 根据skuId、flightId查询创意列表
	 * @param skuId
	 * @param flightId
	 * @return
	 */
	public List<Material> getMaterialsBySkuIdFlightId(String skuId, Integer flightId) {
		logger.info("getMaterialsBySkuIdFlightId skuId: " + skuId + ", flightId:" + flightId);
		Criteria criteria = this.getSession().createCriteria(Material.class);
		criteria.add(Restrictions.gt("status", Material.Status.DELETE.getValue()));
		criteria.add(Restrictions.like("promotionId", skuId, MatchMode.END));
		criteria.add(Restrictions.eq("flightId", flightId));
		List<Material> materials = criteria.list();
		logger.info("getMaterialsBySkuIdFlightId materials: " + materials);
		return materials;
	}
}
