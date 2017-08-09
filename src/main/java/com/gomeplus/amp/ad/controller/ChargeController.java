package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.api.pay.model.RequestPayModel;
import com.gomeplus.adm.common.pay.unionpay.PayConstant;
import com.gomeplus.adm.common.util.EncrUtil;
import com.gomeplus.adm.common.web.AjaxResponse;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.form.AccountForm;
import com.gomeplus.amp.ad.model.Charge;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.gomeplus.amp.ad.service.ChargeService;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by liuchen on 2016/9/27.
 */
@Controller
@RequestMapping("/api")
public class ChargeController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ChargeController.class);

	@Autowired
	ChargeService chargeService;

	@Value("${adpay.siteAccount}")
	private String siteAccount;

	@Value("${adpay.userNo}")
	private String userNo;

	@Value("${adpay.md5key}")
	private String md5key;

	@Value("${adpay.notifyUrl}")
	private String notifyUrl;

	@Value("${adpay.pageBackUrl}")
	private String pageBackUrl;

	@Value("${adpay.orderExpireMsg}")
	private String orderExpireMsg;

	@Value("${adpay.requestUrl}")
	private String requestUrl;


	/**
	 * @todo 充值记录状态查询
	 *
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/status")
	@ResponseBody
	public AjaxResponse getChargeByOrderId(@RequestParam("orderId") String orderId) {
		//根据orderId查询支付订单状态
		Charge charge = chargeService.getChargeByOrderId(orderId);
		if (charge.getStatus() == Charge.Status.PAY_SUCCESS.getValue().intValue()) {
			logger.info("订单号orderId= " + orderId + "的状态是支付成功");
			return AjaxResponse.success("支付成功");
		} else {
			logger.info("订单号orderId= " + orderId + "的状态是支付失败");
			return AjaxResponse.error("支付失败");
		}
	}


	/**
	 * 账户充值
	 * @param accountForm
	 * @return
	 */
	@RequestMapping(value = "/recharge", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public FeAjaxResponse preRecharge(@RequestBody AccountForm accountForm) {
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("url", "/api/recharge/" + accountForm.getType() + "?txnAmt=" + accountForm.getAmount());
		} catch (Exception e) {
			return FeAjaxResponse.error(400, "获取充值地址失败！");
		}
		return FeAjaxResponse.success(map, "获取充值地址成功！");
	}


	/**
	 * 充值请求
	 * 组装收银台请求数据请求收银台
	 * @param payMoney
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/cashier/charge", method = RequestMethod.GET)
	public ResponseEntity requestCashier(@RequestParam("payMoney") BigDecimal payMoney) throws Exception {


		// 20170609获取不同环境配置临时方案
		String environment = System.getenv().get("ENVIRONMENT");
		switch (environment) {
			case "development" :
				siteAccount= "1021010010";
				userNo = "PNON_1021010010";
				md5key = "96E79218965EB72C";
				notifyUrl = "http://admapi.pre.gomeplus.com/charge/cashier/notify";
				pageBackUrl = "NOT_PAGE_BACK";
				orderExpireMsg = "24H";
				requestUrl = "https://b.gpay.atguat.com.cn:30023/cashier/default.dhtml?reqInfo=";
				break;
			case "preproduction":
				siteAccount= "1021010010";
				userNo = "PNON_1021010010";
				md5key = "96E79218965EB72C";
				notifyUrl = "http://admapi.pre.gomeplus.com/charge/cashier/notify";
				pageBackUrl = "NOT_PAGE_BACK";
				orderExpireMsg = "24H";
				requestUrl = "https://b.gpay.atguat.com.cn:30023/cashier/default.dhtml?reqInfo=";
				break;
			case "production":
				siteAccount= "1021010010";
				userNo = "PNON_1021010010";
				md5key = "96E79218965EB72C";
				notifyUrl = "http://admapi.pro.gomeplus.com/charge/cashier/notify";
				pageBackUrl = "NOT_PAGE_BACK";
				orderExpireMsg = "24H";
				// 待修改
				requestUrl = "https://b.gpay.atguat.com.cn:30023/cashier/default.dhtml?reqInfo=";
				break;
		}

		// 充值金额 Decimal 转换为 BigInteger
		BigInteger amount = payMoney.multiply(new BigDecimal(100)).toBigInteger();
		/**
		 * 哪些参数走配置文件？
		 *
		 * 站点账号
		 * 站点秘钥 (MD5KEY)
		 * userNO
		 * 回调通知地址
		 * 回调通知页面
		 * 订单过期时长
		 *
		 */
		RequestPayModel requestPayModel = new RequestPayModel();
		// 用于收银台关联支付方式和支付银行，由收银台分配给站点
		//		广告站点UAT环境配置信息：
		//		站点账号：1021010010
		//		站点秘钥 (MD5KEY)：96E79218965EB72C
		requestPayModel.setSiteAccount(siteAccount);
		String orderNo = "AD" + ThreadLocalRandom.current().nextLong(1607230000000001L, 2607230000000001L + 1);
		logger.info("订单OrderNo是" + orderNo);
		requestPayModel.setOrderNo(orderNo);
		requestPayModel.setOrderMoney(amount.toString());
		requestPayModel.setPayMoney(amount.toString());
		// 用户的userId  PNON_1021010010
		requestPayModel.setUserNo(userNo);
		// 如果不需要页面回调请填"NOT_PAGE_BACK"
		requestPayModel.setPageBackUrl(pageBackUrl);
		// 点对点回调地址用于通知站点订单支付成功
		requestPayModel.setNotifyUrl(notifyUrl);
		// 订单销售来源
		requestPayModel.setOrderType(PayConstant.ORDER_TYPE);
		// 1：支持分期、0：不支持分期（商品是否支持分期）
		requestPayModel.setIsSupportStages(PayConstant.IS_SUPPORTSTAGES);
		// 1：分期置顶显示、0：分期不置顶显示（用户是否选择分期支付）、2：企业网银支付
		requestPayModel.setIsByStages(PayConstant.IS_BY_STAGES);
		// 过期时长（36H，表示36小时；15M，表示15分钟）
		requestPayModel.setOrderExpireMsg(orderExpireMsg);
		// 订单过期时间yyyyMMddhhmmss
		requestPayModel.setOrderExpireTime(DateFormatUtils.format(DateUtils.addHours(new Date(), Integer.parseInt
				(requestPayModel.getOrderExpireMsg().replace("H", "").trim())), "yyyyMMddhhmmss"));
		// 商户分账信息，跨境支付必传，格式为：公司编码_店铺编码，如：H001_2
		// 你们有海外购吗没有的话商户分账信息-partDetail我们怎么填？这个就不用填
		//	requestPayModel.setPartDetail("");
		// 0:代表正常支付跳转到收银台，1或者空值:代表从我的国美或我的订单跳转到收银台（正常跳转支付时必填）
		requestPayModel.setIsNormalPay(PayConstant.IS_NORMAL_PAY);
		// 站点名称（各站点埋码的时候填写的流程名称 比如团抢、固收等）
		requestPayModel.setSiteName(PayConstant.SITE_NAME);

		Integer userId = PrincipalUtil.getUserId();

		try {
			//充值入库操作
			Charge charge = new Charge();
			charge.setPayMoney(amount);
			charge.setName("充值-普通充值");
			charge.setUserId(userId);
			charge.setOrderNo(orderNo);
			charge.setStatus(Charge.Status.PAYING.getValue());
			charge.setTime(new Date());
			charge.setCreateTime(new Date());
			charge.setUpdateTime(new Date());
			chargeService.save(charge);
		} catch (Exception exception) {
			logger.error("userId=" + userId + "在日期是" + DateFormatUtils.ISO_DATE_FORMAT.format(new Date()) +
					"充值入库保存异常");
			throw new RuntimeException("userId=" + userId + "在日期是" + DateFormatUtils.ISO_DATE_FORMAT.format(new Date()
			) + "充值入库保存异常");
		}

		/**
		 * siteAccount=val1&orderNo=val2&orderMoney=val3&payMoney=val4&userNo=val5&telphone=val6&sukCode=val7
		 * &productType=val&pageBackUrl=val9&notifyUrl=val10&orderType=val11&isSupportStages=val20
		 * &isByStages=val21&remark=val12&orderExpireMsg=val14&orderExpireTime=val15&partDetail=val16
		 * &key=md5Key进行Md5
		 * 签名，空值不参加签名。
		 */
		String securitySignString = "siteAccount=" + requestPayModel.getSiteAccount() + "&orderNo=" + requestPayModel
				.getOrderNo()
				+ "&orderMoney=" + requestPayModel.getOrderMoney() + "&payMoney=" + requestPayModel.getPayMoney() +
				"&userNo=" + requestPayModel.getUserNo()
				+ "&pageBackUrl=" + requestPayModel.getPageBackUrl() + "&notifyUrl=" + requestPayModel.getNotifyUrl()
				+ "&orderType=" + requestPayModel.getOrderType()
				+ "&isSupportStages=" + requestPayModel.getIsSupportStages() + "&isByStages=" + requestPayModel
				.getIsByStages() + "&orderExpireMsg=" + requestPayModel.getOrderExpireMsg() + "&orderExpireTime=" +
				requestPayModel.getOrderExpireTime() + "&key=" + md5key;
		logger.info("#######SecuritySignString是" + securitySignString);

		String securitySignMD5 = EncrUtil.encodeMessage(securitySignString);

		logger.info("#######MD5加签后的SecuritySignString是" + securitySignString);

		String reqInfo = "siteAccount=" + requestPayModel.getSiteAccount() + "&orderNo=" + requestPayModel.getOrderNo()
				+ "&orderMoney=" + requestPayModel.getOrderMoney() + "&payMoney=" + requestPayModel.getPayMoney() +
				"&userNo=" + requestPayModel.getUserNo() + "&pageBackUrl=" + requestPayModel.getPageBackUrl() +
				"&notifyUrl=" + requestPayModel.getNotifyUrl() + "&orderType=" + requestPayModel.getOrderType() +
				"&isSupportStages=" + requestPayModel
				.getIsSupportStages() + "&isByStages=" + requestPayModel.getIsByStages() + "&orderExpireMsg=" +
				requestPayModel.getOrderExpireMsg() + "&orderExpireTime=" +
				requestPayModel.getOrderExpireTime() + "&securitySign=" + securitySignMD5;

		logger.info("#######reqInfo是" + reqInfo);

		String reqInfoAES = EncrUtil.encrypt(reqInfo);

		logger.info("#######AES加密后的reqInfo是" + reqInfoAES);

		URI cashierURI = new URI(requestUrl + reqInfoAES);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("securitySign", securitySignMD5);
		httpHeaders.set("reqInfo", URLEncoder.encode(reqInfoAES, "UTF-8"));
		httpHeaders.setLocation(cashierURI);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

}
