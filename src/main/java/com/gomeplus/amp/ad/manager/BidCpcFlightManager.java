package com.gomeplus.amp.ad.manager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.ads.AdsGroupOperations;
import com.gomeplus.adm.common.api.ads.model.AdsGroup;
import com.gomeplus.adm.common.util.TimeUtil;
import com.gomeplus.amp.ad.dao.AdvertisementDao;
import com.gomeplus.amp.ad.dao.CampaignDao;
import com.gomeplus.amp.ad.dao.CampaignExtDao;
import com.gomeplus.amp.ad.dao.FlightAdvertisementDao;
import com.gomeplus.amp.ad.dao.FlightDao;
import com.gomeplus.amp.ad.dao.KeywordDao;
import com.gomeplus.amp.ad.dao.StrategyDao;
import com.gomeplus.amp.ad.form.FlightForm;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.CampaignExt;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Keyword;
import com.gomeplus.amp.ad.model.Strategy;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;

/**
 * 
 * @author wangwei01
 *
 */
@Component
public class BidCpcFlightManager {

	@Autowired
	private FlightDao flightDao;
	@Autowired
	private StrategyDao strategyDao;
	@Autowired
	private AdvertisementDao advertisementDao;
	@Autowired
	private FlightAdvertisementDao flightAdvertisementDao;
	@Autowired
	private CampaignDao campaignDao;
	@Autowired
	private CampaignExtDao campaignExtDao;
	@Autowired
	private KeywordDao keywordDao;
	private static Logger logger = LoggerFactory.getLogger(BidCpcFlightManager.class);

	private AdsGroupOperations adsGroupOperations;

	public BidCpcFlightManager() {
		adsGroupOperations = new AdsGroupOperations();
	}

	private Gson gson = new Gson();

