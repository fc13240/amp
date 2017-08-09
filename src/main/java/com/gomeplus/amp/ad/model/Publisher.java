package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

/**
 * 媒体 model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_publisher")
@DynamicUpdate(true)
public class Publisher implements Serializable {

	private static final long serialVersionUID = -5984547396289418818L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "publisher_id", unique = true, nullable = false)
	private Integer publisherId;
	@Column(name = "dsp_publisher_id", nullable = false)
	private Integer dspPublisherId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "url", nullable = false)
	private String url;
	@Column(name = "is_internal", nullable = false)
	private Integer isInternal;
	@Column(name = "platform", nullable = false)
	private Integer platform;
	@Column(name = "account_id", nullable = false)
	private Integer accountId;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Publisher() {
	}

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}

	public Integer getDspPublisherId() {
		return dspPublisherId;
	}

	public void setDspPublisherId(Integer dspPublisherId) {
		this.dspPublisherId = dspPublisherId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getIsInternal() {
		return isInternal;
	}

	public void setIsInternal(Integer isInternal) {
		this.isInternal = isInternal;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
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

	public enum Status {
		DELETE(-1), OFFLINE(0), ONLINE(1);

		private Integer value;

		private Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum Platform {
		APP(1), WAP(2), PC(3);

		private Integer value;

		private Platform(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "Publisher [publisherId=" + publisherId + ", name=" + name + ", accountId=" + accountId + ", status=" + status
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

}
