package com.gomeplus.amp.ad.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.ads.AdsAdOperations;
import com.gomeplus.adm.common.api.ads.AdsBannedCategoryOperations;
import com.gomeplus.adm.common.api.ads.model.AdsAd;
import com.gomeplus.adm.common.api.ads.model.AdsBannedCategory;
import com.gomeplus.adm.common.api.bs.BsItemOperation;
import com.gomeplus.adm.common.api.bs.BsSearchOperation;
import com.gomeplus.adm.common.api.mall.MallProductOperations;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.util.DESUtil;
import com.gomeplus.adm.common.util.FileUtil;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.dao.AdvertisementDao;
import com.gomeplus.amp.ad.dao.BannedCategoryDao;
import com.gomeplus.amp.ad.dao.FlightAdvertisementDao;
import com.gomeplus.amp.ad.dao.FlightDao;
import com.gomeplus.amp.ad.dao.FlightMaterialDao;
import com.gomeplus.amp.ad.dao.MaterialDao;
import com.gomeplus.amp.ad.dao.MaterialItemDao;
import com.gomeplus.amp.ad.dao.PageItemDao;
import com.gomeplus.amp.ad.form.MaterialForm;
import com.gomeplus.amp.ad.manager.FixedCpcFlightManager;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.BannedCategory;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.Flight;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Material;
import com.gomeplus.amp.ad.model.MaterialItem;
import com.gomeplus.amp.ad.model.MaterialItem.UserDefine;
import com.gomeplus.amp.ad.model.PageTemplate;
import com.gomeplus.amp.ad.model.Slot;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;

/**
 * 素材service
 * 
 * @author wangwei01
 *
 */
@Service
@Transactional(readOnly = true)
public class MaterialService extends BaseService<Material, Integer> {

	@Autowired
	private MaterialDao materialDao;
	@Autowired
	private FlightDao flightDao;
	@Autowired
	private FlightAdvertisementDao flightAdvertisementDao;
	@Autowired
	private FlightMaterialDao flightMaterialDao;
	@Autowired
	private MaterialItemDao materialItemDao;
	@Autowired
	private BannedCategoryDao bannedCategoryDao;
	@Autowired
	private PageItemDao pageItemDao;
	@Autowired
	private AdvertisementDao advertisementDao;
	@Autowired
	private FixedCpcFlightManager fixedCpcFlightManager;
	private static Logger logger = LoggerFactory.getLogger(MaterialService.class);
	private MallProductOperations mallProductOperations = new MallProductOperations();
	private Gson gson = new Gson();
	private BsItemOperation bsItemOperation = new BsItemOperation();
	private AdsAdOperations adsAdOperations = new AdsAdOperations();
	private BsSearchOperation bsSearchOperation = new BsSearchOperation();
	private AdsBannedCategoryOperations adsBannedCategoryOperations = new AdsBannedCategoryOperations();

	@Override
	public HibernateDao<Material, Integer> getEntityDao() {
		return materialDao;
	}

