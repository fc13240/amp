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
 * 创意跟单model
 * 
 * @author wangwei01
 *
 */
@Entity
@Table(name = "ams_material_item")
@DynamicUpdate(true)
@DynamicInsert(true)
public class MaterialItem implements Serializable {

	private static final long serialVersionUID = 255314478321263409L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "material_item_id", unique = true, nullable = false)
	private Integer materialItemId;
	@Column(name = "material_id", nullable = false)
	private Integer materialId;
	@Column(name = "product_id", nullable = false)
	private String productId;
	@Column(name = "sku_id", nullable = false)
	private String skuId;
	@Column(name = "shop_id", nullable = false)
	private String shopId;
	@Column(name = "description", nullable = false)
	private String description;
	@Column(name = "image", nullable = false)
	private String image;
	@Column(name = "is_user_define", nullable = false)
	private Integer isUserDefine;
	@Column(name = "type", nullable = false)
	private Integer type;
	@Column(name = "status", nullable = false)
	private Integer status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	public Integer getMaterialItemId() {
		return materialItemId;
	}

	public void setMaterialItemId(Integer materialItemId) {
		this.materialItemId = materialItemId;
	}

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
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

	public Integer getIsUserDefine() {
		return isUserDefine;
	}

	public void setIsUserDefine(Integer isUserDefine) {
		this.isUserDefine = isUserDefine;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	/**
	 * 状态 -1删除 0正常
	 * @author wangwei01
	 *
	 */
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
	
	/**
	 * 是否用户自定义  0-用户自定义  1-非用户自定义
	 * @author wangwei01
	 *
	 */
	public enum UserDefine {
		CUSTOM(0), DEFAULT(1);

		private Integer value;

		private UserDefine(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
	
	/**
	 * 推广内容类型(1-商品 2-店铺)
	 * @author wangwei01
	 *
	 */
	public enum Type {
		ITEM(1), SHOP(2);

		private Integer value;

		private Type(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "MaterialItem [materialItemId=" + materialItemId + ", materialId=" + materialId + ", productId="
				+ productId + ", skuId=" + skuId + ", shopId=" + shopId + ", description=" + description + ", image="
				+ image + ", isUserDefine=" + isUserDefine + ", type=" + type + ", status=" + status + ", createTime="
				+ createTime + ", updateTime=" + updateTime + "]";
	}

}
