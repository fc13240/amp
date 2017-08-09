package com.gomeplus.amp.ad.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.amp.ad.model.Video;
/**
 * 视频Dao
 * @author zhangqian
 *
 */
@Repository
@Transactional(value = "launchTransaction")
public class VideoDao extends HibernateDao<Video, Integer> {
	
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
	 * 根据videoId获取视频详情信息
	 * @param videoId
	 * @return
	 */
	public Video getVideoByVideoId(Long videoId){
		Criteria criteria = this.getSession().createCriteria(Video.class);
		criteria.add(Restrictions.eq("videoId", videoId));
		return (Video)criteria.uniqueResult();
	}
	
}
