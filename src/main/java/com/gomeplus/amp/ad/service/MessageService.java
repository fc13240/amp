package com.gomeplus.amp.ad.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.web.Pagination;

import com.gomeplus.amp.ad.dao.MessageDao;
import com.gomeplus.amp.ad.model.Message;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;

/**
 * 消息service
 * 
 * @author lifei01
 *
 */
@Service
@Transactional(readOnly = true)
public class MessageService extends BaseService<Message, Integer> {
	
	@Autowired
	private MessageDao messageDao;
	
	@Override
	public HibernateDao<Message, Integer> getEntityDao() {
		return messageDao;
	}
	
	/**
	 * 站内信查询
	 * @param pagination 分页类
	 * @param type 类型1系统公告 2资金变动 3账单
	 * @param keyword 关键字
	 * @param startTimeValue 开始时间（时间戳）
	 * @param endTimeValue 结束时间（时间戳）
	 * @return
	 */
	public Map<String, Object> getMessages(Pagination pagination, Integer type, String keyword,
								Long startTimeValue, Long endTimeValue) {

		Integer userId = PrincipalUtil.getUserId();

		Date startTime = new Date(new Timestamp(startTimeValue).getTime());
		Date endTime = new Date(new Timestamp(endTimeValue).getTime());

		Map<String, Object> data = new LinkedHashMap<String, Object>();

		List<Message> messages = messageDao.getMessagesByUserId(pagination, userId, type, keyword, startTime, endTime);

		List<Map<String, Object>> messagesList = new ArrayList<Map<String, Object>>();
		for (Message message : messages) {
			Map<String, Object> messageMap = new LinkedHashMap<String, Object>();
			messageMap.put("messageId", message.getMessageId());
			messageMap.put("title", message.getTitle());
			messageMap.put("content", message.getContent());
			messageMap.put("type", message.getType());
			messageMap.put("isRead", message.getIsRead());
			messageMap.put("time", new Timestamp(message.getCreateTime().getTime()));
			messagesList.add(messageMap);
		}

		data.put("page", pagination.getCurrentPage());
		data.put("number", pagination.getNumber());
		data.put("totalCount", pagination.getTotalCount());
		data.put("list", messagesList);

		return data;
	}

	/**
	 * 根据messageId查询站内信
	 * @param messageId 消息id
	 * @return
	 */
	public Map<String, Object> getMessageByMessageId(Integer messageId) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		// @todo check userId
		Integer userId = PrincipalUtil.getUserId();

		Message message = messageDao.getUniqueBy("messageId", messageId);
		data.put("messageId", message.getMessageId());
		data.put("title", message.getTitle());
		data.put("content", message.getContent());
		data.put("isRead", message.getIsRead());
		data.put("time", new Timestamp(message.getCreateTime().getTime()));

		return data;
	}

	/**
	 * 获取最近的消息
	 * @param number 消息条数
	 */
	public List<Map<String, Object>> getLatestMessages(Integer number) {
		// @todo 
		Integer userId = PrincipalUtil.getUserId();

		List<Message> messages = messageDao.getLatestMessagesByUserId(userId, number);

		List<Map<String, Object>> messagesList = new ArrayList<Map<String, Object>>();
		for (Message message : messages) {
			Map<String, Object> messageMap = new LinkedHashMap<String, Object>();
			messageMap.put("messageId", message.getMessageId());
			messageMap.put("title", message.getTitle());
			messageMap.put("content", message.getContent());
			messageMap.put("type", message.getType());
			messageMap.put("isRead", message.getIsRead());
			messageMap.put("time", new Timestamp(message.getCreateTime().getTime()));
			messagesList.add(messageMap);
		}

		return messagesList;
	}
}
