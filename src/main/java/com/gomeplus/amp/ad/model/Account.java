package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 账户model
 * @author suna01
 *
 */

@Entity
@Table(name = "ams_account")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Account implements Serializable {
	
	private static final long serialVersionUID = -285010895588462001L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "account_id", unique = true, nullable = false)
	private Integer accountId;
	@Column(name = "dsp_account_id", nullable = false)
	private Integer dspAccountId;
	@Column(name = "balance", nullable = false)
	private BigInteger balance;
	@Column(name = "type", nullable = false)
	private Integer type;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "create_time", nullable = false)
	private Date createTime;	
	@Column(name = "update_time", nullable = false)
	private Date updateTime;
	
	public enum Status {
		DELETE(-1), NORMAL(0);

		private Integer value;

		private Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
	
	public enum Type {
		ADVERT_ACCOUNT(1), REBATE_ACCOUNT(2);

		private Integer value;

		private Type(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public Account() {

	}

	public Account(Integer dspAccountId, BigInteger balance, Integer type, Integer status, Integer userId,
					Date createTime, Date updateTime) {
		this.dspAccountId = dspAccountId;
		this.balance = balance;
		this.type = type;
		this.status = status;
		this.userId = userId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getDspAccountId() {
		return dspAccountId;
	}

	public void setDspAccountId(Integer dspAccountId) {
		this.dspAccountId = dspAccountId;
	}

	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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
	
}
