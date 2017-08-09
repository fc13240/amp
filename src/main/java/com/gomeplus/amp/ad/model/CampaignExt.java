package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 投放计划扩展model
 * 
 * @author baishen
 *
 */
@Entity
@Table(name = "ams_campaign_ext")
@DynamicUpdate(true)
@DynamicInsert(true)
public class CampaignExt implements Serializable {

	private static final long serialVersionUID = 4295723671885291735L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "campaign_ext_id", unique = true, nullable = false)
	private Integer campaignExtId;
	@Column(name = "campaign_id", nullable = false)
	private Integer campaignId;
	@Column(name = "is_continuous", nullable = false)
	private Integer isContinuous;
	@Column(name = "schedule", nullable = false)
	private String schedule;
	@Column(name = "sale_mode", nullable = false)
	private Integer saleMode;
	@Column(name = "is_rebate", nullable = false)
	private Integer isRebate;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public final static Map<Integer, String> SALEMODE;

	static {
		SALEMODE = new HashMap<Integer, String>();
		SALEMODE.put(1, "CPM");
		SALEMODE.put(2, "CPD");
		SALEMODE.put(3, "CPC");
	}

	public CampaignExt() {

	}

	public CampaignExt(Integer campaignId, Integer isContinuous, Integer saleMode, Integer isRebate,
			String schedule, Date createTime, Date updateTime) {
		this.campaignId = campaignId;
		this.isContinuous = isContinuous;
		this.saleMode = saleMode;
		this.isRebate = isRebate;
		this.schedule = schedule;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getCampaignExtId() {
		return campaignExtId;
	}

	public void setCampaignExtId(Integer campaignExtId) {
		this.campaignExtId = campaignExtId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getIsContinuous() {
		return isContinuous;
	}

	public void setIsContinuous(Integer isContinuous) {
		this.isContinuous = isContinuous;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Integer getSaleMode() {
		return saleMode;
	}

	public void setSaleMode(Integer saleMode) {
		this.saleMode = saleMode;
	}

	public Integer getIsRebate() {
		return isRebate;
	}

	public void setIsRebate(Integer isRebate) {
		this.isRebate = isRebate;
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
