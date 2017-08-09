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
 * 素材Model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_material")
@DynamicUpdate(true)
@DynamicInsert(true)
public class Material implements Serializable {

	private static final long serialVersionUID = 3378082234286384019L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "material_id", unique = true, nullable = false)
	private Integer materialId;
	@Column(name = "dsp_material_id", unique = true, nullable = false)
	private Integer dspMaterialId;
	@Column(name = "flight_id", nullable = false)
	private Integer flightId;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "width", nullable = false)
	private Integer width;
	@Column(name = "height", nullable = false)
	private Integer height;
	@Column(name = "link_type", nullable = false)
	private Integer linkType;
	@Column(name = "promotion_id", nullable = false)
	private String promotionId;
	@Column(name = "title", nullable = false)
	private String title;
	@Column(name = "description", nullable = false)
	private String description;
	@Column(name = "image", nullable = false)
	private String image;
	@Column(name = "click_url", nullable = false)
	private String clickUrl;
	@Column(name = "landing_page", nullable = false)
	private String landingPage;
	@Column(name = "webpage_id", nullable = false)
	private Integer webpageId;
	@Column(name = "related_items", nullable = false)
	private String relatedItems;
	@Column(name = "related_item_strategy", nullable = false)
	private Integer relatedItemStrategy;
	@Column(name = "remark", nullable = false)
	private String remark;
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	@Column(name = "type", nullable = false)
	private Integer type;
	@Column(name = "approve_status", nullable = false)
	private Integer approveStatus;
	@Column(name = "create_from", nullable = false)
	private String createFrom;
	@Column(name = "approve_role", nullable = false)
	private String approveRole;
	@Column(name = "product_line", nullable = false)
	private Integer productLine;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Material() {

	}

	public Material(Integer flightId, String name, Integer width, Integer height, String promotionId, String title,
			String description, String image, String landingPage, Integer userId, Integer type, Integer linkType,
			Integer approveStatus, Integer productLine, Integer status, Date createTime, Date updateTime) {
		super();
		this.flightId = flightId;
		this.name = name;
		this.width = width;
		this.height = height;
		this.promotionId = promotionId;
		this.title = title;
		this.description = description;
		this.image = image;
		this.landingPage = landingPage;
		this.userId = userId;
		this.type = type;
		this.linkType = linkType;
		this.approveStatus = approveStatus;
		this.productLine = productLine;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.clickUrl = "";
		this.remark = "";
		this.dspMaterialId = 0;
	}

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public Integer getDspMaterialId() {
		return dspMaterialId;
	}

	public void setDspMaterialId(Integer dspMaterialId) {
		this.dspMaterialId = dspMaterialId;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getLinkType() {
		return linkType;
	}

	public void setLinkType(Integer linkType) {
		this.linkType = linkType;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
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

	public String getClickUrl() {
		return clickUrl;
	}

	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}

	public String getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}

	public String getRelatedItems() {
		return relatedItems;
	}

	public void setRelatedItems(String relatedItems) {
		this.relatedItems = relatedItems;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(Integer approveStatus) {
		this.approveStatus = approveStatus;
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

	public Integer getProductLine() {
		return productLine;
	}

	public void setProductLine(Integer productLine) {
		this.productLine = productLine;
	}

	public Integer getWebpageId() {
		return webpageId;
	}

	public void setWebpageId(Integer webpageId) {
		this.webpageId = webpageId;
	}

	public Integer getRelatedItemStrategy() {
		return relatedItemStrategy;
	}

	public void setRelatedItemStrategy(Integer relatedItemStrategy) {
		this.relatedItemStrategy = relatedItemStrategy;
	}

	public String getCreateFrom() {
		return createFrom;
	}

	public void setCreateFrom(String createFrom) {
		this.createFrom = createFrom;
	}

	public String getApproveRole() {
		return approveRole;
	}

	public void setApproveRole(String approveRole) {
		this.approveRole = approveRole;
	}

	@Override
	public String toString() {
		return "Material [materialId=" + materialId + ", dspMaterialId=" + dspMaterialId + ", flightId=" + flightId
				+ ", name=" + name + ", width=" + width + ", height=" + height + ", linkType=" + linkType
				+ ", promotionId=" + promotionId + ", title=" + title + ", description=" + description + ", image="
				+ image + ", clickUrl=" + clickUrl + ", landingPage=" + landingPage + ", webpageId=" + webpageId
				+ ", relatedItems=" + relatedItems + ", relatedItemStrategy=" + relatedItemStrategy + ", remark="
				+ remark + ", userId=" + userId + ", type=" + type + ", approveStatus=" + approveStatus
				+ ", createFrom=" + createFrom + ", approveRole=" + approveRole + ", productLine=" + productLine
				+ ", status=" + status + ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
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

	public enum ApproveStatus {
		REJECT(-1), APPROVING(0), APPROVED(1);

		private Integer value;

		private ApproveStatus(Integer value) {
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

	public enum State {
		APPROVING(1), NORMAL(2), SUSPEND(3), REJECT(4);

		private Integer value;

		private State(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public Integer getState() {
		if (approveStatus == ApproveStatus.APPROVING.getValue()) {
			return State.APPROVING.getValue();
		} else if (approveStatus == ApproveStatus.REJECT.getValue()) {
			return State.REJECT.getValue();
		} else {
			// 只有审核通过的创意，才可以执行"启用"、"暂停"操作,本期无审核操作，新建的创意都是"审核通过"
			if (status.equals(Status.SUSPEND.getValue())) {
				return State.SUSPEND.getValue();
			} else {
				return State.NORMAL.getValue();
			}
		}
	}

	/**
	 * 素材类型枚举
	 * ams_material表type字段
	 *
	 */
	public enum Type {
		IMAGE(1), IMAGE_TEXT(2), VIDEO(3), FLASH(4), WORD(5);

		private Integer value;

		private Type(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	/**
	 * 链接类型枚举
	 * ams_material表link_type字段
	 *
	 */
	public enum LinkType {
		SHOP(0), ITEM(1), URL(2), CIRCLE(3), TOPIC(4), SHARE(5), ACTIVITY(6), FLAGSHIP(7), CMS(8), VIDEO(9);

		private Integer value;

		private LinkType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
}
