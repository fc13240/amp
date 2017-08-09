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
 * 出价表
 * 
 * @author sunyunlong
 * @description
 * @parameter
 */
@Entity
@Table(name = "ams_bid")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Bid implements Serializable {

	private static final long serialVersionUID = -4947667396629712502L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "bid_id", unique = true, nullable = false)
	private Integer bidId;
	@Column(name = "advertisement_id", nullable = false)
	private Integer advertisementId;
	@Column(name = "cpc_bid", nullable = false)
	private BigInteger cpcBid;
	@Column(name = "account_id", nullable = false)
	private Integer accountId;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "start_time", nullable = false)
	private Date startTime;
	@Column(name = "end_time", nullable = false)
	private Date endTime;
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

	public Integer getBidId() {
		return bidId;
	}

	public void setBidId(Integer bidId) {
		this.bidId = bidId;
	}

	public Integer getAdvertisementId() {
		return advertisementId;
	}

	public void setAdvertisementId(Integer advertisementId) {
		this.advertisementId = advertisementId;
	}

	public BigInteger getCpcBid() {
		return cpcBid;
	}

	public void setCpcBid(BigInteger cpcBid) {
		this.cpcBid = cpcBid;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
