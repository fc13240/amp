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

/**
 * 定向model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_strategy")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Strategy implements Serializable {

	private static final long serialVersionUID = -1376395530052296566L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "strategy_id", unique = true, nullable = false)
	private Integer strategyId;
	@Column(name = "campaign_id", nullable = false)
	private Integer campaignId;
	@Column(name = "flight_id", nullable = false)
	private Integer flightId;
	@Column(name = "region_type", nullable = false)
	private Integer regionType;
	@Column(name = "region", nullable = false)
	private String region;
	@Column(name = "time_type", nullable = false)
	private Integer timeType;
	@Column(name = "time", nullable = false)
	private String time;
	@Column(name = "age_type", nullable = false)
	private Integer ageType;
	@Column(name = "age", nullable = false)
	private String age;
	@Column(name = "gender_type", nullable = false)
	private Integer genderType;
	@Column(name = "gender", nullable = false)
	private String gender;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;


	public Strategy() {

	}

	public Strategy (Integer campaignId, Integer flightId, Integer regionType, String region,
				Integer timeType, String time, Integer status, Date createTime, Date updateTime) {
		this.campaignId = campaignId;
		this.flightId = flightId;
		this.regionType = regionType;
		this.region = region;
		this.timeType = timeType;
		this.time = time;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(Integer strategyId) {
		this.strategyId = strategyId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public Integer getRegionType() {
		return regionType;
	}

	public void setRegionType(Integer regionType) {
		this.regionType = regionType;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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
	
	public Integer getAgeType() {
		return ageType;
	}

	public void setAgeType(Integer ageType) {
		this.ageType = ageType;
	}

	public String getAge() {
		return age;
	}
	
	public void setAge(String age) {
		this.age = age;
	}
	
	public Integer getGenderType() {
		return genderType;
	}
	
	public void setGenderType(Integer genderType) {
		this.genderType = genderType;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((flightId == null) ? 0 : flightId.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((regionType == null) ? 0 : regionType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((strategyId == null) ? 0 : strategyId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((timeType == null) ? 0 : timeType.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
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
		Strategy other = (Strategy) obj;
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
		if (flightId == null) {
			if (other.flightId != null)
				return false;
		} else if (!flightId.equals(other.flightId))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (regionType == null) {
			if (other.regionType != null)
				return false;
		} else if (!regionType.equals(other.regionType))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (strategyId == null) {
			if (other.strategyId != null)
				return false;
		} else if (!strategyId.equals(other.strategyId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (timeType == null) {
			if (other.timeType != null)
				return false;
		} else if (!timeType.equals(other.timeType))
			return false;
		if (updateTime == null) {
			if (other.updateTime != null)
				return false;
		} else if (!updateTime.equals(other.updateTime))
			return false;
		return true;
	}

}