	/**
	 * 添加创意
	 * 
	 * materialMap 数据格式
	 * 
	 * flightId		投放单元id	Number	必须
	 * itemId		商品id	Number	必须
	 * name			创意名称	String	必须
	 * type			素材类型 1图片 2图文 3商品	Number	必须
	 * linkType		链接类型 1商品 2url	Number	必须
	 * title		广告标题	String	必须
	 * image		创意图片	String	必须
	 * wdith		图片宽度	Number	非必须
	 * height		图片高度	Number	非必须
	 * description	广告文案	String	必须
	 * landingPage	落地页	String	非必须
	 * relatedItems	联合推广商品	Array	必须
	 * 
	 * @param materialMap
	 */	
	@Transactional(readOnly = false)
	public HashMap<String, Object> save(MaterialForm materialForm) throws Exception {
		logger.info("============material create");
		logger.info("materialForm: " + materialForm);
		HashMap<String, Object> saveResult = new LinkedHashMap<String, Object>();
		Integer flightId = materialForm.getFlightId();
		String productId = materialForm.getProductId();
		String promotionId = materialForm.getPromotionId();
		String name = materialForm.getName();
		Integer type = materialForm.getType();
		if(null == type){
			type = Material.Type.IMAGE.getValue();			
		}
		Integer linkType = materialForm.getLinkType();
		String title = materialForm.getTitle();
		List<String> images = materialForm.getImage();
		Integer width = materialForm.getWidth();
		Integer height = materialForm.getHeight();
		String description = materialForm.getDescription();
		String landingPage = materialForm.getLandingPage();
		Integer webpageId = materialForm.getWebpageId();
		if (!StringUtils.isEmpty(landingPage) && (Material.LinkType.URL.getValue().equals(linkType) || Material.LinkType.SHOP.getValue().equals(linkType) || Material.LinkType.VIDEO.getValue().equals(linkType))) {
			landingPage = new DESUtil().decode(landingPage);
		}
		List<Map<String,Object>> relatedItems = materialForm.getRelatedItems();
		Integer relatedItemStrategy = materialForm.getRelatedItemStrategy();
		String relatedItemsJson = JSON.toJSONString(relatedItems);
		Integer productLine = materialForm.getProductLine();
		if (null == productLine) {
			productLine = Material.ProductLine.FIXED_BID_CPC.getValue();
		}
		
		String image = "";
		if(!CollectionUtils.isEmpty(images)){
			image = StringUtil.join(images.toArray(), ",");	
		}

		logger.info("flightId: " + flightId);
		logger.info("productId: " + productId);
		logger.info("promotionId: " + promotionId);
		logger.info("name: " + name);
		logger.info("type: " + type);
		logger.info("linkType: " + linkType);
		logger.info("title: " + title);
		logger.info("images: " + images);
		logger.info("width: " + width);
		logger.info("height: " + height);
		logger.info("description: " + description);
		logger.info("landingPage: " + landingPage);
		logger.info("webpageId: " + webpageId);
		logger.info("relatedItems: " + relatedItems);
		logger.info("relatedItemStrategy: " + relatedItemStrategy);
		logger.info("relatedItemsJson: " + relatedItemsJson);

		logger.info("============material create params success");

		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();

		logger.info("userId: " + userId);
		logger.info("userName: " + userName);

		Integer status = Material.Status.NORMAL.getValue();
//		Integer approveStatus = Material.ApproveStatus.APPROVED.getValue();
		Integer approveStatus = Material.ApproveStatus.APPROVING.getValue();
		Date currentTime = new Date();

		Material material = new Material(flightId, name, width, height, promotionId, emojiConvert(title), emojiConvert(description), image, landingPage, userId, type, linkType, approveStatus, productLine, status, currentTime, currentTime);
		if(!CollectionUtils.isEmpty(images)){
			material.setImage(StringUtil.join(images.toArray(), ","));	
		}
		//设置特殊的promotionId
		if (Material.LinkType.ITEM.getValue().equals(linkType)) {
			material.setPromotionId(productId + ":" + promotionId);
			material.setRelatedItems(getRelatedItems(relatedItemsJson));
		}
		
		if (null != relatedItemStrategy){
			material.setRelatedItemStrategy(relatedItemStrategy);
		}
		
		if (null == webpageId) {
			webpageId = 0;
		}
		material.setWebpageId(webpageId);
		
		// 配合二次审核需求，创意来源、审核角色 记录固定值
		material.setApproveRole("advertisement_approve");
		material.setCreateFrom("amp");
		materialDao.save(material);

		// 创意保存成功，获得materialId
		Integer materialId = material.getMaterialId();
		logger.info("============material save success");
		logger.info("materialId: " + materialId);

		//商品创意之外的relatedItems中放跟单sku,需要解析存入ams_material_item表
		if (!Material.LinkType.ITEM.getValue().equals(linkType) && !CollectionUtils.isEmpty(relatedItems)) {
			List<MaterialItem> relatedItemList = new ArrayList<MaterialItem>();
			for (Map<String, Object> relatedItem : relatedItems) {
				MaterialItem materialItem = new MaterialItem();
				// 前端传来的type字段（0-用户自定义, 1-非用户自定义）
				if (null == relatedItem.get("type")	|| !MaterialItem.UserDefine.CUSTOM.getValue().equals(Integer.parseInt(relatedItem.get("type").toString()))) {
					continue;
				}
				materialItem.setIsUserDefine(Integer.parseInt((relatedItem.get("type").toString())));
				materialItem.setMaterialId(materialId);
				if (null != relatedItem.get("skuId")) {
					materialItem.setSkuId(relatedItem.get("skuId").toString());
				}
				if (null != relatedItem.get("productId")) {
					materialItem.setProductId(relatedItem.get("productId").toString());
				}
				if (null != relatedItem.get("shopId")) {
					materialItem.setShopId(relatedItem.get("shopId").toString());
				}
				if (null != relatedItem.get("description")) {
					materialItem.setDescription(relatedItem.get("description").toString());
				}
				if (null != relatedItem.get("images")) {
					List<String> relatedItemImages = (List<String>) relatedItem.get("images");
					if (!CollectionUtils.isEmpty(relatedItemImages)) {
						materialItem.setImage(relatedItemImages.get(0));
					}
				}
				
				//目前,前端传入的都是手填的商品skuId
				materialItem.setType(MaterialItem.Type.ITEM.getValue());
				
				relatedItemList.add(materialItem);
			}
			if (!CollectionUtils.isEmpty(relatedItemList)) {
				materialItemDao.batchSaveMaterialItems(relatedItemList);
			}

		}
		
		// 特殊处理话题带过来的跟单商品
		if (Material.LinkType.TOPIC.getValue().equals(linkType)) {
			List<MaterialItem> topicMaterialItems = getTopicMaterialItems(promotionId);
			if (!CollectionUtils.isEmpty(topicMaterialItems)) {
				for (MaterialItem materialItem : topicMaterialItems) {
					materialItem.setMaterialId(materialId);
				}
				materialItemDao.batchSaveMaterialItems(topicMaterialItems);
			}
		}

		// 设置中间页
		// 是否返利,都传中间页
		// 非自建页面的创意（话题、商品）需要拼地址； 自建页面类创意，可以从前端直接传landingPage过来
		if (Material.LinkType.TOPIC.getValue().equals(linkType)) {
			String environment = System.getenv().get("ENVIRONMENT");
			
			Advertisement advertisement = getAdvertisementByFlightId(flightId);
			if (null != advertisement && PageTemplate.WebpageTemplateType.EXPLORE_SELECTION.getValue().equals(advertisement.getWebpageTemplateId())) {
				if (environment.equals("production")) {
					landingPage = "https://m-discovery.gomeplus.com/topic/" + materialId + ".html";
				} else if (environment.equals("preproduction")) {
					landingPage = "http://discovery.pre.ds.gome.com.cn/topic/" + materialId + ".html";
				} else {
					landingPage = "http://discovery.dev.ds.gome.com.cn/topic/" + materialId + ".html";
				}
			} else {
				if (environment.equals("production")) {
					landingPage = "http://m-awall.gomeplus.com/topic/detail?id=" + materialId;
				} else if (environment.equals("preproduction")) {
					landingPage = "http://m-awall.pre.gomeplus.com/topic/detail?id=" + materialId;
				} else {
					landingPage = "http://m-awall.dev.gomeplus.com/topic/detail?id=" + materialId;
				}
			}
		} else if (Material.LinkType.ITEM.getValue().equals(linkType)) {
			String environment = System.getenv().get("ENVIRONMENT");
			
			Integer productType = fixedCpcFlightManager.getProductTypeByFlightId(flightId);
			logger.info("save material item advertisement productType: " + productType);
			
			if (FlightAdvertisement.VariableAdGroup.MINISITE.getValue().equals(productType)){
				if (environment.equals("production")) {
					landingPage = "http://h5-awall.gomeplus.com/item/detail?id=" + materialId;
				} else if (environment.equals("preproduction")) {
					landingPage = "http://h5-awall.pre.gomeplus.com/item/detail?id=" + materialId;
				} else {
					landingPage = "http://h5-awall.dev.gomeplus.com/item/detail?id=" + materialId;
				}
			} else if (FlightAdvertisement.VariableAdGroup.EXPLORE.getValue().equals(productType)) {
				if (environment.equals("production")) {
					landingPage = "https://m-discovery.gomeplus.com/product/" + materialId + ".html";
				} else if (environment.equals("preproduction")) {
					landingPage = "http://discovery.pre.ds.gome.com.cn/product/" + materialId + ".html";
				} else {
					landingPage = "http://discovery.dev.ds.gome.com.cn/product/" + materialId + ".html";
				}
			}
		} 
		// @todo 无法获取图片尺寸
		String size = "200";

		// 自建活动页需要landingPage追加flightId参数
		if ((linkType == Material.LinkType.URL.getValue() || linkType == Material.LinkType.SHOP.getValue() || linkType == Material.LinkType.VIDEO.getValue()) && !StringUtils.isEmpty(landingPage)) {
			if (landingPage.contains("?")) {
				landingPage = landingPage + "&flightId=" + flightId;
			} else {
				landingPage = landingPage + "?flightId=" + flightId;
			}
		}

		material.setLandingPage(landingPage);
		
//		Flight flight = flightDao.get(flightId);
//		Integer groupId = flight.getDspFlightId();
		
		//投放系统中的实体状态 0-启用  1-暂停
//		AdsAd adsAd = new AdsAd(groupId, name, emojiConvert(title), images, description, width, height,
//						size, linkType, material.getPromotionId(), landingPage, 0, userName);
//		if (!CollectionUtils.isEmpty(images)) {
//			adsAd.setImage(images);
//		} else {
//			adsAd.setImage(new ArrayList<String>());
//		}
//		
//		if (Material.LinkType.URL.getValue().equals(linkType)) {
//			List<Map<String, Object>> products = new ArrayList<Map<String, Object>>();
//			if (material.getWebpageId() != null) {
//				List<PageItem> pageItems = pageItemDao.getPageItemsByPageId(material.getWebpageId(), 3);
//				if (!CollectionUtils.isEmpty(pageItems)) {
//					for (PageItem pageItem : pageItems) {
//						Map<String, Object> product = new LinkedHashMap<String, Object>();
//						product.put("name", pageItem.getName());
//						product.put("productId", pageItem.getProductId());
//						product.put("skuId", pageItem.getSkuId());
//						product.put("image", pageItem.getImage());
//						product.put("description", pageItem.getDescription());
//						products.add(product);
//					}
//				}
//			}
//			adsAd.setProducts(products);
//		}
//
//		AdsAdOperations adsAdOperations = new AdsAdOperations();
//		ApiResponse response = adsAdOperations.create(adsAd);
//		Map<String, Object> data = response.getData();
//		Integer dspMaterialId = Integer.parseInt((String) (data.get("id")));
//		material.setDspMaterialId(dspMaterialId);

		logger.info("material result: " + material);
		materialDao.update(material);

		logger.info("============material create send to ads success");
		saveResult.put("materialId", materialId);
		return saveResult;
	}

