package com.gomeplus.amp.ad.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.bs.BsMerchantInfoOperations;
import com.gomeplus.adm.common.api.mall.MallProductOperations;
import com.gomeplus.adm.common.api.mall.MallShopOperations;
import com.gomeplus.adm.common.util.DESUtil;
import com.gomeplus.adm.common.util.FieldUtil;
import com.gomeplus.amp.ad.dao.PageTemplateDao;
import com.gomeplus.amp.ad.dao.UserDao;
import com.gomeplus.amp.ad.dao.VideoDao;
import com.gomeplus.amp.ad.model.PageTemplate;
import com.gomeplus.amp.ad.model.User;
import com.gomeplus.amp.ad.model.Video;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.dao.PageDao;
import com.gomeplus.amp.ad.model.Page;
import com.gomeplus.amp.ad.dao.PageItemDao;
import com.gomeplus.amp.ad.model.PageItem;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import java.util.Properties;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;


/**
 * 页面service
 * 
 * @author lifei01
 */
@Service
@Transactional(readOnly = true)
public class PageService extends BaseService<Page, Integer>{
	
	@Autowired
	private PageDao pageDao;
	
	@Override
	public HibernateDao<Page, Integer> getEntityDao() {
		return pageDao;
	}

	@Autowired
	private PageTemplateDao pageTemplateDao;

	@Autowired
	private PageItemDao pageItemDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private VideoDao videoDao;

	@Autowired
	private ApplicationContext applicationContext;

	private Gson gson = new Gson();

	private static Logger logger = LoggerFactory.getLogger(PageService.class);

	// 页面中商品描述(名称)最大长度
	public static final int MAX_LENGTH_OF_DESCRIPTION = 200;

	// 页面中商品要求的数量(模板一)
	public static final int REQUIRED_COUNT_OF_ITEMS = 10;

	// 页面中商品要求的数量(模板二)
	public static final int SHOP_MIN_COUNT_OF_ITEMS = 4;
	public static final int SHOP_MAX_COUNT_OF_ITEMS = 10;

	// 页面中商品要求的数量(模板三)
	public static final int VIDEO_MIN_COUNT_OF_ITEMS = 1;
	public static final int VIDEO_MAX_COUNT_OF_ITEMS = 3;

	// 页面模板三种类型
	public static final String PAGE_TEMPLATE_DEFAULT = "default";
	public static final String PAGE_TEMPLATE_SHOP = "shop";
	public static final String PAGE_TEMPLATE_VIDEO = "video";
	public static final String PAGE_TEMPLATE_ITEM = "item";

	// MD5 盐
	public static final String SALT = "54818b05d116eadc7f67517a3a6e4b33";


	//资源文件路径
	private static final String PAGE_PATH = "/" + getEnvironment() + "/page.properties";

	// 配置属性
	public static final Properties properties;

	static {
		try {
			logger.info("自建页面属性文件路径是: " + PAGE_PATH);
			ClassPathResource resource = new ClassPathResource(PAGE_PATH, PageService.class);
			properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			throw new IllegalStateException("找不到自建页面属性文件page.properties: " + ex.getMessage());
		}
	}

	/**
	 * 获取环境变量
	 */
	public static String getEnvironment() {
		String environment = System.getenv("ENVIRONMENT");
		if (environment == null) {
			environment = "development";
		}
		return environment;
	}

	/**
	 * 页面查询
	 * @param pagination 分页类
	 * @param name 页面名称
	 * @param platform 设备( 1APP 2WAP 3PC)
	 * @param status 状态（-1删除 0草稿 1发布）
	 * @return
	 */
	public Map<String, Object> getPages(Pagination pagination, String name, Integer platform, Integer status, Integer pageTemplateId) throws Exception {

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		DESUtil des = new DESUtil();

		List<Page> pages = pageDao.findByPagination(pagination, name, platform, status, pageTemplateId);

		Map<Integer, PageTemplate> webpageTemplateMap = pageTemplateDao.getWebpageTemplateMap();
		
		List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
		for (Page page : pages) {
			Map<String, Object> pageMap = new LinkedHashMap<String, Object>();
			pageMap.put("pageId", page.getPageId());
			pageMap.put("pageTemplateId", page.getPageTemplateId());
			if (!CollectionUtils.isEmpty(webpageTemplateMap)) {
				PageTemplate PageTemplate = webpageTemplateMap.get(page.getPageTemplateId());
				if (null != PageTemplate) {
					pageMap.put("pageTemplateName", PageTemplate.getName());
				}
			}
			pageMap.put("name", page.getName());
			pageMap.put("platform", page.getPlatform());
			pageMap.put("status", page.getStatus());
			pageMap.put("createTime", page.getCreateTime());

			// @todo 加密落地页url
			pageMap.put("landingPage", StringUtils.isEmpty(page.getLandingPage()) ? "" : des.encode(page.getLandingPage()));
			// @todo 创意新建修改时 要调用此接口 追加以下字段
			pageMap.put("title", page.getTitle());
			pageMap.put("description", page.getDescription());
			pageMap.put("shopId", page.getShopId());
			pageMap.put("videoId", page.getVideoId());
			pageMap.put("image", page.getImage());
			pageMap.put("useDefaultImage", page.getUseDefaultImage());

			pageList.add(pageMap);
		}

		data.put("page", pagination.getCurrentPage());
		data.put("number", pagination.getNumber());
		data.put("totalCount", pagination.getTotalCount());
		data.put("list", pageList);

		return data;
	}

