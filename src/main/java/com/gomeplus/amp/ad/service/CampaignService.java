package com.gomeplus.amp.ad.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.ads.AdsCampaignOperations;
import com.gomeplus.adm.common.api.ads.AdsGroupOperations;
import com.gomeplus.adm.common.api.ads.model.AdsCampaign;
import com.gomeplus.adm.common.api.ads.model.AdsGroup;
import com.gomeplus.adm.common.api.dmp.DmpFlightOperations;
import com.gomeplus.adm.common.exception.ApiException;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.util.TimeUtil;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.dao.AccountDao;
import com.gomeplus.amp.ad.dao.AdvertisementDao;
import com.gomeplus.amp.ad.dao.CampaignDao;
import com.gomeplus.amp.ad.dao.CampaignExtDao;
import com.gomeplus.amp.ad.dao.FlightAdvertisementDao;
import com.gomeplus.amp.ad.dao.FlightDao;
import com.gomeplus.amp.ad.dao.MaterialDao;
import com.gomeplus.amp.ad.dao.StrategyDao;
import com.gomeplus.amp.ad.manager.BidCpcFlightManager;
import com.gomeplus.amp.ad.manager.FixedCpcFlightManager;
import com.gomeplus.amp.ad.model.Account;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.CampaignExt;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Material;
import com.gomeplus.amp.ad.model.Strategy;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;

/**
 * 广告计划service
 * 
 * @author wangwei01
 *
 */
@Service
@Transactional(readOnly = true)
public class CampaignService extends BaseService<Campaign, Integer> {

	@Autowired
	private CampaignDao campaignDao;
	@Autowired
	private CampaignExtDao campaignExtDao;
	@Autowired
	private FlightDao flightDao;
	@Autowired
	private MaterialDao materialDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private StrategyDao strategyDao;
	@Autowired
	private AdvertisementDao advertisementDao;
	@Autowired
	private FlightAdvertisementDao flightAdvertisementDao;
	@Autowired
	private FixedCpcFlightManager fixedCpcFlightManager;
	@Autowired
	private BidCpcFlightManager bidCpcFlightManager;
	//service平级互调，设计原则的问题，后续修改
	@Autowired
	private ExpenseService expenseService;
	private AdsCampaignOperations adsCampaignOperations;
	private AdsGroupOperations adsGroupOperations;
	private DmpFlightOperations dmpFlightOperations;
	private Gson gson;
	private static Logger logger = LoggerFactory.getLogger(CampaignService.class);
	private static String timestampMax = "2038-01-19";

	public CampaignService() {
		adsCampaignOperations = new AdsCampaignOperations();
		adsGroupOperations = new AdsGroupOperations();
		dmpFlightOperations = new DmpFlightOperations();
		gson = new Gson();
	}

	@Override
	public HibernateDao<Campaign, Integer> getEntityDao() {
		return campaignDao;
	}

