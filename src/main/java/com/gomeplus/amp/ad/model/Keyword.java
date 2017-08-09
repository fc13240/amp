package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 关键词model
 * 
 * @author sunyunlong
 * @description
 * @parameter
 */
@Entity
@Table(name = "ams_keyword")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Keyword {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "keyword_id", unique = true, nullable = false)
	private Integer keywordId;
	@Column(name = "dmp_keyword_id", nullable = false)
	private Integer dmpKeywordId;
	@Column(name = "flight_id", nullable = false)
	private Integer flightId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "pc_bid", nullable = false)
	private Integer pcBid;
	@Column(name = "purchase_star", nullable = false)
	private float purchaseStar;
	@Column(name = "search_star", nullable = false)
	private float searchStar;
	@Column(name = "average_price", nullable = false)
	private Integer averagePrice;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "platform", nullable = false)
	private Integer platform;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Integer getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(Integer keywordId) {
		this.keywordId = keywordId;
	}

	public Integer getDmpKeywordId() {
		return dmpKeywordId;
	}

	public void setDmpKeywordId(Integer dmpKeywordId) {
		this.dmpKeywordId = dmpKeywordId;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPcBid() {
		return pcBid;
	}

	public void setPcBid(Integer pcBid) {
		this.pcBid = pcBid;
	}

	public float getPurchaseStar() {
		return purchaseStar;
	}

	public void setPurchaseStar(float purchaseStar) {
		this.purchaseStar = purchaseStar;
	}

	public float getSearchStar() {
		return searchStar;
	}

	public void setSearchStar(float searchStar) {
		this.searchStar = searchStar;
	}
	
	public Integer getAveragePrice() {
		return averagePrice;
	}
	
	public void setAveragePrice(Integer averagePrice) {
		this.averagePrice = averagePrice;
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

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
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
