package com.gomeplus.amp.ad.form;

import java.util.List;
import java.util.Map;

public class MaterialForm {

	private Integer materialId;
	private Integer flightId;
	private String productId;
	private String promotionId;
	private String name;
	private Integer type;
	private Integer linkType;
	private String title;
	private List<String> image;
	private Integer width;
	private Integer height;
	private String description;
	private String landingPage;
	private Integer webpageId;
	private List<Map<String, Object>> relatedItems;
	private Integer relatedItemStrategy;
	private Integer productLine;

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getLinkType() {
		return linkType;
	}

	public void setLinkType(Integer linkType) {
		this.linkType = linkType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}

	public List<Map<String, Object>> getRelatedItems() {
		return relatedItems;
	}

	public void setRelatedItems(List<Map<String, Object>> relatedItems) {
		this.relatedItems = relatedItems;
	}

	public Integer getRelatedItemStrategy() {
		return relatedItemStrategy;
	}

	public void setRelatedItemStrategy(Integer relatedItemStrategy) {
		this.relatedItemStrategy = relatedItemStrategy;
	}

	public Integer getProductLine() {
		return productLine;
	}

	public void setProductLine(Integer productLine) {
		this.productLine = productLine;
	}

	public List<String> getImage() {
		return image;
	}

	public void setImage(List<String> image) {
		this.image = image;
	}

	public Integer getWebpageId() {
		return webpageId;
	}

	public void setWebpageId(Integer webpageId) {
		this.webpageId = webpageId;
	}

	@Override
	public String toString() {
		return "MaterialForm [materialId=" + materialId + ", flightId=" + flightId + ", productId=" + productId
				+ ", promotionId=" + promotionId + ", name=" + name + ", type=" + type + ", linkType=" + linkType
				+ ", title=" + title + ", image=" + image + ", width=" + width + ", height=" + height + ", description="
				+ description + ", landingPage=" + landingPage + ", webpageId=" + webpageId + ", relatedItems="
				+ relatedItems + ", relatedItemStrategy=" + relatedItemStrategy + ", productLine=" + productLine + "]";
	}

}
