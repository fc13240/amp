package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.gomeplus.adm.common.util.TimeUtil;

/**
 * 投放计划model
 * 
 * @author wangwei01
 *
 */

@Entity
@Table(name = "ams_campaign")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Campaign implements Serializable {

	private static final long serialVersionUID = -1461413017091363751L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "campaign_id", unique = true, nullable = false)
	private Integer campaignId;
	@Column(name = "dsp_campaign_id", unique = true, nullable = false)
	private Integer dspCampaignId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "start_time", nullable = false)
	private Date startTime;
	@Column(name = "end_time", nullable = false)
	private Date endTime;
	@Column(name = "is_immediate", nullable = false)
	private Integer isImmediate;
	@Column(name = "is_unlimited", nullable = false)
	private Integer isUnlimited;
	@Column(name = "ad_limited", nullable = false)
	private Integer adLimited;
	@Column(name = "next_ad_limited", nullable = false)
	private Integer nextAdLimited;
	@Column(name = "daily_ad_budget", nullable = false)
	private Long dailyAdBudget;
	@Column(name = "next_daily_ad_budget", nullable = false)
	private Long nextDailyAdBudget;
	@Column(name = "rebate_limited", nullable = false)
	private Integer rebateLimited;
	@Column(name = "next_rebate_limited", nullable = false)
	private Integer nextRebateLimited;
	@Column(name = "daily_rebate_budget", nullable = false)
	private Long dailyRebateBudget;
	@Column(name = "next_daily_rebate_budget", nullable = false)
	private Long nextDailyRebateBudget;
	@Column(name = "product_line", nullable = false)
	private Integer productLine;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Campaign() {

	}

	public Campaign(String name, Date startTime, Date endTime, Integer isImmediate, Integer isUnlimited, Integer adLimited,
			Long dailyAdBudget, Integer rebateLimited, Long dailyRebateBudget, Integer status, Integer userId, Date createTime,
			Date updateTime) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isImmediate = isImmediate;
		this.isUnlimited = isUnlimited;
		this.adLimited = adLimited;
		this.dailyAdBudget = dailyAdBudget;
		this.rebateLimited = rebateLimited;
		this.dailyRebateBudget = dailyRebateBudget;
		this.status = status;
		this.userId = userId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getDspCampaignId() {
		return dspCampaignId;
	}

	public void setDspCampaignId(Integer dspCampaignId) {
		this.dspCampaignId = dspCampaignId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getIsImmediate() {
		return isImmediate;
	}

	public void setIsImmediate(Integer isImmediate) {
		this.isImmediate = isImmediate;
	}

	public Integer getIsUnlimited() {
		return isUnlimited;
	}

	public void setIsUnlimited(Integer isUnlimited) {
		this.isUnlimited = isUnlimited;
	}

	public Integer getAdLimited() {
		return adLimited;
	}

	public void setAdLimited(Integer adLimited) {
		this.adLimited = adLimited;
	}

	public Long getDailyAdBudget() {
		return dailyAdBudget;
	}

	public void setDailyAdBudget(Long dailyAdBudget) {
		this.dailyAdBudget = dailyAdBudget;
	}

	public Integer getRebateLimited() {
		return rebateLimited;
	}

	public void setRebateLimited(Integer rebateLimited) {
		this.rebateLimited = rebateLimited;
	}

	public Long getDailyRebateBudget() {
		return dailyRebateBudget;
	}

	public void setDailyRebateBudget(Long dailyRebateBudget) {
		this.dailyRebateBudget = dailyRebateBudget;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getNextDailyAdBudget() {
		return nextDailyAdBudget;
	}

	public void setNextDailyAdBudget(Long nextDailyAdBudget) {
		this.nextDailyAdBudget = nextDailyAdBudget;
	}

	public Long getNextDailyRebateBudget() {
		return nextDailyRebateBudget;
	}

	public void setNextDailyRebateBudget(Long nextDailyRebateBudget) {
		this.nextDailyRebateBudget = nextDailyRebateBudget;
	}

	/**
	 * 页面投放计划状态
	 * 1、暂停（包括余额不足）
	 * 2、有效
	 * 3、过期
	 * 4、未开始
	 */
	public Integer getState() {
		// 未开始的投放计划可以执行"暂停" 过期的不能"暂停"
		if (status.equals(Status.SUSPEND.getValue())) {
			return State.SUSPEND.getValue();
		}
		// 未开始、过期的都不能执行"启用"
		Date currentTime = new Date();
		if (TimeUtil.formateDate(currentTime).compareTo(startTime) < 0) {
			return State.UNSTART.getValue();
		} else if (TimeUtil.formateDate(currentTime).compareTo(endTime) > 0) {
			return State.FINISHED.getValue();
		} else if (status.equals(Status.NORMAL.getValue())) {
			return State.NORMAL.getValue();
		} else {
			return State.DELETE.getValue();
		}
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
	
	public Integer getProductLine() {
		return productLine;
	}

	public void setProductLine(Integer productLine) {
		this.productLine = productLine;
	}

	public Integer getNextAdLimited() {
		return nextAdLimited;
	}

	public void setNextAdLimited(Integer nextAdLimited) {
		this.nextAdLimited = nextAdLimited;
	}

	public Integer getNextRebateLimited() {
		return nextRebateLimited;
	}

	public void setNextRebateLimited(Integer nextRebateLimited) {
		this.nextRebateLimited = nextRebateLimited;
	}

	/**
	 * 产品线类型
	 * 2、定价CPC
	 * 3、竞价CPC
	 */
	public enum ProductLine {
		FIXED_BID_CPC(2), BID_CPC(3);

		private Integer value;

		private ProductLine(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum Status {
		DELETE(-1), SUSPEND(0), NORMAL(1);

		private Integer value;

		private Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	/**
	 * 页面投放计划状态
	 * 1、暂停（包括余额不足）
	 * 2、有效
	 * 3、过期
	 * 4、未开始
	 * 5、预算用完
	 * 6、不在投放时间段
	 * 7、余额不足
	 */
	public enum State {
		DELETE(-1), NORMAL(2), SUSPEND(1), FINISHED(3), UNSTART(4), RUN_OUT_OF_BUDGET(5), NOT_IN_FLIGHT(6), LACK_BLANCE(7);

		private Integer value;

		private State(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adLimited == null) ? 0 : adLimited.hashCode());
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((dailyAdBudget == null) ? 0 : dailyAdBudget.hashCode());
		result = prime * result + ((dailyRebateBudget == null) ? 0 : dailyRebateBudget.hashCode());
		result = prime * result + ((dspCampaignId == null) ? 0 : dspCampaignId.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((isImmediate == null) ? 0 : isImmediate.hashCode());
		result = prime * result + ((isUnlimited == null) ? 0 : isUnlimited.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rebateLimited == null) ? 0 : rebateLimited.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Campaign other = (Campaign) obj;
		if (adLimited == null) {
			if (other.adLimited != null)
				return false;
		} else if (!adLimited.equals(other.adLimited))
			return false;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (dailyAdBudget == null) {
			if (other.dailyAdBudget != null)
				return false;
		} else if (!dailyAdBudget.equals(other.dailyAdBudget))
			return false;
		if (dailyRebateBudget == null) {
			if (other.dailyRebateBudget != null)
				return false;
		} else if (!dailyRebateBudget.equals(other.dailyRebateBudget))
			return false;
		if (dspCampaignId == null) {
			if (other.dspCampaignId != null)
				return false;
		} else if (!dspCampaignId.equals(other.dspCampaignId))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (isImmediate == null) {
			if (other.isImmediate != null)
				return false;
		} else if (!isImmediate.equals(other.isImmediate))
			return false;
		if (isUnlimited == null) {
			if (other.isUnlimited != null)
				return false;
		} else if (!isUnlimited.equals(other.isUnlimited))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rebateLimited == null) {
			if (other.rebateLimited != null)
				return false;
		} else if (!rebateLimited.equals(other.rebateLimited))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Campaign [campaignId=" + campaignId + ", dspCampaignId=" + dspCampaignId + ", name=" + name
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", isImmediate=" + isImmediate
				+ ", isUnlimited=" + isUnlimited + ", adLimited=" + adLimited + ", nextAdLimited=" + nextAdLimited
				+ ", dailyAdBudget=" + dailyAdBudget + ", nextDailyAdBudget=" + nextDailyAdBudget + ", rebateLimited="
				+ rebateLimited + ", nextRebateLimited=" + nextRebateLimited + ", dailyRebateBudget="
				+ dailyRebateBudget + ", nextDailyRebateBudget=" + nextDailyRebateBudget + ", productLine="
				+ productLine + ", status=" + status + ", userId=" + userId + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}

}
