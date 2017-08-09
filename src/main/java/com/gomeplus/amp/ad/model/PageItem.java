package com.gomeplus.amp.ad.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 页面表model
 * @author baishen
 */
@Entity
@Table(name = "ams_webpage_item")
@DynamicUpdate(true)
@DynamicInsert(true)
public class PageItem implements Serializable {
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "webpage_item_id", unique = true, nullable = false)
	private Integer pageItemId;
	@Column(name = "webpage_id", nullable = false)
	private Integer pageId;
	@Column(name = "product_id", nullable = false)
	private String productId;
	@Column(name = "sku_id", nullable = false)
	private String skuId;
	@Column(name = "image", nullable = false)
	private String image;
	@Column(name = "description", nullable = false)
	private String description;
	@Column(name = "create_time", nullable = false)
	private Date createTime;

	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "shop_id", nullable = false)
	private String shopId;

	@Transient
	private String url;
	@Transient
	private Double price;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getPageItemId() {
		return pageItemId;
	}

	public void setPageItemId(Integer pageItemId) {
		this.pageItemId = pageItemId;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
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