	/**
	 * 自建页面保存前 公共字段预处理
	 * @param pageMap
	 * @return
	 * @throws Exception
	 */
	private Page setCommonFields(Map<String, Object> pageMap) throws Exception {
		Date currentTime = new Date();

		// 自建页面第一页 公共字段
		String name = FieldUtil.getString("页面名称", pageMap.get("name"), null);
		Integer platform = FieldUtil.getInteger("设备类型", pageMap.get("pageTemplatePlatform"));
		Integer pageTemplateId = FieldUtil.getInteger("模板id", pageMap.get("pageTemplateId"));

		// 自建页面第二页 公共字段
		String cardTitle = ObjectUtils.isEmpty(pageMap.get("cardTitle")) ? "" : FieldUtil.getString("分享卡名称", pageMap.get("cardTitle"), null);
		String cardImage = ObjectUtils.isEmpty(pageMap.get("cardImage")) ? "" : FieldUtil.getString("分享卡图片", pageMap.get("cardImage"), null);
		String cardDescription = ObjectUtils.isEmpty(pageMap.get("cardDescription")) ? "" : FieldUtil.getString("分享卡文案", pageMap.get("cardDescription"), null);

		Page page = new Page();
		if (ObjectUtils.isEmpty(pageMap.get("pageId"))) {
			page.setCreateTime(currentTime);
		} else {
			page = pageDao.get(((Double) pageMap.get("pageId")).intValue());

		}

		page.setPageTemplateId(pageTemplateId);
		page.setName(name);
		page.setPlatform(platform);

		page.setCardTitle(cardTitle);
		page.setCardImage(cardImage);
		page.setCardDescription(cardDescription);

		page.setLandingPage("");
		page.setUserId(PrincipalUtil.getUserId());
		page.setStatus(Page.Status.DRAFT.getValue());
		page.setUpdateTime(currentTime);

		return page;
	}

	/**
	 * 批量保存页面上的商品
	 * @param pageMap
	 * @param pageId
	 * @param pageTemplateType
	 * @throws Exception
	 */
	private void batchSavePageItems(Map<String, Object> pageMap, Integer pageId, String pageTemplateType) throws Exception {
		Date currentTime = new Date();
		Integer promotionType = pageMap.get("promotionType") == null ? 1 : FieldUtil.getInteger("推广内容类型", pageMap.get("promotionType"));

		List<Map<String, Object>> data = (List<Map<String, Object>>)pageMap.get("data");
		for (Map<String, Object> item : data) {

			PageItem pageItem = new PageItem();
			pageItem.setPageId(pageId);
			pageItem.setCreateTime(currentTime);

			if (PAGE_TEMPLATE_DEFAULT.equals(pageTemplateType) || PAGE_TEMPLATE_SHOP.equals(pageTemplateType)
					|| (PAGE_TEMPLATE_VIDEO.equals(pageTemplateType) && (Page.PromotionType.PRODUCT.getValue() == promotionType))) {
				// 默认模板 || 店铺模板 || 视频模板(推广内容: 商品)

				if (ObjectUtils.isEmpty(item.get("skuId"))) {
					continue;
				}

				String itemSkuId = ObjectUtils.isEmpty(item.get("skuId")) ? "" : FieldUtil.getString("商品SKU", item.get("skuId"), null);
				String itemProductId = ObjectUtils.isEmpty(item.get("productId")) ? "" : FieldUtil.getString("productId", item.get("productId"), null);
				String itemImage = ObjectUtils.isEmpty(item.get("image")) ? "" : FieldUtil.getString("商品图片", item.get("image"), null);
				String itemName = ObjectUtils.isEmpty(item.get("name")) ? "" : FieldUtil.getString("商品名称", item.get("name"), null);
				String itemDescription = ObjectUtils.isEmpty(item.get("description")) ? "" : FieldUtil.getString("商品文案", item.get("description"), null);

				pageItem.setSkuId(itemSkuId);
				pageItem.setProductId(itemProductId);
				pageItem.setImage(itemImage);
				pageItem.setName(itemName);
				pageItem.setDescription(itemDescription);

			} else if (PAGE_TEMPLATE_VIDEO.equals(pageTemplateType) && (Page.PromotionType.SHOP.getValue() == promotionType)) {
				// 视频模板(推广内容: 店铺)

				String shopId = ObjectUtils.isEmpty(item.get("shopId")) ? "" : FieldUtil.getString("店铺Id", item.get("shopId"), null);
				String shopLogo = ObjectUtils.isEmpty(item.get("image")) ? "" : FieldUtil.getString("店铺Logo", item.get("image"), null);
				String shopName = ObjectUtils.isEmpty(item.get("name")) ? "" : FieldUtil.getString("店铺名称", item.get("name"), null);
				String shopDescription = ObjectUtils.isEmpty(item.get("description")) ? "" : FieldUtil.getString("店铺文案", item.get("description"), null);

				pageItem.setShopId(shopId);
				pageItem.setImage(shopLogo);
				pageItem.setName(shopName);
				pageItem.setDescription(shopDescription);

			}

			pageItemDao.save(pageItem);

		}
	}