	/**
	 * 修改创意
	 * 
	 * materialMap 数据格式
	 * 
	 * materialId	创意id	Number	必须
	 * flightId		投放单元id	Number	必须
	 * itemId		商品id	Number	必须
	 * name			创意名称	String	必须
	 * type			素材类型 1图片 2图文 3商品	Number	必须
	 * linkType		链接类型 1商品 2url	Number	必须
	 * title		广告标题	String	必须
	 * image		创意图片	String	必须
	 * wdith		图片宽度	Number	非必须
	 * height		图片高度	Number	非必须
	 * description	广告文案	String	必须
	 * landingPage	落地页	String	非必须
	 * relatedItems	联合推广商品	Array	必须
	 * 
	 * @param materialMap
	 */	
	@Transactional(readOnly = false)
	public void update(MaterialForm materialForm) throws Exception {

		logger.info("============material update");
		logger.info("materialForm: " + materialForm);
		Integer materialId = materialForm.getMaterialId();
		String productId = materialForm.getProductId();
		String promotionId = materialForm.getPromotionId();
		String name = materialForm.getName();
		Integer type = materialForm.getType();
		if(null == type){
			type = Material.Type.IMAGE.getValue();			
		}
		Integer linkType = materialForm.getLinkType();
		String title = materialForm.getTitle();
		List<String> images = materialForm.getImage();
		Integer width = materialForm.getWidth();
		Integer height = materialForm.getHeight();
		String description = materialForm.getDescription();
		String landingPage = materialForm.getLandingPage();
		Integer webpageId = materialForm.getWebpageId();
		if (null == webpageId) {
			webpageId = 0;
		}
		if (!StringUtils.isEmpty(landingPage) && (Material.LinkType.URL.getValue().equals(linkType) || Material.LinkType.SHOP.getValue().equals(linkType) || Material.LinkType.VIDEO.getValue().equals(linkType))) {
			landingPage = new DESUtil().decode(landingPage);
		}
		List<Map<String,Object>> relatedItems = materialForm.getRelatedItems();
		String relatedItemsJson = JSON.toJSONString(relatedItems);
		Integer relatedItemStrategy = materialForm.getRelatedItemStrategy();

		logger.info("materialId: " + materialId);
		logger.info("productId: " + productId);
		logger.info("promotionId: " + promotionId);
		logger.info("name: " + name);
		logger.info("type: " + type);
		logger.info("linkType: " + linkType);
		logger.info("title: " + title);
		logger.info("images: " + images);
		logger.info("width: " + width);
		logger.info("height: " + height);
		logger.info("description: " + description);
		logger.info("relatedItemStrategy: " + relatedItemStrategy);
		logger.info("relatedItems: " + relatedItems);
		logger.info("relatedItemsJson: " + relatedItemsJson);

		logger.info("============material update params success");

		Date currentTime = new Date();
		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();

		logger.info("userId: " + userId);
		logger.info("userName: " + userName);

		Material oldMaterial = materialDao.get(materialId);
		Integer flightId = oldMaterial.getFlightId();
		Flight flight = flightDao.get(flightId);
		Integer groupId = flight.getDspFlightId();
		// 是否返利都传中间页
		if (Material.LinkType.TOPIC.getValue() == linkType) {
			String environment = System.getenv().get("ENVIRONMENT");
			Advertisement advertisement = getAdvertisementByFlightId(flightId);
			if (null != advertisement && PageTemplate.WebpageTemplateType.EXPLORE_SELECTION.getValue().equals(advertisement.getWebpageTemplateId())) {
				if (environment.equals("production")) {
					landingPage = "https://m-discovery.gomeplus.com/topic/" + materialId + ".html";
				} else if (environment.equals("preproduction")) {
					landingPage = "http://discovery.pre.ds.gome.com.cn/topic/" + materialId + ".html";
				} else {
					landingPage = "http://discovery.dev.ds.gome.com.cn/topic/" + materialId + ".html";
				}
			} else {
				if (environment.equals("production")) {
					landingPage = "http://m-awall.gomeplus.com/topic/detail?id=" + materialId;
				} else if (environment.equals("preproduction")) {
					landingPage = "http://m-awall.pre.gomeplus.com/topic/detail?id=" + materialId;
				} else {
					landingPage = "http://m-awall.dev.gomeplus.com/topic/detail?id=" + materialId;
				}
			}
		} else if (linkType == Material.LinkType.ITEM.getValue()) {
			String environment = System.getenv().get("ENVIRONMENT");
			
			Integer productType = fixedCpcFlightManager.getProductTypeByFlightId(flightId);
			logger.info("save material item advertisement productType: " + productType);
			
			if (FlightAdvertisement.VariableAdGroup.MINISITE.getValue().equals(productType)){
				if (environment.equals("production")) {
					landingPage = "http://h5-awall.gomeplus.com/item/detail?id=" + materialId;
				} else if (environment.equals("preproduction")) {
					landingPage = "http://h5-awall.pre.gomeplus.com/item/detail?id=" + materialId;
				} else {
					landingPage = "http://h5-awall.dev.gomeplus.com/item/detail?id=" + materialId;
				}
			} else if (FlightAdvertisement.VariableAdGroup.EXPLORE.getValue().equals(productType)) {
				if (environment.equals("production")) {
					landingPage = "https://m-discovery.gomeplus.com/product/" + materialId + ".html";
				} else if (environment.equals("preproduction")) {
					landingPage = "http://discovery.pre.ds.gome.com.cn/product/" + materialId + ".html";
				} else {
					landingPage = "http://discovery.dev.ds.gome.com.cn/product/" + materialId + ".html";
				}
			}
		}

		oldMaterial.setName(name);
		if (!StringUtils.isEmpty(promotionId)) {
			oldMaterial.setPromotionId(promotionId);
		}
		oldMaterial.setType(type);
		oldMaterial.setLinkType(linkType);
		oldMaterial.setTitle(emojiConvert(title));
		oldMaterial.setImage(StringUtil.join(images.toArray(), ","));
		oldMaterial.setWidth(width);
		oldMaterial.setHeight(height);
		oldMaterial.setDescription(emojiConvert(description));
		oldMaterial.setLandingPage(landingPage);
		oldMaterial.setWebpageId(webpageId);
		oldMaterial.setRelatedItemStrategy(relatedItemStrategy);
		oldMaterial.setUpdateTime(currentTime);
		if (Material.LinkType.ITEM.getValue() == linkType) {
			oldMaterial.setRelatedItems(getRelatedItems(relatedItemsJson));
			oldMaterial.setPromotionId(productId + ":" + promotionId);
		}
		oldMaterial.setApproveStatus(Material.ApproveStatus.APPROVING.getValue());
		materialDao.update(oldMaterial);

		// 修改创意，告诉引擎下线，amp创意本身的审核状态置为待审核
		Integer dspMaterialId = oldMaterial.getDspMaterialId();
		// 投放系统中的实体状态 0-启用 1-暂停
		logger.info("update material dspMaterialId: " + dspMaterialId);
		if (dspMaterialId > 0) {
			adsAdOperations.updateStatus(dspMaterialId, 1);
		}
		
		// @todo 无法获取图片尺寸
//		String size = "200";
//
//		if (linkType == Material.LinkType.URL.getValue() && !StringUtils.isEmpty(landingPage)) {
//			if (landingPage.contains("?")) {
//				landingPage = landingPage + "&flightId=" + flightId;
//			} else {
//				landingPage = landingPage + "?flightId=" + flightId;
//			}
//		}
//		AdsAd adsAd = new AdsAd(dspMaterialId, groupId, name, emojiConvert(title), images, description, width, height, size,
//								linkType, oldMaterial.getPromotionId(), landingPage, 0, userName);
//
//		if (Material.LinkType.URL.getValue().equals(linkType)) {
//			List<Map<String, Object>> products = new ArrayList<Map<String, Object>>();
//			if (oldMaterial.getWebpageId() != null) {
//				List<PageItem> pageItems = pageItemDao.getPageItemsByPageId(oldMaterial.getWebpageId(), 3);
//				if (!CollectionUtils.isEmpty(pageItems)) {
//					for (PageItem pageItem : pageItems) {
//						Map<String, Object> product = new LinkedHashMap<String, Object>();
//						product.put("name", pageItem.getName());
//						product.put("productId", pageItem.getProductId());
//						product.put("skuId", pageItem.getSkuId());
//						product.put("image", pageItem.getImage());
//						product.put("description", pageItem.getDescription());
//						products.add(product);
//					}
//				}
//			}
//			adsAd.setProducts(products);
//		}
//
//		// 投放系统中的实体状态 0-启用 1-暂停
//		if (Flight.Status.NORMAL.getValue() == oldMaterial.getStatus()) {
//			adsAd.setStatus(0);
//		} else if (Flight.Status.SUSPEND.getValue() == oldMaterial.getStatus()) {
//			adsAd.setStatus(1);
//		}
//		AdsAdOperations adsAdOperations = new AdsAdOperations();
//		ApiResponse response = adsAdOperations.update(adsAd);

		logger.info("============material update send to ads success");
	}

