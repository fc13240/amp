package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.service.PageTemplateService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面模板 controller
 * @author DèngBīn
 */
@Controller
public class PageTemplateController extends BaseController {

	@Autowired
	private PageTemplateService pageTemplateService;

	private static Logger logger = LoggerFactory.getLogger(PageTemplateController.class);

	private Gson gson = new Gson();

	/**
	 * 获取页面模板列表
	 * @param pageTemplatesJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/pageTemplates", method = RequestMethod.GET)
	public FeAjaxResponse getPageTemplates(@RequestParam(value = "platform", required = false, defaultValue = "1") Integer platform) {

		try {
			Map<String, Object> data = pageTemplateService.getPageTemplates(platform);
			return FeAjaxResponse.success(data, "获取页面模板列表成功!");
		} catch (Exception e) {
			logger.error("获取页面模板列表失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "获取页面模板列表失败!");
		}

	}
}
