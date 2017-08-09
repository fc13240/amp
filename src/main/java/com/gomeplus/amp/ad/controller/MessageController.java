package com.gomeplus.amp.ad.controller;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.adm.common.web.Pagination;

import com.gomeplus.amp.ad.model.Message;
import com.gomeplus.amp.ad.service.MessageService;

/**
 * 消息 controller
 * 
 * @author lifei01
 */
@Controller
public class MessageController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private MessageService messageService;

	
	/**
	 * 站内信接口
	 * @param keyword 关键字
	 * @param type 类型1系统公告 2资金变动 3账单
	 * @param startTime 开始时间（时间戳）
	 * @param endTime 结束时间（时间戳）
	 * @param page 页码
	 * @param number 每页行数
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/messages", method = RequestMethod.GET)
	public FeAjaxResponse getMessages(@RequestParam(value = "type", required = true, defaultValue = "1") Integer type,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") Long startTime, 
			@RequestParam(name = "endTime", required = false, defaultValue = "0") Long endTime,
			HttpServletRequest request) {

		Pagination pagination = getPagination(request);
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = messageService.getMessages(pagination, type, keyword, startTime, endTime);

		} catch (Exception exception) {
			logger.error("getMessages", exception);
			return FeAjaxResponse.error(400, "查询失败！" + exception.getMessage());
		}
			
		return FeAjaxResponse.success(data, "获取信息成功！");
	}
	
	/**
	 * 站内信详情接口
	 * @param messageId 消息id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/message", method = RequestMethod.GET)
	public FeAjaxResponse getMessage(@RequestParam(value = "messageId", required = true) Integer messageId) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = messageService.getMessageByMessageId(messageId);

		} catch (Exception exception) {
			logger.error("get message failed ", exception);
			return FeAjaxResponse.error(400, "获取站内信失败！" + exception.getMessage());
		}
			
		return FeAjaxResponse.success(data, "获取站内信成功！");
	}
}
