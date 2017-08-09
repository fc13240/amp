package com.gomeplus.amp.ad.manager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.ads.AdsGroupOperations;
import com.gomeplus.adm.common.api.ads.model.AdsGroup;
import com.gomeplus.adm.common.util.TimeUtil;
import com.gomeplus.amp.ad.dao.AdvertisementDao;
import com.gomeplus.amp.ad.dao.BidDao;
import com.gomeplus.amp.ad.dao.CampaignDao;
import com.gomeplus.amp.ad.dao.CampaignExtDao;
import com.gomeplus.amp.ad.dao.FlightAdvertisementDao;
import com.gomeplus.amp.ad.dao.FlightDao;
import com.gomeplus.amp.ad.dao.PublisherDao;
import com.gomeplus.amp.ad.dao.SlotDao;
import com.gomeplus.amp.ad.dao.StrategyDao;
import com.gomeplus.amp.ad.form.FlightForm;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.Bid;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.CampaignExt;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Material;
import com.gomeplus.amp.ad.model.Publisher;
import com.gomeplus.amp.ad.model.Slot;
import com.gomeplus.amp.ad.model.Strategy;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;

/**
 * 
 * @author wangwei01
 *
 */
@Component
public class FixedCpcFlightManager {

	@Autowired
	private FlightDao flightDao;
	@Autowired
	private StrategyDao strategyDao;
	@Autowired
	private AdvertisementDao advertisementDao;
	@Autowired
	private PublisherDao publisherDao;
	@Autowired
	private SlotDao slotDao;
	@Autowired
	private FlightAdvertisementDao flightAdvertisementDao;
	@Autowired
	private CampaignDao campaignDao;
	@Autowired
	private CampaignExtDao campaignExtDao;
	@Autowired
	private BidDao bidDao;
	@Autowired
	private static Logger logger = LoggerFactory.getLogger(FixedCpcFlightManager.class);

	private AdsGroupOperations adsGroupOperations;

	public FixedCpcFlightManager() {
		adsGroupOperations = new AdsGroupOperations();
	}

	private Gson gson = new Gson();

