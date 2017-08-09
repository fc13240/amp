package com.gomeplus.amp.ad.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by liuchen on 2016/9/23. 充值记录表
 *
 */
@Entity
@Table(name = "ams_charge")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Charge implements Serializable {


	private static final long serialVersionUID = 8542526637122886349L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "charge_id", unique = true, nullable = false)
	private int chargeId;
	//本地生成的总订单号
	@Column(name = "order_no", nullable = false)
	private String orderNo;
	//用户ID
	@Column(name = "user_id", nullable = false)
	private int userId;
	//充值名称
	@Column(name = "name", nullable = false)
	private String name;
	// 充值金额（单位：分）
	@Column(name = "pay_money", nullable = false)
	private BigInteger payMoney;
	//  支付方式
	@Column(name = "pay_mode", nullable = false)
	private String payMode;
	// 支付银行
	@Column(name = "pay_bank", nullable = false)
	private String payBank;
	// 收银台流水号
	@Column(name = "cashier_no", nullable = false)
	private String cashierNo;
	// 支付供应商商户号
	@Column(name = "merchant_code", nullable = false)
	private String merchantCode;
	// 支付请求时间14位支付时间
	@Column(name = "pay_req_time", nullable = false)
	private String  payReqTime;
	// 支付时间14位支付时间
	@Column(name = "pay_time", nullable = false)
	private String payTime;
	//支付状态
	@Column(name = "status", nullable = false)
	private Integer status;
	//充值日期
	@Column(name = "time", nullable = false)
	private Date time;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;


	public int getChargeId() {
		return chargeId;
	}

	public void setChargeId(int chargeId) {
		this.chargeId = chargeId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigInteger getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigInteger payMoney) {
		this.payMoney = payMoney;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getPayBank() {
		return payBank;
	}

	public void setPayBank(String payBank) {
		this.payBank = payBank;
	}

	public String getCashierNo() {
		return cashierNo;
	}

	public void setCashierNo(String cashierNo) {
		this.cashierNo = cashierNo;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getPayReqTime() {
		return payReqTime;
	}

	public void setPayReqTime(String payReqTime) {
		this.payReqTime = payReqTime;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusString() {

		String statusName = "";
		if (this.status == Charge.Status.PAY_SUCCESS.getValue()) {
			statusName = "支付成功";
		} else if (status == Charge.Status.PAYING.getValue()) {
			statusName = "支付中";
		} else {
			statusName = "支付失败";
		}

		return statusName;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public enum Status {
		PAYING(1), PAY_SUCCESS(0), PAY_FAIL(-1);
		private Integer value;

		Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
	// 页面传过来的状态
	public enum State {
		PAYING(1), PAY_SUCCESS(2), PAY_All(0);
		private Integer value;

		State(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	// 支付方式
	public enum PayMode {
		// 010-网银支付,030-分期支付,020-快捷支付,040-扫码支付,050-支付平台,060-企业银行,070-美盈宝
		NET_BANK("010"), QUICK_PAY("020"), INSTALMENT("030"), QRCODE_PAY("040"), PLATFORM_PAY("050"),
		ENTERPRISE_PAY("060"), GOME_PAY("070");
		private String value;

		PayMode(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
