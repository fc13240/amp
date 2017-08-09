package com.gomeplus.amp.ad.service;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.pay.PrePayOperations;
import com.gomeplus.adm.common.api.pay.model.PrePayment;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.dao.ChargeDao;
import com.gomeplus.amp.ad.model.Charge;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by liuchen on 2016/9/20.
 */
@Service
@Transactional(readOnly = true)
public class ChargeService extends BaseService<Charge, Integer> {

	@Autowired
	ChargeDao chargeDao;

	@Override
	public HibernateDao<Charge, Integer> getEntityDao() {
		return chargeDao;
	}


	/**
	 * 根据orderId获取充值记录
	 *
	 * @param orderId
	 * @return
	 */
	public Charge getChargeByOrderId(String orderId) {
		return chargeDao.getUniqueBy("orderId", orderId);
	}

	public List<Charge> getChargesByPagination(Integer userId, Pagination pagination) {
		return chargeDao.findByPagination(userId, pagination);
	}

	/**
	 * 预支付请求和保存操作
	 * @param prePayment
	 * @return
	 * @throws Exception
	 */
//	@Transactional
//	public Map<String,String> insertCharge(PrePayment prePayment) throws Exception {
//
//		Integer userId = PrincipalUtil.getUserId();
//
//		PrePayOperations prePayOperations = new PrePayOperations();
//
//		//平台预支付请求
//		ApiResponse apiResponse;
//		try {
//			apiResponse = prePayOperations.create(prePayment);
//		} catch (Exception e) {
//			throw new RuntimeException("平台预支付orderId=" + prePayment.getOrderId() + "请求异常");
//		}
//
//		//判断预支付接口是否成功
//		//预支付请求失败则返回错误信息
//		if (StringUtils.isNotEmpty(apiResponse.getMessage())) {
//			throw new RuntimeException("平台预支付orderId=" + prePayment.getOrderId() + "请求错误,错误信息是" + apiResponse.getMessage());
//		}
//
//		Map<String, String> unionWapParametersMap = (Map) apiResponse.getData().get("unionWapParameters");
//
//		//充值入库操作
//		Charge charge = new Charge();
//		charge.setTxnAmt(new BigInteger(unionWapParametersMap.get("txnAmt")));
//		// @todo 多种充值方式
//		charge.setName("充值-普通充值");
//		charge.setUserId(userId);
//		charge.setOrderId(prePayment.getOrderId());
//		charge.setTradeNo(unionWapParametersMap.get("orderId"));
//		charge.setStatus(Charge.Status.PAYING.getValue());
//		charge.setTxnTime(unionWapParametersMap.get("txnTime"));
//		charge.setChannel(prePayment.getChannel());
//		charge.setTime(new Date());
//		charge.setCreateTime(new Date());
//		charge.setUpdateTime(new Date());
//		try {
//			chargeDao.save(charge);
//		} catch (Exception e) {
//			throw new RuntimeException("orderId=" + prePayment.getOrderId() + "充值记录保存异常");
//		}
//
//		return unionWapParametersMap;
//	}
}