	/**
	 * 自建页面保存(默认模板)
	 * @param pageMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> saveWithPageTemplateDefault(Map<String, Object> pageMap) throws Exception {
		Map<String, Object> returnData = new LinkedHashMap<String, Object>();

		// 自建页面第二页 独有字段
		String title = ObjectUtils.isEmpty(pageMap.get("title")) ? "" : FieldUtil.getString("名称", pageMap.get("title"), null);
		String description = ObjectUtils.isEmpty(pageMap.get("description")) ? "" : FieldUtil.getString("文案区", pageMap.get("description"), null);
		String image = ObjectUtils.isEmpty(pageMap.get("image")) ? "" : FieldUtil.getString("顶部大图", pageMap.get("image"), null);
		String backgroundColor = ObjectUtils.isEmpty(pageMap.get("backgroundColor")) ? "" : FieldUtil.getString("背景色", pageMap.get("backgroundColor"), null);
		
		Page page = this.setCommonFields(pageMap);

		page.setTitle(title);
		page.setDescription(description);
		page.setImage(image);
		page.setBackgroundColor(backgroundColor);

		pageDao.save(page);
		Integer pageId = page.getPageId();

		// 如果pageId不为空, 则为修改操作, 需要先删除旧的pageItem再保存新的pageItem
		if (!ObjectUtils.isEmpty(pageMap.get("pageId"))) {
			List<PageItem> oldPageItems = pageItemDao.getBy("pageId", pageId);
			for (PageItem oldPageItem : oldPageItems) {
				pageItemDao.delete(oldPageItem.getPageItemId());
			}
		}
		this.batchSavePageItems(pageMap, pageId, PAGE_TEMPLATE_DEFAULT);

		returnData.put("pageId", pageId);

		return returnData;
	}

	/**
	 * 自建页面保存(店铺模板)
	 * @param pageMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> saveWithPageTemplateShop(Map<String, Object> pageMap) throws Exception {
		Map<String, Object> returnData = new LinkedHashMap<String, Object>();

		// 自建页面第二页 独有字段
		String shopId = (String) pageMap.get("shopId");
		String shopName = ObjectUtils.isEmpty(pageMap.get("title")) ? "" : FieldUtil.getString("店铺名称", pageMap.get("title"), null);
		String shopLogo = ObjectUtils.isEmpty(pageMap.get("image")) ? "" : FieldUtil.getString("店铺LOGO", pageMap.get("image"), null);
		String description = ObjectUtils.isEmpty(pageMap.get("description")) ? "" : FieldUtil.getString("店铺描述", pageMap.get("description"), null);

		Page page = this.setCommonFields(pageMap);

		page.setShopId(shopId);
		page.setTitle(shopName);
		page.setImage(shopLogo);
		page.setDescription(description);

		pageDao.save(page);
		Integer pageId = page.getPageId();

		// 如果pageId不为空, 则为修改操作, 需要先删除旧的pageItem再保存新的pageItem
		if (!ObjectUtils.isEmpty(pageMap.get("pageId"))) {
			List<PageItem> oldPageItems = pageItemDao.getBy("pageId", pageId);
			for (PageItem oldPageItem : oldPageItems) {
				pageItemDao.delete(oldPageItem.getPageItemId());
			}
		}
		this.batchSavePageItems(pageMap, pageId, PAGE_TEMPLATE_SHOP);

		returnData.put("pageId", pageId);

		return returnData;
	}

	/**
	 * 自建页面保存(视频模板)
	 * @param pageMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> saveWithPageTemplateVideo(Map<String, Object> pageMap) throws Exception {
		Map<String, Object> returnData = new LinkedHashMap<String, Object>();

		// 自建页面第二页 独有字段
		String title = ObjectUtils.isEmpty(pageMap.get("title")) ? "" : FieldUtil.getString("广告标题", pageMap.get("title"), null);
		String image = ObjectUtils.isEmpty(pageMap.get("image")) ? "" : FieldUtil.getString("封面图片", pageMap.get("image"), null);
		String description = ObjectUtils.isEmpty(pageMap.get("description")) ? "" : FieldUtil.getString("视频描述", pageMap.get("description"), null);
		Long videoId = pageMap.get("videoId") == null ? 0L : FieldUtil.getLong("视频id", pageMap.get("videoId"));
		Integer useDefaultImage = pageMap.get("useDefaultImage") == null ? 0 : FieldUtil.getInteger("封面图片类型", pageMap.get("useDefaultImage"));
		Integer promotionType = pageMap.get("promotionType") == null ? 0 : FieldUtil.getInteger("推广内容类型", pageMap.get("promotionType"));

		// 保存时 如果选择默认图 会去video表取默认图
		if (useDefaultImage.equals(Page.UseDefaultImage.DEFAULT.getValue())) {
			Video video = videoDao.getVideoByVideoId(videoId);
			if (video != null && !StringUtils.isEmpty(video.getImage())) {
				image = video.getImage();
			}
		}

		Page page = this.setCommonFields(pageMap);

		page.setTitle(title);
		page.setVideoId(videoId);
		page.setUseDefaultImage(useDefaultImage);
		page.setImage(image);
		page.setPromotionType(promotionType);
		page.setDescription(description);

		pageDao.save(page);
		Integer pageId = page.getPageId();

		// 如果pageId不为空, 则为修改操作, 需要先删除旧的pageItem再保存新的pageItem
		if (!ObjectUtils.isEmpty(pageMap.get("pageId"))) {
			List<PageItem> oldPageItems = pageItemDao.getBy("pageId", pageId);
			for (PageItem oldPageItem : oldPageItems) {
				pageItemDao.delete(oldPageItem.getPageItemId());
			}
		}
		this.batchSavePageItems(pageMap, pageId, PAGE_TEMPLATE_VIDEO);

		returnData.put("pageId", pageId);

		return returnData;
	}


	/*
	 * 自建页面保存(路由)
	 * @param pageMap
	 */
	private Map<String, Object> saveRouter(Map<String, Object> pageMap) throws Exception {

		Map<String, Object> returnData = new LinkedHashMap<String, Object>();

		Integer pageTemplateId = FieldUtil.getInteger("模板id", pageMap.get("pageTemplateId"));

		// 根据模板id查询模板标题, 以匹配不同的templates文件
		PageTemplate pageTemplate = pageTemplateDao.get(pageTemplateId);
		if (pageTemplate == null || StringUtils.isEmpty(pageTemplate.getTitle())) {
			throw new Exception("找不到模板信息");
		}

		if (PAGE_TEMPLATE_DEFAULT.equals(pageTemplate.getTitle())
				|| PAGE_TEMPLATE_ITEM.equals(pageTemplate.getTitle())) {
			returnData = this.saveWithPageTemplateDefault(pageMap);

		} else if (PAGE_TEMPLATE_SHOP.equals(pageTemplate.getTitle())) {
			returnData = this.saveWithPageTemplateShop(pageMap);

		} else if (PAGE_TEMPLATE_VIDEO.equals(pageTemplate.getTitle())) {
			returnData = this.saveWithPageTemplateVideo(pageMap);

		}

		return returnData;
	}

	/*
	 * 新建自建页面
	 * @param pageMap
	 */	
	@Transactional(readOnly = false, value = "transactionManager", rollbackFor = { Exception.class })
	public Map<String, Object> save(Map<String, Object> pageMap) throws Exception {
		return this.saveRouter(pageMap);
	}
	
