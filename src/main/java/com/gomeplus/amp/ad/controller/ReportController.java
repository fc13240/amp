package com.gomeplus.amp.ad.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.service.ReportService;

/**
 * 报表 Controller
 * @author DèngBīn
 */
@Controller
public class ReportController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private ReportService reportService;

	/**
	 * 查询 返利报表--投放计划 列表数据
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@RequestMapping(value = "/api/report/rebate/campaign", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfCampaignOfRebate(@RequestParam(name = "startTime", required = false) Long startTime,
													@RequestParam(name = "endTime", required = false) Long endTime,
													@RequestParam(name = "page", required = false) Integer page,
													@RequestParam(name = "number", required = false) Integer number,
													@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
													@RequestParam(name = "rebateType" , required = false, defaultValue = "0") Integer rebateType) {

		try {
			Map<String, Object> data = reportService.getReportOfCampaignOfRebate(startTime, endTime, page, number, campaignId,rebateType);

			if (data.get("error") != null) {
				return FeAjaxResponse.error(400, (String) data.get("error"));
			}

			return FeAjaxResponse.success(data, "查询成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 返利报表-投放计划 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception e) {
			logger.error("查询 返利报表-投放计划 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}

	/**
	 * 查询 返利报表--投放单元 列表数据
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@RequestMapping(value = "/api/report/rebate/flight", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfFlightOfRebate(@RequestParam(name = "startTime", required = false) Long startTime,
													@RequestParam(name = "endTime", required = false) Long endTime,
													@RequestParam(name = "page", required = false) Integer page,
													@RequestParam(name = "number", required = false) Integer number,
													@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
													@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
													@RequestParam(name = "rebateType" , required = false, defaultValue = "0") Integer rebateType) {

		try {
			Map<String, Object> data = reportService.getReportOfFlightOfRebate(startTime, endTime, page, number, campaignId, flightId, rebateType);

			if (data.get("error") != null) {
				return FeAjaxResponse.error(400, (String) data.get("error"));
			}

			return FeAjaxResponse.success(data, "查询成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 返利报表-投放单元 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception e) {
			logger.error("查询 返利报表-投放单元 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}

	/**
	 * 查询 返利报表--创意 列表数据
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@RequestMapping(value = "/api/report/rebate/material", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfMaterialOfRebate(@RequestParam(name = "startTime", required = false) Long startTime,
													@RequestParam(name = "endTime", required = false) Long endTime,
													@RequestParam(name = "page", required = false) Integer page,
													@RequestParam(name = "number", required = false) Integer number,
													@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
													@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
													@RequestParam(name = "materialId" , required = false, defaultValue = "0") Integer materialId,
													@RequestParam(name = "rebateType" , required = false, defaultValue = "0") Integer rebateType) {

		try {
			Map<String, Object> data = reportService.getReportOfMaterialOfRebate(startTime, endTime, page, number, campaignId, flightId, materialId,rebateType);

			if (data.get("error") != null) {
				return FeAjaxResponse.error(400, (String) data.get("error"));
			}

			return FeAjaxResponse.success(data, "查询成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 返利报表-创意 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception e) {
			logger.error("查询 返利报表-创意 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}

	/**
	 * 查询 效果报表--订单效果 列表数据
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@RequestMapping(value = "/api/report/effect/order", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfOrderOfEffect(Long startTime, Long endTime, Integer page, Integer number,
			@RequestParam(name = "productLine", required = false, defaultValue = "2") Integer productLine) {

		try {
			Map<String, Object> data = reportService.getReportOfOrderOfEffect(startTime, endTime, page, number, productLine);

			if (data.get("error") != null) {
				return FeAjaxResponse.error(400, (String) data.get("error"));
			}

			return FeAjaxResponse.success(data, "查询成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 效果报表--订单效果 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception e) {
			logger.error("查询 效果报表--订单效果 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}


	/**
	 * 查询 效果报表--订单汇总效果 列表数据
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@RequestMapping(value = "/api/report/effect/orderSummary", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfOrderSummaryOfEffect(
			@RequestParam(name = "startTime", required = false,defaultValue = "0") Long startTime,
			@RequestParam(name = "endTime", required = false,defaultValue = "0") Long endTime,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "number", required = false, defaultValue = "30") Integer number,
			@RequestParam(name = "orderDays", required = false, defaultValue = "15") Integer orderDays,
			@RequestParam(name = "productLine", required = false, defaultValue = "2") Integer productLine) {

		try {
			Map<String, Object> data = reportService.getReportOfOrderSummaryOfEffect(startTime, endTime,orderDays, page, number, productLine);

			if (data.get("error") != null) {
				return FeAjaxResponse.error(400, (String) data.get("error"));
			}

			return FeAjaxResponse.success(data, "查询成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 效果报表--订单汇总效果 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception e) {
			logger.error("查询 效果报表--订单汇总效果 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}


	/**
	 * 广告报表--投放计划 接口
	 * @param deviceType 设备类型
	 * @param campaignId 投放计划id
	 * @param startTime 开始时间戳
	 * @param endTime 结束时间戳
	 * @param page 页码
	 * @param number 每页行数
	 * @param productLine 广告售卖类型(支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 */
	@RequestMapping(value = "/api/report/anice/campaign", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfCampaign(
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "orderDays", required = false, defaultValue = "15") Integer orderDays,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "productLine", required = false,defaultValue = "2") Integer productLine,
			@RequestParam(name = "number", required = false, defaultValue = "30") Integer number) {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			data = reportService.getReportOfCampaign(deviceType, campaignId, startTime , endTime,orderDays, page, number ,productLine);

		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 广告报表--投放计划 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("查询 广告报表--投放计划 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(400, "查询失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "查询成功");
	}


	/**
	 * 广告报表--投放单元 接口
	 * @param deviceType 设备类型
	 * @param campaignId 投放计划id
	 * @param flightId 投放单元id
	 * @param startTime 开始时间戳
	 * @param endTime 结束时间戳
	 * @param page 页码
	 * @param number 每页行数
	 * @param productLine 广告售卖类型(支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 */
	@RequestMapping(value = "/api/report/anice/flight", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfFlight(
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "orderDays", required = false, defaultValue = "15") Integer orderDays,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "number", required = false, defaultValue = "30") Integer number,
			@RequestParam(name = "productLine", required = false, defaultValue = "2") Integer productLine) {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			data = reportService.getReportOfFlight(deviceType, campaignId, flightId, startTime, endTime, orderDays, page, number,productLine);

		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 广告报表--投放单元 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("查询 广告报表--投放单元 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(400, "查询失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "查询成功");
	}

	/**
	 * 广告报表--创意 接口
	 * @param deviceType 设备类型
	 * @param campaignId 投放计划id
	 * @param flightId 投放单元id
	 * @param materialId 素材id
	 * @param startTime 开始时间戳
	 * @param endTime 结束时间戳
	 * @param page 页码
	 * @param number 每页行数
	 * @param productLine 广告售卖类型(支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 */
	@RequestMapping(value = "/api/report/anice/material", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfMaterial(
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "materialId" , required = false, defaultValue = "0") Integer materialId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "orderDays", required = false, defaultValue = "15") Integer orderDays,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "number", required = false, defaultValue = "30") Integer number,
			@RequestParam(name = "productLine", required = false, defaultValue = "2") Integer productLine) {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			data = reportService.getReportOfMaterialId(deviceType, campaignId, flightId, materialId, startTime, endTime, orderDays, page, number,productLine);

		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询 广告报表--创意 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("查询 广告报表--创意 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(400, "查询失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "查询成功");
	}

	@RequestMapping(value = "/api/report/anice/keywords", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportOfKeywords(
			@RequestParam(name = "keywordId", required = false, defaultValue = "0") Integer keywordId,
			@RequestParam(name = "deviceType" , required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId", required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "orderDays", required = false, defaultValue = "15") Integer orderDays,
			@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(name = "number", required = false, defaultValue = "30") Integer number) {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			data = reportService.getReportOfKeyword(keywordId, deviceType, campaignId, flightId, startTime, endTime,orderDays, page, number);

		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("查询关键词报表失败! " + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("查询关键词报表失败! " + exception.getMessage());
			return FeAjaxResponse.error(400, "查询失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "查询成功");
	}
}
