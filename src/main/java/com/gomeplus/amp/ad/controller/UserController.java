package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.exception.FieldException;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.service.UserService;
import com.google.gson.Gson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户 controller
 * @author DèngBīn
 */
@Controller
public class UserController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	private Gson gson = new Gson();

	@Autowired
	private UserService userService;

	/**
	 * 获取联系人信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/contact", method = RequestMethod.GET)
	public FeAjaxResponse getContactInfo() {

		try {
			Map<String, Object> data = userService.getContactInfo();
			return FeAjaxResponse.success(data, "查询成功!");
		} catch (Exception e) {
			logger.error("查询用户失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败!");
		}

	}

	/**
	 * 修改联系人信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/contact", method = RequestMethod.POST)
	public FeAjaxResponse saveContactInfo(@RequestBody String certJson) {

		Map<String, Object> contactMap = new LinkedHashMap<String, Object>();
		contactMap = (Map<String, Object>) gson.fromJson(certJson, contactMap.getClass());

		try {
			userService.saveContactInfo(contactMap);
			return FeAjaxResponse.success("修改联系人成功!");
		} catch (FieldException e) {
			logger.error("修改联系人失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "修改联系人失败! 原因: " + e.getMessage());
		} catch (Exception e) {
			logger.error("修改联系人失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "修改联系人失败!");
		}

	}

	/**
	 * 获取用户基本信息
	 */
	@ResponseBody
	@RequestMapping(value = "/api/user", method = RequestMethod.GET)
	public FeAjaxResponse getUserInfo() {

		Subject subject = SecurityUtils.getSubject();

		if (!subject.isRemembered() && !subject.isAuthenticated()) {
			return FeAjaxResponse.error(401, "用户信息过期");
		}

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = userService.getUserInfo();

		} catch (Exception e) {
			logger.error("查询用户信息失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询用户信息失败!");
		}

		return FeAjaxResponse.success(data, "查询用户信息成功");
	}
}
