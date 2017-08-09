package com.gomeplus.amp.ad.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.ads.AdsGroupOperations;
import com.gomeplus.adm.common.api.ads.model.AdsGroup;
import com.gomeplus.adm.common.api.dmp.DmpFlightOperations;
import com.gomeplus.adm.common.api.dmp.DmpKeywordOperations;
import com.gomeplus.adm.common.api.mall.MallProductOperations;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.util.TimeUtil;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.dao.AdvertisementDao;
import com.gomeplus.amp.ad.dao.CampaignDao;
import com.gomeplus.amp.ad.dao.CampaignExtDao;
import com.gomeplus.amp.ad.dao.FlightAdvertisementDao;
import com.gomeplus.amp.ad.dao.FlightDao;
import com.gomeplus.amp.ad.dao.KeywordDao;
import com.gomeplus.amp.ad.dao.MaterialDao;
import com.gomeplus.amp.ad.dao.PriceDao;
import com.gomeplus.amp.ad.dao.PublisherDao;
import com.gomeplus.amp.ad.dao.SlotDao;
import com.gomeplus.amp.ad.dao.StrategyDao;
import com.gomeplus.amp.ad.form.FlightForm;
import com.gomeplus.amp.ad.manager.BidCpcFlightManager;
import com.gomeplus.amp.ad.manager.FixedCpcFlightManager;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Keyword;
import com.gomeplus.amp.ad.model.Material;
import com.gomeplus.amp.ad.model.Strategy;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;

/**
 * Created by liuchen on 2016/9/1.
 */
@Service
@Transactional(readOnly = true)
public class FlightService extends BaseService<Flight, Integer> {

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
	private PriceDao priceDao;
	@Autowired
	private FlightAdvertisementDao flightAdvertisementDao;
	@Autowired
	private CampaignDao campaignDao;
	@Autowired
	private CampaignExtDao campaignExtDao;
	@Autowired
	private MaterialDao materialDao;
	@Autowired
	private KeywordDao keywordDao;
	//service平级互调，设计原则的问题，后续修改
	@Autowired
	private ExpenseService expenseService;
	@Autowired
	private FixedCpcFlightManager fixedCpcFlightManager;
	@Autowired
	private BidCpcFlightManager bidCpcFlightManager;
	private MallProductOperations mallProductOperations = new MallProductOperations();
	private static Logger logger = LoggerFactory.getLogger(FlightService.class);


	private AdsGroupOperations adsGroupOperations;
	private DmpFlightOperations dmpFlightOperations;

	public FlightService() {
		adsGroupOperations = new AdsGroupOperations();
		dmpFlightOperations = new DmpFlightOperations();
	}

	private Gson gson = new Gson();

	@Override
	public HibernateDao<Flight, Integer> getEntityDao() {
		return flightDao;
	}

	/**
	 * 添加投放单元
	 * @param flightForm
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public HashMap<String, Object> save(FlightForm flightForm) throws Exception {
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (flightForm.getProductLine().equals(Flight.ProductLine.FIXED_BID_CPC.getValue())) {
			return fixedCpcFlightManager.save(flightForm);
		} else if (flightForm.getProductLine().equals(Flight.ProductLine.BID_CPC.getValue())) {
			return bidCpcFlightManager.save(flightForm);
		}
		return data;
	}

	/**
	 * 修改投放单元
	 * @param flightForm
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void update(FlightForm flightForm) throws Exception {
		if (flightForm.getProductLine().equals(Flight.ProductLine.FIXED_BID_CPC.getValue())) {
			fixedCpcFlightManager.update(flightForm);
		} else if (flightForm.getProductLine().equals(Flight.ProductLine.BID_CPC.getValue())) {
			bidCpcFlightManager.update(flightForm);
		}
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
	 * 根据flightId获取投放单元简明信息
	 * 
	 * @param flightId
	 * @return
	 */
	public Map<String, Object> getFlightBriefByFlightId(Integer flightId) {

		Flight flight = flightDao.get(flightId);
		// @todo 获取有效广告数量
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("flightId", flight.getFlightId());
		data.put("name", flight.getName());
		data.put("platform", flight.getPlatform());
		data.put("type", flight.getType());
		data.put("adNumber", flightAdvertisements.size());
		data.put("state", flight.getState());
		data.put("campaignId", flight.getCampaignId());

		if (null != flightAdvertisements.get(0)) {
			Advertisement advertisement = advertisementDao.getAdvertisementByAdvertisementId(flightAdvertisements.get(0).getAdvertisementId());
			if (null != advertisement) {
				List<Integer> linkType = new ArrayList<Integer>();
				linkType.add(advertisement.getGeneralizeType());

				// 设置链接类型
				data.put("linkType", linkType);
				String environment = System.getenv().get("ENVIRONMENT");
				if (environment.equals("production")) {
					if (10040 == advertisement.getAdvertisementId()) {
						data.put("linkType", Arrays.asList(new Integer[] { Material.LinkType.URL.getValue(), Material.LinkType.TOPIC.getValue() }));
					}
				} else if (environment.equals("preproduction")) {
					if (10173 == advertisement.getAdvertisementId()) {
						data.put("linkType", Arrays.asList(new Integer[] { Material.LinkType.URL.getValue(), Material.LinkType.TOPIC.getValue() }));
					}
				} else if (187 == advertisement.getAdvertisementId()) {
					data.put("linkType", Arrays.asList(new Integer[] { Material.LinkType.URL.getValue(), Material.LinkType.TOPIC.getValue() }));
				}
				data.put("templateId", advertisement.getWebpageTemplateId());
			}
		}
		return data;
	}

