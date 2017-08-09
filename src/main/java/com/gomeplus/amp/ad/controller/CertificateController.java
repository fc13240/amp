package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.exception.FieldException;
import com.gomeplus.adm.common.util.FieldUtil;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.service.CertificateService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

/**
 * 商家资质 controller
 * @author DèngBīn
 */
@Controller
public class CertificateController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(CertificateController.class);
	private Gson gson = new Gson();

	@Autowired
	private CertificateService certificateService;

	/**
	 * 获取商家资质信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/cert", method = RequestMethod.GET)
	public FeAjaxResponse getCertificate(@RequestParam(required = true) Integer certificateId) {

		try {
			Map<String, Object> data = certificateService.getCertificate(certificateId);
			return FeAjaxResponse.success(data, "获取商家资质成功!");
		} catch (Exception e) {
			logger.error("获取商家资质失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "获取商家资质失败!");
		}

	}

	/**
	 * 保存商家资质信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/cert", method = RequestMethod.POST)
	public FeAjaxResponse saveCertificate(@RequestBody String certJson) {

		Map<String, Object> certMap = new HashMap<String, Object>();
		certMap = (Map<String, Object>) gson.fromJson(certJson, certMap.getClass());

		try {
			certificateService.save(certMap);
		} catch (FieldException e) {
			logger.error("新建商家资质失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "新建商家资质失败! 原因: " + e.getMessage());
		} catch (Exception e) {
			logger.error("新建商家资质失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "新建商家资质失败!");
		}

		return FeAjaxResponse.success("新建商家资质成功!");

	}

	/**
	 * 修改商家资质信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/cert", method = RequestMethod.PUT)
	public FeAjaxResponse modifyCertificate(@RequestBody String certJson) {

		Map<String, Object> certMap = new HashMap<String, Object>();
		certMap = (Map<String, Object>) gson.fromJson(certJson, certMap.getClass());

		try {
			certificateService.update(certMap);
		} catch (FieldException e) {
			logger.error("修改商家资质失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "修改商家资质失败! 原因: " + e.getMessage());
		} catch (Exception e) {
			logger.error("修改商家资质失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "修改商家资质失败!");
		}

		return FeAjaxResponse.success("修改商家资质成功!");

	}

	/**
	 * 删除商家资质信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/cert", method = RequestMethod.DELETE)
	public FeAjaxResponse deleteCertificate(@RequestBody String certJson) {

		Map<String, Object> certMap = new HashMap<String, Object>();
		certMap = (Map<String, Object>) gson.fromJson(certJson, certMap.getClass());

		try {
			certificateService.delete(FieldUtil.getInteger("资质id", certMap.get("certificateId")));
		} catch (Exception e) {
			logger.error("删除商家资质失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "删除商家资质失败");
		}

		return FeAjaxResponse.success("删除商家资质成功");

	}

	/**
	 * 获取商家资质列表信息
	 * @param keyword
	 * @param page
	 * @param number
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/certs", method = RequestMethod.GET)
	public FeAjaxResponse getCertificates(@RequestParam(required = false, defaultValue = "") String keyword,
										  @RequestParam(required = false, defaultValue = "1") Integer page,
										  @RequestParam(required = false, defaultValue = "30") Integer number) {

		try {
			Map<String, Object> data = certificateService.getCertificates(keyword, page, number);
			return FeAjaxResponse.success(data, "获取商家资质列表成功!");
		} catch (Exception e) {
			logger.error("获取商家资质列表失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "获取商家资质列表失败!");
		}

	}
}
