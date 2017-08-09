package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.service.AdvertisementService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 广告 controller
 * @author baishen
 */
@Controller
public class AdvertisementController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AdvertisementController.class);
	private Gson gson = new Gson();

	@Autowired
	private AdvertisementService advertisementService;

	/**
	 * 查询所有有效媒体
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/publishers", method = RequestMethod.GET)
	public FeAjaxResponse getPublishers(HttpServletRequest request,
		@RequestParam(required = false, defaultValue = "0") Integer platform) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = advertisementService.getOnlinePublishers(platform);
		} catch (Exception exception) {
			logger.error("publisher list", exception);
			return FeAjaxResponse.error(500, data, "获取媒体列表失败！");
		}
		return FeAjaxResponse.success(data, "获取媒体列表成功！");
	}

	/**
	 * 创意尺寸列表
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/sizes", method = RequestMethod.GET)
	public FeAjaxResponse getSizes(HttpServletRequest request,
		@RequestParam(required = false, defaultValue = "0") Integer platform) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = advertisementService.getSizes(platform);
		} catch (Exception exception) {
			logger.error("size list", exception);
			return FeAjaxResponse.error(500, data, "获取创意尺寸列表失败！");
		}
		return FeAjaxResponse.success(data, "获取创意尺寸列表成功！");
	}

	/**
	 * 广告列表
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/anices", method = RequestMethod.GET)
	public FeAjaxResponse getAdvertisements(HttpServletRequest request,
		@RequestParam(required = false, defaultValue = "") String keyword,
		@RequestParam(required = false, defaultValue = "0") Integer publisherId,
		@RequestParam(required = false, defaultValue = "0") Integer platform,
		@RequestParam(required = false, defaultValue = "0") Integer width,
		@RequestParam(required = false, defaultValue = "0") Integer height,
		@RequestParam(required = false, defaultValue = "1") Integer saleMode) {

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);
			data = advertisementService.getAdvertisements(pagination, keyword, publisherId, platform, width, height, saleMode);
		} catch (Exception exception) {
			logger.error("get advertisements list", exception);
			return FeAjaxResponse.error(500, data, "获取广告列表失败！");
		}
		return FeAjaxResponse.success(data, "获取广告列表成功！");
	}
}