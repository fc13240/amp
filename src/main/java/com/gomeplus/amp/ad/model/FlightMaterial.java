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
 * 素材商品model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_flight_material")
@DynamicUpdate(true)
@DynamicInsert(true)
public class FlightMaterial implements Serializable {

	private static final long serialVersionUID = 5493768555250807017L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "flight_material_id", unique = true, nullable = false)
	private Integer flightMaterialId;
	@Column(name = "flight_id", nullable = false)
	private Integer flightId;
	@Column(name = "advertisement_id", nullable = false)
	private Integer advertisementId;
	@Column(name = "material_id", nullable = false)
	private Integer materialId;
	@Column(name = "status", nullable = false)
	private Integer status;
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

	public Integer getFlightMaterialId() {
		return flightMaterialId;
	}

	public void setFlightMaterialId(Integer flightMaterialId) {
		this.flightMaterialId = flightMaterialId;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public Integer getAdvertisementId() {
		return advertisementId;
	}

	public void setAdvertisementId(Integer advertisementId) {
		this.advertisementId = advertisementId;
	}

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((advertisementId == null) ? 0 : advertisementId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((flightId == null) ? 0 : flightId.hashCode());
		result = prime * result + ((flightMaterialId == null) ? 0 : flightMaterialId.hashCode());
		result = prime * result + ((materialId == null) ? 0 : materialId.hashCode());
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
		FlightMaterial other = (FlightMaterial) obj;
		if (advertisementId == null) {
			if (other.advertisementId != null)
				return false;
		} else if (!advertisementId.equals(other.advertisementId))
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
		if (flightMaterialId == null) {
			if (other.flightMaterialId != null)
				return false;
		} else if (!flightMaterialId.equals(other.flightMaterialId))
			return false;
		if (materialId == null) {
			if (other.materialId != null)
				return false;
		} else if (!materialId.equals(other.materialId))
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

}
