package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.exception.FieldException;
import com.gomeplus.adm.common.web.*;
import com.gomeplus.amp.ad.model.Advertiser;
import com.gomeplus.amp.ad.service.AdvertiserService;
import java.io.IOException;
import java.util.HashMap;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 广告主 controller
 * @author DèngBīn
 */
@Controller
public class AdvertiserController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AdvertiserController.class);
	private Gson gson = new Gson();

	@Autowired
	private AdvertiserService advertiserService;


	/**
	 * 检查 广告主是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "/api/anicer/exist", method = RequestMethod.GET)
	public FeAjaxResponse isAdvertiserExist() throws IOException {

		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			return FeAjaxResponse.error(400, "查询失败");
		}

		try {
			Map<String, Object> data = new HashMap<String, Object>();
			// 判断 账号下是否存在广告主信息, 如果无, 跳广告主创建页;
			if (advertiserService.getAdvertiserByUserId() == null) {
				data.put("exist", 1);
				return FeAjaxResponse.success(data, "查询成功");
			} else {
				data.put("exist", 0);
				return FeAjaxResponse.success(data, "查询成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询广告主是否存在失败! 原因如下: " + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}

	}

	/**
	 * Ajax新建或者修改广告主信息
	 * @return
	 */
	@RequestMapping(value = "/api/anicer", method = RequestMethod.POST)
	@ResponseBody
	public FeAjaxResponse save(@RequestBody String advertiserJson) {
		Map<String, Object> advertiserMap = new HashMap<String, Object>();

		try {
			advertiserMap = (Map<String, Object>) gson.fromJson(advertiserJson, advertiserMap.getClass());
			advertiserService.save(advertiserMap);
		} catch (FieldException e) {
			logger.error("保存广告主失败!" + e.getMessage());
			return FeAjaxResponse.error(400, "新建广告主失败!" + e.getMessage());
		} catch (Exception e) {
			logger.error("新建广告主失败!" + e.getMessage());
			return FeAjaxResponse.error(400, "新建广告主失败!");
		}

		return FeAjaxResponse.success("新建广告主成功!");
	}

	/**
	 * 获取入驻商户信息
	 * @return
	 */
	@RequestMapping(value = "/api/anicer", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getSettledMerchantInfo() {

		try {
			Map<String, Object> data = advertiserService.getSettledMerchantInfo();
			return FeAjaxResponse.success(data, "查询成功");
		} catch (Exception e) {
			logger.error("查询入驻商家信息失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}

	/**
	 * 跳转广告主审核页
	 * @return
	 */
	@RequestMapping(value = "/anicerApproval", method = RequestMethod.GET)
	public String list() {
		return "ad/advertiser/advertiserApproval";
	}

	/**
	 * 分页取所有的广告主
	 * @param request
	 * @param model
	 * @param keyword
	 * @param approvalStatus
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getAdvertisers", method = RequestMethod.GET)
	public AjaxResponse getAdvertisers(HttpServletRequest request, Model model, @RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "approvalStatus", required = false) Integer approvalStatus) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);
			List<Advertiser> advertisers = advertiserService.getAllAdvertisersByPagination(pagination, keyword, approvalStatus);

			data.put("advertisers", advertisers);
			data.put("page", pagination.getCurrentPage());
			data.put("totalCount", pagination.getTotalCount());
			data.put("pageSize", pagination.getNumber());
		} catch (Exception exception) {
			logger.error("getAdvertisers", exception);
			return AjaxResponse.error("获取广告主失败！", data);
		}

		return AjaxResponse.success("获取广告主成功！", data);
	}

	/**
	 * 广告主审核通过
	 * @param advertiserId 广告主id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/pass/{advertiserId}", method = RequestMethod.GET)
	public AjaxResponse pass(@PathVariable Integer advertiserId) {
		try {
			advertiserService.passByAdvertiserId(advertiserId);
		} catch (Exception exception) {
			logger.error("广告主审核通过失败！", exception);
			return AjaxResponse.error("广告主审核通过失败！");
		}
		return AjaxResponse.success("广告主审核通过成功！");
	}

	/**
	 * 广告主审核不通过
	 * @param advertiserId 广告主id
	 * @param remark 不通过原因备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/revoke/{advertiserId}/{remark}", method = RequestMethod.GET)
	public AjaxResponse revoke(@PathVariable Integer advertiserId, @PathVariable String remark) {
		try {
			advertiserService.revokeByAdvertiserId(advertiserId, remark);
		} catch (Exception exception) {
			logger.error("广告主审核不通过！", exception);
			return AjaxResponse.error("广告主审核不通过失败！");
		}
		return AjaxResponse.success("广告主审核不通过成功！");
	}
	
	/**
	 * 获取广告主审核拒绝理由
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/anicer/reason", method = RequestMethod.GET)
	public FeAjaxResponse getRejectReson(@RequestParam Integer userId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = advertiserService.getRejectReason(userId);
		} catch (Exception exception) {
			logger.error("获取广告主拒绝原因失败！", exception);
			return FeAjaxResponse.error(400, "获取广告主拒绝原因失败！" + exception);
		}
		return FeAjaxResponse.success(data, "获取广告主拒绝原因成功！");
	}
	
	/**
	 * 获取广告主信息
	 * @return
	 */
	@RequestMapping(value = "/api/anicer/info", method = RequestMethod.GET)
	@ResponseBody
	public FeAjaxResponse getAdvertiser() {

		try {
			Map<String, Object> data = advertiserService.getAdvertiser();
			return FeAjaxResponse.success(data, "查询成功");
		} catch (Exception e) {
			logger.error("查询广告主信息失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "查询失败");
		}
	}
}
