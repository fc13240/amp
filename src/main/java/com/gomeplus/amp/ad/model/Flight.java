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
 * 投放单元model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_flight")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Flight implements Serializable {

	private static final long serialVersionUID = 4295723671885291735L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "flight_id", unique = true, nullable = false)
	private Integer flightId;
	@Column(name = "dsp_flight_id", nullable = false)
	private Integer dspFlightId;
	@Column(name = "campaign_id", nullable = false)
	private Integer campaignId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "is_continuous", nullable = false)
	private Integer isContinuous;
	@Column(name = "schedule", nullable = false)
	private String schedule;
	@Column(name = "platform", nullable = false)
	private Integer platform;
	@Column(name = "type", nullable = false)
	private Integer type;
	@Column(name = "sale_mode", nullable = false)
	private Integer saleMode;
	@Column(name = "daily_ad_budget", nullable = false)
	private Long dailyAdBudget;
	@Column(name = "daily_rebate_budget", nullable = false)
	private Long dailyRebateBudget;
	@Column(name = "is_rebate", nullable = false)
	private Integer isRebate;
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

	public final static Map<Integer, String> SALEMODE;

	static {
		SALEMODE = new HashMap<Integer, String>();
		SALEMODE.put(1, "CPM");
		SALEMODE.put(2, "CPD");
		SALEMODE.put(3, "CPC");
	}

	public Flight() {
		this.isContinuous = 0;
		this.schedule = "";
		this.platform = 0;
		this.type = 0;
		this.saleMode = 0;
		this.dailyAdBudget = 0L;
		this.dailyRebateBudget = 0L;
		this.isRebate = 0;
		this.productLine = 0;
	}

	public Flight(Integer campaignId, Integer isContinuous, Integer saleMode, Integer isRebate,
			String schedule) {
		this.campaignId = campaignId;
		this.isContinuous = isContinuous;
		this.saleMode = saleMode;
		this.isRebate = isRebate;
		this.schedule = schedule;
	}

	public Flight(Integer campaignId, String name, Integer isContinuous, String schedule, Integer type, Integer saleMode,
			Integer isRebate, Integer status, Integer userId, Date createTime, Date updateTime) {
		this.campaignId = campaignId;
		this.name = name;
		this.isContinuous = isContinuous;
		this.schedule = schedule;
		this.type = type;
		this.saleMode = saleMode;
		this.isRebate = isRebate;
		this.status = status;
		this.userId = userId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	public Flight(Integer campaignId, String name, Integer isContinuous, String schedule,
			Integer platform, Integer type, Integer saleMode, Integer isRebate, Integer status,
			Integer userId, Date createTime, Date updateTime) {
		this.campaignId = campaignId;
		this.name = name;
		this.isContinuous = isContinuous;
		this.schedule = schedule;
		this.platform = platform;
		this.type = type;
		this.saleMode = saleMode;
		this.isRebate = isRebate;
		this.status = status;
		this.userId = userId;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public Integer getDspFlightId() {
		return dspFlightId;
	}

	public void setDspFlightId(Integer dspFlightId) {
		this.dspFlightId = dspFlightId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSaleMode() {
		return saleMode;
	}

	public void setSaleMode(Integer saleMode) {
		this.saleMode = saleMode;
	}

	public Long getDailyAdBudget() {
		return dailyAdBudget;
	}

	public void setDailyAdBudget(Long dailyAdBudget) {
		this.dailyAdBudget = dailyAdBudget;
	}

	public Long getDailyRebateBudget() {
		return dailyRebateBudget;
	}

	public void setDailyRebateBudget(Long dailyRebateBudget) {
		this.dailyRebateBudget = dailyRebateBudget;
	}

	public Integer getIsRebate() {
		return isRebate;
	}

	public void setIsRebate(Integer isRebate) {
		this.isRebate = isRebate;
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
	
	public Integer getProductLine() {
		return productLine;
	}
	
	public void setProductLine(Integer productLine) {
		this.productLine = productLine;
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
	
	//广告类型
	public enum productType {
		SEARCH(1), RECOMMEND(2), AD_CHANNEL(3);

		private Integer value;

		private productType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
			}
		}
	
	public enum State {
		DELETE(-1), ALL(0), SUSPEND(1), NORMAL(2);
		
		private Integer value;
		
		private State(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return value;
		}
	}
	
	public Integer getState() {
		if (status.equals(Status.NORMAL.getValue())) {
			return State.NORMAL.getValue();
		} else if (status.equals(Status.SUSPEND.getValue())) {
			return State.SUSPEND.getValue();
		} else {
			return State.DELETE.getValue();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((dailyAdBudget == null) ? 0 : dailyAdBudget.hashCode());
		result = prime * result + ((dailyRebateBudget == null) ? 0 : dailyRebateBudget.hashCode());
		result = prime * result + ((dspFlightId == null) ? 0 : dspFlightId.hashCode());
		result = prime * result + ((flightId == null) ? 0 : flightId.hashCode());
		result = prime * result + ((isContinuous == null) ? 0 : isContinuous.hashCode());
		result = prime * result + ((isRebate == null) ? 0 : isRebate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((platform == null) ? 0 : platform.hashCode());
		result = prime * result + ((saleMode == null) ? 0 : saleMode.hashCode());
		result = prime * result + ((schedule == null) ? 0 : schedule.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Flight other = (Flight) obj;
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
		if (dspFlightId == null) {
			if (other.dspFlightId != null)
				return false;
		} else if (!dspFlightId.equals(other.dspFlightId))
			return false;
		if (flightId == null) {
			if (other.flightId != null)
				return false;
		} else if (!flightId.equals(other.flightId))
			return false;
		if (isContinuous == null) {
			if (other.isContinuous != null)
				return false;
		} else if (!isContinuous.equals(other.isContinuous))
			return false;
		if (isRebate == null) {
			if (other.isRebate != null)
				return false;
		} else if (!isRebate.equals(other.isRebate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (platform == null) {
			if (other.platform != null)
				return false;
		} else if (!platform.equals(other.platform))
			return false;
		if (saleMode == null) {
			if (other.saleMode != null)
				return false;
		} else if (!saleMode.equals(other.saleMode))
			return false;
		if (schedule == null) {
			if (other.schedule != null)
				return false;
		} else if (!schedule.equals(other.schedule))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
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
		return "Flight [flightId=" + flightId + ", dspFlightId=" + dspFlightId
				+ ", campaignId=" + campaignId + ", name=" + name
				+ ", isContinuous=" + isContinuous + ", schedule=" + schedule
				+ ", platform=" + platform + ", type=" + type + ", saleMode="
				+ saleMode + ", dailyAdBudget=" + dailyAdBudget
				+ ", dailyRebateBudget=" + dailyRebateBudget + ", isRebate="
				+ isRebate + ", status=" + status + ", userId=" + userId
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
}
