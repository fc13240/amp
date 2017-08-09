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
 * 三级类目黑名单model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_banned_category")
@DynamicUpdate(true)
@DynamicInsert(true)
public class BannedCategory {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "banned_category_id", unique = true, nullable = false)
	private Integer bannedCategoryId;
	@Column(name = "mall_category_id", nullable = false)
	private String mallCategoryId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Integer getBannedCategoryId() {
		return bannedCategoryId;
	}

	public void setBannedCategoryId(Integer bannedCategoryId) {
		this.bannedCategoryId = bannedCategoryId;
	}

	public String getMallCategoryId() {
		return mallCategoryId;
	}

	public void setMallCategoryId(String mallCategoryId) {
		this.mallCategoryId = mallCategoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return "BannedCategory [bannedCategoryId=" + bannedCategoryId + ", mallCategoryId=" + mallCategoryId + ", name=" + name
				+ ", status=" + status + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bannedCategoryId == null) ? 0 : bannedCategoryId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((mallCategoryId == null) ? 0 : mallCategoryId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		BannedCategory other = (BannedCategory) obj;
		if (bannedCategoryId == null) {
			if (other.bannedCategoryId != null)
				return false;
		} else if (!bannedCategoryId.equals(other.bannedCategoryId))
			return false;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (mallCategoryId == null) {
			if (other.mallCategoryId != null)
				return false;
		} else if (!mallCategoryId.equals(other.mallCategoryId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		return true;
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