	/**
	 * 根据flightId获取投放单元
	 *
	 * @param flightId
	 * @return
	 */
	public FlightForm getFlightByFlightId(Integer flightId) throws Exception {
		FlightForm data = new FlightForm();

		Flight flight = flightDao.get(flightId);
		if (null == flight) {
			throw new Exception("flight not exist flightId: " + flightId);
		}
		logger.info("getFlightByFlightId flight: " + flight);
		if (Flight.ProductLine.FIXED_BID_CPC.getValue().equals(flight.getProductLine())) {
			return fixedCpcFlightManager.getFlightByFlightId(flightId);
		} else if (Flight.ProductLine.BID_CPC.getValue().equals(flight.getProductLine())) {
			return bidCpcFlightManager.getFlightByFlightId(flightId);
		}
		return data;
	}

	/**
	 * 根据某campaignId对应的投放单元分页列表
	 * @param pagination
	 * @param campaignId
	 * @param keyword
	 * @param platform
	 * @param state
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public HashMap<String, Object> getFlightsByCampaignId(Pagination pagination, Integer campaignId, String keyword,
			Integer platform, Integer state, String startTime, String endTime, Integer productLine) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> flights = new ArrayList<Map<String, Object>>();
		Map<String, Object> search = null;
		Map<String, Object> recomment = null;
		Map<String, Object> productType = null;
		List<Map<String, Object>> list =  null;
		DecimalFormat format = new DecimalFormat("0.00");
		double searchMinBid = 0.0;
		double searchMaxBid = 0.0;
		double adBid = 0.0;
		List<Flight> flightList = flightDao.getFlightsByCampaignId(pagination, campaignId, keyword, platform, state, productLine);
		if (!CollectionUtils.isEmpty(flightList)) {

			List<Integer> flightIds = new ArrayList<Integer>();
			List<Integer> dspFlightIds = new ArrayList<Integer>();
			for (Flight flight : flightList) {
				flightIds.add(flight.getFlightId());
				dspFlightIds.add(flight.getDspFlightId());
			}

			// 查询dmp统计数据
			Map<Integer, Map<String, Object>> flightStatistics = new HashMap<Integer, Map<String, Object>>();
			//竞价cpc
			Map<Integer, List<Map<String, Object>>> flightsMap = new HashMap<Integer, List<Map<String, Object>>>();
			try {
				String dspFlightIdsStringTmp = dspFlightIds.toString();
				String dspFlightIdsString = dspFlightIdsStringTmp.substring(1, dspFlightIdsStringTmp.length() - 1);
				String startEventTime = TimeUtil.dateToString(TimeUtil.formateDate((new Date(Long.parseLong(startTime)))));
				String endEventTime = TimeUtil.dateToString(TimeUtil.formateDate((new Date(Long.parseLong(endTime)))));
				String dspAdvertiserId = PrincipalUtil.getDspAdvertiserId().toString();
				logger.info("dspFlightIdsString: " + dspFlightIdsString);
				logger.info("advertiserId: " + dspAdvertiserId);
				logger.info("startEventTime: " + startEventTime);
				logger.info("endEventTime: " + endEventTime);
				ApiResponse response = dmpFlightOperations.getFlightStatistics(dspFlightIdsString, dspAdvertiserId,
						startEventTime, endEventTime, productLine);
				Map<String, Object> flightData = response.getData();
				logger.info("flightData:" + flightData);
				List<Map<String, Object>> report = (List<Map<String, Object>>) flightData.get("report");
				logger.info("report:" + report);
				Map<Double, Integer> flightCount = new HashMap<Double, Integer>();
				if (!CollectionUtils.isEmpty(report)) {
					for (Map<String, Object> countMap : report) {
						Double dspFlightId = Double.parseDouble(countMap.get("flightId").toString());
						if(flightCount.containsKey(dspFlightId)){
							int count = flightCount.get(dspFlightId);
							flightCount.put(dspFlightId, ++count);
						}else{
							flightCount.put(dspFlightId, 1);
						}
					}
					for (Map<String, Object> tmp : report) {
						search = new HashMap<String, Object>();
						recomment = new HashMap<String, Object>();
						productType = new HashMap<String, Object>();
						list = new ArrayList<Map<String, Object>>();
						Double dspFlightId = Double.parseDouble(tmp.get("flightId").toString());
						if(Flight.ProductLine.FIXED_BID_CPC.getValue() == productLine){
							flightStatistics.put(dspFlightId.intValue(), tmp);
						} else if (Flight.ProductLine.BID_CPC.getValue() == productLine) {
							Double type = Double.parseDouble(tmp.get("productType").toString());
							//获取对应额投放单元信息
							Flight flight = flightDao.getFlightByDspFlightId(dspFlightId.intValue());
							//获取资源位信息
							List<Integer> source = new ArrayList<Integer>();
							List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getRecommendByFlightIdAndReco(flight.getFlightId());
							if(!CollectionUtils.isEmpty(flightAdvertisements)){
								for(FlightAdvertisement flightAdvertisement : flightAdvertisements){
									if(flightAdvertisement.getAdGroup() == FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue()){
										adBid = flightAdvertisement.getAdBid().floatValue() / 100;
									}
									source.add(flightAdvertisement.getAdGroup());
								}
							}
							//获取投放单元对应的关键词信息
							List<Keyword> getKeywordsByFlightId = keywordDao.getKeywordsByFlightId(flight.getFlightId());
							if(!CollectionUtils.isEmpty(getKeywordsByFlightId)){
								//最小及最大出价
								searchMinBid = getKeywordsByFlightId.get(0).getPcBid().floatValue() / 100;
								searchMaxBid = getKeywordsByFlightId.get(getKeywordsByFlightId.size()-1).getPcBid().floatValue() / 100;
							}
							if(Flight.productType.RECOMMEND.getValue() == type.intValue() && flightCount.get(dspFlightId)==1){
								if(!source.contains(FlightAdvertisement.VariableAdGroup.SEARCH.getValue())){
									search.put("searchMinBid", null);
									search.put("searchMaxBid", null);
								}else{
									search.put("searchMinBid", format.format(searchMinBid));
									search.put("searchMaxBid", format.format(searchMaxBid));
								}
								search.put("impressionNumber", null);
								search.put("clickNumber", null);
								search.put("clkRate", null);
								search.put("totalFee", null);
								search.put("clickUnitPrice", null);
								search.put("costPerMills",null);
								productType.put("search", search);
							}else if(Flight.productType.SEARCH.getValue() == type.intValue() &&  flightCount.get(dspFlightId)==1){
								if(!source.contains(FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue())){
									recomment.put("adBid", null);
								}else{
									recomment.put("adBid", format.format(adBid));
								}
								recomment.put("impressionNumber", null);
								recomment.put("clickNumber", null);
								recomment.put("clkRate", null);
								recomment.put("totalFee", null);
								recomment.put("clickUnitPrice", null);
								recomment.put("costPerMills", null);
								productType.put("recomment", recomment);
							}
							if (Flight.productType.SEARCH.getValue() == type.intValue()) {
								Double totalFee = Double.parseDouble(tmp.get("totalFee").toString());
								Double clickNumber = Double.parseDouble(tmp.get("clickNumber").toString());
								Double impressionNumber = Double.parseDouble(tmp.get("impressionNumber").toString());
								Double ctr = Double.parseDouble(tmp.get("clkRate").toString());
								if(!source.contains(FlightAdvertisement.VariableAdGroup.SEARCH.getValue())){
									search.put("searchMinBid", null);
									search.put("searchMaxBid", null);
								}else{
									search.put("searchMinBid", format.format(searchMinBid));
									search.put("searchMaxBid", format.format(searchMaxBid));
								}
								search.put("impressionNumber", tmp.get("impressionNumber"));
								search.put("clickNumber", tmp.get("clickNumber"));
								search.put("clkRate", format.format(ctr * 100));
								search.put("totalFee", totalFee / 1000000);
								search.put("clickUnitPrice", format.format(clickNumber.intValue() == 0 ? 0
										:totalFee / (clickNumber * 1000000)));
								search.put("costPerMills",
										format.format((impressionNumber.intValue() == 0 ? 0
												:totalFee / (impressionNumber * 1000000)) * 1000));
								productType.put("search", search);
								if(flightsMap.containsKey(dspFlightId.intValue())){
									flightsMap.get(dspFlightId.intValue()).get(0).put("search", search);
								}else{
									list.add(productType);
									flightsMap.put(dspFlightId.intValue(), list);
								}
							} else if (Flight.productType.RECOMMEND.getValue() == type.intValue()) {
								Double totalFee = Double.parseDouble(tmp.get("totalFee").toString());
								Double clickNumber = Double.parseDouble(tmp.get("clickNumber").toString());
								Double impressionNumber = Double.parseDouble(tmp.get("impressionNumber").toString());
								Double ctr = Double.parseDouble(tmp.get("clkRate").toString());
								if(!source.contains(FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue())){
									recomment.put("adBid", null);
								}else{
									recomment.put("adBid", format.format(adBid));
								}
								recomment.put("impressionNumber", tmp.get("impressionNumber"));
								recomment.put("clickNumber", tmp.get("clickNumber"));
								recomment.put("clkRate", format.format(ctr * 100));
								recomment.put("totalFee", totalFee / 1000000);
								recomment.put("clickUnitPrice", format.format(clickNumber.intValue() == 0 ? 0
										: totalFee / (clickNumber.intValue() * 1000000)));
								recomment.put("costPerMills", format.format((impressionNumber.intValue() == 0 ? 0
										: totalFee / (impressionNumber.intValue() * 1000000)) * 1000));
								productType.put("recomment", recomment);
								if(flightsMap.containsKey(dspFlightId.intValue())){
									flightsMap.get(dspFlightId.intValue()).get(0).put("recomment", recomment);
								}else{
									list.add(productType);
									flightsMap.put(dspFlightId.intValue(), list);
								}
								
							}
						}
					}
				}
			} catch (Exception exception) {
				logger.error("getFlightStatistics from dmp fail throw Exception", exception);
			}
			logger.info("flightStatistics: " + flightStatistics);

			//投放单元维度，查询当前用户的广告总花费与返利总花费
			//Map<Integer, BigInteger> adFlightExpense = null;
			Map<Integer, BigInteger> rebateFlightExpense = null;
			try {
				Map<String, Map<Integer, BigInteger>> campaignExpense =  expenseService.getAmountByDspFlightIds(dspFlightIds, TimeUtil.formateDate((new Date(Long.parseLong(startTime)))), TimeUtil.formateDate((new Date(Long.parseLong(endTime)))));
				logger.info("campaignExpense: "+campaignExpense);
				//adFlightExpense = campaignExpense.get("adAmount");
				rebateFlightExpense = campaignExpense.get("rebateAmount");
			} catch (Exception e) {
				logger.error("expenseService.getAmountByDspCampaignIds throw Exception ",e);
			}
			
			Map<Integer, Strategy> strategyMap = strategyDao.getStrategiesByFlightIds(flightIds);
			for (Flight flight : flightList) {
				Map<String, Object> flightMap = new HashMap<String, Object>();
				flightMap.put("campaignId", campaignId);
				flightMap.put("flightId", flight.getFlightId());
				flightMap.put("name", flight.getName());
				flightMap.put("state", flight.getState());
				flightMap.put("platform", flight.getPlatform());
				flightMap.put("type", flight.getType());

				if (!CollectionUtils.isEmpty(strategyMap)) {
					Strategy strategy = strategyMap.get(flight.getFlightId());
					if (null != strategy) {
						flightMap.put("timeType", strategy.getTimeType());
						flightMap.put("regionType", strategy.getRegionType());
					}
				}
				Map<String, Object> flightReport = null;
				if(Flight.ProductLine.FIXED_BID_CPC.getValue() == productLine){
					if (!CollectionUtils.isEmpty(flightStatistics)) {
						flightReport = flightStatistics.get(flight.getDspFlightId());
						if (null != flightReport) {
								Double totalFee = Double.parseDouble(flightReport.get("totalFee").toString());
								flightMap.put("impression", flightReport.get("impressionNumber"));
								flightMap.put("click", flightReport.get("clickNumber"));
								flightMap.put("adAmount", totalFee / 1000000);
								if (null != flightReport.get("clkRate")){
									Double ctr = Double.parseDouble(flightReport.get("clkRate").toString());
									BigDecimal bigDecimal = new BigDecimal(ctr * 100);
									flightMap.put("ctr", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
								}
							
						}
					}
				}
			    if(Flight.ProductLine.BID_CPC.getValue() == productLine){
					//竞价cpc
					flightMap.put("dmpData", flightsMap.get(flight.getDspFlightId()));
				}
				if(Flight.ProductLine.FIXED_BID_CPC.getValue() == productLine){
					// 填充广告总花费、返利总花费
					/*if (!CollectionUtils.isEmpty(adFlightExpense)) {
						if (null != adFlightExpense.get(flight.getDspFlightId())) {
							Double adAmount = Double.parseDouble(adFlightExpense.get(flight.getDspFlightId()).toString());
							flightMap.put("adAmount", adAmount / 100);
							
						}
					}*/
	
