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

@Entity
@Table(name = "ams_flight_advertisement")
@DynamicUpdate(true)
@DynamicInsert(true)
public class FlightAdvertisement implements Serializable {

	private static final long serialVersionUID = -5075951780542037689L;
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "flight_advertisement_id", unique = true, nullable = false)
	private Integer flightAdvertisementId;
	@Column(name = "flight_id", nullable = false)
	private Integer flightId;
	@Column(name = "advertisement_id", nullable = false)
	private Integer advertisementId;
	@Column(name = "ad_bid", nullable = false)
	private Long adBid;
	@Column(name = "rebate_bid", nullable = false)
	private Long rebateBid;
	@Column(name = "video_bid", nullable = false)
	private Long videoBid;
	@Column(name = "questionnaire_bid", nullable = false)
	private Long questionnaireBid;
	@Column(name = "questionnaire_id", nullable = false)
	private Integer questionnaireId;
	@Column(name = "questionnaire_total", nullable = false)
	private Integer questionnaireTotal;
	@Column(name = "questionnaire_total_limited", nullable = false)
	private Integer questionnaireTotalLimited;
	@Column(name = "ad_group", nullable = false)
	private Integer adGroup;
	@Column(name = "wireless_ad_bid_ratio", nullable = false)
	private Integer wirelessAdBidRatio;
	@Column(name = "status", nullable = false)
	private int status;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	@Transient
	private Advertisement advertisement;
	
	public static Integer getAdBidValue() {
		// 广告出价
		String environment = System.getenv().get("ENVIRONMENT");
		if (environment.equals("production")) {
			return 0;
		} else if (environment.equals("preproduction")) {
			return 1;
		} else {
			return 1;
		}
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

	public FlightAdvertisement() {

	}
		
	public FlightAdvertisement(Integer flightId, Integer advertisementId, Long rebateBid, Date createTime, Date updateTime) {
		this.flightId = flightId;
		this.advertisementId = advertisementId;
		this.rebateBid = rebateBid;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	public FlightAdvertisement(Integer flightId, Integer advertisementId, Long adBid, Long rebateBid, Long videoBid,
			Long questionnaireBid, Integer questionnaireId, Integer questionnaireTotal, Integer questionnaireTotalLimited, Date createTime,
			Date updateTime) {
		this.flightId = flightId;
		this.advertisementId = advertisementId;
		this.adBid = adBid;
		this.rebateBid = rebateBid;
		this.videoBid = videoBid;
		this.questionnaireBid = questionnaireBid;
		this.questionnaireId = questionnaireId;
		this.questionnaireTotal = questionnaireTotal;
		this.questionnaireTotalLimited = questionnaireTotalLimited;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Integer getFlightAdvertisementId() {
		return flightAdvertisementId;
	}

	public void setFlightAdvertisementId(Integer flightAdvertisementId) {
		this.flightAdvertisementId = flightAdvertisementId;
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

	public Long getAdBid() {
		return adBid;
	}

	public void setAdBid(Long adBid) {
		this.adBid = adBid;
	}

	public Long getRebateBid() {
		return rebateBid;
	}

	public void setRebateBid(Long rebateBid) {
		this.rebateBid = rebateBid;
	}

	public Long getVideoBid() {
		return videoBid;
	}

	public void setVideoBid(Long videoBid) {
		this.videoBid = videoBid;
	}

	public Long getQuestionnaireBid() {
		return questionnaireBid;
	}

	public void setQuestionnaireBid(Long questionnaireBid) {
		this.questionnaireBid = questionnaireBid;
	}

	public Integer getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(Integer questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public Integer getQuestionnaireTotal() {
		return questionnaireTotal;
	}

	public void setQuestionnaireTotal(Integer questionnaireTotal) {
		this.questionnaireTotal = questionnaireTotal;
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

	public Advertisement getAdvertisement() {
		return advertisement;
	}

	public void setAdvertisement(Advertisement advertisement) {
		this.advertisement = advertisement;
	}
	
	public Integer getAdGroup() {
		return adGroup;
	}
	
	public void setAdGroup(Integer adGroup) {
		this.adGroup = adGroup;
	}

	public Integer getWirelessAdBidRatio() {
		return wirelessAdBidRatio;
	}
	
	public void setWirelessAdBidRatio(Integer wirelessAdBidRatio) {
		this.wirelessAdBidRatio = wirelessAdBidRatio;
	}

	public Integer getQuestionnaireTotalLimited() {
		return questionnaireTotalLimited;
	}
	
	public void setQuestionnaireTotalLimited(Integer questionnaireTotalLimited) {
		this.questionnaireTotalLimited = questionnaireTotalLimited;
	}

	// 资源位类型 （1-搜索位   2-推荐位  3-广告频道  4-探索频道 ）
	public enum VariableAdGroup {
		SEARCH(1), RECOMMEND(2), MINISITE(3), EXPLORE(4);

		private Integer value;

		private VariableAdGroup(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "FlightAdvertisement [flightAdvertisementId=" + flightAdvertisementId + ", flightId=" + flightId
				+ ", advertisementId=" + advertisementId + ", adBid=" + adBid + ", rebateBid=" + rebateBid + ", adGroup=" + adGroup
				+ ", wirelessAdBidRatio=" + wirelessAdBidRatio + ", status=" + status + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", advertisement=" + advertisement + "]";
	}
	
}