	/*
	 * 修改自建页面
	 * @param pageMap
	 */	
	@Transactional(readOnly = false, value = "transactionManager", rollbackFor = { Exception.class })
	public Map<String, Object> update(Map<String, Object> pageMap) throws Exception {

		Integer pageId = FieldUtil.getInteger("自建页面id", pageMap.get("pageId"));
		Page oldPage = pageDao.get(pageId);

		if (oldPage.getStatus() == Page.Status.PUBLISHED.getValue()) {
			throw new Exception("已发布页面不能修改");
		}
		if (oldPage.getStatus() == Page.Status.DELETE.getValue()) {
			throw new Exception("已删除页面不能修改");
		}

		return this.saveRouter(pageMap);
	}

	/**
	 * 获取单个自建活动页数据
	 * @param pageId
	 * @return
	 */
	public Map<String,Object> getPage(Integer pageId) throws Exception {

		// 数据容器初始化
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		Page page = pageDao.get(pageId);
		if (page == null) {
			throw new Exception("未查到相关页面数据");
		}

		List<PageItem> pageItems = pageItemDao.getBy("pageId", pageId);
		PageTemplate pageTemplate = pageTemplateDao.get(page.getPageTemplateId());

		// 拼接前端数据格式
		data.put("pageId", page.getPageId());
		data.put("pageTemplateId", page.getPageTemplateId());
		data.put("pageTemplatePreview", pageTemplate.getPreview());
		data.put("pageTemplateTitle", pageTemplate.getTitle());
		data.put("platform", page.getPlatform());
		data.put("title", page.getTitle());
		data.put("name", page.getName());
		data.put("description", page.getDescription());
		data.put("image", page.getImage());
		data.put("data", pageItems);
		data.put("backgroundColor", page.getBackgroundColor());
		data.put("cardTitle", page.getCardTitle());
		data.put("cardImage", page.getCardImage());
		data.put("cardDescription", page.getCardDescription());

		data.put("shopId", page.getShopId());
		data.put("videoId", page.getVideoId());
		data.put("useDefaultImage", page.getUseDefaultImage());
		data.put("promotionType", page.getPromotionType());

		return data;
	}

	/**
	 * 获取页面列表上的预览信息
	 * @param pageId
	 * @return
	 */
	public Map<String,Object> getPagePreview(Integer pageId) throws Exception {

		// 数据容器初始化
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		Page page = pageDao.get(pageId);
		if (ObjectUtils.isEmpty(page) || ObjectUtils.isEmpty(page.getPageTemplateId())) {
			throw new Exception("找不到自建页面数据");
		}

		// 根据模板id查询模板标题, 以匹配不同的templates文件
		PageTemplate pageTemplate = pageTemplateDao.get(page.getPageTemplateId());
		if (pageTemplate == null || StringUtils.isEmpty(pageTemplate.getTitle()) || StringUtils.isEmpty(pageTemplate.getName())) {
			throw new Exception("获取自建页面对应的模板信息失败");
		}

		String pageTemplateType = pageTemplate.getTitle();
		Integer promotionType = page.getPromotionType();
		List<PageItem> pageItems = pageItemDao.getBy("pageId", pageId);

		if (PAGE_TEMPLATE_DEFAULT.equals(pageTemplateType)
				|| PAGE_TEMPLATE_ITEM.equals(pageTemplateType)
				|| PAGE_TEMPLATE_SHOP.equals(pageTemplateType)
				|| (PAGE_TEMPLATE_VIDEO.equals(pageTemplateType) && (Page.PromotionType.PRODUCT.getValue() == promotionType))) {
			// 默认模板 || 店铺模板 || 视频模板(推广内容: 商品)
			if (!CollectionUtils.isEmpty(pageItems)) {
//				// 批量设置商品链接
//				this.setPageItemUrl(pageItems);
				// 批量设置商品价格
				this.setPageItemPrice(pageItems);
			}
		}

		// 根据page数据和模板名称 生成HTML代码
		data.put("html", this.createHTML(page, pageItems, pageTemplate.getTitle()));

		return data;
	}

	/**
	 * 发布页面
	 * @param pageId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public Map<String,Object> publishPage(Integer pageId) throws Exception {

		Page page = pageDao.get(pageId);

		// 校验page
		List<String> errors = this.checkPageData(page);

		if (!CollectionUtils.isEmpty(errors)) {
			String errorsString = String.join(",\n -", errors);
			throw new Exception("表单中共有" + errors.size() + "处错误\n -" + errorsString);
		}

//		List<PageItem> pageItems = pageItemDao.getBy("pageId", pageId);
//
//		// 批量设置商品链接
//		this.setPageItemUrl(pageItems);
//
//		// 获取模板名称, HTML文件路径
//		PageTemplate pageTemplate = pageTemplateDao.get(page.getPageTemplateId());
//		if (ObjectUtils.isEmpty(pageTemplate) || StringUtils.isEmpty(pageTemplate.getName())) {
//			throw new Exception("获取页面对应的模板名称失败");
//		}
//		String filePath = "/tmp/" + page.getPageId() + ".html";
//
//		// 生成HTML代码, 并创建HTML文件
//		String html = this.createHTML(page, pageItems, pageTemplate.getName());
//		FileUtils.writeStringToFile(new File(filePath), html);
//
//		// 上传HTML文件, 并返回url
//		String url = this.uploadHTML(filePath);

		// 根据模板id查询模板标题, 以匹配不同的校验方法
		PageTemplate pageTemplate = pageTemplateDao.get(page.getPageTemplateId());
		if (pageTemplate == null || StringUtils.isEmpty(pageTemplate.getTitle())) {
			throw new Exception("找不到模板信息");
		}

		String url = "";
		String hash = Hashing.md5().newHasher()
				.putLong(page.getPageId())
				.putString(SALT, Charsets.UTF_8)
				.hash().toString();
		if (PAGE_TEMPLATE_DEFAULT.equals(pageTemplate.getTitle())) {
			url = properties.getProperty("landingPageUrlOfDefault") + hash;
		} else if (PAGE_TEMPLATE_SHOP.equals(pageTemplate.getTitle())) {
			url = properties.getProperty("landingPageUrlOfShop") + hash + ".html";
		} else if (PAGE_TEMPLATE_VIDEO.equals(pageTemplate.getTitle())) {
			url = properties.getProperty("landingPageUrlOfVideo") + hash + ".html";
		} else if (PAGE_TEMPLATE_ITEM.equals(pageTemplate.getTitle())) {
			url = properties.getProperty("landingPageUrlOfItem") + hash + ".html";
		}

		// 设置落地页url, 发布页面
		Date currentTime = new Date();
		page.setLandingPage(url);
		page.setHash(hash);
		page.setStatus(Page.Status.PUBLISHED.getValue());
		page.setUpdateTime(currentTime);
		page.setPublishTime(currentTime);
		
		/*
		 * 设置自建页的发布状态，此次需求仅处理视频自建页；
		 * 转码状态为'转码失败'，webpage状态记为'草稿'
		 * 转码状态为'转码异常'，webpage状态记为'草稿'
		 * 转码状态为'转码成功'，webpage状态记为'发布'
		 * 其他转码状态，webpage状态记为'发布中'
		 */
		logger.info("publishPage page webpageId: " + pageId + " webpageTemplageId: " + page.getPageTemplateId() );
		if (page.getPageTemplateId().equals(PageTemplate.WebpageTemplateType.EXPLORE_VIDEO.getValue())) {
			Long videoId = page.getVideoId();
			logger.info("publishPage page videoId: " + videoId);
			Video video = videoDao.getVideoByVideoId(videoId);
			logger.info("publishPage page video: " + video);
			if (null != video) {
				if (Video.ConvertStatus.SUCCESS.getValue().equals(video.getConvertStatus())) {
					page.setStatus(Page.Status.PUBLISHED.getValue());
				} else if (Video.ConvertStatus.FAIL.getValue().equals(video.getConvertStatus()) || Video.ConvertStatus.EXCEPTION.getValue().equals(video.getConvertStatus())) {
					page.setStatus(Page.Status.DRAFT.getValue());
				} else {
					page.setStatus(Page.Status.PUBLISHING.getValue());
				}
			} else {
				page.setStatus(Page.Status.PUBLISHING.getValue());
			}
		}