	/**
	 * 添加投放单元
	 * 
	 * flightMap 数据格式
	 * 
	 * campaignId 投放计划Id Number 必须
	 * name 投放单元名称 String 必须
	 * platform 投放平台（1-APP 2-WAP 3-PC） Number 必须
	 * type 投放类型（1-商品推广 2-活动推广） Number 必须
	 * timeType 时间定向类型 （0-不限 1-定向） Number 必须
	 * time 时间定向 timeType为1时，必填 Array 非必须
	 * regionType 地域定向类型（0-不限 1-定向） Number 必须
	 * region 地域定向 regionType为1时，必填 Array 非必须
	 * advertisements 广告
	 * 
	 * @param flightMap
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public HashMap<String, Object> save(FlightForm flightForm) throws Exception {
		logger.info("============flight create");
		logger.info("flightForm: " + flightForm);
		String environment = System.getenv().get("ENVIRONMENT");
		HashMap<String, Object> saveResult = new LinkedHashMap<String, Object>();

		Integer campaignId = flightForm.getCampaignId();
		String name = flightForm.getName();
		Integer platform = flightForm.getPlatform();
		Integer type = flightForm.getType();
		Integer timeType = flightForm.getTimeType();
		Map<String, Object> time = flightForm.getTime();
		Integer regionType = flightForm.getRegionType();
		List<String> region = flightForm.getRegion();

		Long rebateBid = flightForm.getRebateBid();
		Long videoRebate = flightForm.getVideoRebate();
		Long researchRebate = flightForm.getResearchRebate();
		Integer questionnaireId = flightForm.getSurveyId();
		Integer questionnaireTotal = flightForm.getValidQuestionnaireNum();
		Integer questionnaireTotalLimited = flightForm.getQuestionnaireTotalLimited();
		
		List<Map<String, Object>> advertisements = flightForm.getAdvertisements();
		Integer productLine = 0;
		productLine = flightForm.getProductLine();

		logger.info("campaignId: " + campaignId);
		logger.info("name: " + name);
		logger.info("platform: " + platform);
		logger.info("type: " + type);
		logger.info("timeType: " + timeType);
		logger.info("time: " + time);
		logger.info("regionType: " + regionType);
		logger.info("region: " + region);
		logger.info("rebateBid: " + rebateBid);
		logger.info("advertisements: " + advertisements);

		List<Integer> advertisementIds = new ArrayList<Integer>();

		// @todo 获取广告出价
		// Long adBid = new Long(30);
		// Long adBid = new Long(10);
		// Long adBid = new Long(0);
		Long adBid = new Long(FlightAdvertisement.getAdBidValue());
		for (Map<String, Object> advertisement : advertisements) {
			Integer advertisementId = (Integer) advertisement.get("advertisementId");
			advertisementIds.add(advertisementId);

			// 广告返利频道优化一期 广告位固定
			if (environment.equals("production")) {
				if (10038 == advertisementId) {
					//好东西
					advertisementIds.add(10039);
				} else if (10040 == advertisementId) {
					//有腔调
					advertisementIds.add(10041);
				} else if (10062 == advertisementId) {
					//好店
					advertisementIds.add(10063);
				} else if (10064 == advertisementId) {
					//视频
					advertisementIds.add(10065);
				} else if (10066 == advertisementId) {
					//清单
					advertisementIds.add(10067);
				} else if (10068 == advertisementId) {
					//精选
					advertisementIds.add(10069);
				} else if (10074 == advertisementId) {
					//好物
					advertisementIds.add(10075);
				}
			} else if (environment.equals("preproduction")) {
				//只能选择一个广告位
				if (10171 == advertisementId) {
					//好东西
					advertisementIds.add(10172);
				} else if (10173 == advertisementId) {
					//有腔调
					advertisementIds.add(10174);
				} else if (10175 == advertisementId) {
					//精选
					advertisementIds.add(10176);
				} else if (10177 == advertisementId) {
					//好店
					advertisementIds.add(10178);
				} else if (10179 == advertisementId) {
					//清单
					advertisementIds.add(10180);
				} else if (10181 == advertisementId) {
					//视频
					advertisementIds.add(10182);
				} else if (10172261 == advertisementId) {
					//好物
					advertisementIds.add(10172260);
				}
				
			} else if (225 == advertisementId) {
				advertisementIds.add(227);
			}

			// @todo get adBid
		}

		logger.info("advertisementIds: " + advertisementIds);

		String timeJson = gson.toJson(time);
		String regionJson = gson.toJson(region);

		logger.info("timeJson: " + timeJson);
		logger.info("regionJson: " + regionJson);
		logger.info("============flight create params success");

		// @todo 参数验证

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();

		logger.info("userId: " + userId);
		logger.info("userName: " + userName);

		Integer status = Flight.Status.NORMAL.getValue();
		Date currentTime = new Date();

		CampaignExt oldCampaignExt = campaignExtDao.getUniqueBy("campaignId", campaignId);
		Integer isContinuous = oldCampaignExt.getIsContinuous();
		Integer saleMode = oldCampaignExt.getSaleMode();
		Integer isRebate = oldCampaignExt.getIsRebate();
		String schedule = oldCampaignExt.getSchedule();

		logger.info("============old campaign ext info");
		logger.info("isContinuous: " + isContinuous);
		logger.info("saleMode: " + saleMode);
		logger.info("isRebate: " + isRebate);
		logger.info("schedule: " + schedule);

		List<Flight> flights = flightDao.getFlightsByNameCampaignId(name, campaignId);
		if (flights.size() > 0) {
			// TODO 投放单元名称重复,提示文案
			throw new RuntimeException("您已使用过\"" + name + "\"作为投放单元名称，请重命名");
		}

		Flight flight = new Flight(	campaignId, name, isContinuous, schedule, platform, type, saleMode, isRebate, status, userId,
									currentTime, currentTime);
		flight.setProductLine(productLine);
		flightDao.save(flight);

		logger.info("============flight create success");

		Integer flightId = flight.getFlightId();
		logger.info("flightId: " + flightId);

		Integer strategyStatus = Strategy.Status.NORMAL.getValue();

		Strategy strategy = new Strategy(	campaignId, flightId, regionType, regionJson, timeType, timeJson, strategyStatus,
											currentTime, currentTime);
		strategyDao.save(strategy);

		logger.info("============flight strategy create success");

		for (Integer advertisementId : advertisementIds) {
			FlightAdvertisement flightAdvertisement = new FlightAdvertisement(	flightId, advertisementId, adBid, rebateBid, videoRebate, researchRebate, questionnaireId, questionnaireTotal, questionnaireTotalLimited, currentTime, currentTime);
			flightAdvertisementDao.save(flightAdvertisement);
		}
		logger.info("============flight advertisements create success");

		// 新增投放单元后，同步数据到dsp
		Campaign campaign = campaignDao.getCampaignByCampaignId(campaignId);
		AdsGroup adsGroup = new AdsGroup();
		adsGroup.setCampaignId(campaign.getDspCampaignId());
		adsGroup.setName(name);
		adsGroup.setType(type);
		adsGroup.setBuyMode(Flight.SALEMODE.get(saleMode));
		adsGroup.setIsRebate(isRebate);
		adsGroup.setPlatform(platform);
		adsGroup.setAdBid(BigInteger.valueOf(adBid));
		adsGroup.setRebateBid(BigInteger.valueOf(rebateBid));
		adsGroup.setWatchRebateBid(BigInteger.valueOf(videoRebate));
		adsGroup.setResearchRebateBid(BigInteger.valueOf(researchRebate));
		// 有效问卷收集总数限制 0-不限、1-自定义
		if (0 == questionnaireTotalLimited) {
			adsGroup.setValidQuestionnaireNum(Integer.MAX_VALUE);
		} else {
			adsGroup.setValidQuestionnaireNum(questionnaireTotal);
		}
		adsGroup.setQuestionnaireId(questionnaireId);
		adsGroup.setProductTypes(getProductTypesByAdvertisementIds(advertisementIds));
		adsGroup.setAdType(productLine);
		
		List<Integer> dspAdvertisementIds = new ArrayList<Integer>();
		List<Advertisement> oldAdvertisements = advertisementDao.getAdvertisementsByAdvertisementIds(advertisementIds);
		for (Advertisement oldAdvertisement : oldAdvertisements) {
			dspAdvertisementIds.add(oldAdvertisement.getDspAdvertisementId());
		}

		// TODO 广告单元集合
		// adsGroup.setAdUnitIds(Arrays.asList(new Integer[] { 1, 2, 3 }));
		// adsGroup.setAdUnitIds(Arrays.asList(new Integer[] { 10075 }));
		adsGroup.setAdUnitIds(dspAdvertisementIds);

		if (isContinuous == 1 && campaign.getIsUnlimited() == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(campaign.getStartTime()) + "~");
		} else if (isContinuous == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(campaign.getStartTime()) + "~"
					+ TimeUtil.dateToString(campaign.getEndTime()));
		} else {
			adsGroup.setOnlineDate(getScheduleForAds(gson.fromJson(schedule, new ArrayList<List<String>>().getClass())));
		}
		// 投放系统中的实体状态 0-启用 1-暂停
		adsGroup.setStatus(0);

		Map<String, Object> strategies = new HashMap<String, Object>();
		strategies.put("region", region);
		strategies.put("time", time);
		adsGroup.setStrategies(strategies);
		adsGroup.setCreateUser(userName);
		ApiResponse response = adsGroupOperations.create(adsGroup);
		Map<String, Object> data = response.getData();
		Integer dspFlightId = Integer.parseInt((String) (data.get("id")));

		logger.info("dspFlightId: " + dspFlightId);

		flight.setDspFlightId(dspFlightId);
		flightDao.update(flight);

		logger.info("============flight create send to ads success");
		saveResult.put("flightId", flightId);
		return saveResult;
	}

	/**
	 * 修改投放单元
	 * 
	 * flightMap 数据格式
	 * 
	 * campaignId 投放计划Id Number 必须
	 * flightId 投放单元Id Number 必须
	 * name 投放单元名称 String 必须
	 * platform 投放平台（1-APP 2-WAP 3-PC） Number 必须
	 * type 投放类型（1-商品推广 2-活动推广） Number 必须
	 * timeType 时间定向类型 （0-不限 1-定向） Number 必须
	 * time 时间定向 timeType为1时，必填 Array 非必须
	 * regionType 地域定向类型（0-不限 1-定向） Number 必须
	 * region 地域定向 regionType为1时，必填 Array 非必须
	 * advertisements 广告
	 * 
	 * @param flightMap
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void update(FlightForm flightForm) throws Exception {

		logger.info("============flight update");
		logger.info("flightForm: " + flightForm);
		String environment = System.getenv().get("ENVIRONMENT");

		Integer flightId = flightForm.getFlightId();
		String name = flightForm.getName();
		Integer platform = flightForm.getPlatform();
		Integer type = flightForm.getType();
		Integer timeType = flightForm.getTimeType();
		Map<String, Object> time = flightForm.getTime();
		Integer regionType = flightForm.getRegionType();
		List<String> region = flightForm.getRegion();
		Long rebateBid = flightForm.getRebateBid();
		Long videoRebate = flightForm.getVideoRebate();
		Long researchRebate = flightForm.getResearchRebate();
		Integer questionnaireId = flightForm.getSurveyId();
		Integer questionnaireTotal = flightForm.getValidQuestionnaireNum();
		Integer questionnaireTotalLimited = flightForm.getQuestionnaireTotalLimited();
				
		List<Map<String, Object>> advertisements = flightForm.getAdvertisements();

		logger.info("flightId: " + flightId);
		logger.info("name: " + name);
		logger.info("platform: " + platform);
		logger.info("type: " + type);
		logger.info("timeType: " + timeType);
		logger.info("time: " + time);
		logger.info("regionType: " + regionType);
		logger.info("region: " + region);
		logger.info("rebateBid: " + rebateBid);
		logger.info("advertisements: " + advertisements);

		// @todo 获取广告出价
		// Long adBid = new Long(30);
		// Long adBid = new Long(10);
		// Long adBid = new Long(0);
		Long adBid = new Long(FlightAdvertisement.getAdBidValue());
		List<Integer> advertisementIds = new ArrayList<Integer>();
		for (Map<String, Object> advertisement : advertisements) {
			Integer advertisementId = (Integer) advertisement.get("advertisementId");
			advertisementIds.add(advertisementId);

			// 广告返利频道优化一期 广告位固定
			if (environment.equals("production")) {
				if (10038 == advertisementId) {
					//好东西
					advertisementIds.add(10039);
				} else if (10040 == advertisementId) {
					//有腔调
					advertisementIds.add(10041);
				} else if (10062 == advertisementId) {
					//好店
					advertisementIds.add(10063);
				} else if (10064 == advertisementId) {
					//视频
					advertisementIds.add(10065);
				} else if (10066 == advertisementId) {
					//清单
					advertisementIds.add(10067);
				} else if (10068 == advertisementId) {
					//精选
					advertisementIds.add(10069);
				} else if (10074 == advertisementId) {
					//好物
					advertisementIds.add(10075);
				}
			} else if (environment.equals("preproduction")) {
				// 只能选择一个广告位
				if (10171 == advertisementId) {
					// 好东西
					advertisementIds.add(10172);
				} else if (10173 == advertisementId) {
					// 有腔调
					advertisementIds.add(10174);
				} else if (10175 == advertisementId) {
					// 精选
					advertisementIds.add(10176);
				} else if (10177 == advertisementId) {
					// 好店
					advertisementIds.add(10178);
				} else if (10179 == advertisementId) {
					// 清单
					advertisementIds.add(10180);
				} else if (10181 == advertisementId) {
					// 视频
					advertisementIds.add(10182);
				} else if (10172261 == advertisementId) {
					//好物
					advertisementIds.add(10172260);
				}
			} else if (225 == advertisementId) {
				advertisementIds.add(227);
			}

			// @todo get adBid
		}

		logger.info("advertisementIds: " + advertisementIds);

		String timeJson = gson.toJson(time);
		String regionJson = gson.toJson(region);

		logger.info("timeJson: " + timeJson);
		logger.info("regionJson: " + regionJson);
		logger.info("============flight update params success");

		// @todo 参数验证

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();

		logger.info("userId: " + userId);
		logger.info("userName: " + userName);

		Date currentTime = new Date();

		Flight oldFlight = flightDao.get(flightId);

		Integer isContinuous = oldFlight.getIsContinuous();
		String schedule = oldFlight.getSchedule();

		oldFlight.setName(name);
		oldFlight.setPlatform(platform);
		oldFlight.setType(type);
		oldFlight.setUpdateTime(currentTime);

		flightDao.update(oldFlight);
		logger.info("============flight update success");

		Strategy oldStrategy = strategyDao.getUniqueBy("flightId", flightId);
		oldStrategy.setRegionType(regionType);
		oldStrategy.setRegion(regionJson);
		oldStrategy.setTimeType(timeType);
		oldStrategy.setTime(timeJson);
		oldStrategy.setUpdateTime(currentTime);

		strategyDao.update(oldStrategy);
		logger.info("============flight strategy update success");

		// 保存投放单元下的广告
		List<FlightAdvertisement> oldFlightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		for (FlightAdvertisement oldFlightAdvertisement : oldFlightAdvertisements) {
			oldFlightAdvertisement.setStatus(FlightAdvertisement.Status.DELETE.getValue());
			oldFlightAdvertisement.setUpdateTime(currentTime);
			flightAdvertisementDao.update(oldFlightAdvertisement);
		}
		logger.info("============flight old advertisements delete success");

		// @todo 获取广告出价
		for (Integer advertisementId : advertisementIds) {
			FlightAdvertisement flightAdvertisement = new FlightAdvertisement(flightId, advertisementId, adBid,	rebateBid, videoRebate, researchRebate, questionnaireId, questionnaireTotal, questionnaireTotalLimited, currentTime, currentTime);
			flightAdvertisementDao.save(flightAdvertisement);
		}
		logger.info("============flight new advertisements save success");

		Campaign campaign = campaignDao.getCampaignByCampaignId(oldFlight.getCampaignId());
		AdsGroup adsGroup = new AdsGroup();
		adsGroup.setCampaignId(campaign.getDspCampaignId());
		adsGroup.setId(oldFlight.getDspFlightId());
		adsGroup.setName(name);
		adsGroup.setType(type);
		adsGroup.setBuyMode(Flight.SALEMODE.get(oldFlight.getSaleMode()));
		adsGroup.setIsRebate(oldFlight.getIsRebate());
		adsGroup.setPlatform(platform);
		adsGroup.setAdBid(BigInteger.valueOf(adBid));
		adsGroup.setRebateBid(BigInteger.valueOf(rebateBid));
		adsGroup.setWatchRebateBid(BigInteger.valueOf(videoRebate));
		adsGroup.setResearchRebateBid(BigInteger.valueOf(researchRebate));
		// 有效问卷收集总数限制 0-不限、1-自定义
		if (0 == questionnaireTotalLimited) {
			adsGroup.setValidQuestionnaireNum(Integer.MAX_VALUE);
		} else {
			adsGroup.setValidQuestionnaireNum(questionnaireTotal);
		}
		adsGroup.setQuestionnaireId(questionnaireId);
		adsGroup.setProductTypes(getProductTypesByAdvertisementIds(advertisementIds));
		adsGroup.setAdType(oldFlight.getProductLine());

		List<Integer> dspAdvertisementIds = new ArrayList<Integer>();
		List<Advertisement> oldAdvertisements = advertisementDao.getAdvertisementsByAdvertisementIds(advertisementIds);
		for (Advertisement oldAdvertisement : oldAdvertisements) {
			dspAdvertisementIds.add(oldAdvertisement.getDspAdvertisementId());
		}

		adsGroup.setAdUnitIds(dspAdvertisementIds);
		if (isContinuous == 1 && campaign.getIsUnlimited() == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(campaign.getStartTime()) + "~");
		} else if (isContinuous == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(campaign.getStartTime()) + "~"
					+ TimeUtil.dateToString(campaign.getEndTime()));
		} else {
			adsGroup.setOnlineDate(getScheduleForAds(gson.fromJson(schedule, new ArrayList<List<String>>().getClass())));
		}
		// 投放系统中的实体状态 0-启用 1-暂停
		if (Flight.Status.NORMAL.getValue() == oldFlight.getStatus()) {
			adsGroup.setStatus(0);
		} else if (Flight.Status.SUSPEND.getValue() == oldFlight.getStatus()) {
			adsGroup.setStatus(1);
		}
		// 定向
		Map<String, Object> strategies = new HashMap<String, Object>();
		strategies.put("region", region);
		strategies.put("time", time);
		adsGroup.setStrategies(strategies);

		adsGroup.setUpdateUser(userName);
		adsGroupOperations.update(adsGroup);

		logger.info("============flight update send to ads success");
	}

	/**
	 * 构建ads所需的排期数据
	 * 
	 * @param scheduleStrings
	 * @return
	 */
	private String getScheduleForAds(List<List<String>> scheduleStrings) {
		StringBuilder schedule = new StringBuilder();
		if (CollectionUtils.isEmpty(scheduleStrings)) {
			return schedule.toString();
		}
		for (List<String> scheduleString : scheduleStrings) {
			String start = TimeUtil.dateToString(new Date(Long.parseLong(scheduleString.get(0))));
			String end = TimeUtil.dateToString(new Date(Long.parseLong(scheduleString.get(1))));
			if (start.equals(end)) {
				schedule.append(start).append(",");
			} else {
				schedule.append(start).append("~").append(end).append(",");
			}
		}
		return schedule.substring(0, schedule.length() - 1).toString();
	}
	
