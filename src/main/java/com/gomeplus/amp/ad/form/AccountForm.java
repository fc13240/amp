package com.gomeplus.amp.ad.form;

import java.math.BigDecimal;

/**
 * @author: liuchen
 * @date: 2017-01-04 14:02
 * @description: 接收充值对象
 */
public class AccountForm {

	private BigDecimal amount;
	private Integer type;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
