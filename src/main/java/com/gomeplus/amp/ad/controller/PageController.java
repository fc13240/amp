package com.gomeplus.amp.ad.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.gomeplus.adm.common.util.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.service.PageService;
import com.google.gson.Gson;

/**
 * 页面 controller
 * @author lifei01
 */
@Controller
public class PageController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(PageController.class);

	private Gson gson = new Gson();

	@Autowired
	private PageService pageService;




	/**
	 * 页面接口
	 * @param name 标题名称
	 * @param platform 设备( 1APP 2WAP 3PC)
	 * @param status 开状态（-1删除 0草稿 1发布）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/pages", method = RequestMethod.GET)
	public FeAjaxResponse getPages(@RequestParam(value = "name", required = false, defaultValue = "") String name,
								   @RequestParam(value = "platform", required = false, defaultValue = "0") Integer platform,
								   @RequestParam(name = "status", required = false) Integer status,
								   @RequestParam(name = "templateId", required = false) Integer templateId,
								   HttpServletRequest request) {

		Pagination pagination = getPagination(request);
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = pageService.getPages(pagination, name, platform, status, templateId);

		} catch (Exception exception) {
			logger.error("getPages", exception);
			return FeAjaxResponse.error(400, "查询失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取自建页面成功！");
	}


	/**
	 * 添加自建活动页
	 *
	 * @param pageJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/page", method = RequestMethod.POST)
	public FeAjaxResponse save(@RequestBody String pageJson) {
		Map<String, Object> pageMap = new HashMap<String, Object>();
		pageMap = (Map<String, Object>) gson.fromJson(pageJson, pageMap.getClass());


		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = pageService.save(pageMap);
		} catch (Exception exception) {
			logger.error("add page failed ",exception);
			return FeAjaxResponse.error(400, "添加自建页面失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "添加自建页面成功！");

	}

	/**
	 * 更改自建活动页
	 *
	 * @param pageJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/page", method = RequestMethod.PUT)
	public FeAjaxResponse update(@RequestBody String pageJson) {
		Map<String, Object> pageMap = new HashMap<String, Object>();
		pageMap = (Map<String, Object>) gson.fromJson(pageJson, pageMap.getClass());


		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = pageService.update(pageMap);
		} catch (Exception exception) {
			logger.error("update page failed ",exception);
			return FeAjaxResponse.error(400, "修改自建页面失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "修改自建页面成功！");

	}

	/**
	 * 获取单个自建活动页数据
	 * @param pageId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/page", method = RequestMethod.GET)
	public FeAjaxResponse getPage(@RequestParam Integer pageId) {

		if (ObjectUtils.isEmpty(pageId) || pageId <= 0) {
			logger.error("获取单个自建活动页数据失败: pageId格式错误");
			return FeAjaxResponse.error(400, "获取单个自建活动页数据失败!");
		}

		try {
			Map<String, Object> data = pageService.getPage(pageId);
			return FeAjaxResponse.success(data, "获取单个自建活动页数据成功!");
		} catch (Exception e) {
			logger.error("获取单个自建活动页数据失败: " + e.getMessage());
			return FeAjaxResponse.error(400, "获取单个自建活动页数据失败!");
		}

	}

	/**
	 * 获取页面列表上的预览信息
	 * @param pageId 页面id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/page/preview", method = RequestMethod.GET)
	public FeAjaxResponse pagePreview(@RequestParam Integer pageId) {

		if (ObjectUtils.isEmpty(pageId) || pageId <= 0) {
			logger.error("获取页面列表上的预览信息失败: pageId格式错误");
			return FeAjaxResponse.error(400, "获取页面列表上的预览信息失败!");
		}

		try {
			Map<String, Object> data = pageService.getPagePreview(pageId);
			return FeAjaxResponse.success(data, "获取页面列表上的预览信息成功!");
		} catch (Exception e) {
			logger.error("获取页面列表上的预览信息失败: " + e.getMessage());
			return FeAjaxResponse.error(400, "获取页面列表上的预览信息失败!");
		}
	}

	/**
	 * 发布页面
	 * @param pageJson pageJson数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/page/publish", method = RequestMethod.PUT)
	public FeAjaxResponse pagePublish(@RequestBody String pageJson) {
		Map<String, Object> pageMap = new HashMap<String, Object>();

		try {
			pageMap = (Map<String, Object>) gson.fromJson(pageJson, pageMap.getClass());

			Integer pageId = FieldUtil.getInteger("页面id", pageMap.get("pageId"));
			if (ObjectUtils.isEmpty(pageId) || pageId <= 0) {
				logger.error("发布页面失败: pageId格式错误");
				return FeAjaxResponse.error(400, "发布页面失败!");
			}

			Map<String, Object> data = pageService.publishPage(pageId);
			return FeAjaxResponse.success(data, "发布页面成功!");
		} catch (Exception e) {
			logger.error("发布页面失败: " + e.getMessage());
			return FeAjaxResponse.error(400, "发布页面失败: " + e.getMessage());
		}

	}

	/**
	 * 获取店铺信息
	 *
	 * @param shopId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/api/page/shop", method = RequestMethod.GET)
	public FeAjaxResponse getShopItemByShopId(@RequestParam(name = "shopId", required = false, defaultValue = "") String shopId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = pageService.getShopItemByShopId(shopId);
		} catch (Exception exception) {
			logger.error("getShopItemByShopId failed "+ exception.getMessage());
			return FeAjaxResponse.error(400, "获取店铺信息失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取店铺信息成功！");
	}
}