	/**
	 * 添加投放计划
	 * 
	 * 投放计划数据：投放计划名称、开始时间、结束时间、是否不限结束时间
	 *				是否限制广告日预算、广告日预算、是否限制返利日预算
	 *				返利日预算
	 * 投放单元数据：是否连续投放、售卖方式、是否返利、详细排期
	 * 
	 * @param campaignMap
	 */	
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public HashMap<String, Object> save(Map<String, Object> campaignMap) throws Exception {
		HashMap<String, Object> saveResult = new LinkedHashMap<String, Object>();
		// 投放计划数据

		logger.info("============campaign create");
		logger.info("campaignMap: " + campaignMap);

		String name = ((String) campaignMap.get("name")).trim();
		
		logger.info("name: " + name);

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		Integer dspAdAccountId = PrincipalUtil.getDspAdAccountId();
		Integer dspRebateAccountId = PrincipalUtil.getDspRebateAccountId();

		//产品线获取
		Integer productLine = 0;
		if (null != campaignMap.get("productLine")) {
			productLine = ((Double) campaignMap.get("productLine")).intValue();
		}
		
		logger.info("userId: " + userId);
		logger.info("userName: " + userName);
		logger.info("dspAdvertiserId: " + dspAdvertiserId);
		logger.info("dspAdAccountId: " + dspAdAccountId);
		logger.info("dspRebateAccountId: " + dspRebateAccountId);

		List<Campaign> oldCampaigns = campaignDao.getCampaignsByNameUserId(userId, name, productLine);
		if (oldCampaigns.size() > 0) {
			// TODO 投放计划名称重复,提示文案
			throw new RuntimeException("您已使用过\"" + name + "\"作为投放计划名称，请重命名");
		}
		
		// @todo 优化时间处理
		String startTimeValue = String.valueOf(campaignMap.get("startTime"));
		String endTimeValue = String.valueOf(campaignMap.get("endTime"));
		Date startTime = TimeUtil.formateDate(new Date(new Timestamp(new Double(startTimeValue).longValue()).getTime()));
		Date endTime = TimeUtil.formateDate(new Date(new Timestamp(new Double(endTimeValue).longValue()).getTime()));

		logger.info("startTimeValue: " + startTimeValue);
		logger.info("startTime: " + startTime);
		logger.info("endTimeValue: " + endTimeValue);
		logger.info("endTime: " + endTime);

		// 暂无是否立即开始
		Integer isImmediate = 0;
		Integer isUnlimited = ((Double)campaignMap.get("isUnlimited")).intValue();
		Integer adLimited = ((Double)campaignMap.get("adLimited")).intValue();
		Long dailyAdBudget = ((Double)campaignMap.get("dailyAdBudget")).longValue();
		Integer rebateLimited = ((Double)campaignMap.get("rebateLimited")).intValue();
		Long dailyRebateBudget = ((Double)campaignMap.get("dailyRebateBudget")).longValue();
		//结束时间类型  0-正常, 1-不限结束时间
		if (1 == isUnlimited) {
			endTime = TimeUtil.stringToDate(timestampMax);
		}

		logger.info("isUnlimited: " + isUnlimited);
		logger.info("adLimited: " + adLimited);
		logger.info("dailyAdBudget: " + dailyAdBudget);
		logger.info("rebateLimited: " + rebateLimited);
		logger.info("dailyRebateBudget: " + dailyRebateBudget);

		// 投放单元数据
		
		List<List<String>> scheduleStrings = (List<List<String>>)campaignMap.get("schedule");
		String schedule = gson.toJson(scheduleStrings);

		logger.info("scheduleStrings: " + scheduleStrings);
		logger.info("schedule: " + schedule);
		
		Integer isContinuous = ((Double)campaignMap.get("isContinuous")).intValue();
		//0-不连续    1-连续
		if (isContinuous == 0) {
			isUnlimited = 0;
			if (!CollectionUtils.isEmpty(scheduleStrings)) {
				List<String> startTimes = new ArrayList<String>();
				List<String> endTimes = new ArrayList<String>();
				for (List<String> scheduleString : scheduleStrings) {
					startTimes.add(scheduleString.get(0));
					endTimes.add(scheduleString.get(1));
				}
				startTime = TimeUtil.formateDate(new Date(Long.parseLong((String) Collections.min(startTimes))));
				endTime = TimeUtil.formateDate(new Date(Long.parseLong((String) Collections.max(endTimes))));
			}
		}
		Integer saleMode = ((Double)campaignMap.get("saleMode")).intValue();
		Integer isRebate = ((Double)campaignMap.get("isRebate")).intValue();

		logger.info("isContinuous: " + isContinuous);
		logger.info("saleMode: " + saleMode);
		logger.info("isRebate: " + isRebate);
		
		logger.info("============campaign create params success");

		// @todo 参数验证

		Integer status = Campaign.Status.NORMAL.getValue();
		Date currentTime = new Date();

		Campaign campaign = new Campaign(name, startTime, endTime, isImmediate, isUnlimited,
							adLimited, dailyAdBudget, rebateLimited, dailyRebateBudget,
							status, userId, currentTime, currentTime);
		campaign.setProductLine(productLine);
//		新增投放计划时，直接用budget给nextBudget赋值
		campaign.setNextDailyAdBudget(dailyAdBudget);
		campaign.setNextDailyRebateBudget(dailyRebateBudget);
//		新增投放计划时，直接用limited给nextLimited赋值
		campaign.setNextAdLimited(adLimited);
		campaign.setNextRebateLimited(rebateLimited);
		
		campaignDao.save(campaign);

		Integer campaignId = campaign.getCampaignId();

		logger.info("============campaign save success");
		logger.info("campaignId: " + campaignId);

		CampaignExt campaignExt = new CampaignExt(campaignId, isContinuous, saleMode, isRebate,
							schedule, currentTime, currentTime);
		campaignExtDao.save(campaignExt);

		logger.info("============campaign ext save success");

		AdsCampaign adsCampaign = new AdsCampaign();
		adsCampaign.setName(campaign.getName());
		adsCampaign.setAdvertiserId(dspAdvertiserId);
		adsCampaign.setAdAccountId(dspAdAccountId);
		adsCampaign.setRebateAccountId(dspRebateAccountId);
		if (null != adLimited && 1 == adLimited) {
			adsCampaign.setAdDailyBudget(new BigInteger(campaign.getDailyAdBudget().toString()));
		} else {
			adsCampaign.setAdDailyBudget(null);
		}
		if (null != rebateLimited && 1 == rebateLimited) {
			adsCampaign.setRebateDailyBudget(new BigInteger(campaign.getDailyRebateBudget().toString()));
		} else {
			adsCampaign.setRebateDailyBudget(null);
		}
		//投放系统中的实体状态 0-启用  1-暂停
		adsCampaign.setStatus(0);
		adsCampaign.setCreateUser(userName);
		ApiResponse response = adsCampaignOperations.create(adsCampaign);
		Map<String, Object> data = response.getData();
		Integer dspCampaignId = Integer.parseInt((String) (data.get("id")));

		logger.info("dspCampaignId: " + dspCampaignId);

		campaign.setDspCampaignId(dspCampaignId);
		campaignDao.update(campaign);

		logger.info("============campaign create send to ads success");
		saveResult.put("campaignId", campaignId);
		return saveResult;
	}

