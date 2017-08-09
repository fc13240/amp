package com.gomeplus.amp.ad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gomeplus.adm.common.web.AjaxResponse;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.adm.common.web.ValidationError;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.gomeplus.amp.ad.service.AdvertisementService;
import com.gomeplus.amp.ad.service.CampaignService;
import com.gomeplus.amp.ad.service.FlightService;
import com.google.gson.Gson;

/**
 * 广告计划 controller
 *
 * @author wangwei01
 *
 */
@Controller
public class CampaignController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(CampaignController.class);

	private Gson gson = new Gson();

	@Autowired
	private AdvertisementService advertisementService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private FlightService flightService;

	/**
	 * 添加投放计划
	 *
	 * @param campaignJson
	 * @return
	 */
	// @RequiresPermissions("ad:campaign:add")
	@ResponseBody
	@RequestMapping(value = "/api/campaign", method = RequestMethod.POST)
	public FeAjaxResponse save(@RequestBody String campaignJson) {
		Map<String, Object> campaignMap = new HashMap<String, Object>();
		campaignMap = (Map<String, Object>) gson.fromJson(campaignJson, campaignMap.getClass());

		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			data = campaignService.save(campaignMap);
		} catch (Exception exception) {
//			logger.error("add campaign failed [" + exception.getMessage() + "]");
			logger.error("add campaign failed ",exception);
			return FeAjaxResponse.error(500, "添加投放计划失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "添加投放计划成功！");
	}

	/**
	 * 修改投放计划
	 *
	 * @param campaignJson
	 * @return
	 */
	// @RequiresPermissions("ad:campaign:add")
	@ResponseBody
	@RequestMapping(value = "/api/campaign", method = RequestMethod.PUT)
	public FeAjaxResponse modify(@RequestBody String campaignJson) {
		Map<String, Object> campaignMap = new HashMap<String, Object>();
		campaignMap = (Map<String, Object>) gson.fromJson(campaignJson, campaignMap.getClass());

		try {
			campaignService.update(campaignMap);
		} catch (Exception exception) {
//			logger.error("add campaign failed [" + exception.getMessage() + "]");
			logger.error("add campaign failed ", exception);
			return FeAjaxResponse.error(500, "修改投放计划失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("修改投放计划成功！");
	}

	/**
	 * 批量修改投放计划状态
	 *
	 * @param campaignsStatusJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaigns/status", method = RequestMethod.PUT)
	public FeAjaxResponse batchUpdateStatus(@RequestBody String campaignsStatusJson) {
		Map<String, Object> campaignsStatusMap = new HashMap<String, Object>();
		campaignsStatusMap = (Map<String, Object>) gson.fromJson(campaignsStatusJson,
													campaignsStatusMap.getClass());

		try {
			campaignService.batchUpdateStatus(campaignsStatusMap);
		} catch (Exception exception) {
			logger.error("modify campaigns status failed [" + exception.getMessage() + "]");
			return FeAjaxResponse.error(500, "修改投放计划状态失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("修改投放计划状态成功！");
	}

	/**
	 * 批量删除投放计划
	 *
	 * @param campaignsDeleteJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaigns", method = RequestMethod.DELETE)
	public FeAjaxResponse batchDelete(@RequestBody String campaignsDeleteJson) {
		Map<String, Object> campaignsDeleteMap = new HashMap<String, Object>();
		campaignsDeleteMap = (Map<String, Object>) gson.fromJson(campaignsDeleteJson,
													campaignsDeleteMap.getClass());

		try {
			campaignService.batchDelete(campaignsDeleteMap);
		} catch (Exception exception) {
			logger.error("delete campaigns failed [" + exception + "]");
			return FeAjaxResponse.error(500, "批量删除投放计划失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("批量删除投放计划成功！");
	}

	/**
	 * 获取投放计划简明信息
	 *
	 * @param request
	 * @param campaignId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaign/brief", method = RequestMethod.GET)
	public FeAjaxResponse getBrief(HttpServletRequest request, @RequestParam(required = true) Integer campaignId) {

		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = campaignService.getCampaignBriefByCampaignId(campaignId);

		} catch (Exception exception) {
			logger.error("get campaign brief failed", exception);
			return FeAjaxResponse.error(500, "获取投放计划简明信息失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取投放计划简明信息成功！");
	}

	/**
	 * 根据条件查询广告计划列表
	 * 
	 * @param request
	 * @param keyword
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaigns", method = RequestMethod.GET)
	public FeAjaxResponse list(HttpServletRequest request, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer state, @RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime, @RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer number, @RequestParam(required = false) Integer productLine) {

		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);
			data = campaignService.getCampaignsByPagination(pagination, keyword, state, startTime, endTime, productLine);
		} catch (Exception exception) {
			logger.error("campaign list", exception);
			return FeAjaxResponse.error(500, data, "获取广告计划列表失败！");
		}

		return FeAjaxResponse.success(data, "获取广告计划列表成功！");
	}

	/**
	 * 根据campaignId获取广告计划
	 * 
	 * @param campaignId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaign", method = RequestMethod.GET)
	public FeAjaxResponse getCampaignByCampaignId(@RequestParam Integer campaignId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = campaignService.getCampaignByCampaignId(campaignId);
		} catch (Exception exception) {
			logger.error("get campaign by  campaignId", exception);
			return FeAjaxResponse.error(500, data, "获取投放计划失败！");
		}
		return FeAjaxResponse.success(data, "获取投放计划成功！");
	}

	/**
	 * 获取所有的投放计划列表
	 * @param productLine 产品线 默认2(定价cpc); 3(竞价cpc);
	 * @return FeAjaxResponse
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaigns/all", method = RequestMethod.GET)
	public FeAjaxResponse getAllCampaigns(
			@RequestParam(name = "productLine" , required = true, defaultValue = "2") Integer productLine) {
		Map<String, Object> data = new HashMap<>();
		try {
			List<Map<String, Object>> dataList = campaignService.getAllCampaigns(productLine);
			data.put("list", dataList);
		} catch (Exception exception) {
			logger.error("获取所有的投放计划失败", exception);
			return FeAjaxResponse.error(500, data, "获取所有的投放计划失败！");
		}
		return FeAjaxResponse.success(data, "获取所有的投放计划成功！");
	}

	/**
	 * 获取所有的投放计划和投放单元下拉列表（1对多）
	 * @param productLine 产品线 默认2(定价cpc); 3(竞价cpc);
	 * @return FeAjaxResponse
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaigns/flights/all", method = RequestMethod.GET)
	public FeAjaxResponse getAllCampaignsAndFlights(
			@RequestParam(name = "productLine" , required = true, defaultValue = "2") Integer productLine) {
		Map<String, Object> data = new HashMap<>();
		try {
			List<Map<String, Object>> dataList = campaignService.getCampaignsAndFlights(productLine);
			data.put("list", dataList);
		} catch (Exception exception) {
			logger.error("获取所有的投放计划和投放单元失败", exception);
			return FeAjaxResponse.error(500, data, "获取所有的投放计划和投放单元失败！");
		}
		return FeAjaxResponse.success(data, "获取所有的投放计划和投放单元成功！");
	}


	/**
	 * 获取所有的投放计划和投放单元以及创意的下拉列表
	 * @param productLine 产品线 默认2(定价cpc); 3(竞价cpc);
	 * @return FeAjaxResponse
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaigns/flights/materials/all", method = RequestMethod.GET)
	public FeAjaxResponse getAllCampaignsAndFlightsAndMaterials(
			@RequestParam(name = "productLine" , required = true, defaultValue = "2") Integer productLine) {
		Map<String, Object> data = new HashMap<>();
		try {
			List<Map<String, Object>> dataList = campaignService.getCampaignsAndFlightsAndMaterials(productLine);
			data.put("list", dataList);
		} catch (Exception exception) {
			logger.error("获取所有的投放计划和投放单元以及创意的下拉列表", exception);
			return FeAjaxResponse.error(500, data, "获取所有的投放计划和投放单元以及创意的下拉列表！");
		}
		return FeAjaxResponse.success(data, "获取所有的投放计划和投放单元以及创意的下拉列表！");
	}
	
	/**
	 * 判断当前用户下是否存在名为campaignName的投放计划
	 * @param name (投放计划名称  非必需)
	 * @param productLine (产品线标识  非必需)
	 * @return exist (0-不存在  1-存在)
	 */
	@ResponseBody
	@RequestMapping(value = "/api/campaign/exist", method = RequestMethod.GET)
	public FeAjaxResponse isExistCampaignName(@RequestParam(name = "name", required = false) String name, @RequestParam(name = "campaignId", required = false) Integer campaignId, @RequestParam(name = "productLine", required = false) Integer productLine) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Integer userId = PrincipalUtil.getUserId();
			if (campaignService.isExistCampaignName(userId, name, campaignId, productLine)) {
				data.put("exist", 1);				
			} else {
				data.put("exist", 0);								
			}
		} catch (Exception exception) {
			logger.error("campaignService.isExistCampaignName throw Exception ", exception);
			return FeAjaxResponse.error(500, data, "查询投放计划失败！");
		}
		return FeAjaxResponse.success(data, "查询投放计划成功！");
	}

}