	public FlightForm getFlightByFlightId(Integer flightId) throws Exception {
		FlightForm data = new FlightForm();

		Flight flight = flightDao.get(flightId);
		if (null == flight) {
			throw new Exception("flight not exist flightId: " + flightId);
		}

		data.setCampaignId(flight.getCampaignId());
		data.setFlightId(flight.getFlightId());
		data.setName(flight.getName());
		data.setPlatform(flight.getPlatform());
		data.setType(flight.getType());
		data.setIsRebate(flight.getIsRebate());

		Strategy strategy = strategyDao.getUniqueBy("flightId", flightId);
		if (null != strategy) {
			data.setTimeType(strategy.getTimeType());
			data.setRegionType(strategy.getRegionType());
			data.setTime(gson.fromJson(strategy.getTime(), new LinkedHashMap<String, Object>().getClass()));
			data.setRegion(gson.fromJson(strategy.getRegion(), new ArrayList<String>().getClass()));
		}

		List<Integer> advertisementIds = new ArrayList<Integer>();
		List<Integer> slotIds = new ArrayList<Integer>();
		List<Integer> publisherIds = new ArrayList<Integer>();
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);

		// 没有广告就默认返利出价为0.01元
		Long rebateBid = 10L;
		Long videoRebate = 10L;
		Long researchRebate = 30L;
		if (!CollectionUtils.isEmpty(flightAdvertisements)) {
			rebateBid = flightAdvertisements.get(0).getRebateBid();
			videoRebate = flightAdvertisements.get(0).getVideoBid();
			researchRebate = flightAdvertisements.get(0).getQuestionnaireBid();
			for (FlightAdvertisement flightAdvertisement : flightAdvertisements) {

				// 广告返利频道优化一期 广告位固定
				String environment = System.getenv().get("ENVIRONMENT");
				if (environment.equals("production") && 
						(10039 == flightAdvertisement.getAdvertisementId() 
						|| 10041 == flightAdvertisement.getAdvertisementId()
						|| 10063 == flightAdvertisement.getAdvertisementId()
						|| 10065 == flightAdvertisement.getAdvertisementId()
						|| 10067 == flightAdvertisement.getAdvertisementId()
						|| 10069 == flightAdvertisement.getAdvertisementId()
						|| 10075 == flightAdvertisement.getAdvertisementId())) {
					continue;
				} else if (environment.equals("preproduction") && 
						(10172 == flightAdvertisement.getAdvertisementId()
						|| 10174 == flightAdvertisement.getAdvertisementId()
						|| 10176 == flightAdvertisement.getAdvertisementId()
						|| 10178 == flightAdvertisement.getAdvertisementId()
						|| 10180 == flightAdvertisement.getAdvertisementId()
						|| 10182 == flightAdvertisement.getAdvertisementId()
						|| 10172260 == flightAdvertisement.getAdvertisementId())) {
					continue;
				} else if (227 == flightAdvertisement.getAdvertisementId()) {
					continue;
				}
				advertisementIds.add(flightAdvertisement.getAdvertisementId());
			}
		}
		data.setRebateBid(rebateBid);
		data.setVideoRebate(videoRebate);
		data.setResearchRebate(researchRebate);
		data.setSurveyId(flightAdvertisements.get(0).getQuestionnaireId());
		data.setValidQuestionnaireNum(flightAdvertisements.get(0).getQuestionnaireTotal());
		data.setQuestionnaireTotalLimited(flightAdvertisements.get(0).getQuestionnaireTotalLimited());
		List<Advertisement> advertisements = advertisementDao.getAdvertisementsByAdvertisementIds(advertisementIds);
		for (Advertisement advertisement : advertisements) {
			slotIds.add(advertisement.getSlotId());
		}
		List<Slot> slots = slotDao.getSlotsBySlotIds(slotIds);
		Map<Integer, Slot> slotsMap = new LinkedHashMap<Integer, Slot>();
		for (Slot slot : slots) {
			publisherIds.add(slot.getPublisherId());
			slotsMap.put(slot.getSlotId(), slot);
		}