	/**
	 * 修改投放计划
	 * 
	 * 投放计划数据：投放计划名称、开始时间、结束时间、是否不限结束时间
	 *				是否限制广告日预算、广告日预算、是否限制返利日预算
	 *				返利日预算
	 * 投放单元数据：是否连续投放、售卖方式、是否返利、详细排期
	 * 
	 * @param campaignMap
	 */	
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public void update(Map<String, Object> campaignMap) throws Exception {
		// 投放计划数据

		logger.info("============campaign update");
		logger.info("campaignMap: " + campaignMap);

		Integer campaignId = ((Double)campaignMap.get("campaignId")).intValue();
		String name = ((String) campaignMap.get("name")).trim();

		logger.info("campaignId: " + campaignId);
		logger.info("name: " + name);
		
		Campaign oldCampaign = campaignDao.get(campaignId);
		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		Integer dspAdAccountId = PrincipalUtil.getDspAdAccountId();
		Integer dspRebateAccountId = PrincipalUtil.getDspRebateAccountId();

		logger.info("userId: " + userId);
		logger.info("userName: " + userName);
		logger.info("dspAdvertiserId: " + dspAdvertiserId);
		logger.info("dspAdAccountId: " + dspAdAccountId);
		logger.info("dspRebateAccountId: " + dspRebateAccountId);
		
		List<Campaign> oldCampaigns = campaignDao.getCampaignsByNameUserId(userId, name, oldCampaign.getProductLine());
		for (Campaign oldCampaignTmp : oldCampaigns) {
			if (!oldCampaignTmp.getCampaignId().equals(campaignId) && oldCampaignTmp.getName().equals(name)) {
				// TODO 投放计划名称重复,提示文案
				throw new RuntimeException("您已使用过\"" + name + "\"作为投放计划名称，请重命名");
			}
		}

		// @todo 优化时间处理
		String startTimeValue = String.valueOf(campaignMap.get("startTime"));
		String endTimeValue = String.valueOf(campaignMap.get("endTime"));
		Date startTime = TimeUtil.formateDate(new Date(new Timestamp(new Double(startTimeValue).longValue()).getTime()));
		Date endTime = TimeUtil.formateDate(new Date(new Timestamp(new Double(endTimeValue).longValue()).getTime()));


		logger.info("startTimeValue: " + startTimeValue);
		logger.info("startTime: " + startTime);
		logger.info("endTimeValue: " + endTimeValue);
		logger.info("endTime: " + endTime);


		// 暂无是否立即开始
		Integer isImmediate = 0;
		Integer isUnlimited = ((Double)campaignMap.get("isUnlimited")).intValue();
		Integer adLimited = ((Double)campaignMap.get("adLimited")).intValue();
		Long dailyAdBudget = ((Double)campaignMap.get("dailyAdBudget")).longValue();
		Integer rebateLimited = ((Double)campaignMap.get("rebateLimited")).intValue();
		Long dailyRebateBudget = ((Double)campaignMap.get("dailyRebateBudget")).longValue();
		
		// 结束时间类型 0-正常, 1-不限结束时间
		if (1 == isUnlimited) {
			endTime = TimeUtil.stringToDate(timestampMax);
		}

		logger.info("isUnlimited: " + isUnlimited);
		logger.info("adLimited: " + adLimited);
		logger.info("dailyAdBudget: " + dailyAdBudget);
		logger.info("rebateLimited: " + rebateLimited);
		logger.info("dailyRebateBudget: " + dailyRebateBudget);

		// 投放单元数据
		
		List<List<String>> scheduleStrings = (List<List<String>>)campaignMap.get("schedule");
		String schedule = gson.toJson(scheduleStrings);
		logger.info("scheduleStrings: " + scheduleStrings);
		logger.info("schedule: " + schedule);
		
		Integer isContinuous = ((Double)campaignMap.get("isContinuous")).intValue();
		
		//0-不连续    1-连续
		if (isContinuous == 0) {
			isUnlimited = 0;
			if (!CollectionUtils.isEmpty(scheduleStrings)) {
				List<String> startTimes = new ArrayList<String>();
				List<String> endTimes = new ArrayList<String>();
				for (List<String> scheduleString : scheduleStrings) {
					startTimes.add(scheduleString.get(0));
					endTimes.add(scheduleString.get(1));
				}
				startTime = TimeUtil.formateDate(new Date(Long.parseLong((String) Collections.min(startTimes))));
				endTime = TimeUtil.formateDate(new Date(Long.parseLong((String) Collections.max(endTimes))));
			}
		}

		Integer saleMode = ((Double)campaignMap.get("saleMode")).intValue();
		Integer isRebate = ((Double)campaignMap.get("isRebate")).intValue();

		logger.info("isContinuous: " + isContinuous);
		logger.info("saleMode: " + saleMode);
		logger.info("isRebate: " + isRebate);

		logger.info("============campaign update params success");


		// @todo 参数验证

		Date currentTime = new Date();
		
		oldCampaign.setName(name);
		oldCampaign.setStartTime(startTime);
		oldCampaign.setEndTime(endTime);
		oldCampaign.setIsImmediate(isImmediate);
		oldCampaign.setIsUnlimited(isUnlimited);
//		修改投放计划时，不改limited值，只改nextLimited
//		oldCampaign.setAdLimited(adLimited);
		oldCampaign.setNextAdLimited(adLimited);
//		oldCampaign.setRebateLimited(rebateLimited);
		oldCampaign.setNextRebateLimited(rebateLimited);
		
//		修改投放计划时，不改budget值，只改nextBudget
//		oldCampaign.setDailyAdBudget(dailyAdBudget);
		oldCampaign.setNextDailyAdBudget(dailyAdBudget);
//		oldCampaign.setDailyRebateBudget(dailyRebateBudget);
		oldCampaign.setNextDailyRebateBudget(dailyRebateBudget);
		oldCampaign.setUpdateTime(currentTime);
		campaignDao.update(oldCampaign);

		logger.info("============campaign update success");

		CampaignExt oldCampaignExt = campaignExtDao.getUniqueBy("campaignId", campaignId);
		oldCampaignExt.setIsContinuous(isContinuous);
		oldCampaignExt.setSaleMode(saleMode);
		oldCampaignExt.setIsRebate(isRebate);
		oldCampaignExt.setSchedule(schedule);
		oldCampaignExt.setUpdateTime(currentTime);
		campaignExtDao.update(oldCampaignExt);

		logger.info("============campaign ext update success");

		List<Flight> oldFlights = flightDao.getFlightsByCampaignId(campaignId);
		if (!CollectionUtils.isEmpty(oldFlights)) {
			for (Flight oldFlight : oldFlights) {
				oldFlight.setIsContinuous(isContinuous);
				oldFlight.setSaleMode(saleMode);
				oldFlight.setIsRebate(isRebate);
				oldFlight.setSchedule(schedule);

				flightDao.update(oldFlight);

				logger.info("============campaign flights update success");

				AdsGroup adsGroup = new AdsGroup();
				Integer productLine = oldFlight.getProductLine();
				logger.info("productLine: " + productLine);
				if (productLine.equals(Flight.ProductLine.FIXED_BID_CPC.getValue())) {
					adsGroup = fixedCpcFlightManager.buildAdGroup(oldFlight.getFlightId());
					adsGroupOperations.update(adsGroup);
				} else if (productLine.equals(Flight.ProductLine.BID_CPC.getValue())) {
					adsGroup = bidCpcFlightManager.buildAdGroup(oldFlight.getFlightId());
					adsGroupOperations.update(adsGroup);
				}
			}
		}
		AdsCampaign adsCampaign = new AdsCampaign();
		adsCampaign.setId(oldCampaign.getDspCampaignId());
		adsCampaign.setName(oldCampaign.getName());
		adsCampaign.setAdvertiserId(dspAdvertiserId);
		adsCampaign.setAdAccountId(dspAdAccountId);
		adsCampaign.setRebateAccountId(dspRebateAccountId);
		
//		日预算次日生效，所以将nextBudget值同步给引擎
		if (null != adLimited && 1 == adLimited) {
//			adsCampaign.setAdDailyBudget(new BigInteger(oldCampaign.getDailyAdBudget().toString()));
			adsCampaign.setAdDailyBudget(new BigInteger(oldCampaign.getNextDailyAdBudget().toString()));
		} else {
			adsCampaign.setAdDailyBudget(null);
		}
		if (null != rebateLimited && 1 == rebateLimited) {
//			adsCampaign.setRebateDailyBudget(new BigInteger(oldCampaign.getDailyRebateBudget().toString()));
			adsCampaign.setRebateDailyBudget(new BigInteger(oldCampaign.getNextDailyRebateBudget().toString()));
		} else {
			adsCampaign.setRebateDailyBudget(null);
		}
		
		//投放系统中的实体状态 0-启用  1-暂停
		if(Campaign.Status.NORMAL.getValue() == oldCampaign.getStatus()){
			adsCampaign.setStatus(0);			
		}else if(Campaign.Status.SUSPEND.getValue() == oldCampaign.getStatus()){
			adsCampaign.setStatus(1);		
		}
		adsCampaign.setUpdateUser(userName);
		adsCampaignOperations.update(adsCampaign);

		logger.info("============campaign update send to ads success");
	}
	