		logger.info("publishPage page: " + page);
		
		pageDao.update(page);

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("pageId", pageId);
		return data;
	}

	/**
	 * 生成HTML源码
	 * @param page Page实体
	 * @param pageItems Page对应的商品
	 * @param templateName 模板名称
	 * @return
	 */
	private String createHTML(Page page, List<PageItem> pageItems, String templateName) {

		final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(true);

		final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setEnableSpringELCompiler(true);

		Locale locale = new Locale("zh");

		final Context ctx = new Context(locale);
		ctx.setVariable("page", page);
		ctx.setVariable("items", pageItems);

//		ctx.setVariable("baseUrl", properties.getProperty("baseUrl"));
//		ctx.setVariable("shareUrl", properties.getProperty("shareUrl"));
//		ctx.setVariable("productUrl", properties.getProperty("productUrl"));
//		ctx.setVariable("loadImpressionUrl", properties.getProperty("loadImpressionUrl"));
//		ctx.setVariable("appInterfaceJs", properties.getProperty("appInterfaceJs"));
//		ctx.setVariable("activePageJs", properties.getProperty("activePageJs"));

		final String textContent = templateEngine.process(templateName, ctx);

		return textContent;
	}

	/**
	 * 上传HTML文件
	 * @param filePath 待上传的HTML文件路径
	 * @return
	 */
	private String uploadHTML(String filePath) throws Exception {

		// 准备HTML文件, 请求参数
		Resource resource = new FileSystemResource(filePath);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("Content-Type", "text/html");
		parts.add("file", resource);

		RestTemplate restTemplate = new RestTemplate();

		// 根据环境获取上传接口地址
		String uploadApi = properties.getProperty("uploadHost") + "/upload";

		// 发起上传HTML文件请求
		ResponseEntity<String> response = restTemplate.exchange(uploadApi, HttpMethod.POST,
				new HttpEntity<MultiValueMap<String, Object>>(parts),
				String.class);

		// 解析上传HTML文件请求的返回结果
		if (ObjectUtils.isEmpty(response) || StringUtils.isEmpty(response.getBody())) {
			throw new Exception("上传HTML返回结果为空");
		}
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new Exception("上传HTML返回状态码不为200");
		}

		String responseBody = response.getBody();
		Map<String, Object> responseMap = new LinkedHashMap<String, Object>();
		Map<String, String> responseData = null;
		String url = null;

		try {
			responseMap = (Map<String, Object>) gson.fromJson(responseBody, responseMap.getClass());
			responseData = (Map<String, String>) responseMap.get("data");
			url = responseData.get("url");
		} catch (Exception e) {
			throw new Exception("上传HTML返回结果转换异常");
		}

		if (!StringUtils.isEmpty(responseMap.get("message").toString())) {
			throw new Exception("上传HTML返回结果中的message不为空: " + responseMap.get("message").toString());
		}
		if (StringUtils.isEmpty(url)) {
			throw new Exception("上传HTML返回的url为空");
		}

		return url;
	}

