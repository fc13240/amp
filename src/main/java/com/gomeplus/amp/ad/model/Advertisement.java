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
 * ams广告model
 * 
 * @author wangwei01
 *
 */

@Entity
@Table(name = "ams_advertisement")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Advertisement implements Serializable {

	private static final long serialVersionUID = -5401410423317260655L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "advertisement_id", unique = true, nullable = false)
	private Integer advertisementId;
	@Column(name = "dsp_advertisement_id", nullable = false)
	private Integer dspAdvertisementId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "pinyin", nullable = false)
	private String pinyin;
	@Column(name = "slot_id", nullable = false)
	private Integer slotId;
	@Column(name = "format_id", nullable = false)
	private Integer formatId;
	@Column(name = "width", nullable = false)
	private Integer width;
	@Column(name = "height", nullable = false)
	private Integer height;
	@Column(name = "is_bid", nullable = false)
	private Integer isBid;
	@Column(name = "sale_mode", nullable = false)
	private Integer saleMode;
	@Column(name = "generalize_type", nullable = false)
	private Integer generalizeType;
	@Column(name = "template_id", nullable = false)
	private Integer templateId;
	@Column(name = "webpage_template_id", nullable = false)
	private Integer webpageTemplateId;
	@Column(name = "account_id", nullable = false)
	private Integer accountId;
	@Column(name = "status", nullable = false)
	private int status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

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

	// 推广类型
	public enum GeneralizeType {
		SHOP(0), PRODUCT(1), URL(2), CIRCLE(3), TOPIC(4), SHARE(5), ACTIVITY(6), FLAGSHIP(7), CMS(8), VIDEO(9);

		private Integer value;

		private GeneralizeType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public Integer getAdvertisementId() {
		return advertisementId;
	}

	public void setAdvertisementId(Integer advertisementId) {
		this.advertisementId = advertisementId;
	}

	public Integer getDspAdvertisementId() {
		return dspAdvertisementId;
	}

	public void setDspAdvertisementId(Integer dspAdvertisementId) {
		this.dspAdvertisementId = dspAdvertisementId;
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

	public Integer getSlotId() {
		return slotId;
	}

	public void setSlotId(Integer slotId) {
		this.slotId = slotId;
	}

	public Integer getFormatId() {
		return formatId;
	}

	public void setFormatId(Integer formatId) {
		this.formatId = formatId;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getIsBid() {
		return isBid;
	}

	public void setIsBid(Integer isBid) {
		this.isBid = isBid;
	}

	public Integer getSaleMode() {
		return saleMode;
	}

	public void setSaleMode(Integer saleMode) {
		this.saleMode = saleMode;
	}

	public Integer getGeneralizeType() {
		return generalizeType;
	}

	public void setGeneralizeType(Integer generalizeType) {
		this.generalizeType = generalizeType;
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

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getWebpageTemplateId() {
		return webpageTemplateId;
	}

	public void setWebpageTemplateId(Integer webpageTemplateId) {
		this.webpageTemplateId = webpageTemplateId;
	}

	@Override
	public String toString() {
		return "Advertisement [advertisementId=" + advertisementId + ", dspAdvertisementId=" + dspAdvertisementId + ", name=" + name
				+ ", pinyin=" + pinyin + ", slotId=" + slotId + ", formatId=" + formatId + ", width=" + width + ", height=" + height
				+ ", isBid=" + isBid + ", saleMode=" + saleMode + ", generalizeType=" + generalizeType + ", templateId="
				+ templateId + ", webpageTemplateId=" + webpageTemplateId + ", accountId=" + accountId + ", status=" + status
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

}