	/**
	 * 构建ads所需的排期数据
	 * @param scheduleStrings
	 * @return
	 */
	private String getScheduleForAds(List<List<String>> scheduleStrings){
		StringBuilder  schedule = new StringBuilder();
		if(CollectionUtils.isEmpty(scheduleStrings)){
			return schedule.toString();
		}
		for(List<String> scheduleString : scheduleStrings){
			String start = TimeUtil.dateToString(new Date(Long.parseLong(scheduleString.get(0))));
			String end = TimeUtil.dateToString(new Date(Long.parseLong(scheduleString.get(1))));
			if(start.equals(end)){
				schedule.append(start).append(",");
			}else{
				schedule.append(start).append("~").append(end).append(",");
			}
		}
		return schedule.substring(0, schedule.length()-1).toString();
	}

	/**
	 * 批量修改投放计划状态
	 *
	 * 启用：将选中的处于暂停状态（除余额不足外）的计划设为有效状态
	 * 暂停：将选中的处于有效、未开始状态及余额不足的计划设置为暂停状态；
	 * 
	 * @param campaignsStatusMap
	 */
	@Transactional(readOnly = false)
	public void batchUpdateStatus(Map<String, Object> campaignsStatusMap) throws Exception {

		List<Double> campaignDoubleIds = (List<Double>) campaignsStatusMap.get("campaignIds");
		Integer status = ((Double) campaignsStatusMap.get("status")).intValue();

		List<Integer> campaignIds = new ArrayList<Integer>();
		for (Double campaignDoubleId : campaignDoubleIds) {
			Integer campaignId = campaignDoubleId.intValue();
			campaignIds.add(campaignId);
		}

		// @todo 参数验证


		Integer normalStatus = Campaign.Status.NORMAL.getValue();
		Integer suspendStatus = Campaign.Status.SUSPEND.getValue();
		List<String> errors = new ArrayList<String>();
		Date currentTime = new Date();

		List<Campaign> campaigns = campaignDao.get(campaignIds);

		// @todo 余额不足状态判断
		// 启用：将选中的处于暂停状态（除余额不足外）的计划设为有效状态
		if (status.equals(normalStatus)) {
			for (Campaign campaign : campaigns) {
				if (!campaign.getStatus().equals(suspendStatus)) {
					errors.add(campaign.getName() + "状态异常");
					continue;
				}
				campaign.setStatus(normalStatus);
				campaign.setUpdateTime(currentTime);
				campaignDao.update(campaign);
				//投放系统中的实体状态 0-启用  1-暂停
				adsCampaignOperations.updateStatus(campaign.getDspCampaignId(), 0);
			}
		// @todo 余额不足、未开始、过期状态判断
		// 暂停：将选中的处于有效、未开始状态及余额不足的计划设置为暂停状态；
		} else if (status.equals(suspendStatus)) {
			for (Campaign campaign : campaigns) {
				if (!campaign.getStatus().equals(normalStatus)) {
					errors.add(campaign.getName() + "状态异常");
					continue;
				}
				campaign.setStatus(suspendStatus);
				campaign.setUpdateTime(currentTime);
				campaignDao.update(campaign);
				//投放系统中的实体状态 0-启用  1-暂停
				adsCampaignOperations.updateStatus(campaign.getDspCampaignId(), 1);
			}
		}

		if (errors.size() > 0) {
			String errorsString = String.join(",", errors);
			logger.error("batchUpdateStatus error: " + errorsString);
			throw new Exception(errorsString);
		}
	}

	/**
	 * 批量删除投放计划
	 *
	 * 删除：将选中的处于暂停、过期、未开始状态的计划删除且无法撤销操作，计划列表中将不再展示该计划
	 * 
	 * @param campaignsDeleteMap
	 */
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public void batchDelete(Map<String, Object> campaignsDeleteMap) throws Exception {

		List<Double> campaignDoubleIds = (List<Double>) campaignsDeleteMap.get("campaignIds");

		List<Integer> campaignIds = new ArrayList<Integer>();
		for (Double campaignDoubleId : campaignDoubleIds) {
			Integer campaignId = campaignDoubleId.intValue();
			campaignIds.add(campaignId);
		}

		// @todo 参数验证


		Integer deleteStatus = Campaign.Status.DELETE.getValue();
		Integer normalState = Campaign.State.NORMAL.getValue();
		List<String> errors = new ArrayList<String>();
		Date currentTime = new Date();

		List<Campaign> campaigns = campaignDao.get(campaignIds);
		// @todo 详细的验证规则
		List<Integer> deleteCampaignIds = new ArrayList<Integer>();
		for (Campaign campaign : campaigns) {
			if (campaign.getState().equals(normalState)) {
				errors.add(campaign.getName() + "不能删除");
				continue;
			}
			campaign.setStatus(deleteStatus);
			campaign.setUpdateTime(currentTime);
			campaignDao.update(campaign);
			deleteCampaignIds.add(campaign.getCampaignId());
			
			adsCampaignOperations.delete(campaign.getDspCampaignId());
		}
		
		// 将成功删除的投放计划对应的所有投放单元一并删除，并级联删除相关创意
		List<Flight> deleteFlights = null;
		if (!CollectionUtils.isEmpty(deleteCampaignIds)) {
			deleteFlights = flightDao.getFlightsByCampaignIds(deleteCampaignIds);
		}
		List<Integer> deleteFlightIds = new ArrayList<Integer>();
		if (!CollectionUtils.isEmpty(deleteFlights)) {
			for (Flight flight : deleteFlights) {
				flight.setStatus(Flight.Status.DELETE.getValue());
				deleteFlightIds.add(flight.getFlightId());
			}
			flightDao.batchDeleteFlights(deleteFlights);
		}
		List<Material> deleteMaterials = null;
		if (!CollectionUtils.isEmpty(deleteFlightIds)) {
			deleteMaterials = materialDao.getMaterialsByFlightIds(deleteFlightIds);
		}
		if (!CollectionUtils.isEmpty(deleteMaterials)) {
			for (Material material : deleteMaterials) {
				material.setStatus(Material.Status.DELETE.getValue());
			}
			materialDao.batchDeleteMaterials(deleteMaterials);
		}

		if (errors.size() > 0) {
			String errorsString = String.join(",", errors);
			logger.error("batchDelete error: " + errorsString);
			throw new Exception(errorsString);
		}
	}