					if (!CollectionUtils.isEmpty(rebateFlightExpense)) {
						if (null != rebateFlightExpense.get(flight.getDspFlightId())) {
							Double rebateAmount = Double.parseDouble(rebateFlightExpense.get(flight.getDspFlightId()).toString());
							flightMap.put("rebateAmount", rebateAmount / 100);
						}
					}
					
					if (null == flightMap.get("impression")) {
						flightMap.put("impression", 0);
					}
					if (null == flightMap.get("click")) {
						flightMap.put("click", 0);
					}
					if (null == flightMap.get("ctr")) {
						flightMap.put("ctr", 0);
					}
					if (null == flightMap.get("adAmount")) {
						flightMap.put("adAmount", 0);
					}
					if (null == flightMap.get("rebateAmount")) {
						flightMap.put("rebateAmount", 0);
					}
				}else{
					if(null == flightMap.get("dmpData")){
						search = new HashMap<String, Object>();
						recomment = new HashMap<String, Object>();
						productType = new HashMap<String, Object>();
						list = new ArrayList<Map<String, Object>>();
						List<Keyword> getKeywordsByFlightId = keywordDao.getKeywordsByFlightId(flight.getFlightId());
						if(!CollectionUtils.isEmpty(getKeywordsByFlightId)){
							//最小及最大出价
							searchMinBid = getKeywordsByFlightId.get(0).getPcBid().floatValue() / 100;
							searchMaxBid = getKeywordsByFlightId.get(getKeywordsByFlightId.size()-1).getPcBid().floatValue() / 100;
						}
						List<Integer> source = new ArrayList<Integer>();
						List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getRecommendByFlightIdAndReco(flight.getFlightId());
						if(!CollectionUtils.isEmpty(flightAdvertisements)){
							for(FlightAdvertisement flightAdvertisement : flightAdvertisements){
								if(flightAdvertisement.getAdGroup() == FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue()){
									adBid = flightAdvertisement.getAdBid().floatValue() / 100;
								}
								source.add(flightAdvertisement.getAdGroup());
							}
						}
						search.put("impressionNumber", null);
						search.put("clickNumber", null);
						search.put("clkRate", null);
						search.put("totalFee", null);
						search.put("clickUnitPrice", null);
						search.put("costPerMills",null);
						recomment.put("impressionNumber", null);
						recomment.put("clickNumber", null);
						recomment.put("clkRate", null);
						recomment.put("totalFee", null);
						recomment.put("clickUnitPrice", null);
						recomment.put("costPerMills",null);
						productType.put("search", search);
						productType.put("recomment", recomment);
						for (String key : productType.keySet()) {
							if(key.equals("search")){
								if(!source.contains(FlightAdvertisement.VariableAdGroup.SEARCH.getValue())){
									search.put("searchMinBid", null);
									search.put("searchMaxBid", null);
								}else{
									search.put("searchMinBid", format.format(searchMinBid));
									search.put("searchMaxBid", format.format(searchMaxBid));
								}
							}else if(key.equals("recomment")){
								if(!source.contains(FlightAdvertisement.VariableAdGroup.RECOMMEND.getValue())){
									recomment.put("adBid", null);
								}else{
									recomment.put("adBid", format.format(adBid));
								}
							}
						}
						list.add(productType);
						flightMap.put("dmpData", list);
					}
				}
				flights.add(flightMap);
			}
		}
		data.put("totalCount", pagination.getTotalCount());
		data.put("page", pagination.getCurrentPage());
		data.put("number", pagination.getNumber());
		data.put("list", flights);

		return data;
	}
	
	/**
	 * 批量删除投放单元
	 * @param flightsDeleteMap
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void batchDelete(Map<String, Object> flightsDeleteMap) throws Exception {

		List<Double> flightDoubleIds = (List<Double>) flightsDeleteMap.get("flightIds");

		List<Integer> flightIds = new ArrayList<Integer>();
		for (Double flightDoubleId : flightDoubleIds) {
			Integer flightId = flightDoubleId.intValue();
			flightIds.add(flightId);
		}

		Integer deleteStatus = Flight.Status.DELETE.getValue();
		Integer normalStatus = Flight.Status.NORMAL.getValue();
		
		List<String> errors = new ArrayList<String>();

		List<Flight> flights = flightDao.get(flightIds);
		// TODO 详细的验证规则
		List<Integer> deleteflightIds = new ArrayList<Integer>();
		for (Flight flight : flights) {
			if (flight.getStatus().equals(normalStatus)) {
				errors.add(flight.getName() + "不能删除");
				continue;
			}
			flight.setStatus(deleteStatus);
			flight.setUpdateTime(new Date());
			flightDao.update(flight);
			deleteflightIds.add(flight.getFlightId());
			
			adsGroupOperations.delete(flight.getDspFlightId());
		}
		
		// 将成功删除的投放单元对应的所有创意一并删除
		List<Material> deleteMaterials = null;
		if (!CollectionUtils.isEmpty(deleteflightIds)) {
			deleteMaterials = materialDao.getMaterialsByFlightIds(deleteflightIds);
		}
		if (!CollectionUtils.isEmpty(deleteMaterials)) {
			for (Material material : deleteMaterials) {
				material.setStatus(Material.Status.DELETE.getValue());
			}
			materialDao.batchDeleteMaterials(deleteMaterials);
		}

		if (errors.size() > 0) {
			String errorsString = String.join(",", errors);
			throw new Exception(errorsString);
		}
	}
	
	/**
	 * 批量修改投放单元状态
	 * @param flightsStatusMap
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void batchUpdateStatus(Map<String, Object> flightsStatusMap) throws Exception {

		List<Double> flightDoubleIds = (List<Double>) flightsStatusMap.get("flightIds");
		Integer status = ((Double) flightsStatusMap.get("status")).intValue();

		List<Integer> flightIds = new ArrayList<Integer>();
		for (Double flightDoubleId : flightDoubleIds) {
			Integer flightId = flightDoubleId.intValue();
			flightIds.add(flightId);
		}
		logger.info("batchUpdateStatus flightIds: "+flightIds);
		logger.info("batchUpdateStatus status: "+status);

		// @todo 参数验证


		Integer normalStatus = Flight.Status.NORMAL.getValue();
		Integer suspendStatus = Flight.Status.SUSPEND.getValue();
		List<String> errors = new ArrayList<String>();
		Date currentTime = new Date();

		List<Flight> flights = flightDao.get(flightIds);

		// @todo 余额不足状态判断
		// 启用：将选中的处于暂停状态（除余额不足外）的投放单元设为有效状态
		if (status.equals(normalStatus)) {
			for (Flight flight : flights) {
				if (!flight.getStatus().equals(suspendStatus)) {
					errors.add(flight.getName() + "状态异常");
					continue;
				}
				flight.setStatus(normalStatus);
				flight.setUpdateTime(currentTime);
				flightDao.update(flight);
				
				//投放系统中的实体状态 0-启用  1-暂停
				adsGroupOperations.updateStatus(flight.getDspFlightId(), 0);
			}
		// @todo 余额不足、未开始、过期状态判断
		// 暂停：将选中的处于有效、未开始状态及余额不足的投放单元设置为暂停状态；
		} else if (status.equals(suspendStatus)) {
			for (Flight flight : flights) {
				if (!flight.getStatus().equals(normalStatus)) {
					errors.add(flight.getName() + "状态异常");
					continue;
				}
				flight.setStatus(suspendStatus);
				flight.setUpdateTime(currentTime);
				flightDao.update(flight);
				//投放系统中的实体状态 0-启用  1-暂停
				adsGroupOperations.updateStatus(flight.getDspFlightId(), 1);
			}
		}

		if (errors.size() > 0) {
			String errorsString = String.join(",", errors);
			throw new Exception(errorsString);
		}
	}
	
	
	/**
	 * 添加自定义关键词
	 * @param name
	 * @return
	 */
	public Map<String, Object> addDefinedKeyword(String name) {
		DmpKeywordOperations dmpKeywordOperations = new DmpKeywordOperations();
		ApiResponse apiResponse = dmpKeywordOperations.addKeyword(name);
		Map<String, Object> data = apiResponse.getData();
		Map<String,Object> reSetData = new HashMap<String,Object>();
		if(null!=data){
			reSetData.put("id", data.get("id"));
			reSetData.put("name", data.get("name"));
		}
		return reSetData;
	}
	
	/**
	 * 关键词管理 修改关键词
	 * @param keywordsMap
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void updateKeywords(Map<String, Object> keywordsMap) throws Exception {
		logger.info("updateKeywords keywordsMap: " + keywordsMap);
		Integer flightId = Integer.parseInt((keywordsMap.get("flightId")).toString());
		List<Map<String, Object>> keywords = (List<Map<String, Object>>) keywordsMap.get("keywords");

		// 修改关键词
		List<Keyword> oldKeywords = keywordDao.getKeywordsByFlightId(flightId);
		if (!CollectionUtils.isEmpty(oldKeywords)) {
			keywordDao.batchDeleteKeywords(oldKeywords);
		}

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();

		List<Map<String, Object>> keywordsForAdsList = new ArrayList<Map<String, Object>>();
		if (!CollectionUtils.isEmpty(keywords)) {
			List<Keyword> keywordList = new ArrayList<Keyword>();
			for (Map<String, Object> keywordTmp : keywords) {
				Keyword keyword = new Keyword();
				keyword.setFlightId(flightId);

				keyword.setName((String) keywordTmp.get("name"));
				keyword.setDmpKeywordId((Integer) keywordTmp.get("id"));
				keyword.setPcBid(new Float(100 * new Float(keywordTmp.get("pcBid").toString())).intValue());
				keyword.setAveragePrice(new Double(keywordTmp.get("averagePrice").toString()).intValue() * 100);
				keyword.setPurchaseStar(new Float(keywordTmp.get("purchaseStar").toString()));
				keyword.setSearchStar(new Float(keywordTmp.get("searchStar").toString()));
				keyword.setPlatform((Integer) keywordTmp.get("source"));
				keyword.setUserId(userId);
				keyword.setStatus(Keyword.Status.NORMAL.getValue());
				keywordList.add(keyword);

				Map<String, Object> keywordForAds = new LinkedHashMap<String, Object>();
				keywordForAds.put("keywordId", (Integer) keywordTmp.get("id"));
				keywordForAds.put("keyword", (String) keywordTmp.get("name"));
				keywordForAds.put("bid", 100 * new Float(keywordTmp.get("pcBid").toString()));
				keywordsForAdsList.add(keywordForAds);
			}
			keywordDao.batchSaveKeywords(keywordList);
		}
		AdsGroup adsGroup = bidCpcFlightManager.buildAdGroup(flightId);
		adsGroup.setUpdateUser(userName);
		adsGroup.setKeywords(keywordsForAdsList);
		adsGroupOperations.update(adsGroup);
	}
	
	/**
	 * 查询当前用户在投放计划下的有效投放单元总数
	 * @param name
	 * @param campaignId
	 * @return
	 */
	public Boolean isExistFlightName(String name, Integer flightId, Integer campaignId) {
		Boolean isExist = false;
		List<Flight> flights = flightDao.getFlightsByNameCampaignId(name, campaignId);
		if (!CollectionUtils.isEmpty(flights)) {
			for (Flight flight : flights) {
				if (flight.getName().equals(name) && !flight.getFlightId().equals(flightId)) {
					isExist = true;
					break;
				}
			}
		}
		logger.info("isExistFlightName isExist: " + isExist);
		return isExist;
	}
}
