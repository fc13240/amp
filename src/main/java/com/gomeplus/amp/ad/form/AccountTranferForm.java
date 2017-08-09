package com.gomeplus.amp.ad.form;

import java.math.BigInteger;

/**
 * 
 * @author sunyunlong
 * @description 资金划拨需要接收的参数
 * @parameter
 */
public class AccountTranferForm {
	// 总可用余额
	private BigInteger totalBalance;
	// 广告账户余额
	private BigInteger adBalance;
	// 返利账户余额
	private BigInteger rebateBalance;

	public BigInteger getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(BigInteger totalBalance) {
		this.totalBalance = totalBalance;
	}

	public BigInteger getAdBalance() {
		return adBalance;
	}

	public void setAdBalance(BigInteger adBalance) {
		this.adBalance = adBalance;
	}

	public BigInteger getRebateBalance() {
		return rebateBalance;
	}

	public void setRebateBalance(BigInteger rebateBalance) {
		this.rebateBalance = rebateBalance;
	}

}