	/**
	 * 根据campaignId获取投放计划简明信息
	 * 
	 * @param campaignId
	 * @return
	 */
	public Map<String, Object> getCampaignBriefByCampaignId(Integer campaignId) {
		Campaign campaign = campaignDao.get(campaignId);

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("campaignId", campaign.getCampaignId());
		data.put("name", campaign.getName());
		data.put("startTime", new Timestamp(campaign.getStartTime().getTime()));
		data.put("endTime", new Timestamp(campaign.getEndTime().getTime()));
		data.put("isUnlimited", campaign.getIsUnlimited());
		data.put("state", campaign.getState());

		return data;
	}

	/**
	 * 根据campaignId获取广告计划
	 * 
	 * @param campaignId
	 * @return
	 */
	public Map<String, Object> getCampaignByCampaignId(Integer campaignId) throws Exception {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Campaign campaign = campaignDao.getCampaignByCampaignId(campaignId);
		if (null != campaign) {
			data.put("campaignId", campaign.getCampaignId());
			data.put("name", campaign.getName());
			data.put("isUnlimited", campaign.getIsUnlimited());
			data.put("startTime", campaign.getStartTime());
			// 不限结束时间(unlimited为1), 不返回endTime
			if (0 == campaign.getIsUnlimited()) {
				data.put("endTime", campaign.getEndTime());				
			}
//			前端展示nextLimited值
//			data.put("adLimited", campaign.getAdLimited());
//			data.put("rebateLimited", campaign.getRebateLimited());
			data.put("adLimited", campaign.getNextAdLimited());
			data.put("rebateLimited", campaign.getNextRebateLimited());
//			前端展示nextBudget值
//			data.put("dailyAdBudget", campaign.getDailyAdBudget());
//			data.put("dailyRebateBudget", campaign.getDailyRebateBudget());
			data.put("dailyAdBudget", campaign.getNextDailyAdBudget());
			data.put("dailyRebateBudget", campaign.getNextDailyRebateBudget());
			data.put("state", campaign.getState());

			CampaignExt campaignExt = campaignExtDao.getCampaignExtByCampaignId(campaignId);
			if (null != campaignExt) {
				data.put("schedule", gson.fromJson(campaignExt.getSchedule(), new ArrayList<List<String>>().getClass()));
				data.put("saleMode", campaignExt.getSaleMode());
				data.put("isRebate", campaignExt.getIsRebate());
				data.put("isContinuous", campaignExt.getIsContinuous());
			}
		}
		return data;
	}

