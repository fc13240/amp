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
import org.hibernate.annotations.Formula;

/**
 * 消费记录model
 * @author suna01
 *
 */
@Entity
@Table(name = "ams_expense")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Expense implements Serializable {
	
	private static final long serialVersionUID = 5162642074552789542L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "expense_id", unique = true, nullable = false)
	private Integer expenseId;
	@Column(name = "dsp_campaign_id", nullable = false)
	private Integer dspCampaignId;
	@Column(name = "dsp_flight_id", nullable = false)
	private Integer dspFlightId;
	@Column(name = "amount", nullable = false)
	private BigInteger amount;
	@Column(name = "type", nullable = false)
	private Integer type;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "time", nullable = false)
	private Date time;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;
	// 时间和类型查询条件
	@Formula("concat_ws('_', time, type)")
	private String concatedType;

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

	public Integer getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(Integer expenseId) {
		this.expenseId = expenseId;
	}

	public Integer getDspCampaignId() {
		return dspCampaignId;
	}

	public void setDspCampaignId(Integer dspCampaignId) {
		this.dspCampaignId = dspCampaignId;
	}

	public Integer getDspFlightId() {
		return dspFlightId;
	}

	public void setDspFlightId(Integer dspFlightId) {
		this.dspFlightId = dspFlightId;
	}

	public BigInteger getAmount() {
		return amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
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

	public String getConcatedType() {
		return concatedType;
	}

	public void setConcatedType(String concatedType) {
		this.concatedType = concatedType;
	}
	
	public String getTypeString() {

		String typeName = "";
		if (this.type == Expense.Type.ADVERT_ACCOUNT.getValue()) {
			typeName = "广告账户";
		} else if (type == Expense.Type.REBATE_ACCOUNT.getValue()) {
			typeName = "返利账户";
		}
		
		return typeName;
	}
}
