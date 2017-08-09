package com.gomeplus.amp.ad.model;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * 商家资质 model
 * @author DèngBīn
 */
@Entity
@Table(name = "ams_certificate")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Certificate implements Serializable {

	private static final long serialVersionUID = 4512485591235621255L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "certificate_id", unique = true, nullable = false)
	private Integer certificateId;

	// 资质名称
	@Column(name = "name", nullable = false)
	private String name;

	// 资质图片
	@Column(name = "images", nullable = false)
	private String images;

	// 开始时间
	@Column(name = "start_time", nullable = false)
	private Date startTime;

	// 结束时间
	@Column(name = "end_time", nullable = false)
	private Date endTime;

	// 资质类型 1商家资质 2品牌授权
	@Column(name = "type", nullable = false)
	private Integer type;

	// 用户id
	@Column(name = "user_id", nullable = false)
	private Integer userId;

	// 创建时间
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	// 修改时间
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public enum CertificateType {
		MERCHANT_CERT(1), BRAND_AUZ(2);

		private Integer value;

		private CertificateType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}


	public Integer getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(Integer certificateId) {
		this.certificateId = certificateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((certificateId == null) ? 0 : certificateId.hashCode());
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Certificate other = (Certificate) obj;
		if (certificateId == null) {
			if (other.certificateId != null) {
				return false;
			}
		} else if (!certificateId.equals(other.certificateId)) {
			return false;
		}
		if (createTime == null) {
			if (other.createTime != null) {
				return false;
			}
		} else if (!createTime.equals(other.createTime)) {
			return false;
		}
		if (endTime == null) {
			if (other.endTime != null) {
				return false;
			}
		} else if (!endTime.equals(other.endTime)) {
			return false;
		}
		if (images == null) {
			if (other.images != null) {
				return false;
			}
		} else if (!images.equals(other.images)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (startTime == null) {
			if (other.startTime != null) {
				return false;
			}
		} else if (!startTime.equals(other.startTime)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (updateTime == null) {
			if (other.updateTime != null) {
				return false;
			}
		} else if (!updateTime.equals(other.updateTime)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}

}
