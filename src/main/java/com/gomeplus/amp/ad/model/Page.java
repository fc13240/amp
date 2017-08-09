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
 * 页面表model
 * @author lifei01
 *
 */

@Entity
@Table(name = "ams_webpage")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Page implements Serializable {
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "webpage_id", unique = true, nullable = false)
	private Integer pageId;
	@Column(name = "webpage_template_id", nullable = false)
	private Integer pageTemplateId;
	@Column(name = "platform", nullable = false)
	private Integer platform;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "title", nullable = false)
	private String title;
	@Column(name = "description", nullable = false)
	private String description;
	@Column(name = "image", nullable = false)
	private String image;
	@Column(name = "landing_page", nullable = false)
	private String landingPage;
	@Column(name = "background_color", nullable = false)
	private String backgroundColor;
	@Column(name = "card_title", nullable = false)
	private String cardTitle;
	@Column(name = "card_image", nullable = false)
	private String cardImage;
	@Column(name = "card_description", nullable = false)
	private String cardDescription;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "create_time", nullable = false)
	private Date createTime;	
	@Column(name = "update_time", nullable = false)
	private Date updateTime;
	@Column(name = "publish_time", nullable = false)
	private Date publishTime;

	@Column(name = "shop_id", nullable = false)
	private String shopId;
	@Column(name = "video_id", nullable = false)
	private Long videoId;
	@Column(name = "promotion_type", nullable = false)
	private Integer promotionType;
	@Column(name = "use_default_image", nullable = false)
	private Integer useDefaultImage;
	@Column(name = "hash", nullable = false)
	private String hash;


	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public Integer getPageTemplateId() {
		return pageTemplateId;
	}

	public void setPageTemplateId(Integer pageTemplateId) {
		this.pageTemplateId = pageTemplateId;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getCardTitle() {
		return cardTitle;
	}

	public void setCardTitle(String cardTitle) {
		this.cardTitle = cardTitle;
	}

	public String getCardImage() {
		return cardImage;
	}

	public void setCardImage(String cardImage) {
		this.cardImage = cardImage;
	}

	public String getCardDescription() {
		return cardDescription;
	}

	public void setCardDescription(String cardDescription) {
		this.cardDescription = cardDescription;
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

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Integer getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(Integer promotionType) {
		this.promotionType = promotionType;
	}

	public Integer getUseDefaultImage() {
		return useDefaultImage;
	}

	public void setUseDefaultImage(Integer useDefaultImage) {
		this.useDefaultImage = useDefaultImage;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public enum Status {
		DELETE(-1), DRAFT(0), PUBLISHED(1), PUBLISHING(2);

		private Integer value;

		private Status(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum PromotionType {
		PRODUCT(1), SHOP(2);

		private Integer value;

		private PromotionType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum UseDefaultImage {
		DEFAULT(0), CUSTOM(1);

		private Integer value;

		private UseDefaultImage(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "Page [pageId=" + pageId + ", pageTemplateId=" + pageTemplateId + ", platform=" + platform + ", name="
				+ name + ", title=" + title + ", description=" + description + ", image=" + image + ", landingPage="
				+ landingPage + ", backgroundColor=" + backgroundColor + ", cardTitle=" + cardTitle + ", cardImage="
				+ cardImage + ", cardDescription=" + cardDescription + ", status=" + status + ", userId=" + userId
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", publishTime=" + publishTime
				+ ", shopId=" + shopId + ", videoId=" + videoId + ", promotionType=" + promotionType
				+ ", useDefaultImage=" + useDefaultImage + ", hash=" + hash + "]";
	}

}
