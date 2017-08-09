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
 * ams广告位model
 * 
 * @author wangwei01
 *
 */

@Entity
@Table(name = "ams_slot")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Slot implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "slot_id", unique = true, nullable = false)
	private Integer slotId;
	@Column(name = "dsp_slot_id", nullable = false)
	private Integer dspSlotId;
	@Column(name = "publisher_id", nullable = false)
	private Integer publisherId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "pinyin", nullable = false)
	private String pinyin;
	@Column(name = "description", nullable = false)
	private String description;
	@Column(name = "product_type", nullable = false)
	private Integer productType;
	@Column(name = "rotators", nullable = false)
	private Integer rotators;
	@Column(name = "account_id", nullable = false)
	private Integer accountId;
	@Column(name = "status", nullable = false)
	private int status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Integer getSlotId() {
		return slotId;
	}

	public void setSlotId(Integer slotId) {
		this.slotId = slotId;
	}

	public Integer getDspSlotId() {
		return dspSlotId;
	}

	public void setDspSlotId(Integer dspSlotId) {
		this.dspSlotId = dspSlotId;
	}

	public Integer getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Integer publisherId) {
		this.publisherId = publisherId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRotators() {
		return rotators;
	}

	public void setRotators(Integer rotators) {
		this.rotators = rotators;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((slotId == null) ? 0 : slotId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (!(object instanceof Slot)) {
			return false;
		}
		Slot other = (Slot) object;
		if (slotId == null) {
			if (other.getSlotId() != null) {
				return false;
			}
		} else if (!slotId.equals(other.getSlotId())) {
			return false;
		}
		return true;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
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
}