//	/**
//	 * 批量生成设置商品链接 (根据链接模板, productId, skuId)
//	 * @param pageItems
//	 * @return
//	 * @throws Exception
//	 */
//	private List<PageItem> setPageItemUrl(List<PageItem> pageItems) throws Exception {
//
//		// 根据环境获取商品链接模板
//		String itemUrlTemplate = properties.getProperty("itemUrl");
//
//		// 循环处理商品链接
//		for (PageItem pageItem : pageItems) {
//			String productId = pageItem.getProductId();
//			String skuId = pageItem.getSkuId();
//			if (StringUtils.isEmpty(productId) || StringUtils.isEmpty(skuId)) {
//				throw new Exception("页面中的商品productId或skuId为空");
//			}
//
//			// 商品链接模板: http://item.m.uatplus.com/p-[productid]-[skuid].html
//			String itemUrl = itemUrlTemplate.replace("[productid]", productId).replace("[skuid]", skuId);
//			pageItem.setUrl(itemUrl);
//		}
//
//		return pageItems;
//	}

	/**
	 * 批量设置商品价格 (根据productId, skuId, 并调用接口)
	 * @param pageItems
	 * @return
	 * @throws Exception
	 */
	private List<PageItem> setPageItemPrice(List<PageItem> pageItems) throws Exception {

		// 创建价格获取接口的参数容器
		Map<String, String> itemsData = new HashMap<String, String>();

		// 循环获取PageItem的productId和skuId
		for (PageItem pageItem : pageItems) {
			String productId = pageItem.getProductId();
			String skuId = pageItem.getSkuId();
			if (StringUtils.isEmpty(productId) || StringUtils.isEmpty(skuId)) {
				logger.error("页面中的商品productId或skuId为空");
				throw new Exception("页面中的商品productId或skuId为空");
			}

			itemsData.put(skuId, productId);
		}

		// 调用价格获取接口
		Map<String, Double> itemsPrice =  new MallProductOperations().getPriceByProductIdsAndSkuIds(itemsData);
		if (CollectionUtils.isEmpty(itemsPrice)) {
			logger.error("页面中的商品价格为空");
			throw new Exception("页面中的商品价格为空");
		}

		// 循环设置PageItem的price
		for (PageItem pageItem : pageItems) {
			String skuId = pageItem.getSkuId();
			Double price = itemsPrice.get(skuId);
			if (ObjectUtils.isEmpty(price)) {
				logger.error("页面中的商品skuId找不到对应价格");
				throw new Exception("页面中的商品skuId找不到对应价格");
			}

			pageItem.setPrice(price);
		}
		return pageItems;
	}

	/**
	 * 检验Page中的公共字段
	 * @param page
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	private List<String> checkPageDataOfCommon(Page page, List<String> errors) throws Exception {

		// 校验page
		if (ObjectUtils.isEmpty(page)) {
			logger.error("page为null");
			errors.add("自建页面数据找不到");
		}

		// 校验page状态
		if (page.getStatus() != Page.Status.DRAFT.getValue()) {
			logger.error("page发布时状态必须为草稿");
			errors.add("发布时状态必须为草稿");
		}

		// 校验page名称
		if (StringUtils.isEmpty(page.getName())) {
			logger.error("page的页面名称为空");
			errors.add("页面名称未填写");
		}

		// 校验page设备类型
		if (ObjectUtils.isEmpty(page.getPlatform())) {
			logger.error("page的设备类型为空");
			errors.add("设备类型未选择");
		}

		// 校验page模板id
		if (ObjectUtils.isEmpty(page.getPageTemplateId())) {
			logger.error("page的模板id为空");
			errors.add("模板未选择");
		}

		// 校验page分享卡名称
		if (StringUtils.isEmpty(page.getCardTitle())) {
			logger.error("page的分享卡名称为空");
			errors.add("分享卡名称未填写");
		}

		// 校验page分享卡文案
		if (StringUtils.isEmpty(page.getCardDescription())) {
			logger.error("page的分享卡文案为空");
			errors.add("分享卡文案未填写");
		}

		// 校验page分享卡图片
		if (StringUtils.isEmpty(page.getCardImage())) {
			logger.error("page的分享卡图片为空");
			errors.add("分享卡图片未上传");
		}

		return errors;
	}

	/**
	 * 检验Page(模板一,模板四)中的特有字段
	 * @param page
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	private List<String> checkPageDataOfDefaultTemplate(Page page, List<String> errors) throws Exception {

		// 校验page标题
		if (StringUtils.isEmpty(page.getTitle())) {
			logger.error("page的标题为空");
			errors.add("名称未填写");
		}

		// 校验page主图
		if (StringUtils.isEmpty(page.getImage())) {
			logger.error("page的主图为空");
			errors.add("主图未上传");
		}

		List<PageItem> pageItems = pageItemDao.getBy("pageId", page.getPageId());

		// 模板一 校验page商品数, 必须为10个
		if (CollectionUtils.isEmpty(pageItems) || pageItems.size() != REQUIRED_COUNT_OF_ITEMS) {
			logger.error("发布时页面中的商品数量必须为" + REQUIRED_COUNT_OF_ITEMS + "个");
			errors.add("发布时商品数量必须为" + REQUIRED_COUNT_OF_ITEMS + "个");
		}

		// 校验pageItem中的图片地址, productId, skuId, 文案
		for (PageItem pageItem : pageItems) {
			if (StringUtils.isEmpty(pageItem.getImage())) {
				logger.error("自建页面(默认)的商品{pageItemId: " + pageItem.getPageItemId() + "} image为空");
				errors.add("页面中存在有商品的图片为空白");
			}
			if (StringUtils.isEmpty(pageItem.getProductId())) {
				logger.error("自建页面(默认)的商品{pageItemId: " + pageItem.getPageItemId() + "} productId为空");
				errors.add("页面中存在有商品的productId为空白");
			}
			if (StringUtils.isEmpty(pageItem.getSkuId())) {
				logger.error("自建页面(默认)的商品{pageItemId: " + pageItem.getPageItemId() + "} skuId为空");
				errors.add("页面中存在有商品的skuId为空白");
			}
			if (StringUtils.isEmpty(pageItem.getDescription())) {
				logger.error("自建页面(默认)的商品{pageItemId: " + pageItem.getPageItemId() + "} description为空");
				errors.add("页面中存在有商品的文案为空白");
			}
			if (pageItem.getDescription().length() > MAX_LENGTH_OF_DESCRIPTION) {
				logger.error("自建页面(默认)的商品{pageItemId: " + pageItem.getPageItemId() + "} description长度大于" + MAX_LENGTH_OF_DESCRIPTION);
				errors.add("页面中存在有商品的文案字数大于" + MAX_LENGTH_OF_DESCRIPTION + "个");
			}
		}

		return errors;
	}

	/**
	 * 检验Page(模板二 店铺)中的特有字段
	 * @param page
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	private List<String> checkPageDataOfShopTemplate(Page page, List<String> errors) throws Exception {

		// 校验 店铺ID
		if (StringUtils.isEmpty(page.getShopId())) {
			logger.error("自建页面(店铺)的店铺ID为空");
			errors.add("店铺ID未填写");
		}

		// 校验 店铺文案 @todo 字符超长校验
		if (!StringUtils.isEmpty(page.getDescription()) && page.getDescription().length() > 200) {
			logger.error("自建页面(店铺)的店铺文案字数超长(200字符)");
			errors.add("店铺文案字数超长");
		}

		List<PageItem> pageItems = pageItemDao.getBy("pageId", page.getPageId());

		// 模板二(店铺) 校验 商品数(最少4个, 最多10个)
		if (CollectionUtils.isEmpty(pageItems) || pageItems.size() < SHOP_MIN_COUNT_OF_ITEMS || pageItems.size() > SHOP_MAX_COUNT_OF_ITEMS) {
			logger.error("发布时自建页面(店铺)的商品数量最少为" + SHOP_MIN_COUNT_OF_ITEMS + "个, 最多为" + SHOP_MAX_COUNT_OF_ITEMS + "个");
			errors.add("发布时商品数量最少为" + SHOP_MIN_COUNT_OF_ITEMS + "个, 最多为" + SHOP_MAX_COUNT_OF_ITEMS + "个");
		}

		// 校验pageItem中的图片地址, productId, skuId, 文案, 商品名称
		for (PageItem pageItem : pageItems) {
			if (StringUtils.isEmpty(pageItem.getImage())) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} image为空");
				errors.add("页面中存在有商品的图片为空白");
			}
			if (StringUtils.isEmpty(pageItem.getProductId())) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} productId为空");
				errors.add("页面中存在有商品的productId为空白");
			}
			if (StringUtils.isEmpty(pageItem.getSkuId())) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} skuId为空");
				errors.add("页面中存在有商品的skuId为空白");
			}
			if (StringUtils.isEmpty(pageItem.getDescription())) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} description为空");
				errors.add("页面中存在有商品的文案为空白");
			}
			if (pageItem.getDescription().length() > MAX_LENGTH_OF_DESCRIPTION) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} description长度大于" + MAX_LENGTH_OF_DESCRIPTION);
				errors.add("页面中存在有商品的文案字数大于" + MAX_LENGTH_OF_DESCRIPTION + "个");
			}
			if (StringUtils.isEmpty(pageItem.getName())) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} name为空");
				errors.add("页面中存在有商品的名称为空白");
			}
			if (pageItem.getName().length() > MAX_LENGTH_OF_DESCRIPTION) {
				logger.error("自建页面(店铺)的商品{pageItemId: " + pageItem.getPageItemId() + "} name长度大于" + MAX_LENGTH_OF_DESCRIPTION);
				errors.add("页面中存在有商品的名称字数大于" + MAX_LENGTH_OF_DESCRIPTION + "个");
			}
		}

		return errors;
	}

	/**
	 * 检验Page(模板三 视频)中的特有字段
	 * @param page
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	private List<String> checkPageDataOfVideoTemplate(Page page, List<String> errors) throws Exception {

		// 校验 广告标题
		if (StringUtils.isEmpty(page.getTitle())) {
			logger.error("自建页面(视频)的广告标题为空");
			errors.add("广告标题未填写");
		}

		// 校验 广告标题 @todo 字符超长校验
		if (!StringUtils.isEmpty(page.getTitle()) && page.getTitle().length() > 30) {
			logger.error("自建页面(视频)的广告标题字数超长(30字符)");
			errors.add("店铺文案字数超长");
		}

		// 校验 视频ID
		if (page.getVideoId() == null || page.getVideoId() == 0) {
			logger.error("自建页面(视频)的视频ID为null或0");
			errors.add("视频未上传");
		}

		// 校验 视频封面图片
		if (page.getUseDefaultImage() == 1 && StringUtils.isEmpty(page.getImage())) {
			logger.error("自建页面(视频)的封面图片(自定义)为空");
			errors.add("封面图片(自定义)未上传");
		}

		// 校验 推广内容类型
		if (page.getPromotionType() == null || page.getPromotionType() == 0) {
			logger.error("自建页面(视频)的推广内容类型未选择");
			errors.add("推广内容未选择");
		}

		// 校验 视频描述 @todo 字符超长校验
		if (!StringUtils.isEmpty(page.getDescription()) && page.getDescription().length() > 200) {
			logger.error("自建页面(视频)的视频描述字数超长(200字符)");
			errors.add("视频描述字数超长");
		}

		List<PageItem> pageItems = pageItemDao.getBy("pageId", page.getPageId());

		// 推广内容 为 商品
		if (page.getPromotionType() == Page.PromotionType.PRODUCT.getValue()) {
			// 模板三(视频-商品) 校验 商品数(最少1个, 最多3个)
			if (CollectionUtils.isEmpty(pageItems) || pageItems.size() < VIDEO_MIN_COUNT_OF_ITEMS || pageItems.size() > VIDEO_MAX_COUNT_OF_ITEMS) {
				logger.error("发布时自建页面(视频)的商品数量最少为" + VIDEO_MIN_COUNT_OF_ITEMS + "个, 最多为" + VIDEO_MAX_COUNT_OF_ITEMS + "个");
				errors.add("发布时商品数量最少为" + VIDEO_MIN_COUNT_OF_ITEMS + "个, 最多为" + VIDEO_MAX_COUNT_OF_ITEMS + "个");
			}

			// 校验pageItem中的图片地址, productId, skuId, 文案, 商品名称
			for (PageItem pageItem : pageItems) {
				if (StringUtils.isEmpty(pageItem.getImage())) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} image为空");
					errors.add("页面中存在有商品的图片为空白");
				}
				if (StringUtils.isEmpty(pageItem.getProductId())) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} productId为空");
					errors.add("页面中存在有商品的productId为空白");
				}
				if (StringUtils.isEmpty(pageItem.getSkuId())) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} skuId为空");
					errors.add("页面中存在有商品的skuId为空白");
				}
				if (StringUtils.isEmpty(pageItem.getDescription())) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} description为空");
					errors.add("页面中存在有商品的文案为空白");
				}
				if (pageItem.getDescription().length() > MAX_LENGTH_OF_DESCRIPTION) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} description长度大于" + MAX_LENGTH_OF_DESCRIPTION);
					errors.add("页面中存在有商品的文案字数大于" + MAX_LENGTH_OF_DESCRIPTION + "个");
				}
				if (StringUtils.isEmpty(pageItem.getName())) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} name为空");
					errors.add("页面中存在有商品的名称为空白");
				}
				if (pageItem.getName().length() > MAX_LENGTH_OF_DESCRIPTION) {
					logger.error("自建页面(视频)的商品{pageItemId: " + pageItem.getPageItemId() + "} name长度大于" + MAX_LENGTH_OF_DESCRIPTION);
					errors.add("页面中存在有商品的名称字数大于" + MAX_LENGTH_OF_DESCRIPTION + "个");
				}
			}

		}

		// 推广内容 为 店铺
		if (page.getPromotionType() == Page.PromotionType.SHOP.getValue()) {
			// 模板三(视频-店铺) 校验 店铺(1个)
			if (CollectionUtils.isEmpty(pageItems) || pageItems.size() != VIDEO_MIN_COUNT_OF_ITEMS) {
				logger.error("发布时自建页面(视频)的店铺信息为空");
				errors.add("发布时店铺信息未填写");
			}

			// 校验pageItem中的店铺id, 店铺描述
			for (PageItem pageItem : pageItems) {
				if (StringUtils.isEmpty(pageItem.getShopId())) {
					logger.error("自建页面(视频)的店铺{pageItemId: " + pageItem.getPageItemId() + "} shopId为空");
					errors.add("店铺ID未填写");
				}
				if (StringUtils.isEmpty(pageItem.getDescription())) {
					logger.error("自建页面(视频)的店铺{pageItemId: " + pageItem.getPageItemId() + "} description为空");
					errors.add("店铺描述未填写");
				}
				if (pageItem.getDescription().length() > 100) {
					logger.error("自建页面(视频)的店铺{pageItemId: " + pageItem.getPageItemId() + "} description长度大于" + 100);
					errors.add("店铺描述字数超长");
				}
			}

		}

		return errors;
	}

	/**
	 * 校验Page中的数据
	 * @param page 待校验的page
	 * @throws Exception
	 */
	private List<String> checkPageData(Page page) throws Exception {

		List<String> errors = new ArrayList<String>();

		errors = this.checkPageDataOfCommon(page, errors);

		// 根据模板id查询模板标题, 以匹配不同的校验方法
		PageTemplate pageTemplate = pageTemplateDao.get(page.getPageTemplateId());
		if (pageTemplate == null || StringUtils.isEmpty(pageTemplate.getTitle())) {
			throw new Exception("找不到模板信息");
		}

		if (PAGE_TEMPLATE_DEFAULT.equals(pageTemplate.getTitle())
				|| PAGE_TEMPLATE_ITEM.equals(pageTemplate.getTitle())) {
			errors = this.checkPageDataOfDefaultTemplate(page, errors);

		} else if (PAGE_TEMPLATE_SHOP.equals(pageTemplate.getTitle())) {
			errors = this.checkPageDataOfShopTemplate(page, errors);

		} else if (PAGE_TEMPLATE_VIDEO.equals(pageTemplate.getTitle())) {
			errors = this.checkPageDataOfVideoTemplate(page, errors);

		}

		return errors;
	}

	/**
	 * 获取店铺信息
	 * @param shopId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getShopItemByShopId(String shopId) throws Exception {
		String shopIdTemp = "";
		if (StringUtils.isEmpty(shopId.trim())) {
			// 获取用户信息
			User user = userDao.get(PrincipalUtil.getUserId());
			// 调用 bs入驻商家接口,获取 入驻商家 信息
			BsMerchantInfoOperations merchantInfoOperations = new BsMerchantInfoOperations();
			// 校验是否是商家用户
			if (user.getType() != User.Type.MERCHANT.getValue()) {
				throw new Exception("不是商家用户");
			}
			// 校验shopId
			if (!ObjectUtils.isEmpty(user.getExtShopId()) && user.getExtShopId() != 0) {
				ApiResponse merchantInfo = merchantInfoOperations.getMerchantInfoByShopId(user.getExtShopId());
				Map<String, Object> shopInfo = merchantInfo.getData();
				if (!CollectionUtils.isEmpty(shopInfo)) {
					// 获取店铺编码
					shopIdTemp = (String) shopInfo.get("shopNo");
				}
			} else {
				throw new Exception("查找不到店铺信息");
			}
		} else {
			shopIdTemp = shopId;
		}

		MallShopOperations mallShopOperations = new MallShopOperations();
		// 获取店铺信息
		ApiResponse shopResponse = mallShopOperations.getShopItemByShopId(shopIdTemp);
		Map<String, Object> shopData = new HashMap<String, Object>();
		shopData = shopResponse.getData();
		if (CollectionUtils.isEmpty(shopData)) {
			return shopData;
		}
		// 获取店铺收藏数
		ApiResponse collectResponse = mallShopOperations.getCollectionByShopId(shopIdTemp);
		Map<String, Object> collectData = collectResponse.getData();
		Number shopCollectionQuantity = 0;
		if (!CollectionUtils.isEmpty(collectData)) {
			shopCollectionQuantity = collectData.get("shopCollectionQuantity") == null ? 0
					: (Number) collectData.get("shopCollectionQuantity");
		}
		shopData.put("shopCollectionQuantity", shopCollectionQuantity.intValue());
		if (!shopData.containsKey("icon")) {
			shopData.put("icon", "");
		}
		if (!shopData.containsKey("name")) {
			shopData.put("name", "");
		}
		if (!shopData.containsKey("description")) {
			shopData.put("description", "");
		}
		if (!shopData.containsKey("status")) {
			shopData.put("status", "");
		}
		if (!shopData.containsKey("type")) {
			shopData.put("type", "");
		}
		return shopData;
	}
}
