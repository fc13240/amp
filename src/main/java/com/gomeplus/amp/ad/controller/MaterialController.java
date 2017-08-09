package com.gomeplus.amp.ad.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.gomeplus.adm.common.util.DESUtil;
import com.gomeplus.adm.common.util.RandomStringUtil;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.form.MaterialForm;
import com.gomeplus.amp.ad.model.Material;
import com.gomeplus.amp.ad.service.MaterialService;
import com.google.gson.Gson;

/**
 * 素材 controller
 * 
 * @author wangwei01
 */
@Controller
public class MaterialController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MaterialController.class);

	private Gson gson = new Gson();

	@Autowired
	private MaterialService materialService;

	/**
	 * 添加创意
	 *
	 * @param materialJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material", method = RequestMethod.POST)
	public FeAjaxResponse save(@RequestBody MaterialForm materialForm) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			data = materialService.save(materialForm);
		} catch (Exception exception) {
			logger.error("add material failed ",exception);
			return FeAjaxResponse.error(400, "添加创意失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success(data, "添加创意成功！");
	}

	/**
	 * 修改创意
	 *
	 * @param materialJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material", method = RequestMethod.PUT)
	public FeAjaxResponse modify(@RequestBody MaterialForm materialForm) {
		try {
			materialService.update(materialForm);
		} catch (Exception exception) {
			logger.error("update material failed",exception);
			return FeAjaxResponse.error(400, "修改创意失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success("修改创意成功！");
	}

	/**
	 * 上传素材图片
	 * 
	 * @param request
	 * @param file
	 *            上传的图片
	 * @return
	 */
	@ResponseBody
	// @RequiresPermissions("ad:material:add")
	@RequestMapping(value = "/material/imageUpload", method = RequestMethod.POST)
	public FeAjaxResponse imageUpload(HttpServletRequest request, @RequestParam("files") MultipartFile file) {
		HashMap<String, Object> data = new HashMap<String, Object>();

		String imageTmpName = System.getProperty("java.io.tmpdir") + File.separator + RandomStringUtil.getRandomString(10)
				+ new Date().getTime();
		File image = new File(imageTmpName);
		String fileType;
		try {
			file.transferTo(image);
			fileType = file.getContentType();
			// 上传文件至CDN
			String size = Long.toString(file.getSize());
			String imageUrl;
			try {
				imageUrl = materialService.uploadToCDN(image, size);
			} catch (Exception exception) {
				logger.error("file.transferTo IllegalStateException", exception);
				return FeAjaxResponse.error(400, "上传创意失败！" + exception.getMessage());

			}

			data.put("imageUrl", imageUrl);
			data.put("size", size);
			data.put("fileType", fileType);
			String fileName = file.getOriginalFilename();
			data.put("fileName", fileName.substring(0, fileName.lastIndexOf('.')));

		} catch (IllegalStateException exception) {
			logger.error("file.transferTo IllegalStateException", exception);
			return FeAjaxResponse.error(400, "上传创意失败！" + exception.getMessage());
		} catch (IOException exception) {
			logger.error("file.transferTo IOException", exception);
			return FeAjaxResponse.error(400, "上传创意失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "上传素材成功！");
	}

	/**
	 * 创意列表
	 * 
	 * @param request
	 * @param keyword
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/materials", method = RequestMethod.GET)
	public FeAjaxResponse list(HttpServletRequest request, @RequestParam Integer flightId,
			@RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(required = false, defaultValue="0") Integer state) {

		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			Pagination pagination = getPagination(request);
			List<Map<String, Object>> materials = new ArrayList<Map<String, Object>>();

			List<Material> materialList = materialService.getMaterialsByFlightId(pagination, flightId, keyword, state);
			if (!CollectionUtils.isEmpty(materialList)) {
				for (Material material : materialList) {
					Map<String, Object> materialMap = new HashMap<String, Object>();
					materialMap.put("materialId", material.getMaterialId());
					materialMap.put("name", material.getName());
					materialMap.put("title", URLDecoder.decode(material.getTitle(), "UTF-8"));
					String imageStr = material.getImage();;
					if (!StringUtils.isEmpty(imageStr)) {
						List<String> images = Arrays.asList(imageStr.split(","));
						if (!CollectionUtils.isEmpty(images)) {
							materialMap.put("preview", images.get(0));
						}
					}
					materialMap.put("type", material.getType());
					materialMap.put("width", material.getWidth());
					materialMap.put("height", material.getHeight());
					if (Material.LinkType.URL.getValue().equals(material.getLinkType())
							|| Material.LinkType.SHOP.getValue().equals(material.getLinkType())
							|| Material.LinkType.VIDEO.getValue().equals(material.getLinkType())) {
						materialMap.put("landingPage", new DESUtil().encode(material.getLandingPage()));
					} else {
						materialMap.put("landingPage", material.getLandingPage());
					}
					materialMap.put("linkType", material.getLinkType());
					materialMap.put("state", material.getState());
					materialMap.put("promotionId", material.getPromotionId());
					materials.add(materialMap);
				}
			}
			data.put("totalCount", pagination.getTotalCount());
			data.put("page", pagination.getCurrentPage());
			data.put("number", pagination.getNumber());
			data.put("list", materials);
		} catch (Exception exception) {
			logger.error("flight list", exception);
			return FeAjaxResponse.error(500, data, "获取创意列表失败！");
		}
		return FeAjaxResponse.success(data, "获取创意列表成功！");
	}
	
	/**
	 * 根据店铺Id、skuId查询商品列表
	 * （由于商城未提供批量skuId查类目的接口，所以在用skuIds查商品列表的返回结果里先未设置类目id (下一版商榷优化)
	 * （所以,前端无法传categoryId；故改为传skuId，后端通过skuId调商城接口拿到相应的categoryId） 
	 * 
	 * @param shopId
	 * @param skuId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/items", method = RequestMethod.GET)
	public FeAjaxResponse getItemsByShopIdCategoryId(@RequestParam String shopId, @RequestParam(required = false) String categoryId, @RequestParam String skuId, @RequestParam(required = false, defaultValue = "3") Integer relatedItemStrategy) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> items = materialService.getItemsByShopIdCategoryId(shopId, categoryId, skuId, relatedItemStrategy);
			data.put("list", items);
		} catch (Exception exception) {
			logger.error("item list", exception);
			return FeAjaxResponse.error(500, data, "获取商品列表失败！");
		}
		return FeAjaxResponse.success(data, "获取商品列表成功！");
	}
	
	/**
	 * 根据商品Id获取商品信息
	 * 
	 * @param itemId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/item", method = RequestMethod.GET)
	public FeAjaxResponse getItemsByItemId(@RequestParam Long itemId) {
		Map<String, Object> data = null;
		try {
			data = materialService.getItemByItemId(itemId);
		} catch (Exception exception) {
			logger.error("get item", exception);
			return FeAjaxResponse.error(500, data, "获取商品信息失败！");
		}
		return FeAjaxResponse.success(data, "获取商品信息成功！");
	}
	
	/**
	 * 根据商品Id获取商品详细信息
	 * 
	 * @param productId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/product", method = RequestMethod.GET)
	public FeAjaxResponse getItemsByProductId(@RequestParam String productId) {
		Map<String, Object> data = null;
		try {
			data = materialService.getProducts(productId);
		} catch (Exception exception) {
			logger.error("getItemsByProductId error :", exception);
			return FeAjaxResponse.error(500, data, "获取商品信息失败！");
		}
		return FeAjaxResponse.success(data, "获取商品信息成功！");
	}
	
	/**
	 * 查询某投放单元下有效创意个数
	 * 
	 * @param productId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/total", method = RequestMethod.GET)
	public FeAjaxResponse getMaterialTotalByFlightId(@RequestParam Integer flightId) {
		Map<String, Object> data = null;
		try {
			data = materialService.getMaterialTotalByFlightId(flightId);
		} catch (Exception exception) {
			logger.error("getMaterialTotalByFlightId error :", exception);
			return FeAjaxResponse.error(500, data, "获取创意总数失败！");
		}
		return FeAjaxResponse.success(data, "获取创意总数成功！");
	}
	/**
	 * 判断某商品的自营三级类目是否禁投
	 * 
	 * @param productId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/banned", method = RequestMethod.GET)
	public FeAjaxResponse getCategoryBannedStatus(@RequestParam String skuId) {
		Map<String, Object> data = null;
		try {
			data = materialService.getCategoryBannedStatus(skuId);
		} catch (Exception exception) {
			logger.error("getCategoryBannedStatus error :", exception);
			return FeAjaxResponse.error(500, data, "获取三级类目禁投标识失败！");
		}
		return FeAjaxResponse.success(data, "获取三级类目禁投标识成功！");
	}
	
	/**
	 * 根据skuId查sku信息
	 * @param skuId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/sku", method = RequestMethod.GET)
	public FeAjaxResponse getSkuBySkuId(@RequestParam String skuId) {
		Map<String, Object> data = null;
		try {
			data = materialService.getSkuBySkuId(skuId);
		} catch (Exception exception) {
			logger.error("getSkuBySkuId error :", exception);
			return FeAjaxResponse.error(500, data, "获取sku信息失败！");
		}
		return FeAjaxResponse.success(data, "获取sku信息成功！");
	}
	
	/**
	 * 根据skuId查sku信息
	 * @param skuId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/skus", method = RequestMethod.GET)
	public FeAjaxResponse getSkusBySkuIds(@RequestParam List<String> skuIds) {
		Map<String, Object> data = new HashMap<String, Object> ();
		try {
			data.put("skus", materialService.getSkusBySkuIds(skuIds));
		} catch (Exception exception) {
			logger.error("getSkusBySkuIds error :", exception);
			return FeAjaxResponse.error(500, data, "获取sku列表失败！");
		}
		return FeAjaxResponse.success(data, "获取sku列表成功！");
	}
	
	/**
	 * 批量修改创意状态
	 * @param materialsStatusJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/materials/status", method = RequestMethod.PUT)
	public FeAjaxResponse batchUpdateStatus(@RequestBody String materialsStatusJson) {
		Map<String, Object> materialsStatusMap = new HashMap<String, Object>();
		materialsStatusMap = (Map<String, Object>) gson.fromJson(materialsStatusJson,
				materialsStatusMap.getClass());

		try {
			materialService.batchUpdateStatus(materialsStatusMap);
		} catch (Exception exception) {
			logger.error("modify materials status failed [" + exception.getMessage() + "]");
			return FeAjaxResponse.error(400, "修改创意状态失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("修改创意状态成功！");
	}
	
	/**
	 * 批量删除创意
	 * @param materialsStatusJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/materials", method = RequestMethod.DELETE)
	public FeAjaxResponse batchDeleteMaterials(@RequestBody String materialsDeleteJson) {
		Map<String, Object> materialsStatusMap = new HashMap<String, Object>();
		materialsStatusMap = (Map<String, Object>) gson.fromJson(materialsDeleteJson,
				materialsStatusMap.getClass());

		try {
			materialService.batchDeleteMaterials(materialsStatusMap);
		} catch (Exception exception) {
//			logger.error("delete materials failed [" + exception.getMessage() + "]");
			logger.error("delete materials failed ", exception);
			return FeAjaxResponse.error(400, "删除创意失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success("删除创意成功！");
	}
	
	/**
	 * 根据materialId获取创意
	 * 
	 * @param materialId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material", method = RequestMethod.GET)
	public FeAjaxResponse get(@RequestParam Integer materialId) {

		Map<String, Object> data = new HashMap<String, Object>();
		try {

			data = materialService.getMaterial(materialId);

		} catch (Exception exception) {
			logger.error("material", exception);
			return FeAjaxResponse.error(400, "获取创意数据失败！" + exception.getMessage());
		}
		return FeAjaxResponse.success(data, "获取创意数据成功！");
	}
	
	/**
	 * 保存竞价cpc的创意信息
	 * 
	 * @param materialJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/bid", method = RequestMethod.POST)
	public FeAjaxResponse saveMaterials(@RequestBody String materialJson) {
		List<Map<String, Object>> materials = new ArrayList<Map<String, Object>>();
		materials = (List<Map<String, Object>>) JSON.parseObject(materialJson, materials.getClass());
		
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = materialService.saveMaterials(materials);
		} catch (Exception exception) {
			logger.error("saveMaterials failed ", exception);
			return FeAjaxResponse.error(400, "添加竞价cpc创意失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "添加竞价创意成功！");
	}
	
	/**
	 * 根据关键字获取话题名称
	 * 
	 * @param keyword
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/topics", method = RequestMethod.GET)
	public FeAjaxResponse getTopics(@RequestParam String keyword,
			@RequestParam(required = false, defaultValue = "1") Integer pageNum,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = materialService.getTopics(keyword, pageNum, pageSize);
		} catch (Exception exception) {
			logger.error("getTopics failed ", exception);
			return FeAjaxResponse.error(400, "获取话题名称失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取话题名称成功！");
	}
	
	/**
	 * 根据topicId获取话题详情
	 * 
	 * @param topicId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/topic", method = RequestMethod.GET)
	public FeAjaxResponse getTopicDetails(@RequestParam String topicId) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data = materialService.getTopicDetails(topicId);
		} catch (Exception exception) {
			logger.error("getTopicDetails failed ", exception);
			return FeAjaxResponse.error(400, "获取话题详情失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取话题详情成功！");
	}
	
	/**
	 * 推送禁播类目至ads
	 * （用于手动请求 暂设为get请求）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/bannedcategory", method = RequestMethod.GET)
	public FeAjaxResponse pushBannedCategoriesToAds() {
		try {
			materialService.pushBannedCategoriesToAds();
		} catch (Exception exception) {
			logger.error("pushBannedCategoriesToAds failed ", exception);
			return FeAjaxResponse.error(400, "推送禁播类目失败" + exception.getMessage());
		}
		return FeAjaxResponse.success("推送禁播类目成功");
	}
	
	/**
	 * 判断某投放单元下是否存在skuId对应的商品创意
	 * @param skuId (必需)
	 * @param flightId (必需)
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/material/exist", method = RequestMethod.GET)
	public FeAjaxResponse isExistSkuId(@RequestParam(name = "skuId") String skuId, @RequestParam(name = "flightId") Integer flightId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			if (materialService.isExistSkuId(skuId, flightId)) {
				data.put("exist", 1);
			} else {
				data.put("exist", 0);
			}
		} catch (Exception exception) {
			logger.error("materialService.isExistSkuId throw Exception ", exception);
			return FeAjaxResponse.error(500, data, "查询商品创意失败！");
		}
		return FeAjaxResponse.success(data, "查询商品创意成功！");
	}
}