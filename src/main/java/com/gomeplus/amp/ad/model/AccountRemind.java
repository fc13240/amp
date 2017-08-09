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
 * 账户余额提醒model
 *
 * @author baishen
 */
@Entity
@Table(name = "ams_account_remind")
@DynamicUpdate(true)
@DynamicInsert(true)
public class AccountRemind implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "account_remind_id", unique = true, nullable = false)
	private Integer accountRemindId;
	@Column(name = "is_open", nullable = false)
	private Integer isOpen;
	@Column(name = "remind_amount", nullable = false)
	private Long remindAmount;
	@Column(name = "is_sms", nullable = false)
	private Integer isSms;
	@Column(name = "mobile", nullable = false)
	private String mobile;
	@Column(name = "is_email", nullable = false)
	private Integer isEmail;
	@Column(name = "email", nullable = false)
	private String email;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "create_time", nullable = false)
	private Date createTime;	
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public enum IS_OPEN {
		NO(0), YES(0);

		private Integer value;

		private IS_OPEN(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum IS_SMS {
		NO(0), YES(0);

		private Integer value;

		private IS_SMS(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum IS_EMAIL {
		NO(0), YES(0);

		private Integer value;

		private IS_EMAIL(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}


	public AccountRemind() {

	}

	public AccountRemind(Integer isOpen, Long remindAmount, Integer isSms, String mobile, Integer isEmail,
					String email, Integer userId, Date createTime, Date updateTime) {
		this.isOpen = isOpen;
		this.remindAmount = remindAmount;
		this.isSms = isSms;
		this.mobile = mobile;
		this.isEmail = isEmail;
		this.email = email;
		this.userId = userId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getAccountRemindId() {
		return accountRemindId;
	}

	public void setAccountRemindId(Integer accountRemindId) {
		this.accountRemindId = accountRemindId;
	}

	public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

	public Long getRemindAmount() {
		return remindAmount;
	}

	public void setRemindAmount(Long remindAmount) {
		this.remindAmount = remindAmount;
	}

	public Integer getIsSms() {
		return isSms;
	}

	public void setIsSms(Integer isSms) {
		this.isSms = isSms;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(Integer isEmail) {
		this.isEmail = isEmail;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