	/**
	 * 上传图片至CDN，成功则返回url
	 * 
	 * @param image
	 * @param size
	 * @return imageUrl
	 */
	public String uploadToCDN(File image, String size) throws Exception {
		String imageUrl = FileUtil.sendFileToCDN(size, image);
		// 将生成的临时文件删除
		image.delete();
		return imageUrl;
	}

	/**
	 * 查询素材分页列表
	 */
	public List<Material> getMaterialsByFlightId(Pagination pagination, Integer flightId, String keyword, Integer state) throws Exception{
		return materialDao.getMaterialsByPagination(pagination,flightId,keyword,state);
	}
	
	/**
	 * 根据店铺Id、类目Id查询商品列表
	 * bs接口
	 * 
	 * @param shopId
	 * @param categoryId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getItemsByShopIdCategoryId(Integer shopId, Integer categoryId) throws Exception {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		ApiResponse response = bsItemOperation.searchItems(categoryId, null, null, null, 1, 9, null, shopId, null);
		Map<String, Object> data = response.getData();
		List<Map<String, Object>> itemList = (List<Map<String, Object>>) data.get("items");
		
		StringBuilder url = new StringBuilder();
		url.append("https://m.gomeplus.com/item/")
		   .append(shopId)
		   .append("-");
		
		if (!CollectionUtils.isEmpty(itemList)) {
			for (Map<String, Object> item : itemList) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put("itemId", item.get("id"));
				itemMap.put("name", item.get("name"));
				itemMap.put("image", item.get("mainImage"));
				itemMap.put("shopId", shopId);
				itemMap.put("url", url.toString()+((Double) item.get("id")).intValue()+".html");
				items.add(itemMap);
			}
		}
		return items;
	}
	
	/**
	 * 根据店铺Id、类目Id查询商品列表
	 * @param shopId
	 * @param categoryId
	 * @param skuId
	 * @param relatedItemStrategy (0-不拉取 1-店铺、类目  2-店铺   3-先按店铺类目搜索，若不足size，再仅按店铺搜索 )
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getItemsByShopIdCategoryId(String shopId, String categoryId, String skuId, Integer relatedItemStrategy) throws Exception {
		return getItemsByShopIdCategoryId(shopId, categoryId, skuId, 9, relatedItemStrategy);
	}
	
	/**
	 * 根据店铺Id、类目Id查询商品列表
	 * @param shopId
	 * @param categoryId
	 * @param skuId
	 * @param size
	 * @param relatedItemStrategy (0-不拉取 1-店铺、类目  2-店铺   3-先按店铺类目搜索，若不足size，再仅按店铺搜索 )
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getItemsByShopIdCategoryId(String shopId, String categoryId, String skuId, Integer size, Integer relatedItemStrategy) throws Exception {
		logger.info("getItemsByShopIdCategoryId param: ");
		logger.info("shopId: " + shopId);
		logger.info("categoryId: " + categoryId);
		logger.info("skuId: " + skuId);
		logger.info("size: " + size);
		logger.info("relatedItemStrategy: " + relatedItemStrategy);
		
		List<Map<String, Object>> products = new ArrayList<Map<String, Object>>();
		Set<String> repeatedImages = new HashSet<String>();
		// 调远程搜索接口拉取商品列表第1页 （条件： 店铺、类目）
		products = getDifferentSku(shopId, categoryId, skuId, size, 1, repeatedImages, products);

		// 调远程搜索接口拉取商品列表第2页 （条件： 店铺、类目）
		if (products.size() < size) {
			products = getDifferentSku(shopId, categoryId, skuId, size, 2, repeatedImages, products);
		}
		
		/*
		 *  调远程搜索接口拉取商品列表第1页（过滤条件只有店铺id） 
		 *  relatedItemStrategy表示"全部"时， 再仅根据店铺id搜索一次；
		 */
		if (3 == relatedItemStrategy && products.size() < size) {
			products = getDifferentSku(shopId, null, skuId, size, 1, repeatedImages, products);
		}
		
