package com.gomeplus.amp.ad.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;











import javax.servlet.http.HttpServletResponse;

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
import com.gomeplus.amp.ad.service.ExportReportService;
import com.gomeplus.amp.ad.service.ReportService;

/**
 * 导出excelcontroller 
 * @author xiaogengen
 *
 */
@Controller
public class ExportReportController extends BaseController{
	private static Logger logger = LoggerFactory.getLogger(ExportReportController.class);
	//将时间转化成 固定时间格式
	private static DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private ExportReportService exportReportService;
	/**
	 * 导出投放计划报表
	 * @param deviceType
	 * @param campaignId
	 * @param startTime
	 * @param endTime
	 */
			
	@RequestMapping(value = "/api/report/anice/campaign/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportExcelOfCampaign(
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "productLine", required = false,defaultValue = "2") Integer productLine,
			@RequestParam(name = "orderDays", required = false,defaultValue = "2") Integer orderDays,
			HttpServletResponse response) {
		Map<String, Object> data = null;
			String fileName = "广告报表_投放计划_";
			//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfCampaign(deviceType, campaignId, startTime , endTime, orderDays, 1, 1000,productLine);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 广告报表--投放计划 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 广告报表--投放计划 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	/**
	 * 导出广告报表投放单元
	 * @param deviceType
	 * @param campaignId
	 * @param flightId
	 * @param startTime
	 * @param endTime
	 * @param response
	 */
	@RequestMapping(value = "/api/report/anice/flight/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportExcelOfFlight(
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "productLine", required = false,defaultValue = "2") Integer productLine,
			@RequestParam(name = "orderDays", required = false,defaultValue = "2") Integer orderDays,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "广告报表_投放单元_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfFlight(deviceType, campaignId,flightId, startTime , endTime, orderDays, 1, 1000,productLine);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 广告报表--投放单元 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 广告报表--投放单元 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
	/**
	 * 导出广告报表 -创意
	 * @param deviceType
	 * @param campaignId
	 * @param flightId
	 * @param materialId
	 * @param startTime
	 * @param endTime
	 * @param response
	 */
	@RequestMapping(value = "/api/report/anice/material/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportExcelOfMaterial(
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "materialId" , required = false, defaultValue = "0") Integer materialId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "productLine", required = false,defaultValue = "2") Integer productLine,
			@RequestParam(name = "orderDays", required = false,defaultValue = "2") Integer orderDays,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "广告报表_创意_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfMaterialId(deviceType, campaignId,flightId,materialId, startTime , endTime,orderDays, 1, 1000, productLine);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功"); 
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 广告报表--创意 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 广告报表--创意 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
			
		}

	}
	
	@RequestMapping(value = "/api/report/anice/keywords/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getReportExcelOfKeywords(
			@RequestParam(name = "keywordId", required = false, defaultValue = "0") Integer keywordId,
			@RequestParam(name = "deviceType", required = false, defaultValue = "0") String deviceType,
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "productLine", required = false,defaultValue = "2") Integer productLine,
			@RequestParam(name = "orderDays", required = false,defaultValue = "2") Integer orderDays,
			HttpServletResponse response) {
		Map<String, Object> data = null;
			String fileName = "广告报表_关键词_";
			//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfKeyword(keywordId, deviceType, campaignId, flightId, startTime, endTime, orderDays, 1, 1000);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 广告报表--关键词失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 广告报表--关键词失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
	/**
	 * 导出返利 -- 投放计划
	 * @param campaignId
	 * @param startTime
	 * @param endTime
	 * @param response
	 */
	@RequestMapping(value = "/api/report/rebate/campaign/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getRebateReportExcelOfCampaign(
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "rebateType" , required = false, defaultValue = "0") Integer rebateType,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "返利报表_投放计划_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfCampaignOfRebate(startTime , endTime, 1, 1000,campaignId,rebateType);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 返利报表--投放计划 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 返利报表--投放计划 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
	/**
	 * 导出返利 -- 投放单元excel
	 * @param campaignId
	 * @param flightId
	 * @param startTime
	 * @param endTime
	 * @param response
	 */
	@RequestMapping(value = "/api/report/rebate/flight/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getRebateReportExcelOfFlight(
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "rebateType" , required = false, defaultValue = "0") Integer rebateType,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "返利报表_投放单元_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfFlightOfRebate(startTime , endTime, 1, 1000,campaignId,flightId,rebateType);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 返利报表--投放单元 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 返利报表--投放单元 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
	/**
	 * 导出返利--创意报表excel
	 * @param campaignId
	 * @param startTime
	 * @param endTime
	 * @param flightId
	 * @param materialId
	 * @param response
	 */
	@RequestMapping(value = "/api/report/rebate/material/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getRebateReportExcelOfMaterial(
			@RequestParam(name = "campaignId" , required = false, defaultValue = "0") Integer campaignId,
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "flightId" , required = false, defaultValue = "0") Integer flightId,
			@RequestParam(name = "materialId" , required = false, defaultValue = "0") Integer materialId,
			@RequestParam(name = "rebateType" , required = false, defaultValue = "0") Integer rebateType,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "返利报表_创意_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfMaterialOfRebate(startTime , endTime, 1, 1000,campaignId,flightId,materialId, rebateType);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 返利报表--创意 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 返利报表--创意 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
	/**
	 * 导出效果-订单效果报表excel
	 * @param startTime
	 * @param endTime
	 * @param response
	 */
	@RequestMapping(value = "/api/report/effect/order/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getEffectReportExcelOfOrder(
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "productLine", required = false, defaultValue = "2") Integer productLine,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "效果报表_订单_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfOrderOfEffect(startTime , endTime, 1, 1000, productLine);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 订单效果报表 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 订单效果报表 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
	/**
	 * 导出效果-订单汇总效果报表excel
	 * @param startTime
	 * @param endTime
	 * @param response
	 */
	@RequestMapping(value = "/api/report/effect/summary/export", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getEffectReportExcelOfOrderSummary(
			@RequestParam(name = "startTime", required = false, defaultValue = "0") long startTime,
			@RequestParam(name = "endTime", required = false, defaultValue = "0") long endTime,
			@RequestParam(name = "productLine", required = false, defaultValue = "2") Integer productLine,
			@RequestParam(name = "orderDays", required = false,defaultValue = "2") Integer orderDays,
			HttpServletResponse response) {
		Map<String, Object> data = null;
		String fileName = "效果报表_订单汇总_";
		//DateFormat format= new SimpleDateFormat("yyyyMMddHHmm");
		try {
			Date date = new Date();
			data = reportService.getReportOfOrderSummaryOfEffect(startTime , endTime,orderDays, 1, 1000, productLine);
			exportReportService.exportExcel(data, response, fileName+format.format(date));
			return FeAjaxResponse.success("下载成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("导出 订单汇总报表 失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception exception) {
			logger.error("导出 订单汇总报表 失败! 原因如下:" + exception.getMessage());
			return FeAjaxResponse.error(402, "下载失败");
		}

	}
	
}
