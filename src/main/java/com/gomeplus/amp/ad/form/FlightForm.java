package com.gomeplus.amp.ad.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightForm {

	// 投放单元Id
	private Integer flightId;
	// 投放计划Id
	private Integer campaignId;
	// 投放单元名称
	private String name;
	// 设备类型（无线、PC）
	private Integer platform;
	// 投放类型（1-商品推广、2-活动推广）
	private Integer type;
	// 是否连续（源于ampaign_ext表）
	private Integer isContinuous;
	// 排期（源于ampaign_ext表）
	private String schedule;
	// 售卖类型 （源于campaign_ext表 1-CPM、2-CPD、 3-CPC）
	private Integer saleMode;
	// 是否返利（源于campaign_ext表 0-否 1-是）
	private Integer isRebate;
	// 广告日预算
	private Long dailyAdBudget;
	// 返利日预算
	private Long dailyRebateBudget;
	// 产品线类型
	private Integer productLine;
	// 时间定向类型
	private Integer timeType;
	// 时间定向
	private Map<String, Object> time;
	// 地域定向类型
	private Integer regionType;
	// 地域定向
	private List<String> region;
	// 人群年龄定向类型
	private Integer ageType;
	// 人群年龄定向
	private List<String> age;
	// 人群性别定向类型
	private Integer genderType;
	// 人群性别定向
	private List<String> gender;
	// 返利出价
	private Long rebateBid;
	// 视频浏览返利出价
	private Long videoRebate;
	// 调研返利出价
	private Long researchRebate;
	// 有效问卷数量
	private Integer validQuestionnaireNum;
	// 有效问卷数量限制
	private Integer questionnaireTotalLimited;
	// 问卷id
	private Integer surveyId;
	// 广告位
	private List<Map<String, Object>> advertisements;
	// 资源位类型
	private List<Integer> advertisementGroups;
	// 关键词
	private List<Map<String, Object>> keywords;
	// pc端推荐广告出价
	private Float adBid;
	// 无线端广告出价系数
	private Float wirelessAdBidRatio;

	public FlightForm() {
		this.platform = 0;
		this.isContinuous = 0;
		this.schedule = "";
		this.isRebate = 0;
		this.dailyAdBudget = 0L;
		this.dailyRebateBudget = 0L;
		this.timeType = 0;
		this.time = new HashMap<String, Object>();
		this.regionType = 0;
		this.region = new ArrayList<String>();
		this.ageType = 0;
		this.age = new ArrayList<String>();
		this.genderType = 0;
		this.gender = new ArrayList<String>();
		this.rebateBid = 0l;
		this.videoRebate = 0l;
		this.researchRebate = 0l;
		this.validQuestionnaireNum = 0;
		this.surveyId = 0;
		this.adBid = 0.0F;
		this.advertisements = new ArrayList<Map<String, Object>>();
		this.advertisementGroups = new ArrayList<Integer>();
		this.keywords = new ArrayList<Map<String, Object>>();
		this.wirelessAdBidRatio = 0F;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	public Integer getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Integer campaignId) {
		this.campaignId = campaignId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIsContinuous() {
		return isContinuous;
	}

	public void setIsContinuous(Integer isContinuous) {
		this.isContinuous = isContinuous;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Integer getSaleMode() {
		return saleMode;
	}

	public void setSaleMode(Integer saleMode) {
		this.saleMode = saleMode;
	}

	public Integer getIsRebate() {
		return isRebate;
	}

	public void setIsRebate(Integer isRebate) {
		this.isRebate = isRebate;
	}

	public Long getDailyAdBudget() {
		return dailyAdBudget;
	}

	public void setDailyAdBudget(Long dailyAdBudget) {
		this.dailyAdBudget = dailyAdBudget;
	}

	public Long getDailyRebateBudget() {
		return dailyRebateBudget;
	}

	public void setDailyRebateBudget(Long dailyRebateBudget) {
		this.dailyRebateBudget = dailyRebateBudget;
	}

	public Integer getProductLine() {
		return productLine;
	}

	public void setProductLine(Integer productLine) {
		this.productLine = productLine;
	}

	public Integer getTimeType() {
		return timeType;
	}

	public void setTimeType(Integer timeType) {
		this.timeType = timeType;
	}

	public Map<String, Object> getTime() {
		return time;
	}

	public void setTime(Map<String, Object> time) {
		this.time = time;
	}

	public Integer getRegionType() {
		return regionType;
	}

	public void setRegionType(Integer regionType) {
		this.regionType = regionType;
	}

	public List<String> getRegion() {
		return region;
	}

	public void setRegion(List<String> region) {
		this.region = region;
	}

	public List<String> getAge() {
		return age;
	}

	public void setAge(List<String> age) {
		this.age = age;
	}

	public List<String> getGender() {
		return gender;
	}

	public void setGender(List<String> gender) {
		this.gender = gender;
	}

	public Integer getAgeType() {
		return ageType;
	}

	public void setAgeType(Integer ageType) {
		this.ageType = ageType;
	}

	public Integer getGenderType() {
		return genderType;
	}

	public void setGenderType(Integer genderType) {
		this.genderType = genderType;
	}

	public List<Map<String, Object>> getAdvertisements() {
		return advertisements;
	}

	public void setAdvertisements(List<Map<String, Object>> advertisements) {
		this.advertisements = advertisements;
	}

	public List<Integer> getAdvertisementGroups() {
		return advertisementGroups;
	}

	public void setAdvertisementGroups(List<Integer> advertisementGroups) {
		this.advertisementGroups = advertisementGroups;
	}

	public List<Map<String, Object>> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<Map<String, Object>> keywords) {
		this.keywords = keywords;
	}

	public Long getRebateBid() {
		return rebateBid;
	}

	public void setRebateBid(Long rebateBid) {
		this.rebateBid = rebateBid;
	}

	public Long getVideoRebate() {
		return videoRebate;
	}

	public void setVideoRebate(Long videoRebate) {
		this.videoRebate = videoRebate;
	}

	public Long getResearchRebate() {
		return researchRebate;
	}

	public void setResearchRebate(Long researchRebate) {
		this.researchRebate = researchRebate;
	}

	public Integer getValidQuestionnaireNum() {
		return validQuestionnaireNum;
	}

	public void setValidQuestionnaireNum(Integer validQuestionnaireNum) {
		this.validQuestionnaireNum = validQuestionnaireNum;
	}

	public Integer getQuestionnaireTotalLimited() {
		return questionnaireTotalLimited;
	}

	public void setQuestionnaireTotalLimited(Integer questionnaireTotalLimited) {
		this.questionnaireTotalLimited = questionnaireTotalLimited;
	}

	public Integer getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}

	public Float getAdBid() {
		return adBid;
	}

	public void setAdBid(Float adBid) {
		this.adBid = adBid;
	}

	public Float getWirelessAdBidRatio() {
		return wirelessAdBidRatio;
	}

	public void setWirelessAdBidRatio(Float wirelessAdBidRatio) {
		this.wirelessAdBidRatio = wirelessAdBidRatio;
	}

	@Override
	public String toString() {
		return "FlightForm [flightId=" + flightId + ", campaignId=" + campaignId + ", name=" + name + ", platform=" + platform
				+ ", type=" + type + ", isContinuous=" + isContinuous + ", schedule=" + schedule + ", saleMode=" + saleMode
				+ ", isRebate=" + isRebate + ", dailyAdBudget=" + dailyAdBudget + ", dailyRebateBudget=" + dailyRebateBudget
				+ ", productLine=" + productLine + ", timeType=" + timeType + ", time=" + time + ", regionType=" + regionType
				+ ", region=" + region + ", ageType=" + ageType + ", age=" + age + ", genderType=" + genderType + ", gender="
				+ gender + ", rebateBid=" + rebateBid + ", videoRebate=" + videoRebate + ", researchRebate=" + researchRebate
				+ ", validQuestionnaireNum=" + validQuestionnaireNum + ", questionnaireTotalLimited=" + questionnaireTotalLimited
				+ ", surveyId=" + surveyId + ", advertisements=" + advertisements + ", advertisementGroups=" + advertisementGroups
				+ ", keywords=" + keywords + ", adBid=" + adBid + ", wirelessAdBidRatio=" + wirelessAdBidRatio + "]";
	}

}