		// 获取媒体
		Map<Integer, Publisher> publishersMap = new LinkedHashMap<Integer, Publisher>();
		if(!CollectionUtils.isEmpty(publisherIds)){
			List<Publisher> publishers = publisherDao.getPublishersByPublisherIds(publisherIds);
			for (Publisher publisher : publishers) {
				publishersMap.put(publisher.getPublisherId(), publisher);
			}			
		}

		// 构造数据
		List<Map<String, Object>> advertisementsList = new ArrayList<Map<String, Object>>();
		for (Advertisement advertisement : advertisements) {
			Map<String, Object> advertisementMap = new LinkedHashMap<String, Object>();
			//获取当前广告的出价信息
			Bid bid = bidDao.getBidByAdvertisementId(advertisement.getAdvertisementId());
			BigInteger cpcBid = BigInteger.ZERO;
			if(null != bid){
				cpcBid = bid.getCpcBid();
			}
			advertisementMap.put("advertisementId", advertisement.getAdvertisementId());
			advertisementMap.put("advertisementName", advertisement.getName());
			advertisementMap.put("generalizeType", advertisement.getGeneralizeType());
			advertisementMap.put("webpageTemplateId", advertisement.getWebpageTemplateId());
			Slot slot = slotsMap.get(advertisement.getSlotId());
			Publisher publisher = null;
			if (null != slot) {
				advertisementMap.put("productType", slot.getProductType());
				Integer currentPublisherId = slot.getPublisherId();
				publisher = publishersMap.get(currentPublisherId);
			}
			if (null != publisher) {
				advertisementMap.put("publisherId", publisher.getPublisherId());
				advertisementMap.put("publisherName", publisher.getName());
			} else {
				advertisementMap.put("publisherId", 0);
				advertisementMap.put("publisherName", "");
			}

			advertisementMap.put("width", advertisement.getWidth());
			advertisementMap.put("height", advertisement.getHeight());
			advertisementMap.put("adBid", cpcBid);
			// @todo get size
			advertisementMap.put("size", "256KB");

			advertisementsList.add(advertisementMap);
		}
		data.setAdvertisements(advertisementsList);
		return data;
	}
	
	/**
	 * 根据数据库现有数据构造AdGroup
	 * @return
	 */
	public AdsGroup buildAdGroup(Integer flightId) {
		AdsGroup adsGroup = new AdsGroup();
		Flight flight = flightDao.get(flightId);
		if (null == flight) {
			logger.info("buildAdGroup flight is null");
			return adsGroup;
		}
		Integer campaignId = flight.getCampaignId();
		Campaign campaign = campaignDao.getCampaignByCampaignId(campaignId);
		if (null == campaign) {
			logger.info("buildAdGroup campaign is null");
			return adsGroup;
		}
		CampaignExt campaignExt = campaignExtDao.getUniqueBy("campaignId", campaignId);
		if (null == campaignExt) {
			logger.info("buildAdGroup campaignExt is null");
		}
		Integer isContinuous = campaignExt.getIsContinuous();

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();
		logger.info("userId: " + userId);
		logger.info("userName: " + userName);

		adsGroup.setCampaignId(campaign.getDspCampaignId());
		adsGroup.setId(flight.getDspFlightId());
		adsGroup.setName(flight.getName());
		adsGroup.setBuyMode(Flight.SALEMODE.get(flight.getSaleMode()));
		adsGroup.setIsRebate(flight.getIsRebate());
		adsGroup.setType(flight.getType());
		adsGroup.setAdType(flight.getProductLine());
		adsGroup.setUpdateUser(userName);
		adsGroup.setPlatform(flight.getPlatform());

		// 设置广告位
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		List<Integer> dspAdvertisementIds = new ArrayList<Integer>();
		List<Integer> advertisementIds = new ArrayList<Integer>();
		for (FlightAdvertisement flightAdvertisement : flightAdvertisements) {
			advertisementIds.add(flightAdvertisement.getAdvertisementId());
		}
		List<Advertisement> advertisements = advertisementDao.getAdvertisementsByAdvertisementIds(advertisementIds);
		for (Advertisement advertisement : advertisements) {
			dspAdvertisementIds.add(advertisement.getDspAdvertisementId());
		}
		adsGroup.setProductTypes(getProductTypesByAdvertisementIds(advertisementIds));
		adsGroup.setAdUnitIds(dspAdvertisementIds);

		// 广告出价 返利出价
		if (!CollectionUtils.isEmpty(flightAdvertisements)) {
			FlightAdvertisement flightAdvertisement = flightAdvertisements.get(0);
			Long adBid = flightAdvertisement.getAdBid();
			Long rebateBid = flightAdvertisement.getRebateBid();
			adsGroup.setAdBid(BigInteger.valueOf(adBid));
			adsGroup.setRebateBid(BigInteger.valueOf(rebateBid));
		}

		// 定向
		Strategy strategy = strategyDao.getUniqueBy("flightId", flightId);
		Map<String, Object> strategies = new HashMap<String, Object>();
		String region = strategy.getRegion();
		String time = strategy.getTime();
		logger.info("region: " + region);
		logger.info("time: " + time);
		strategies.put("region", gson.fromJson(region, new ArrayList<String>().getClass()));
		strategies.put("time", gson.fromJson(time, new LinkedHashMap<String, Object>().getClass()));
		adsGroup.setStrategies(strategies);

		Integer isUnlimited = campaign.getIsUnlimited();
		Date startTime = campaign.getStartTime();
		Date endTime = campaign.getEndTime();
		if (isContinuous == 1 && isUnlimited == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(startTime) + "~");
		} else if (isContinuous == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(startTime) + "~" + TimeUtil.dateToString(endTime));
		} else {
			adsGroup.setOnlineDate(getScheduleForAds(JSON.parseObject(flight.getSchedule(), new ArrayList<List<String>>().getClass())));
		}

		// 投放系统中的实体状态 0-启用 1-暂停
		if (Flight.Status.NORMAL.getValue() == flight.getStatus()) {
			adsGroup.setStatus(0);
		} else if (Flight.Status.SUSPEND.getValue() == flight.getStatus()) {
			adsGroup.setStatus(1);
		}

		return adsGroup;
	}

	private List<Slot> getSlotsByAdvertisementIds(List<Integer> advertisementIds) {
		List<Slot> slots = new ArrayList<Slot>();
		if (CollectionUtils.isEmpty(advertisementIds)) {
			return slots;
		}
		List<Advertisement> advertisements = advertisementDao.getAdvertisementsByAdvertisementIds(advertisementIds);
		if (CollectionUtils.isEmpty(advertisements)) {
			return slots;
		}
		List<Integer> slotIds = new ArrayList<Integer>();
		for (Advertisement advertisement : advertisements) {
			slotIds.add(advertisement.getSlotId());
		}
		if (CollectionUtils.isEmpty(slotIds)) {
			return slots;
		}
		slots = slotDao.getSlotsBySlotIds(slotIds);
		return slots;
	}
	
	private List<Integer> getProductTypesByAdvertisementIds(List<Integer> advertisementIds) {
		Set<Integer> productTypes = new HashSet<Integer>();
		List<Slot> slots = getSlotsByAdvertisementIds(advertisementIds);
		if (CollectionUtils.isEmpty(slots)) {
			return new ArrayList<Integer>(productTypes);
		}
		for (Slot slot : slots) {
			productTypes.add(slot.getProductType());
		}
		return new ArrayList<Integer>(productTypes);
	}
	
	/**
	 * 根据flightId获取相应广告位的productType
	 * @param flightId
	 * @return
	 */
	public Integer getProductTypeByFlightId(Integer flightId) {
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		logger.info("getProductTypeByFlightId flightAdvertisements: " + flightAdvertisements);
		List<Integer> advertisementIds = new ArrayList<Integer>();
		for (FlightAdvertisement flightAdvertisement : flightAdvertisements) {
			advertisementIds.add(flightAdvertisement.getAdvertisementId());
		}
		List<Integer> productTypes = getProductTypesByAdvertisementIds(advertisementIds);
		logger.info("getProductTypeByFlightId productTypes: " + productTypes);
		if (CollectionUtils.isEmpty(productTypes)) {
			logger.info("getProductTypeByFlightId productTypes is empty");
			return 0;
		}
		//定价cpc,广告位均单选
		return productTypes.get(0);
	}
}
