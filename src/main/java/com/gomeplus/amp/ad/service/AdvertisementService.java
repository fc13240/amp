package com.gomeplus.amp.ad.service;

import java.util.List;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.web.Pagination;

import com.gomeplus.amp.ad.dao.AdvertisementDao;
import com.gomeplus.amp.ad.dao.BidDao;
import com.gomeplus.amp.ad.dao.PublisherDao;
import com.gomeplus.amp.ad.dao.SlotDao;
import com.gomeplus.amp.ad.model.Advertisement;
import com.gomeplus.amp.ad.model.Bid;
import com.gomeplus.amp.ad.model.FlightAdvertisement;
import com.gomeplus.amp.ad.model.Publisher;
import com.gomeplus.amp.ad.model.Slot;

/**
 * 广告service
 * 
 * @author wangwei01
 */
@Service
@Transactional(readOnly = true)
public class AdvertisementService extends BaseService<Advertisement, Integer> {

	@Autowired
	private AdvertisementDao advertisementDao;

	@Autowired
	private PublisherDao publisherDao;

	@Autowired
	private SlotDao slotDao;
	@Autowired
	private BidDao bidDao;
	@Override
	public HibernateDao<Advertisement, Integer> getEntityDao() {
		return advertisementDao;
	}

	/**
	 * 查询某平台的所有媒体
	 * @param platform
	 * 
	 * @return
	 */
	public Map<String, Object> getOnlinePublishers(Integer platform) throws Exception {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<Publisher> publishers = new ArrayList<Publisher>();
		if (0 != platform) {
			publishers = publisherDao.getPublishersByPlatform(platform);
		} else {
			publishers = publisherDao.getOnlinePublishers();
		}

		List<Map<String, Object>> publishersList = new ArrayList<Map<String, Object>>();
		if (!CollectionUtils.isEmpty(publishers)) {
			for (Publisher publisher : publishers) {
				Map<String, Object> publisherMap = new LinkedHashMap<String, Object>();
				publisherMap.put("publisherId", publisher.getPublisherId());
				publisherMap.put("name", publisher.getName());
				publisherMap.put("platform", publisher.getPlatform());
				publishersList.add(publisherMap);
			}
		}
		data.put("list", publishersList);

		return data;
	}

	/**
	 * 创意尺寸列表
	 * @param platform
	 * 
	 * @return
	 */
	public Map<String, Object> getSizes(Integer platform) throws Exception {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Set<Map<String, Integer>> sizes = new HashSet<Map<String, Integer>>();
		data.put("sizes", sizes);

		List<Advertisement> advertisements = new ArrayList<Advertisement>();

		if (platform == 0) {
			advertisements = advertisementDao.getOnlineAdvertisements();
		} else {
			List<Publisher> publishers = publisherDao.getPublishersByPlatform(platform);
			if (CollectionUtils.isEmpty(publishers)) {
				return data;
			}
			List<Integer> publisherIds = new ArrayList<Integer>();
			for (Publisher publisher : publishers) {
				publisherIds.add(publisher.getPublisherId());
			}
			List<Slot> slots = slotDao.getSlotsByPublisherIds(publisherIds);
			if (CollectionUtils.isEmpty(slots)) {
				return data;
			}
			List<Integer> slotIds = new ArrayList<Integer>();
			for (Slot slot : slots) {
				slotIds.add(slot.getSlotId());
			}
			advertisements = advertisementDao.getAdvertisementsBySlotIds(slotIds);
		}

		for (Advertisement advertisement : advertisements) {
			Map<String, Integer> sizeMap = new LinkedHashMap<String, Integer>();
			sizeMap.put("width", advertisement.getWidth());
			sizeMap.put("height", advertisement.getHeight());
			sizes.add(sizeMap);
		}
		data.put("sizes", sizes);

		return data;
	}

