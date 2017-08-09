package com.gomeplus.amp.ad.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 刊例价model
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_price")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Price implements Serializable {

	private static final long serialVersionUID = 2599927613899985630L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "price_id", unique = true, nullable = false)
	private Integer priceId;
	@Column(name = "advertisement_id", nullable = false)
	private Integer advertisementId;
	@Column(name = "time", nullable = false)
	private Date time;
	@Column(name = "cpm_price", nullable = false)
	private BigInteger cpmPrice;
	@Column(name = "cpd_price", nullable = false)
	private BigInteger cpdPrice;
	@Column(name = "cpc_price", nullable = false)
	private BigInteger cpcPrice;
	@Column(name = "account_id", nullable = false)
	private Integer accountId;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;


	public Integer getPriceId() {
		return priceId;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}

	public Integer getAdvertisementId() {
		return advertisementId;
	}

	public void setAdvertisementId(Integer advertisementId) {
		this.advertisementId = advertisementId;
	}

	public Date getTime() {
		return new Date(time.getTime());
	}

	public void setTime(Date time) {
		this.time = time != null ? new Date(time.getTime()) : null;
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

	public BigInteger getCpmPrice() {
		return cpmPrice;
	}

	public void setCpmPrice(BigInteger cpmPrice) {
		this.cpmPrice = cpmPrice;
	}

	public BigInteger getCpdPrice() {
		return cpdPrice;
	}

	public void setCpdPrice(BigInteger cpdPrice) {
		this.cpdPrice = cpdPrice;
	}

	public BigInteger getCpcPrice() {
		return cpcPrice;
	}

	public void setCpcPrice(BigInteger cpcPrice) {
		this.cpcPrice = cpcPrice;
	}



	public enum SALE_MODE {
		CPM(1), CPD(2), CPC(3);
		private Integer value;

		private SALE_MODE(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

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
}
