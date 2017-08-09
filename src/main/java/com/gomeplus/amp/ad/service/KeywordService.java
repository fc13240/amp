package com.gomeplus.amp.ad.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.dmp.DmpKeywordOperations;
import com.gomeplus.adm.common.api.mall.MallProductOperations;
import com.gomeplus.amp.ad.dao.KeywordDao;
import com.gomeplus.amp.ad.model.Keyword;
import com.google.gson.Gson;
import com.mashape.unirest.http.JsonNode;

/**
 * 关键词service
 * 
 * @author sunyunlong
 * @description
 * @parameter
 */
@Service
@Transactional(readOnly = true)
public class KeywordService {
	@Autowired
	private KeywordDao keywordDao;
	private MallProductOperations mallProductOperations = new MallProductOperations();
	private DmpKeywordOperations dmpKeywordOperations = new DmpKeywordOperations();
	private static Logger logger = LoggerFactory.getLogger(KeywordService.class);

	/**
	 * 通过flightId获取关键词信息
	 * 
	 * @param flightId
	 * @return
	 */
	public Map<String, Object> getKeywordsByFlightId(Integer flightId) {
		// 封装数组
		List<Map<String, Object>> resetKeyWordList = new ArrayList<Map<String, Object>>();
		// 封装结果集
		Map<String, Object> resetKeywordMap = new HashMap<String, Object>();
		// 获取投放单元对应的关键词信息集
		List<Keyword> keywordList = keywordDao.getKeywordsByFlightId(flightId);
		if (!CollectionUtils.isEmpty(keywordList)) {
			for (Keyword keyword : keywordList) {
				Map<String, Object> keywordMap = new HashMap<String, Object>();
				keywordMap.put("name", keyword.getName());
				keywordMap.put("id", keyword.getDmpKeywordId());
				keywordMap.put("pcBid", keyword.getPcBid().floatValue() / 100);
				keywordMap.put("averagePrice", keyword.getAveragePrice());
				keywordMap.put("purchaseStar", keyword.getPurchaseStar());
				keywordMap.put("searchStar", keyword.getSearchStar());
				keywordMap.put("source", keyword.getPlatform());
				resetKeyWordList.add(keywordMap);
			}
			resetKeywordMap.put("keywords", resetKeyWordList);
		}
		return resetKeywordMap;
	}

	/**
	 * 通过skuId获取关键词
	 * 
	 * @param skuId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getkeywordsBySkuId(String skuId) throws Exception {
		// 通过skuId获取商品信息
		ApiResponse itemResponse = mallProductOperations.getItemsBySkuIds(skuId);
		// 组装请求参数
		Map<String, Object> item = new HashMap<String, Object>();
		Map<String, Object> material = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> response = itemResponse.getData();
		List<Map<String,Object>> list = (List<Map<String, Object>>) response.get("list");
		if (CollectionUtils.isEmpty(list)) {
			logger.error("getItemsBySkuIds is null,skuId: " + skuId);
			throw new Exception("getItemsBySkuIds is null ");
		}
		Map<String,Object> skq = null;
		if (!CollectionUtils.isEmpty(list)) {
			skq = list.get(0);
		}
		
		item.put("ctgyid1", skq.get("firstcatalogy"));
		item.put("ctgyid2", skq.get("seccatalogy"));
		item.put("ctgyid3", skq.get("thridcatalogy"));
		item.put("product_id", skq.get("productId"));
		item.put("goods_name", skq.get("commodityTitle"));
		item.put("sku_id", skq.get("skuId"));
		item.put("brand", skq.get("brandName"));
		item.put("goods_price", 0);
		item.put("id", 0);
		material.put("sku", item);
		material.put("id", 0);
		params.put("resourceid", 0);
		params.put("price", 0);
		params.put("sid", 0);
		params.put("material", material);
		//dmp返回数据
		JsonNode responseJson = dmpKeywordOperations.getKeyword(params);
		logger.info("dmpKeywordOperations.getKeyword responseJson: " + responseJson);
		// 组装关键词数据
		if (null == responseJson) {
			logger.error("getKeyword is null,skuId: " + skuId);
			throw new Exception("getKeyword is null ");
		}
		List<Map<String, Object>> responseList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> keyWords = new ArrayList<Map<String, Object>>();
		responseList = (List<Map<String, Object>>) new Gson().fromJson(responseJson.toString(),
				responseList.getClass());
		if (!CollectionUtils.isEmpty(responseList)) {
			for (Map<String, Object> map : responseList) {
				// 重组map
				Map<String, Object> resetMap = new HashMap<String, Object>();
				resetMap.put("id", map.get("id"));
				resetMap.put("averagePrice", map.get("pricea"));
				resetMap.put("name", map.get("word"));
				resetMap.put("searchStar", map.get("search_star"));
				resetMap.put("purchaseStar", map.get("purchase_star"));
				resetMap.put("source", map.get("platform"));
				keyWords.add(resetMap);
			}
		}
		data.put("skuId", skuId);
		data.put("keywords", keyWords);
		return data;
	}

}