		logger.info("getItemsByShopIdCategoryId productList: " + products);
		return products;
	}
	
	/**
	 * 获取不重复商品
	 * @param shopId
	 * @param categoryId
	 * @param skuId
	 * @param size
	 * @param pageNumber 调远程搜索接口入参：页码
	 * @param repeatedImages
	 * @param products
	 * @return
	 */
	private List<Map<String, Object>> getDifferentSku(String shopId, String categoryId, String skuId, Integer size, Integer pageNumber, Set<String> repeatedImages, List<Map<String, Object>> products){
		logger.info("getDifferentSku skuId: "  + skuId + " shopId: " + shopId + " categoryId: " + categoryId);
		logger.info("getDifferentSku pageNumber: " + pageNumber);
		logger.info("getDifferentSku repeatedImages: " + repeatedImages);
		logger.info("getDifferentSku products: " + products);
		List<Map<String, Object>> goodsList = mallProductOperations.getProductsByShopIdCategoryId(shopId, categoryId, pageNumber);
		if (!CollectionUtils.isEmpty(goodsList)) {
			for (Map<String, Object> good : goodsList) {
				List<Map<String,Object>> images = (List<Map<String,Object>>)good.get("images");
				String image = "";
				if (!CollectionUtils.isEmpty(images)) {
					Map<String, Object> imageMap = images.get(0);
					image = (String) imageMap.get("sImg");
				}
				String sku = (String) good.get("skuId");
				if (repeatedImages.contains(image) || sku.equals(skuId)) {
					continue;
				}
				Map<String, Object> product = new LinkedHashMap<String, Object>();
				product.put("itemId", sku);
				product.put("productId", good.get("pId"));
				product.put("name", good.get("name"));
				product.put("image", image);
				product.put("shopId", good.get("shopId"));
				logger.info("add the sku:" + sku +" into products");
				products.add(product);
				repeatedImages.add(image);

				if (null != size && products.size() >= size) {
					break;
				}
			}
		}
		
		return products;
	}
	
	/**
	 * 根据商品Id获取商品信息
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getItemByItemId(Long itemId) throws Exception{
		Map<String,Object> item = new HashMap<String, Object>();
		ApiResponse itemResponse = bsItemOperation.getItemByItmeId(itemId);
		Map<String, Object> data = itemResponse.getData();
		item.put("itemId", data.get("id"));
		item.put("shopId", data.get("shopId"));
		item.put("name", data.get("name"));
		item.put("description", "");
		item.put("images", data.get("images"));
		StringBuilder url = new StringBuilder();
		url.append("https://m.gomeplus.com/item/")
		   .append(((Double) data.get("shopId")).intValue())
		   .append("-")
		   .append(((Double) data.get("id")).intValue())
		   .append(".html");
		item.put("url", url.toString());
		try{
			ApiResponse categoryRespose = bsItemOperation.getCategoriesByItmeId(itemId);
			List<String> categoryIds = Arrays.asList(((String)categoryRespose.getData().get("categoryId")).split(","));
			item.put("categoryId", Integer.parseInt(categoryIds.get(categoryIds.size()-1)));			
		}catch(Exception exception){
			logger.error("get categoryId throw Exception ", exception);
		}
		if(null == item.get("categoryId")){
			item.put("categoryId", 0);
		}
		
		return item;
	}
	
	/**
	 * 批量修改创意状态
	 * @param materialsStatusMap
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void batchUpdateStatus(Map<String, Object> materialsStatusMap) throws Exception {

		List<Double> materialDoubleIds = (List<Double>) materialsStatusMap.get("materialIds");
		Integer status = ((Double) materialsStatusMap.get("status")).intValue();

		List<Integer> materialIds = new ArrayList<Integer>();
		for (Double materialDoubleId : materialDoubleIds) {
			Integer materialId = materialDoubleId.intValue();
			materialIds.add(materialId);
		}
		// 状态参数
		Integer normalStatus = Material.Status.NORMAL.getValue();//上线
		Integer suspendStatus = Material.Status.SUSPEND.getValue();//下线
		Integer approveStatus = Material.ApproveStatus.APPROVED.getValue();//审核通过
		List<String> errors = new ArrayList<String>();
		Date currentTime = new Date();

		List<Material> materials = materialDao.get(materialIds);

		// 上线：将选中的处于下线状态的设为上线状态
		if (status.equals(normalStatus)) {
			for (Material material : materials) {
				Integer approve = material.getApproveStatus();
				if (approve.equals(approveStatus)) {
					if (material.getStatus().equals(Material.Status.NORMAL.getValue())) {
						errors.add(material.getName() + "状态异常");
						continue;
					}
					material.setStatus(Material.Status.NORMAL.getValue());
					material.setUpdateTime(currentTime);
					materialDao.update(material);
					//投放系统中的实体状态 0-启用  1-暂停
					if (material.getDspMaterialId() > 0) {
						adsAdOperations.updateStatus(material.getDspMaterialId(), 0);
					}
				} else {
					errors.add(material.getName() + "审核状态异常");
				}
			}
		// 下线：将选中的上线设置为下线状态；
		} else if (status.equals(suspendStatus)) {
			for (Material material : materials) {
				Integer approve = material.getApproveStatus();
				if (approve.equals(approveStatus)) {
					if (material.getStatus().equals(Material.Status.SUSPEND.getValue())) {
						errors.add(material.getName() + "状态异常");
						continue;
					}
					material.setStatus(Material.Status.SUSPEND.getValue());
					material.setUpdateTime(currentTime);
					materialDao.update(material);
					//投放系统中的实体状态 0-启用  1-暂停
					if (material.getDspMaterialId() > 0) {
						adsAdOperations.updateStatus(material.getDspMaterialId(), 1);
					}
				} else {
					errors.add(material.getName() + "审核状态异常");
				}
			}
		}

		if (errors.size() > 0) {
			String errorsString = String.join(",", errors);
			throw new Exception(errorsString);
		}
	}
	
	/**
	 * 批量删除创意
	 * @param materialsStatusMap
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void batchDeleteMaterials(Map<String, Object> materialsMap) throws Exception {
		
		List<Double> materialDoubleIds = (List<Double>) materialsMap.get("materialIds");
		List<Integer> materialIds = new ArrayList<Integer>();
		for (Double materialDoubleId : materialDoubleIds) {
			Integer materialId = materialDoubleId.intValue();
			materialIds.add(materialId);
		}
		logger.info("batchDeleteMaterials materialIds: " + materialIds);
		// 状态参数
		Integer approveStatus = Material.ApproveStatus.APPROVED.getValue();//审核通过
		Integer normalStatus = Material.Status.NORMAL.getValue();//上线
		Date currentTime = new Date();

		List<Material> materials = materialDao.get(materialIds);

		// 删除审核未通过和未上线的创意
		for (Material material : materials) {
			if (!((material.getApproveStatus().equals(Material.ApproveStatus.APPROVING.getValue()) || material.getApproveStatus().equals(Material.ApproveStatus.APPROVED.getValue())) && material.getStatus().equals(normalStatus))) {
				material.setStatus(Material.Status.DELETE.getValue());
				material.setUpdateTime(currentTime);
				materialDao.update(material);
				if (material.getDspMaterialId() > 0) {
					adsAdOperations.delete(material.getDspMaterialId());
				}
			}
		}
	}
	
	/**
	 * 根据materialId获取创意
	 * @param materialId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMaterial(Integer materialId) throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		Material material = materialDao.get(materialId);
		logger.info("getMaterial material: " + material);
		if (null == material) {
			return data;
		}
		data.put("materialId", material.getMaterialId());
		data.put("name", material.getName());
		data.put("type", material.getType());
		data.put("linkType", material.getLinkType());
		data.put("title", URLDecoder.decode(material.getTitle(), "UTF-8"));
		data.put("description", URLDecoder.decode(material.getDescription(), "UTF-8"));
		data.put("webpageId", material.getWebpageId());
		data.put("relatedItemStrategy", material.getRelatedItemStrategy());
		
		String promotionId = material.getPromotionId();
		if (!StringUtils.isEmpty(material.getPromotionId())) {
			if (Material.LinkType.ITEM.getValue().equals(material.getLinkType()) && promotionId.contains(":")) {
				String[] tmp = promotionId.split(":");
				if (tmp != null && tmp.length > 0) {
					data.put("productId", tmp[0]);
					data.put("promotionId", tmp[1]);
				}
			} else {
				data.put("promotionId", promotionId);
			}
		}
		
		// 设置话题预览地址
		String previewUrl = "";
		if (Material.LinkType.TOPIC.getValue() == material.getLinkType()) {
			String environment = System.getenv().get("ENVIRONMENT");
			if (environment.equals("production")) {
				previewUrl = "http://circle.m.gomeplus.com/topic-" + material.getPromotionId() + ".html";
			} else {
				previewUrl = "http://circle.m.uatplus.com/topic-" + material.getPromotionId() + ".html";
			}
			data.put("previewUrl", previewUrl);
		}
		
		// 商品创意联合推广商品 与 其他创意跟单商品
		if (Material.LinkType.ITEM.getValue() == material.getLinkType()){
			data.put("relatedItems", JSON.parseObject(material.getRelatedItems(), new ArrayList<Object>().getClass()));
		} else {
			List<MaterialItem> materialItems = materialItemDao.getMaterialItemsByMaterialId(materialId);
			data.put("relatedItems", materialItems);
		}
		
		if (!StringUtils.isEmpty(material.getImage())) {
			data.put("image", material.getImage().split(","));
		} else {
			data.put("image", new ArrayList<String>());			
		}
		
		if (Material.LinkType.URL.getValue().equals(material.getLinkType())
				|| Material.LinkType.SHOP.getValue().equals(material.getLinkType())
				|| Material.LinkType.VIDEO.getValue().equals(material.getLinkType())) {
			data.put("landingPage", new DESUtil().encode(material.getLandingPage()));
		} else {
			data.put("landingPage", material.getLandingPage());
		}
		
		logger.info("getMaterial data: " + data);
		return data;
	}
	
	/**
	 * 查询某投放单元下有效创意个数
	 * @param flightId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getMaterialTotalByFlightId(Integer flightId) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		Long total = materialDao.getMaterialTotalByFlightId(flightId);
		data.put("materialTotal", total);
		return data;
	}
	
	/**
	 * 判断某skuId的自营三级类目是否禁投
	 * @param skuId
	 * @return
	 */
	public Map<String, Object> getCategoryBannedStatus(@RequestParam String skuId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isBadden", -1);

		ApiResponse itemResponse = mallProductOperations.getItemsBySkuIds(skuId);
		Map<String, Object> itemData = itemResponse.getData();
		List<Map<String, Object>> skus = (List<Map<String, Object>>) itemData.get("list");
		String categoryId = null;
		if (!CollectionUtils.isEmpty(skus)) {
			Map<String, Object> skuTmp = skus.get(0);
			categoryId = (String) skuTmp.get("thridcatalogy");
		}
		if (StringUtils.isEmpty(categoryId)) {
			logger.info("the sku thridcatalogy is empty");
			return data;
		}
		List<BannedCategory> bannedCategories = bannedCategoryDao.getAllBannedCategories();
		for (BannedCategory bannedCategory : bannedCategories) {
			if (categoryId.equals(bannedCategory.getMallCategoryId())) {
				return data;
			}
		}
		data.put("isBadden", 0);
		return data;
	}
	
	/**
	 * 根据productId获取对应的skuIds
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String getSkuIdsByProductId(String productId) throws Exception {
		ApiResponse skuIdsResponse = mallProductOperations.getSkuIdsByProductId(productId);
		Map<String, Object> data = skuIdsResponse.getData();
		List<String> skuIds = (List<String>) data.get("skuItemIds");
		String skuId = null;
		if(!CollectionUtils.isEmpty(skuIds)){
		    skuId = String.join(",", skuIds);
		}
		return skuId;
	}

	/**
	 * 获取商品信息(组装)
	 * 
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getProducts(String productId) throws Exception {
		// 获取skuIds并转换为字符串
		String skuIds = getSkuIdsByProductId(productId);
		// 前端需要的商品信息
		Map<String, Object> products = new HashMap<String, Object>();
		// 重组商品集合
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		// 调用接口获取商品信息
		ApiResponse itemResponse = mallProductOperations.getItemsBySkuIds(skuIds);
		Map<String, Object> data = itemResponse.getData();
		List<Map<String, Object>> itemsGather = (List<Map<String, Object>>) data.get("list");
		if (!CollectionUtils.isEmpty(itemsGather)) {
			for (Map<String, Object> item : itemsGather) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				List<Object> images = new ArrayList<Object>();
				images.add(item.get("mainImage"));
				itemMap.put("shopId", item.get("shopId"));
				itemMap.put("skuId", item.get("skuId"));
				itemMap.put("name", item.get("commodityTitle"));
				itemMap.put("description", item.get("commodityDescription"));
				itemMap.put("images", images);
				itemMap.put("categoryId", item.get("shopCategoryId"));
				items.add(itemMap);
			}
			products.put("productId", productId);
			products.put("sku", items);
		}
		return products;
	}
	
	/**
	 * 根据skuId查sku信息
	 * 
	 * @param skuId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSkuBySkuId(String skuId) throws Exception {
		logger.info("getSkuBySkuId skuId: " + skuId);
		Map<String, Object> sku = new LinkedHashMap<String, Object>();
		if (StringUtils.isEmpty(skuId)) {
			sku.put("skuId", "");
			return sku;
		}
		ApiResponse itemResponse = mallProductOperations.getItemsBySkuIds(skuId);
		Map<String, Object> data = itemResponse.getData();
		List<Map<String, Object>> skus = (List<Map<String, Object>>) data.get("list");
		if (!CollectionUtils.isEmpty(skus)) {
			Map<String, Object> skuTmp = skus.get(0);

			sku.put("shopId", skuTmp.get("shopId"));
			sku.put("skuId", skuTmp.get("skuId"));
			sku.put("productId", skuTmp.get("productId"));
			sku.put("name", skuTmp.get("commodityTitle"));
			sku.put("description", skuTmp.get("commodityDescription"));
			// 单图
			// List<Object> images = new ArrayList<Object>();
			// images.add(skuTmp.get("mainImage"));
			// sku.put("images", images);

			// 多图
			sku.put("images", skuTmp.get("image"));
			sku.put("categoryId", skuTmp.get("shopCategoryId"));
			sku.put("skuNo", skuTmp.get("skuNo"));
		}

		logger.info("getSkuBySkuId sku: " + sku);
		return sku;
	}
	
	/**
	 * 根据skuIds查sku列表
	 * 
	 * @param skuId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getSkusBySkuIds(List<String> skuIds) throws Exception {
		logger.info("getSkusBySkuIds skuIds:" + skuIds);
		List<Map<String, Object>> skus = new ArrayList<Map<String, Object>>();
		if (CollectionUtils.isEmpty(skuIds)) {
			return skus;
		}
		ApiResponse itemResponse = mallProductOperations.getItemsBySkuIds(String.join(",", skuIds));
		Map<String, Object> data = itemResponse.getData();
		List<Map<String, Object>> skuList = (List<Map<String, Object>>) data.get("list");
		if (!CollectionUtils.isEmpty(skuList)) {
			for (Map<String, Object> skuTmp : skuList) {
				Map<String, Object> sku = new LinkedHashMap<String, Object>();
				sku.put("shopId", skuTmp.get("shopId"));
				sku.put("skuId", skuTmp.get("skuId"));
				sku.put("productId", skuTmp.get("productId"));
				sku.put("name", skuTmp.get("commodityTitle"));
				sku.put("description", skuTmp.get("commodityDescription"));
				sku.put("images", skuTmp.get("image"));
				sku.put("categoryId", skuTmp.get("shopCategoryId"));
				sku.put("skuNo", skuTmp.get("skuNo"));

				skus.add(sku);
			}
		}
		logger.info("getSkusBySkuIds skus:" + skus);
		return skus;
	}
	
	
	
	/**
	 * 保存竞价cpc的创意信息
	 * @param materials
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> saveMaterials(List<Map<String, Object>> materials) throws Exception {
		Map<String, Object> saveResult = new HashMap<String, Object>();
		Integer status = Material.Status.NORMAL.getValue();
		Integer approveStatus = Material.ApproveStatus.APPROVED.getValue();
		Date currentTime = new Date();
		String url ="https://item.gome.com.cn/";
		if(!CollectionUtils.isEmpty(materials)){
			Integer userId = PrincipalUtil.getUserId();
			String userName = PrincipalUtil.getName();
			Integer groupId = 0;
			for (Map<String, Object> map : materials) {
				Integer flightId = Integer.parseInt(map.get("flightId").toString());
				String image = (String) map.get("image");
				String promotionId = map.get("productId") + ":" + map.get("skuId");
				String title = (String) map.get("title");
				String landingPage = url + map.get("productId") + "-" + map.get("skuId") + ".html";
				String skuNo = (String)map.get("skuNo");
				List<Material> materialList = materialDao.getMaterialBySkuId(flightId);
				if(!CollectionUtils.isEmpty(materialList)){
					for(Material material: materialList){
						if(!material.getPromotionId().contains(":")){
							continue;
						}
						String skuId [] = material.getPromotionId().split(":");
						if(skuId[1].equals(map.get("skuId").toString())){
							throw new Exception("创意已存在");
						}
					}
				}
				Material material = new Material();
				material.setFlightId(flightId);
				material.setImage(image);
				material.setPromotionId(promotionId);
				material.setTitle(title);
				material.setLandingPage(landingPage);
				material.setStatus(status);
				material.setApproveStatus(approveStatus);
				material.setCreateTime(currentTime);
				material.setUpdateTime(currentTime);
				material.setUserId(userId);
				
				// 配合二次审核需求，创意来源、审核角色 记录固定值
				material.setApproveRole("advertisement_approve");
				material.setCreateFrom("amp");
				materialDao.save(material);
						
				if(0 == groupId){
					Flight flight = flightDao.get(Integer.parseInt(map.get("flightId").toString()));
					groupId = flight.getDspFlightId();
				}
				
				AdsAd adsAd = new AdsAd();
				adsAd.setGroupId(groupId);
				adsAd.setTitle(title);
				List<String> imageList = new ArrayList<String>();
				imageList.add(image);
				adsAd.setImage(imageList);
				adsAd.setPromotionId(promotionId);
				adsAd.setLandpageUrl(landingPage);
				adsAd.setLinkType(Material.LinkType.ITEM.getValue());
				adsAd.setSkuNo(skuNo);
				//投放系统中的实体状态 0-启用  1-暂停
				adsAd.setStatus(0);
				adsAd.setCreateUser(userName);

				AdsAdOperations adsAdOperations = new AdsAdOperations();
				ApiResponse response = adsAdOperations.create(adsAd);
				Map<String, Object> data = response.getData();
				Integer dspMaterialId = Integer.parseInt((String) (data.get("id")));
				material.setDspMaterialId(dspMaterialId);
				materialDao.update(material);
			}
		}
		return saveResult;
	}

	/**
	 * 根据关键字获取话题名称
	 * @param keyword
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getTopics(String keyword, Integer pageNum, Integer pageSize) throws Exception {
	
		ApiResponse response = bsSearchOperation.getTopicInfo(keyword, pageNum, pageSize, null);
		Map<String, Object> data = response.getData();
		return data;
	}
	
	/**
	 * 根据topicId获取话题详情
	 * @param topicId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getTopicDetails(String topicId) throws Exception {
		ApiResponse response = bsSearchOperation.getTopicDetail(topicId, false, null);
		Map<String, Object> data = response.getData();
		if (null == data || data.isEmpty()) {
			return new HashMap<String, Object>();
		}
		// 设置预览地址
		String environment = System.getenv().get("ENVIRONMENT");
		String previewUrl = "";
		if (environment.equals("production")) {
			previewUrl = "http://circle.m.gomeplus.com/topic-" + topicId + ".html";
		} else {
			previewUrl = "http://circle.m.uatplus.com/topic-" + topicId + ".html";
		}
		data.put("previewUrl", previewUrl);
		return data;
	}
	
	/**
	 * 查询话题中关联的跟单商品信息(非用户自定义)
	 * @param topicId
	 * @return
	 */
	private List<MaterialItem> getTopicMaterialItems(String topicId) {
		logger.info("getTopicMaterialItems topicId: " + topicId);
		List<MaterialItem> materialItems = new ArrayList<MaterialItem>();
		ApiResponse response = bsSearchOperation.getTopicDetail(topicId, false, null);
		logger.info("getTopicMaterialItems response: " + response);
		Map<String, Object> data = response.getData();
		logger.info("getTopicMaterialItems response data: " + data);
		if (null == data || data.isEmpty() || null == data.get("components")) {
			return materialItems;
		}
		List<Map<String, Object>> components = (List<Map<String, Object>>) data.get("components");
		logger.info("getTopicMaterialItems components: " + components);
		if (CollectionUtils.isEmpty(components)) {
			return materialItems;
		}

		for (Map<String, Object> component : components) {
			if (null != component.get("outProductId")) {
				MaterialItem materialItem = new MaterialItem();
				materialItem.setProductId(component.get("outProductId").toString());
				materialItem.setIsUserDefine(UserDefine.DEFAULT.getValue());
				materialItem.setStatus(MaterialItem.Status.NORMAL.getValue());
				materialItem.setType(MaterialItem.Type.ITEM.getValue());
				materialItem.setCreateTime(new Date());
				materialItem.setUpdateTime(new Date());
				materialItems.add(materialItem);

				Map<String, Object> item = (Map<String, Object>) component.get("item");
				if (null == item) {
					continue;
				}
				if (null != item.get("mainImage")) {
					materialItem.setImage(item.get("mainImage").toString());
				}
				if (null != item.get("name")) {
					materialItem.setDescription(item.get("name").toString());
				}
				if (null != item.get("shopId")) {
					materialItem.setShopId(item.get("shopId").toString());
				}

			}
		}
		logger.info("getTopicMaterialItems: " + materialItems);
		return materialItems;
	}
		
	
	
	
	/**
	 * 转义emoji表情与ASCII标点符号
	 * @param string
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String emojiConvert(String string) throws UnsupportedEncodingException {
		String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff\\x{21}-\\x{2f}])";

		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(string);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			try {
				matcher.appendReplacement(stringBuffer,  URLEncoder.encode(matcher.group(1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw e;
			}
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}
	
	//根据flightId查询advertisement信息(投放单元中广告位单选)
	private Advertisement getAdvertisementByFlightId(Integer flightId) {
		List<FlightAdvertisement> flightAdvertisements = flightAdvertisementDao.getFlightAdvertisementsByFlightId(flightId);
		if(CollectionUtils.isEmpty(flightAdvertisements)){
			return null;
		}
		FlightAdvertisement flightAdvertisement = flightAdvertisements.get(0);
		Advertisement advertisement = advertisementDao.getAdvertisementByAdvertisementId(flightAdvertisement.getAdvertisementId());
		return advertisement; 
	}
	
	public String getRelatedItems(String relatedItemsJson) {
		if (StringUtils.isEmpty(relatedItemsJson) || "[]".equals(relatedItemsJson)) {
			return relatedItemsJson;
		}
		List<Map<String, Object>> relatedItems = JSON.parseObject(relatedItemsJson,	new ArrayList<Map<String, Object>>().getClass());
		List<Map<String, Object>> relatedItemsList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> relatedItem : relatedItems) {
			Map<String, Object> item = new LinkedHashMap<String, Object>();
			if (null != relatedItem.get("name")) {
				item.put("name", relatedItem.get("name"));
			}
			if (null != relatedItem.get("itemId")) {
				item.put("itemId", relatedItem.get("itemId"));
			} else if (null != relatedItem.get("skuId")) {
				item.put("itemId", relatedItem.get("skuId"));
			}
			if (null != relatedItem.get("productId")) {
				item.put("productId", relatedItem.get("productId"));
			}
			if (null != relatedItem.get("shopId")) {
				item.put("shopId", relatedItem.get("shopId"));
			}
			if (null != relatedItem.get("images")) {
				List<String> images = JSON.parseObject(relatedItem.get("images").toString(), new ArrayList<String>().getClass());
				item.put("image", images.get(0));
			} else if (null != relatedItem.get("image")) {
				item.put("image", relatedItem.get("image"));
			}
			relatedItemsList.add(item);
		}
		return JSON.toJSONString(relatedItemsList);
	}
	
	/**
	 * 推送禁播类目至ads
	 */
	public void pushBannedCategoriesToAds(){
		logger.info("pushBannedCategoriesToAds start...");
		List<BannedCategory> bannedCategories = bannedCategoryDao.getAllBannedCategories();
		if (CollectionUtils.isEmpty(bannedCategories)) {
			logger.info("pushBannedCategoriesToAds bannedCategories is empty");
			return ;
		}
		List<AdsBannedCategory> adsBannedCategories = new ArrayList<AdsBannedCategory>();
		for (BannedCategory bannedCategory : bannedCategories) {
			AdsBannedCategory adsBannedCategory = new AdsBannedCategory(bannedCategory.getMallCategoryId(),bannedCategory.getName());
			adsBannedCategories.add(adsBannedCategory);
		}
		logger.info("pushBannedCategoriesToAds adsBannedCategories: " + adsBannedCategories);
		adsBannedCategoryOperations.create(adsBannedCategories);
	}
	
	/**
	 * 判断某投放单元下是否存在skuId对应的商品创意
	 * @param userId
	 * @param name
	 * @param productLine
	 * @return
	 */
	public Boolean isExistSkuId(String skuId, Integer flightId) {
		Boolean isExist = false;
		List<Material> materials = materialDao.getMaterialsBySkuIdFlightId(skuId, flightId);
		if (!CollectionUtils.isEmpty(materials)) {
			isExist = true;
		}
		logger.info("isExistSkuId isExist: " + isExist);
		return isExist;
	}
}