	/**
	 * 添加投放单元
	 * 
	 * @param flightForm
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public HashMap<String, Object> save(FlightForm flightForm) throws Exception {
		HashMap<String, Object> saveResult = new LinkedHashMap<String, Object>();
		logger.info("============bidcpc flight create");
		logger.info("flightForm: " + flightForm);

		// 投放计划Id
		Integer campaignId = flightForm.getCampaignId();
		// 投放单元名称
		String name = flightForm.getName();
		// 投放类型（商品推广、活动推广）
		Integer type = flightForm.getType();
		// 时间定向类型
		Integer timeType = flightForm.getTimeType();
		// 时间定向
		Map<String, Object> time = flightForm.getTime();
		// 地域定向类型
		Integer regionType = flightForm.getRegionType();
		// 地域定向
		List<String> region = flightForm.getRegion();
		// 年龄定向类型
		Integer ageType = flightForm.getAgeType();
		// 年龄定向
		List<String> age = flightForm.getAge();
		// 性别定向类型
		Integer genderType = flightForm.getGenderType();
		// 性别定向
		List<String> gender = flightForm.getGender();
		// 产品线
		Integer productLine = flightForm.getProductLine();
		// 关键词
		List<Map<String, Object>> keywords = flightForm.getKeywords();
		// pc端推荐广告出价
		Long adBid = new Float(100 * flightForm.getAdBid()).longValue();
		// 无线端广告出价系数
		Float wirelessAdBidRatio = flightForm.getWirelessAdBidRatio();
		if (null != wirelessAdBidRatio) {
			wirelessAdBidRatio = wirelessAdBidRatio * 100;
		}
		// 资源位类型
		List<Integer> advertisementGroups = flightForm.getAdvertisementGroups();

		logger.info("campaignId: " + campaignId);
		logger.info("name: " + name);
		logger.info("type: " + type);
		logger.info("timeType: " + timeType);
		logger.info("time: " + time);
		logger.info("regionType: " + regionType);
		logger.info("region: " + region);
		logger.info("ageType: " + ageType);
		logger.info("age: " + age);
		logger.info("genderType: " + genderType);
		logger.info("gender: " + gender);
		logger.info("productLine: " + productLine);
		logger.info("keywords: " + keywords);
		logger.info("adBid: " + adBid);
		logger.info("wirelessAdBidRatio: " + wirelessAdBidRatio);
		logger.info("advertisementGroups: " + advertisementGroups);

		String timeJson = gson.toJson(time);
		String regionJson = gson.toJson(region);
		String ageJson = gson.toJson(age);
		String genderJson = gson.toJson(gender);

		logger.info("timeJson: " + timeJson);
		logger.info("regionJson: " + regionJson);
		logger.info("ageJson: " + timeJson);
		logger.info("genderJson: " + regionJson);
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
			throw new RuntimeException("您已使用过\"" + name + "\"作为投放单元名称，请重命名");
		}

		Flight flight = new Flight();
		flight.setCampaignId(campaignId);
		flight.setName(name);
		flight.setIsContinuous(isContinuous);
		flight.setSchedule(schedule);
		flight.setType(type);
		flight.setSaleMode(saleMode);
		flight.setIsRebate(isRebate);
		flight.setStatus(status);
		flight.setUserId(userId);
		flight.setCreateTime(currentTime);
		flight.setUpdateTime(currentTime);
		flight.setProductLine(productLine);
		flightDao.save(flight);
		logger.info("============flight create success");

		Integer flightId = flight.getFlightId();
		logger.info("flightId: " + flightId);

		// 定向
		Integer strategyStatus = Strategy.Status.NORMAL.getValue();
		Strategy strategy = new Strategy(campaignId, flightId, regionType, regionJson, timeType, timeJson, strategyStatus, currentTime, currentTime);
		strategy.setAgeType(ageType);
		strategy.setAge(ageJson);
		strategy.setGenderType(genderType);
		strategy.setGender(genderJson);
		strategyDao.save(strategy);
		logger.info("============flight strategy create success");

		// 关键词
		List<Map<String,Object>> keywordsForAdsList = new ArrayList<Map<String,Object>>();
		if (!CollectionUtils.isEmpty(keywords)) {
			List<Keyword> keywordList = new ArrayList<Keyword>();
			for (Map<String, Object> keywordTmp : keywords) {
				Keyword keyword = new Keyword();
				keyword.setFlightId(flightId);
				keyword.setName((String) keywordTmp.get("name"));
				keyword.setDmpKeywordId(Integer.parseInt(keywordTmp.get("id").toString()));
				keyword.setPcBid(new Float(100 * new Float(keywordTmp.get("pcBid").toString())).intValue());
				keyword.setAveragePrice(new Double(keywordTmp.get("averagePrice").toString()).intValue() * 100);
				keyword.setPurchaseStar(new Float(keywordTmp.get("purchaseStar").toString()));
				keyword.setSearchStar(new Float(keywordTmp.get("searchStar").toString()));
				keyword.setPlatform(Integer.parseInt(keywordTmp.get("source").toString()));
				keyword.setUserId(userId);
				keyword.setStatus(Keyword.Status.NORMAL.getValue());
				keywordList.add(keyword);
				
				Map<String,Object> keywordForAds = new LinkedHashMap<String,Object>();
				keywordForAds.put("keywordId", Integer.parseInt(keywordTmp.get("id").toString()));
				keywordForAds.put("keyword", (String) keywordTmp.get("name"));
				keywordForAds.put("bid", 100 * new Float(keywordTmp.get("pcBid").toString()));
				keywordsForAdsList.add(keywordForAds);
			}
			keywordDao.batchSaveKeywords(keywordList);
		}

		// 资源位、pc端推荐出价、无线端出价系数
		if (!CollectionUtils.isEmpty(advertisementGroups)) {
			for (Integer adGroupId : advertisementGroups) {
				FlightAdvertisement flightAdvertisement = new FlightAdvertisement();
				flightAdvertisement.setFlightId(flightId);
				flightAdvertisement.setAdBid(adBid);
				flightAdvertisement.setAdGroup(adGroupId);
				flightAdvertisement.setWirelessAdBidRatio(wirelessAdBidRatio.intValue());
				flightAdvertisement.setCreateTime(new Date());
				flightAdvertisement.setUpdateTime(new Date());
				flightAdvertisement.setStatus(FlightAdvertisement.Status.NORMAL.getValue());
				logger.info("flightAdvertisementDao.save flightAdvertisement: " + flightAdvertisement);
				flightAdvertisementDao.save(flightAdvertisement);
			}
		}

		// 新增投放单元后，同步数据到dsp
		Campaign campaign = campaignDao.getCampaignByCampaignId(campaignId);
		AdsGroup adsGroup = new AdsGroup();
		adsGroup.setCampaignId(campaign.getDspCampaignId());
		adsGroup.setName(name);
		adsGroup.setType(type);
		adsGroup.setBuyMode(Flight.SALEMODE.get(saleMode));
		adsGroup.setAdType(productLine);
		adsGroup.setProductTypes(advertisementGroups);
		adsGroup.setKeywords(keywordsForAdsList);
		adsGroup.setIsRebate(isRebate);
		adsGroup.setAdBid(BigInteger.valueOf(adBid));
		adsGroup.setWirelessBidRatio(wirelessAdBidRatio / 100);
		
		if (null != adBid) {
			adsGroup.setAdBid(BigInteger.valueOf(adBid));
		}

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
	 * @param flightForm
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void update(FlightForm flightForm) throws Exception {

		logger.info("============ bidcpc flight update");
		logger.info("flightForm: " + flightForm);

		// 投放单元Id
		Integer flightId = flightForm.getFlightId();
		Flight oldFlight = flightDao.get(flightId);
		Integer campaignId = oldFlight.getCampaignId();
		// 投放单元名称
		String name = flightForm.getName();
		
		List<Flight> oldFlights = flightDao.getFlightsByNameCampaignId(name, campaignId);
		for (Flight oldFlightTmp : oldFlights) {
			if (!oldFlightTmp.getFlightId().equals(flightId) && oldFlightTmp.getName().equals(name)) {
				// TODO 投放单元名称重复,提示文案
				throw new RuntimeException("您已使用过\"" + name + "\"作为投放单元名称，请重命名");
			}
		}

		// 投放类型（商品推广、活动推广）
		Integer type = flightForm.getType();
		// 时间定向类型
		Integer timeType = flightForm.getTimeType();
		// 时间定向
		Map<String, Object> time = flightForm.getTime();
		// 地域定向类型
		Integer regionType = flightForm.getRegionType();
		// 地域定向
		List<String> region = flightForm.getRegion();
		// 年龄定向类型
		Integer ageType = flightForm.getAgeType();
		// 年龄定向
		List<String> age = flightForm.getAge();
		// 性别定向类型
		Integer genderType = flightForm.getGenderType();
		// 性别定向
		List<String> gender = flightForm.getGender();
		// 产品线
		Integer productLine = flightForm.getProductLine();
		// 关键词
		List<Map<String, Object>> keywords = flightForm.getKeywords();
		// pc端推荐广告出价
		Long adBid = new Float(100 * flightForm.getAdBid()).longValue();
		// 无线端广告出价系数
		Float wirelessAdBidRatio = flightForm.getWirelessAdBidRatio();
		if (null != wirelessAdBidRatio) {
			wirelessAdBidRatio = wirelessAdBidRatio * 100;
		}
		// 资源位类型
		List<Integer> advertisementGroups = flightForm.getAdvertisementGroups();

		logger.info("flightId: " + flightId);
		logger.info("name: " + name);
		logger.info("type: " + type);
		logger.info("timeType: " + timeType);
		logger.info("time: " + time);
		logger.info("regionType: " + regionType);
		logger.info("region: " + region);
		logger.info("ageType: " + ageType);
		logger.info("age: " + age);
		logger.info("genderType: " + genderType);
		logger.info("gender: " + gender);
		logger.info("productLine: " + productLine);
		logger.info("keywords: " + keywords);
		logger.info("adBid: " + adBid);
		logger.info("wirelessAdBidRatio: " + wirelessAdBidRatio);
		logger.info("advertisementGroups: " + advertisementGroups);

		String timeJson = gson.toJson(time);
		String regionJson = gson.toJson(region);
		String ageJson = gson.toJson(age);
		String genderJson = gson.toJson(gender);

		logger.info("timeJson: " + timeJson);
		logger.info("regionJson: " + regionJson);
		logger.info("ageJson: " + timeJson);
		logger.info("genderJson: " + regionJson);
		logger.info("============flight create params success");

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();
		logger.info("userId: " + userId);
		logger.info("userName: " + userName);

		Date currentTime = new Date();
		Integer isContinuous = oldFlight.getIsContinuous();
		String schedule = oldFlight.getSchedule();

		if (!StringUtils.isEmpty(name)) {
			oldFlight.setName(name);
		}
		if (null != type) {
			oldFlight.setType(type);
		}
		oldFlight.setUpdateTime(currentTime);

		flightDao.update(oldFlight);
		logger.info("============flight update success");

		// 修改定向
		Strategy oldStrategy = strategyDao.getUniqueBy("flightId", flightId);
		if (null != regionType) {
			oldStrategy.setRegionType(regionType);
		}
		if (null != regionJson) {
			oldStrategy.setRegion(regionJson);
		}
		if (null != timeType) {
			oldStrategy.setTimeType(timeType);
		}
		if (null != timeJson) {
			oldStrategy.setTime(timeJson);
		}
		if (null != ageJson) {
			oldStrategy.setAge(ageJson);
		}
		if (null != ageType) {
			oldStrategy.setAgeType(ageType);
		}
		if (null != genderJson) {
			oldStrategy.setGender(genderJson);
		}
		if (null != genderType) {
			oldStrategy.setGenderType(genderType);
		}
		oldStrategy.setUpdateTime(currentTime);
		strategyDao.update(oldStrategy);
		logger.info("============flight strategy update success");

		// 修改搜索/推荐资源位 及出价
		List<FlightAdvertisement> oldFlightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		for (FlightAdvertisement oldFlightAdvertisement : oldFlightAdvertisements) {
			oldFlightAdvertisement.setStatus(FlightAdvertisement.Status.DELETE.getValue());
			oldFlightAdvertisement.setUpdateTime(currentTime);
			flightAdvertisementDao.update(oldFlightAdvertisement);
		}
		logger.info("============flight old advertisements delete success");
		
		if (!CollectionUtils.isEmpty(advertisementGroups)) {
			for (Integer adGroupId : advertisementGroups) {
				FlightAdvertisement flightAdvertisement = new FlightAdvertisement();
				flightAdvertisement.setFlightId(flightId);
				flightAdvertisement.setAdBid(adBid);
				flightAdvertisement.setAdGroup(adGroupId);
				flightAdvertisement.setWirelessAdBidRatio(wirelessAdBidRatio.intValue());
				flightAdvertisement.setCreateTime(new Date());
				flightAdvertisement.setUpdateTime(new Date());
				flightAdvertisement.setStatus(FlightAdvertisement.Status.NORMAL.getValue());
				flightAdvertisementDao.save(flightAdvertisement);
			}
		}
		logger.info("============flight new advertisements save success");

		// 修改关键词
		List<Keyword> oldKeywords = keywordDao.getKeywordsByFlightId(flightId);
		if (!CollectionUtils.isEmpty(oldKeywords)) {
			keywordDao.batchDeleteKeywords(oldKeywords);
		}

		List<Map<String,Object>> keywordsForAdsList = new ArrayList<Map<String,Object>>();
		if (!CollectionUtils.isEmpty(keywords)) {
			List<Keyword> keywordList = new ArrayList<Keyword>();
			for (Map<String, Object> keywordTmp : keywords) {
				Keyword keyword = new Keyword();
				keyword.setFlightId(flightId);

				keyword.setName((String) keywordTmp.get("name"));
				keyword.setDmpKeywordId(Integer.parseInt(keywordTmp.get("id").toString()));
				keyword.setPcBid(new Float(100 * new Float(keywordTmp.get("pcBid").toString())).intValue());
				keyword.setAveragePrice(new Double(keywordTmp.get("averagePrice").toString()).intValue() * 100);
				keyword.setPurchaseStar(new Float(keywordTmp.get("purchaseStar").toString()));
				keyword.setSearchStar(new Float(keywordTmp.get("searchStar").toString()));
				keyword.setPlatform(Integer.parseInt(keywordTmp.get("source").toString()));
				keyword.setUserId(userId);
				keyword.setStatus(Keyword.Status.NORMAL.getValue());
				keywordList.add(keyword);
				
				Map<String,Object> keywordForAds = new LinkedHashMap<String,Object>();
				keywordForAds.put("keywordId", Integer.parseInt(keywordTmp.get("id").toString()));
				keywordForAds.put("keyword", (String) keywordTmp.get("name"));
				keywordForAds.put("bid", 100 * new Float(keywordTmp.get("pcBid").toString()));
				keywordsForAdsList.add(keywordForAds);
			}
			keywordDao.batchSaveKeywords(keywordList);
		}

		Campaign campaign = campaignDao.getCampaignByCampaignId(oldFlight.getCampaignId());
		AdsGroup adsGroup = buildAdGroup(flightId);
		adsGroup.setCampaignId(campaign.getDspCampaignId());
		adsGroup.setId(oldFlight.getDspFlightId());
		if (!StringUtils.isEmpty(name)) {
			adsGroup.setName(name);
		}
		if (null != type) {
			adsGroup.setType(type);
		}
		if (null != adBid) {
			adsGroup.setAdBid(BigInteger.valueOf(adBid));
		}
		if (null != wirelessAdBidRatio) {
			adsGroup.setWirelessBidRatio(wirelessAdBidRatio / 100);
		}
		if (null != keywordsForAdsList) {
			adsGroup.setKeywords(keywordsForAdsList);
		}
		if (null != productLine) {
			adsGroup.setAdType(productLine);
		}
		if (null != advertisementGroups) {
			adsGroup.setProductTypes(advertisementGroups);
		}

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
		data.setType(flight.getType());

		Strategy strategy = strategyDao.getUniqueBy("flightId", flightId);
		if (null != strategy) {
			data.setTimeType(strategy.getTimeType());
			data.setRegionType(strategy.getRegionType());
			data.setTime(gson.fromJson(strategy.getTime(), new LinkedHashMap<String, Object>().getClass()));
			data.setRegion(gson.fromJson(strategy.getRegion(), new ArrayList<String>().getClass()));
		}

		// 资源位类型
		List<Integer> advertisementGroups = new ArrayList<Integer>();
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		if (!CollectionUtils.isEmpty(flightAdvertisements)) {
			FlightAdvertisement firstFlightAdvertisement = flightAdvertisements.get(0);
			data.setWirelessAdBidRatio(firstFlightAdvertisement.getWirelessAdBidRatio().floatValue() / 100);
			for (FlightAdvertisement flightAdvertisement : flightAdvertisements) {
				advertisementGroups.add(flightAdvertisement.getAdGroup());
				if (flightAdvertisement.getAdGroup().equals(FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue())) {
					data.setAdBid(flightAdvertisement.getAdBid().floatValue() / 100);
				}
			}
		}
		data.setAdvertisementGroups(advertisementGroups);

		// 关键词
		List<Map<String, Object>> keywordList = new ArrayList<Map<String, Object>>();
		if (advertisementGroups.contains(FlightAdvertisement.VariableAdGroup.SEARCH.getValue())) {
			List<Keyword> keywords = keywordDao.getKeywordsByFlightId(flightId);
			if (!CollectionUtils.isEmpty(keywords)) {
				for (Keyword keyword : keywords) {
					Map<String, Object> keywordMap = new LinkedHashMap<String, Object>();

					keywordMap.put("name", keyword.getName());
					keywordMap.put("id", keyword.getDmpKeywordId());
					keywordMap.put("pcBid", keyword.getPcBid().floatValue() / 100);
					keywordMap.put("averagePrice", keyword.getAveragePrice());
					keywordMap.put("purchaseStar", keyword.getPurchaseStar());
					keywordMap.put("searchStar", keyword.getSearchStar());
					keywordMap.put("source", keyword.getPlatform());

					keywordList.add(keywordMap);
				}
			}
		}

		data.setKeywords(keywordList);
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
		adsGroup.setAdDailyBudget(BigInteger.valueOf(campaign.getDailyAdBudget()));
		adsGroup.setUpdateUser(userName);

		// 资源位类型
		List<Integer> advertisementGroups = new ArrayList<Integer>();
		logger.info("flightId: " + flightId);
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		if (!CollectionUtils.isEmpty(flightAdvertisements)) {
			FlightAdvertisement firstFlightAdvertisement = flightAdvertisements.get(0);
			adsGroup.setWirelessBidRatio(firstFlightAdvertisement.getWirelessAdBidRatio().floatValue() / 100);
			for (FlightAdvertisement flightAdvertisement : flightAdvertisements) {
				advertisementGroups.add(flightAdvertisement.getAdGroup());
				if (flightAdvertisement.getAdGroup().equals(FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue())) {
					adsGroup.setAdBid(BigInteger.valueOf(flightAdvertisement.getAdBid()));
				}
			}
		}
		adsGroup.setProductTypes(advertisementGroups);

		//定向
		Strategy strategy = strategyDao.getUniqueBy("flightId", flightId);
		Map<String, Object> strategies = new HashMap<String, Object>();
		String region = strategy.getRegion();
		String time = strategy.getTime();
		logger.info("region: " + region);
		logger.info("time: " + time);
		strategies.put("region", gson.fromJson(region, new ArrayList<String>().getClass()));
		strategies.put("time", gson.fromJson(time, new LinkedHashMap<String, Object>().getClass()));
		adsGroup.setStrategies(strategies);

		//关键词
		List<Keyword> keywords = keywordDao.getKeywordsByFlightId(flightId);
		List<Map<String,Object>> keywordsForAdsList = new ArrayList<Map<String,Object>>();
		if (!CollectionUtils.isEmpty(keywords)) {
			for (Keyword keyword : keywords) {
				Map<String,Object> keywordForAds = new LinkedHashMap<String,Object>();
				keywordForAds.put("keywordId", keyword.getDmpKeywordId());
				keywordForAds.put("keyword", keyword.getName());
				keywordForAds.put("bid", keyword.getPcBid().floatValue());
				keywordsForAdsList.add(keywordForAds);
			}
		}
		adsGroup.setKeywords(keywordsForAdsList);
		
		CampaignExt campaignExt = campaignExtDao.getUniqueBy("campaignId", campaignId);
		Integer isContinuous = campaignExt.getIsContinuous();
		Integer isUnlimited = campaign.getIsUnlimited();
		Date startTime = campaign.getStartTime();
		Date endTime = campaign.getEndTime();
		if (isContinuous == 1 && isUnlimited == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(startTime) + "~");
		} else if (isContinuous == 1) {
			adsGroup.setOnlineDate(TimeUtil.dateToString(startTime) + "~" + TimeUtil.dateToString(endTime));
		} else {
			adsGroup.setOnlineDate(getScheduleForAds(gson.fromJson(	flight.getSchedule(), new ArrayList<List<String>>().getClass())));
		}
		// 投放系统中的实体状态 0-启用 1-暂停
		if (Flight.Status.NORMAL.getValue() == flight.getStatus()) {
			adsGroup.setStatus(0);
		} else if (Flight.Status.SUSPEND.getValue() == flight.getStatus()) {
			adsGroup.setStatus(1);
		}

		logger.info("buildAdGroup adsGroup: " + adsGroup);
		return adsGroup;
	}
}