	/**
	 * 获取广告
	 * @param pagination
	 * @param keyword
	 * @param publisherId
	 * @param platform
	 * @param width
	 * @param height
	 * 
	 * @return
	 */
	public Map<String, Object> getAdvertisements(Pagination pagination, String keyword,
								Integer publisherId, Integer platform, Integer width, Integer height, Integer saleMode) throws Exception {

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<Map<String, Object>> advertisementsList = new ArrayList<Map<String, Object>>();
		data.put("number", 0);
		data.put("page", 1);
		data.put("totalCount", 0);
		data.put("list", advertisementsList);

		List<Integer> slotIds = new ArrayList<Integer>();
		List<Slot> slots = new ArrayList<Slot>();
		if (publisherId != 0) {
			slots = slotDao.getSlotsByPublisherId(publisherId);
			if (CollectionUtils.isEmpty(slots)) {
				return data;
			}
			for (Slot slot : slots) {
				slotIds.add(slot.getSlotId());
			}
		} else if (platform != 0) {
			List<Publisher> publishers = publisherDao.getPublishersByPlatform(platform);
			if (CollectionUtils.isEmpty(publishers)) {
				return data;
			}
			List<Integer> publisherIds = new ArrayList<Integer>();
			for (Publisher publisher : publishers) {
				publisherIds.add(publisher.getPublisherId());
			}
			slots = slotDao.getSlotsByPublisherIds(publisherIds);
			if (CollectionUtils.isEmpty(slots)) {
				return data;
			}
			for (Slot slot : slots) {
				slotIds.add(slot.getSlotId());
			}
		}

		List<Advertisement> advertisements = advertisementDao.getAdvertisementsByPagination(pagination, keyword, slotIds, width, height, saleMode);

		// 获取广告位
		if (CollectionUtils.isEmpty(slotIds)) {
			for (Advertisement advertisement : advertisements) {
				slotIds.add(advertisement.getSlotId());
			}
			slots = slotDao.getSlotsBySlotIds(slotIds);
		}
		Map<Integer, Slot> slotsMap = new LinkedHashMap<Integer, Slot>();
		List<Integer> selectedPublisherIds = new ArrayList<Integer>();
		for (Slot slot : slots) {
			selectedPublisherIds.add(slot.getPublisherId());
			slotsMap.put(slot.getSlotId(), slot);
		}

		// 获取媒体
		Map<Integer, Publisher> publishersMap = new LinkedHashMap<Integer, Publisher>();
		if (!CollectionUtils.isEmpty(selectedPublisherIds)) {
			List<Publisher> selectedPublishers = publisherDao.getPublishersByPublisherIds(selectedPublisherIds);
			for (Publisher publisher : selectedPublishers) {
				publishersMap.put(publisher.getPublisherId(), publisher);
			}
		}

		// 构造数据
		for (Advertisement advertisement : advertisements) {
			//获取当前广告的出价信息
			Bid bid = bidDao.getBidByAdvertisementId(advertisement.getAdvertisementId());
			BigInteger cpcBid = BigInteger.ZERO;
			if(null != bid){
				cpcBid = bid.getCpcBid();
			}
			Map<String, Object> advertisementMap = new LinkedHashMap<String, Object>();

			advertisementMap.put("advertisementId", advertisement.getAdvertisementId());
			advertisementMap.put("advertisementName", advertisement.getName());
			advertisementMap.put("generalizeType", advertisement.getGeneralizeType());
			advertisementMap.put("webpageTemplateId", advertisement.getWebpageTemplateId());
			Slot slot = slotsMap.get(advertisement.getSlotId());
			if (null != slot) {
				Integer currentPublisherId = slot.getPublisherId();
				Publisher publisher = publishersMap.get(currentPublisherId);
				if (null != publisher) {
					advertisementMap.put("publisherId", publisher.getPublisherId());
					advertisementMap.put("publisherName", publisher.getName());
				} else {
					advertisementMap.put("publisherId", 0);
					advertisementMap.put("publisherName", "");
				}
				advertisementMap.put("productType", slot.getProductType());
			}

			advertisementMap.put("width", advertisement.getWidth());
			advertisementMap.put("height", advertisement.getHeight());
			advertisementMap.put("adBid", cpcBid);
			// @todo get size
			advertisementMap.put("size", "256KB");

			advertisementsList.add(advertisementMap);
		}

		data.put("totalCount", pagination.getTotalCount());
		data.put("page", pagination.getCurrentPage());
		data.put("number", pagination.getNumber());
		data.put("list", advertisementsList);

		return data;
	}
}
