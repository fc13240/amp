package com.gomeplus.amp.ad.controller;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import javax.servlet.http.HttpServletRequest;

import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.form.AccountTranferForm;
import com.gomeplus.amp.ad.service.AccountService;
import com.google.gson.Gson;

@Controller
public class AccountController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(AccountController.class);
	
	@Autowired
	private AccountService accountService;

	private Gson gson = new Gson();

	/**
	 * 账户概况
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account", method = RequestMethod.GET)
	public FeAjaxResponse getAccountData() {
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = accountService.getBasicAccount();
		} catch (Exception e) {
			logger.error("获取账户概况信息失败! 原因如下:" + e);
			return FeAjaxResponse.error(400, "获取账号信息失败！");
		}
		return FeAjaxResponse.success(data, "获取账号信息成功！");
	}
	
	/**
	 * 充值记录
	 * @param state
	 * @param time
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/income", method = RequestMethod.GET)
	public FeAjaxResponse getChargeData(HttpServletRequest request, 
				@RequestParam(required = false, defaultValue = "0") Integer state,
				@RequestParam(required = false, defaultValue = "0") Long time) {

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);

			data = accountService.getCharge(pagination, state, time);
		} catch (Exception e) {
			logger.error("获取充值记录失败! 原因如下:" + e.getMessage());
			String msg = e.getMessage();
			return FeAjaxResponse.error(400, msg);
		}
		return FeAjaxResponse.success(data, "获取充值记录成功！");
	}
	
	/**
	 * 消费记录
	 * @param state
	 * @param time
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/expense", method = RequestMethod.GET)
	public FeAjaxResponse getExpenseData(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "0") Long time) {
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);
			data = accountService.getExpense(pagination, time);
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.error("获取消费记录失败! 原因如下:" + msg);
			return FeAjaxResponse.error(400, msg);
		}
		return FeAjaxResponse.success(data, "获取消费记录成功！");
	}
	
	/**
	 * 单日明细
	 * @param time
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/expense/daily", method = RequestMethod.GET)
	public FeAjaxResponse getDailyExpenseData(@RequestParam Long time) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = accountService.getDailyExpense(time);
		} catch (Exception e) {
			String msg = e.getMessage();
			logger.error("获取单日明细失败! 原因如下:" + msg);
			return FeAjaxResponse.error(400, msg);
		}
		return FeAjaxResponse.success(data, "获取当日明细成功！");
	}

	/**
	 * 获取账户余额提醒
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/balance/remind", method = RequestMethod.GET)
	public FeAjaxResponse getBalanceRemind() {
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		try {
			data = accountService.getBalanceRemind();
		} catch (Exception e) {
			logger.error("get account balance remind failed: " + e.getMessage());
			return FeAjaxResponse.error(400, "获取余额提醒失败！");
		}
		return FeAjaxResponse.success(data, "获取余额提醒成功！");
	}

	/**
	 * 修改余额提醒成功
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/balance/remind", method = RequestMethod.POST)
	public FeAjaxResponse saveBalanceRemind(@RequestBody String remindJson) {

		Map<String, Object> remindMap = new LinkedHashMap<String, Object>();
		remindMap = (Map<String, Object>) gson.fromJson(remindJson, remindMap.getClass());
		try {
			accountService.saveBalanceRemind(remindMap);
		} catch (Exception e) {
			logger.error("save account balance remind failed：" + e.getMessage());
			return FeAjaxResponse.error(400, "保存余额提醒失败!");
		}
		return FeAjaxResponse.success("保存余额成功!");
	}
	
	/**
	 * 获取资金划拨信息
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/transfer", method = RequestMethod.GET)
	public FeAjaxResponse getAccountTransferData() {

		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = accountService.getAccountTransfer();
		} catch (Exception e) {
			logger.error("获取资金划拨信息失败!错误信息:" + e.getMessage());
			return FeAjaxResponse.error(400, "资金划拨操作失败");
		}
		return FeAjaxResponse.success(data, "获取资金划拨信息成功！");
	}

	/**
	 * 更新资金划拨
	 * 
	 * @param advertiserId
	 * @param adBalance
	 * @param rebateBalance
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/account/transfer", method = RequestMethod.POST)
	public FeAjaxResponse updateAccountTransferData(@RequestBody AccountTranferForm accountTranferForm) {
		try {
			accountService.accountTransfer(accountTranferForm.getAdBalance(), accountTranferForm.getRebateBalance());
		} catch (Exception exception) {
			logger.error("资金划拨操作失败！错误信息:" , exception);
			return FeAjaxResponse.error(400, "资金划拨操作失败");
		}
		return FeAjaxResponse.success("账户资金划拨成功！");
	}
}