	/**
	 * 分页获取广告计划信息
	 */
	public HashMap<String, Object> getCampaignsByPagination(Pagination pagination, String keyword, Integer state, String startTime, String endTime, Integer productLine) {
		logger.info("getCampaignsByPagination keyword:" + keyword + "  state:" + state + "  startTime:" + startTime + "  endTime:" + endTime + " productLine:" + productLine);
		HashMap<String, Object> data = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> campaigns = new ArrayList<Map<String, Object>>();

		List<Campaign> campaignList = campaignDao.findByPagination(pagination, keyword, state, productLine);
		if (!CollectionUtils.isEmpty(campaignList)) {
			List<Integer> campaignIds = new ArrayList<Integer>();
			List<Integer> dspCampaignIds = new ArrayList<Integer>();
			for (Campaign campaign : campaignList) {
				campaignIds.add(campaign.getCampaignId());
				dspCampaignIds.add(campaign.getDspCampaignId());
			}
			logger.info("campaignIds: " + campaignIds);
			logger.info("dspCampaignIds: " + dspCampaignIds);
			logger.info("userId: " + PrincipalUtil.getUserId());

			Map<Integer, CampaignExt> campaignExtMap = getCampaignExtMap(campaignIds);

			// 查询dmp统计数据
			Map<Integer, Map<String, Object>> campaignStatistics = new HashMap<Integer, Map<String, Object>>();
			try {
				String dspCampaignIdsStringTmp = dspCampaignIds.toString();
				String dspCampaignIdsString = dspCampaignIdsStringTmp.substring(1, dspCampaignIdsStringTmp.length() - 1);
				String startEventTime = TimeUtil.dateToString(TimeUtil.formateDate((new Date(Long.parseLong(startTime)))));
				String endEventTime = TimeUtil.dateToString(TimeUtil.formateDate((new Date(Long.parseLong(endTime)))));
				String dspAdvertiserId = PrincipalUtil.getDspAdvertiserId().toString();
				logger.info("dspCampaignIdsString: " + dspCampaignIdsString);
				logger.info("advertiserId: " + dspAdvertiserId);
				logger.info("startEventTime: " + startEventTime);
				logger.info("endEventTime: " + endEventTime);
				
				ApiResponse response = dmpFlightOperations.getCampaignStatistics(dspCampaignIdsString, dspAdvertiserId, startEventTime, endEventTime);
				Map<String, Object> campaignData = response.getData();
				logger.info("campaignData:" + campaignData);
				List<Map<String, Object>> report = (List<Map<String, Object>>) campaignData.get("report");
				logger.info("report:" + report);
				if (!CollectionUtils.isEmpty(report)) {
					for (Map<String, Object> tmp : report) {
						Double dspCampaignId = Double.parseDouble(tmp.get("orderId").toString());
						campaignStatistics.put(dspCampaignId.intValue(), tmp);
					}
				}
			} catch (Exception exception) {
				logger.error("getCampaignStatistics from dmp fail throw Exception", exception);
			}
			logger.info("campaignStatistics: " + campaignStatistics);
			
			// 查询各个投放计划当天总花费
			Map<Integer, BigInteger> expenseMap = null;
			try {
				expenseMap = expenseService.getExpenseAmount(dspCampaignIds);
				logger.info("expenseMap: " + expenseMap);
			} catch (Exception exception) {
				logger.error("expenseService.getExpenseAmount throw Exception", exception);
			}
			
			// 查询用户当前广告账户
			Account account = accountDao.getAccountByType(PrincipalUtil.getUserId(), Account.Type.ADVERT_ACCOUNT.getValue());

			// 投放计划维度，查询当前用户的返利总花费
			Map<Integer, BigInteger> rebateCampaignExpense = null;
			try {
				Map<String, Map<Integer, BigInteger>> campaignExpense = expenseService.getAmountByDspCampaignIds(dspCampaignIds, TimeUtil.formateDate((new Date(Long.parseLong(startTime)))), TimeUtil.formateDate((new Date(Long.parseLong(endTime)))));
				rebateCampaignExpense = campaignExpense.get("rebateAmount");
			} catch (Exception e) {
				logger.error("expenseService.getAmountByDspCampaignIds throw Exception ",e);
			}
			
			// 组装数据
			for (Campaign campaign : campaignList) {
				Map<String, Object> campaignMap = new LinkedHashMap<String, Object>();
				campaignMap.put("campaignId", campaign.getCampaignId());
				campaignMap.put("name", campaign.getName());
//              前端列表展示nextBudget值
//				campaignMap.put("dailyAdBudget", campaign.getDailyAdBudget());
//				campaignMap.put("dailyRebateBudget", campaign.getDailyRebateBudget());
				campaignMap.put("dailyAdBudget", campaign.getNextDailyAdBudget());
				campaignMap.put("dailyRebateBudget", campaign.getNextDailyRebateBudget());
				campaignMap.put("startTime", campaign.getStartTime());
				campaignMap.put("endTime", campaign.getEndTime());
				campaignMap.put("isUnlimited", campaign.getIsUnlimited());
//              前端列表展示nextLimited值
//				campaignMap.put("adLimited", campaign.getAdLimited());
//				campaignMap.put("rebateLimited", campaign.getRebateLimited());
				campaignMap.put("adLimited", campaign.getNextAdLimited());
				campaignMap.put("rebateLimited", campaign.getNextRebateLimited());

				// 投放时间 1-连续 0-不连续
				Integer isContinuous = 1;
				ArrayList<List<String>> schedule = new ArrayList<List<String>>();
				if (!CollectionUtils.isEmpty(campaignExtMap)) {
					CampaignExt campaignExt = campaignExtMap.get(campaign.getCampaignId());
					if (null != campaignExt) {
						isContinuous = campaignExt.getIsContinuous();
						schedule = gson.fromJson(campaignExt.getSchedule(), new ArrayList<List<String>>().getClass());
						campaignMap.put("schedule", schedule);
						//是否返利
						campaignMap.put("isRebate", campaignExt.getIsRebate());
					}
				}

				if (null == state || state <= 0) {
					// 查询"全部"状态
					campaignMap.put("state", campaign.getState());
				} else {
					// 根据state条件查询 "有效" "暂停" "过期" "未开始"
					campaignMap.put("state", state);
				}

				// 对"有效"状态进行细分 (前端搜索"全部"、"有效"，返回结果才有可能展示 "预算用完" "不在投放时间段")
				// 对"暂停"状态进行细分 (暂停中分出来"余额不足")
				// 产品需求:计划有效，只管时间，时间满足了才有原因； (startTime <= current && endTime >= current)
				//        计划暂停，只要设置了暂停，就是暂停，不管时间；
				Long current = TimeUtil.formateDate(new Date()).getTime();
				if (((0 == state && campaign.getStatus() == Campaign.Status.NORMAL.getValue())
						|| state == Campaign.State.NORMAL.getValue()) && (campaign.getStartTime().getTime() <= current && campaign.getEndTime().getTime() >= current)) {

					// 判断是否预算用完
					// ad_limited 0不限、1自定义
					if (1 == campaign.getAdLimited() && !CollectionUtils.isEmpty(expenseMap)) {
						logger.info("dspCampaignId: " + campaign.getDspCampaignId());
						logger.info("expenseMap:" + expenseMap);
						BigInteger expense = expenseMap.get(campaign.getDspCampaignId());
						if (null != expense && expense.intValue() >= campaign.getDailyAdBudget().intValue()) {
							campaignMap.put("state", Campaign.State.RUN_OUT_OF_BUDGET.getValue());
						}
					}

					// 投放时间不连续
					if (0 == isContinuous) {
						// 判断当前时间是否在投放时间段内
						if (!CollectionUtils.isEmpty(schedule)) {
							Boolean isNeedFlight = false;
							for (List<String> schedulePiece : schedule) {
								Long start = Long.parseLong(schedulePiece.get(0));
								Long end = Long.parseLong(schedulePiece.get(1));
								logger.info("campaignId" + campaign.getCampaignId());
								logger.info("start: " + start);
								logger.info("end: " + end);
								logger.info("current:" + current);
								if (current >= start && current <= end) {
									isNeedFlight = true;
									break;
								}
							}
							if (!isNeedFlight) {
								campaignMap.put("state", Campaign.State.NOT_IN_FLIGHT.getValue());
							}
						}
					}
//				} else if (null != account && 30 > account.getBalance().intValue()) {
				} 
				if (null != account && 0 > account.getBalance().intValue() && (campaignMap.get("state") != null && Campaign.State.NORMAL.getValue().equals((Integer)campaignMap.get("state")))) {
					// 余额不足（余额不足时，应有定时任务去暂停投放计划； 现在没有定时任务，所以不去筛"暂停"状态，直接查用户余额，只要余额不足，返回"余额不足"state）
					// TODO 此次cpc出价固定0.3元，后续应查询该投放计划中所有投放单元的所有广告的刊例价，取最小值
					campaignMap.put("state", Campaign.State.LACK_BLANCE.getValue());
				}

				// 填充dmp统计数据
				Map<String, Object> campaignReport = null;
				if (!CollectionUtils.isEmpty(campaignStatistics)) {
					campaignReport = campaignStatistics.get(campaign.getDspCampaignId());
					if (null != campaignReport) {
						DecimalFormat format = new DecimalFormat("0.00");
						Double impressionNumber = Double.parseDouble(campaignReport.get("impressionNumber").toString());
						Double clickNumber = Double.parseDouble(campaignReport.get("clickNumber").toString());
						Double adAmount = Double.parseDouble(campaignReport.get("totalFee").toString()) / 1000000;
						campaignMap.put("impression", campaignReport.get("impressionNumber"));
						campaignMap.put("click", campaignReport.get("clickNumber"));
						campaignMap.put("adAmount", adAmount);
						if (null != adAmount) {
							campaignMap.put("clickUnitPrice", format.format(clickNumber.intValue() == 0 ? 0 : adAmount / clickNumber));
							campaignMap.put("costPerMills", format.format(impressionNumber == 0 ? 0	: (adAmount / impressionNumber ) * 1000));
						}
						if (null != campaignReport.get("clkRate")){
							Double ctr = Double.parseDouble(campaignReport.get("clkRate").toString());
							BigDecimal bigDecimal = new BigDecimal(ctr * 100);
							campaignMap.put("ctr", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						}
					}
				}
				
				if (!CollectionUtils.isEmpty(rebateCampaignExpense)) {
					if (null != rebateCampaignExpense.get(campaign.getDspCampaignId())) {
						Double rebateAmount = Double.parseDouble(rebateCampaignExpense.get(campaign.getDspCampaignId()).toString());
						campaignMap.put("rebateAmount", rebateAmount / 100);
					}
				}
				
				if (null == campaignMap.get("impression")) {
					campaignMap.put("impression", 0);
				}
				if (null == campaignMap.get("click")) {
					campaignMap.put("click", 0);
				}
				if (null == campaignMap.get("ctr")) {
					campaignMap.put("ctr", 0);
				}
				if (null == campaignMap.get("adAmount")) {
					campaignMap.put("adAmount", 0);
				}
				if (null == campaignMap.get("rebateAmount")) {
					campaignMap.put("rebateAmount", 0);
				}
				if (null == campaignMap.get("clickUnitPrice")) {
					campaignMap.put("clickUnitPrice", 0);
				}
				if (null == campaignMap.get("costPerMills")) {
					campaignMap.put("costPerMills", 0);
				}
				campaigns.add(campaignMap);
			}
		}
		data.put("totalCount", pagination.getTotalCount());
		data.put("page", pagination.getCurrentPage());
		data.put("number", pagination.getNumber());
		data.put("list", campaigns);

		return data;
	}

	/**
	 * 查询多个campaignId对应的投放计划扩展信息
	 * key: campaignId
	 * value: campaignExt
	 * 
	 * @param campaignIds
	 * @return
	 */
	public Map<Integer, CampaignExt> getCampaignExtMap(List<Integer> campaignIds) {
		Map<Integer, CampaignExt> campaignExtMap = new HashMap<Integer, CampaignExt>();
		List<CampaignExt> campaignExts = campaignExtDao.getCampaignExtsByCampaignIds(campaignIds);
		if (CollectionUtils.isEmpty(campaignExts)) {
			return campaignExtMap;
		}
		for (CampaignExt campaignExt : campaignExts) {
			campaignExtMap.put(campaignExt.getCampaignId(), campaignExt);
		}
		return campaignExtMap;
	}

	/**
	 * 获取所有的广告计划
	 * @param productLine 产品线 默认0(定价cpc); 1(竞价cpc);
	 * @return
	 */
	public List<Map<String, Object>> getAllCampaigns(Integer productLine) {
		List<Map<String, Object>> dataList = new ArrayList();
		Integer userId = PrincipalUtil.getUserId();
		List<Campaign> campaignList = campaignDao.getAllCampaigns(userId, productLine);
		if (!CollectionUtils.isEmpty(campaignList)) {
			campaignList.forEach(campaign -> {
				Map<String, Object> dataMap = new HashMap();
				dataMap.put("dspCampaignId", campaign.getDspCampaignId());
				dataMap.put("name", campaign.getName());
				dataList.add(dataMap);
			});
			return dataList;
		}

		return Collections.EMPTY_LIST;
	}
	

	/**
	 * 获取所有的投放计划和对应的投放单元集合
	 * @param productLine 产品线 默认0(定价cpc); 1(竞价cpc);
	 * @return
	 */
	public List<Map<String, Object>> getCampaignsAndFlights(Integer productLine) {
		List<Map<String, Object>> dataList = new ArrayList();
		Integer userId = PrincipalUtil.getUserId();
		List<Campaign> allCampaigns = campaignDao.getAllCampaigns(userId, productLine);

		if (CollectionUtils.isEmpty(allCampaigns)) {
			return Collections.EMPTY_LIST;
		}

		// 获取campaingId集合
		List<Integer> campaignIds = allCampaigns.stream().map(Campaign::getCampaignId).collect(Collectors.toList());

		// 查询所有的投放单元
		List<Flight> allFlights = flightDao.getAllFlightsByCampaignIds(campaignIds);

		if (CollectionUtils.isEmpty(allFlights)) {
			allCampaigns.forEach(campaign -> {
				Map<String, Object> dataMap = new HashMap();
				dataMap.put("dspCampaignId", campaign.getDspCampaignId());
				dataMap.put("name", campaign.getName());
				dataMap.put("flightList","");

				dataList.add(dataMap);
			});
			return dataList;
		}

		// 按campaignId分组统计投放单元
		Map<Integer, List<Flight>> flightMap = allFlights.stream().collect(Collectors.groupingBy
				(Flight::getCampaignId));

		allCampaigns.forEach(campaign -> {
			Map<String, Object> dataMap = new HashMap();
			dataMap.put("dspCampaignId", campaign.getDspCampaignId());
			dataMap.put("name", campaign.getName());
			List<Flight> campaignAllFlight = flightMap.get(campaign.getCampaignId());
			if (CollectionUtils.isEmpty(campaignAllFlight)) {
				dataMap.put("flightList", "");
			} else {
				dataMap.put("flightList", campaignAllFlight.stream().map(flight -> {
					Map<String, Object> singleFlightMap = new HashMap();
					singleFlightMap.put("name", flight.getName());
					singleFlightMap.put("dspFlightId", flight.getDspFlightId());
					return singleFlightMap;
				}).collect(Collectors.toList()));
			}
			dataList.add(dataMap);
		});
		return dataList;

	}

	/**
	 * 获取所有的投放计划、投放单元和投放创意集合
	 * @param productLine 产品线 默认0(定价cpc); 1(竞价cpc);
	 * @return
	 */
	public List<Map<String, Object>> getCampaignsAndFlightsAndMaterials(Integer productLine) {
		List<Map<String, Object>> dataList = new ArrayList();
		Integer userId = PrincipalUtil.getUserId();
		List<Campaign> allCampaigns = campaignDao.getAllCampaigns(userId, productLine);

		if (CollectionUtils.isEmpty(allCampaigns)) {
			return Collections.EMPTY_LIST;
		}

		// 获取campaingId集合
		List<Integer> campaignIds = allCampaigns.stream().map(Campaign::getCampaignId).collect(Collectors.toList());
		// 查询所有的投放单元
		List<Flight> allFlights = flightDao.getAllFlightsByCampaignIds(campaignIds);

		if (CollectionUtils.isEmpty(allFlights)) {
			allCampaigns.forEach(campaign -> {
				Map<String, Object> dataMap = new HashMap();
				dataMap.put("dspCampaignId", campaign.getDspCampaignId());
				dataMap.put("name", campaign.getName());
				dataMap.put("flightList", "");

				dataList.add(dataMap);
			});
			return dataList;
		}

		// 获取flightId集合
		List<Integer> flightIds = allFlights.stream().map(Flight::getFlightId).collect(Collectors.toList());
		// 按campaignId分组统计投放单元
		Map<Integer, List<Flight>> flightMap = allFlights.stream().collect(Collectors.groupingBy
				(Flight::getCampaignId));


		// 查询所有的创意
		List<Material> allMaterials = materialDao.getMaterialsByFlightIds(flightIds);
		if (CollectionUtils.isEmpty(allMaterials)) {
			allCampaigns.forEach(campaign -> {
				Map<String, Object> dataMap = new HashMap();
				dataMap.put("dspCampaignId", campaign.getDspCampaignId());
				dataMap.put("name", campaign.getName());
				List<Flight> campaignAllFlight = flightMap.get(campaign.getCampaignId());
				if (CollectionUtils.isEmpty(campaignAllFlight)) {
					dataMap.put("flightList", "");
				} else {
					dataMap.put("flightList", campaignAllFlight.stream().map(flight -> {
						Map<String, Object> singleFlightMap = new HashMap();
						singleFlightMap.put("name", flight.getName());
						singleFlightMap.put("dspFlightId", flight.getDspFlightId());
						singleFlightMap.put("materialList", "");
						return singleFlightMap;
					}).collect(Collectors.toList()));
				}
				dataList.add(dataMap);
			});
			return dataList;
		}

		// 按flightId分组创意
		Map<Integer, List<Material>> materialMap = allMaterials.stream().collect(Collectors.groupingBy
				(Material::getFlightId));

		allCampaigns.forEach(campaign -> {
			Map<String, Object> dataMap = new HashMap();
			dataMap.put("dspCampaignId", campaign.getDspCampaignId());
			dataMap.put("name", campaign.getName());
			List<Flight> campaignAllFlight = flightMap.get(campaign.getCampaignId());
			if (CollectionUtils.isEmpty(campaignAllFlight)) {
				dataMap.put("flightList", "");
			} else {
				dataMap.put("flightList", campaignAllFlight.stream().map(flight -> {
					Map<String, Object> singleFlightMap = new HashMap();

					List<Material> flightAllMaterial = materialMap.get(flight.getFlightId());
					if (CollectionUtils.isEmpty(flightAllMaterial)) {
						singleFlightMap.put("materialList", "");
					} else {
						singleFlightMap.put("materialList", flightAllMaterial.stream().map(material -> {
							Map<String, Object> singleMaterialMap = new HashMap();
							singleMaterialMap.put("name", material.getName());
							singleMaterialMap.put("dspMaterialId", material.getDspMaterialId());
							return singleMaterialMap;
						}).collect(Collectors.toList()));
					}

					singleFlightMap.put("name", flight.getName());
					singleFlightMap.put("dspFlightId", flight.getDspFlightId());
					return singleFlightMap;
				}).collect(Collectors.toList()));
			}
			dataList.add(dataMap);
		});
		return dataList;

	}
	
	/**
	 * 判断当前用户下是否存在名为campaignName的投放计划
	 * @param userId
	 * @param name
	 * @param productLine
	 * @return
	 */
	public Boolean isExistCampaignName(Integer userId, String name, Integer campaignId, Integer productLine) {
		Boolean isExist = false;
		List<Campaign> campaigns = campaignDao.getCampaignsByNameUserId(userId, name, productLine);
		if (!CollectionUtils.isEmpty(campaigns)) {
			for (Campaign campaign: campaigns) {
				if (campaign.getName().equals(name) && !campaign.getCampaignId().equals(campaignId)) {
					isExist = true;	
					break;
				}
			}
		}
		logger.info("isExistCampaignName isExist: " + isExist);
		return isExist;
	}
}
