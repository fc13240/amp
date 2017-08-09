package com.gomeplus.amp.ad.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.form.FlightForm;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.gomeplus.amp.ad.service.FlightService;
import com.gomeplus.amp.ad.service.KeywordService;
import com.gomeplus.amp.ad.service.StrategyService;
import com.google.gson.Gson;

/**
 * Created by liuchen on 2016/9/1.
 */
@Controller
public class FlightController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(FlightController.class);

	private Gson gson = new Gson();

	@Autowired
	private FlightService flightService;
	@Autowired
	private StrategyService strategyService;
	@Autowired
	private KeywordService keywordService;
	
	/**
	 * 添加投放单元
	 *
	 * @param flightJson
	 * @return
	 */
	// @RequiresPermissions("ad:flight:add")
	@ResponseBody
	@RequestMapping(value = "/api/flight", method = RequestMethod.POST)
	public FeAjaxResponse save(@RequestBody FlightForm flightForm) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			if (null == flightForm.getProductLine()) {
				flightForm.setProductLine(Flight.ProductLine.FIXED_BID_CPC.getValue());
			}
			data = flightService.save(flightForm);
		} catch (Exception exception) {
			logger.error("add flight failed ", exception);
			return FeAjaxResponse.error(400, "添加投放单元失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success(data, "添加投放单元成功！");
	}

	/**
	 * 修改投放单元
	 *
	 * @param flightJson
	 * @return
	 */
	// @RequiresPermissions("ad:flight:edit")
	@ResponseBody
	@RequestMapping(value = "/api/flight", method = RequestMethod.PUT)
	public FeAjaxResponse modify(@RequestBody FlightForm flightForm) {
		try {
			if (null == flightForm.getProductLine()) {
				flightForm.setProductLine(Flight.ProductLine.FIXED_BID_CPC.getValue());
			}
			flightService.update(flightForm);
		} catch (Exception exception) {
			logger.error("modify flight failed", exception);
			return FeAjaxResponse.error(400, "修改投放单元失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success("修改投放单元成功！");
	}



	/**
	 * 获取投放计划简要信息
	 *
	 * @param flightId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/flight/brief", method = RequestMethod.GET)
	public FeAjaxResponse getBrief(HttpServletRequest request, @RequestParam(required = true) Integer flightId) {

		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = flightService.getFlightBriefByFlightId(flightId);

		} catch (Exception exception) {
			logger.error("get flight brief failed", exception);
			return FeAjaxResponse.error(400, "获取投放单元简明信息失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取投放单元简明信息成功！");
	}

	/**
	 * 根据条件查询投放单元列表
	 * 
	 * @param request
	 * @param keyword
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/flights", method = RequestMethod.GET)
	public FeAjaxResponse list(HttpServletRequest request, @RequestParam Integer campaignId,
			@RequestParam(required = false) String keyword, @RequestParam(required = false) Integer state,
			@RequestParam(required = false, defaultValue = "0") String startTime,
			@RequestParam(required = false, defaultValue = "0") String endTime,
			@RequestParam(required = false) Integer platform, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer number, @RequestParam(required = false) Integer productLine) {

		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);
			data = flightService.getFlightsByCampaignId(pagination, campaignId, keyword, platform, state, startTime, endTime, productLine);
		} catch (Exception exception) {
			logger.error("flight list", exception);
			return FeAjaxResponse.error(500, data, "获取投放单元列表失败！");
		}

		return FeAjaxResponse.success(data, "获取投放单元列表成功！");
	}

	/**
	 * 根据flightIdId获取投放单元
	 * 
	 * @param flightId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/flight", method = RequestMethod.GET)
	public FeAjaxResponse getFlight(HttpServletRequest request,	@RequestParam(required = false, defaultValue = "0") Integer flightId) {
		FlightForm data = new FlightForm();
		try {
			data = flightService.getFlightByFlightId(flightId);
		} catch (Exception exception) {
			logger.error("get flight failed", exception);
			return FeAjaxResponse.error(400, "获取投放单元失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success(JSON.parseObject(JSON.toJSONString(data), new HashMap<String, Object>().getClass()), "获取投放单元成功！");
	}
	
	/**
	 * 批量删除投放单元
	 * 
	 * @param flightsDeleteJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/flights", method = RequestMethod.DELETE)
	public FeAjaxResponse batchDelete(@RequestBody String flightsDeleteJson) {
		Map<String, Object> flightsDeleteMap = new HashMap<String, Object>();
		flightsDeleteMap = (Map<String, Object>) gson.fromJson(flightsDeleteJson, flightsDeleteMap.getClass());

		try {
			flightService.batchDelete(flightsDeleteMap);
		} catch (Exception exception) {
			logger.error("delete flights failed [" + exception.getMessage()	+ "]");
			return FeAjaxResponse.error(500, "批量删除投放单元失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("批量删除投放单元成功！");
	}
	
	/**
	 * 批量修改投放单元状态
	 * @param flightsStatusJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/flights/status", method = RequestMethod.PUT)
	public FeAjaxResponse batchUpdateStatus(@RequestBody String flightsStatusJson) {
		Map<String, Object> flightsStatusMap = new HashMap<String, Object>();
		flightsStatusMap = (Map<String, Object>) gson.fromJson(flightsStatusJson,
				flightsStatusMap.getClass());

		try {
			flightService.batchUpdateStatus(flightsStatusMap);
		} catch (Exception exception) {
			logger.error("modify flights status failed [" + exception + "]");
			return FeAjaxResponse.error(400, "修改投放单元状态失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("修改投放单元状态成功！");
	}
	
	/**
	 * 获取竞价cpc的关键词
	 * @param skuId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/keywords/skuId", method = RequestMethod.GET)
	public FeAjaxResponse getKeywords(@RequestParam String skuId) {
		Map<String, Object> keywords = new HashMap<String, Object>();

		try {
			keywords = keywordService.getkeywordsBySkuId(skuId);
		} catch (Exception exception) {
			logger.error("get flights keywords failed: " + exception);
			return FeAjaxResponse.error(400, "获取关键词失败！");
		}

		return FeAjaxResponse.success(keywords, "获取关键词成功！");
	}
	
	/**
	 * 根据flightId获取关键词
	 * @param flightId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/keywords/flightId", method = RequestMethod.GET)
	public FeAjaxResponse getKeywordsByFlightId(@RequestParam Integer flightId) {
		Map<String, Object> keywords = new HashMap<String, Object>();
		try {
			keywords = keywordService.getKeywordsByFlightId(flightId);
		} catch (Exception exception) {
			logger.error("getKeywordsByFlightId failed: " + exception);
			return FeAjaxResponse.error(400, "获取关键词失败！");
		}

		return FeAjaxResponse.success(keywords, "获取关键词成功！");
	}
	
	/**
	 * 添加自定义关键词
	 * @param name
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/keywords", method = RequestMethod.POST)
	public FeAjaxResponse addDefinedKeyword(@RequestBody String keywordJson) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Map<String, Object> keyword = JSON.parseObject(keywordJson, new HashMap<String, Object>().getClass());
			data = flightService.addDefinedKeyword((String) keyword.get("name"));
		} catch (Exception exception) {
			logger.error("addDefinedKeyword failed: " + exception);
			return FeAjaxResponse.error(400, "添加自定义关键词失败!");
		}
		
		return FeAjaxResponse.success(data, "添加自定义关键词成功！");
	}
	
	/**
	 * 关键词管理 修改关键词
	 * @param keywordsJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/keywords", method = RequestMethod.PUT)
	public FeAjaxResponse modigyKeywords(@RequestBody String keywordsJson) {
		Map<String, Object> keywordsMap = JSON.parseObject(keywordsJson, new HashMap<String, Object>().getClass());
		try {
			flightService.updateKeywords(keywordsMap);
		} catch (Exception exception) {
			logger.error("update keywords failed ", exception);
			return FeAjaxResponse.error(400, "修改关键词失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success("修改关键词成功！");
	}
	
	/**
	 * 判断某投放计划下是否存在名为flightName的投放单元
	 * @param name (非必需)
	 * @param campaignId (必需)
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/flight/exist", method = RequestMethod.GET)
	public FeAjaxResponse isExistFlightName(@RequestParam(name = "name", required = false) String name, @RequestParam(name = "flightId", required = false) Integer flightId, @RequestParam(name = "campaignId") Integer campaignId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			if (flightService.isExistFlightName(name, flightId, campaignId)) {
				data.put("exist", 1);				
			} else {
				data.put("exist", 0);								
			}
		} catch (Exception exception) {
			logger.error("flightService.isExistFlightName throw Exception ", exception);
			return FeAjaxResponse.error(500, data, "查询投放单元失败！");
		}
		return FeAjaxResponse.success(data, "查询投放单元成功！");
	}
}
