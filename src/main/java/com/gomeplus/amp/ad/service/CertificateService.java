package com.gomeplus.amp.ad.service;

import com.gomeplus.adm.common.exception.ApiException;
import com.gomeplus.adm.common.exception.FieldException;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.util.FieldUtil;
import com.gomeplus.amp.ad.dao.CertificateDao;
import com.gomeplus.amp.ad.model.Certificate;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家资质 service
 * @author DèngBīn
 */
@Service
@Transactional(readOnly = true)
public class CertificateService extends BaseService<Certificate, Integer> {

	private static final Long ENDLESS = 2145888000000L;

	@Autowired
	CertificateDao certificateDao;

	@Override
	public HibernateDao<Certificate, Integer> getEntityDao() {
		return certificateDao;
	}

	/**
	 * 获取商家资质列表信息
	 * @param keyword
	 * @param page
	 * @param number
	 * @return
	 */
	public Map<String,Object> getCertificates(String keyword, Integer page, Integer number) {

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		List headerList = new ArrayList();
		List dataList = new ArrayList();
		Integer totalCount = 0;

		// 获取 商家资质分页数据
		List<Certificate> certList = certificateDao.getListOfCertWithPagination(PrincipalUtil.getUserId(), keyword, page, number, totalCount);

		// 拼装符合前端的数据格式
		if (certList != null && certList.size() > 0) {
			for (int i = 0; i < certList.size(); i++) {
				List rowDataList = new ArrayList();
				Certificate rowData = certList.get(i);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String endTimeStr = "长期有效";

				if (rowData.getEndTime().getTime() != ENDLESS) {
					endTimeStr = dateFormat.format(rowData.getEndTime());
				}
				String startTimeStr = dateFormat.format(rowData.getStartTime());
				String timeScope = startTimeStr + " / " + endTimeStr;

				rowDataList.add(rowData.getCertificateId());
				rowDataList.add(rowData.getName());

				List imageList = new ArrayList();
				String imagesUrl = rowData.getImages();
				if (!StringUtils.isEmpty(imagesUrl)) {
					imageList = Arrays.asList(imagesUrl.split(","));
				}
				rowDataList.add(imageList);

				rowDataList.add(timeScope);
				rowDataList.add(rowData.getType());

				dataList.add(rowDataList);
			}
		}

		headerList.add("ID");
		headerList.add("资质名称");
		headerList.add("图片");
		headerList.add("有效期");
		headerList.add("操作");
		list.put("header", headerList);
		list.put("data", dataList);

		data.put("page", page);
		data.put("number", number);
		data.put("totalCount", totalCount);
		data.put("list", list);

		return data;
	}

	/**
	 * 保存商家资质信息
	 */
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public void save(Map<String, Object> certMap) throws Exception {

		Date startTime = null;
		Date endTime = null;
		Date nowTime = new Date();
		Certificate cert = new Certificate();

		// 处理开始时间
		Long startTimeLong = FieldUtil.getLong("开始时间", certMap.get("startTime"));
		if (startTimeLong == 0) {
			throw new FieldException("开始时间毫秒数不能为0");
		} else {
			startTime = new Date(startTimeLong);
		}

		// 处理结束时间, 当结束时间为null时, isEndLess设置成1
		if ((Double) certMap.get("endTime") != 0) {
			endTime = FieldUtil.getDate("结束时间", certMap.get("endTime"));
		} else {
			endTime = new Date(ENDLESS);
		}

		// 比较开始时间和结束时间
		if (endTime != null && startTime.getTime() > endTime.getTime()) {
			throw new FieldException("开始时间不能在结束时间之后");
		}

		// 处理上传的图片组, 并转成字符串
		String imagesUrl = "";
		ArrayList<String> imageList = (ArrayList<String>) certMap.get("images");
		if (!CollectionUtils.isEmpty(imageList)) {
			for (String image: imageList) {
				imagesUrl += image + ",";
			}
			imagesUrl = imagesUrl.substring(0, imagesUrl.length() - 1);
		} else {
			throw new FieldException("上传图片不能为空");
		}

		cert.setName(FieldUtil.getString("资质名称", certMap.get("name"), FieldUtil.lengthIn(255)));
		cert.setImages(imagesUrl);
		cert.setStartTime(startTime);
		cert.setEndTime(endTime);
		cert.setType(FieldUtil.getInteger("资质类型", certMap.get("type")));

		cert.setUserId(PrincipalUtil.getUserId());
		cert.setCreateTime(nowTime);
		cert.setUpdateTime(nowTime);

		certificateDao.save(cert);
	}

	/**
	 * 修改商家资质信息
	 */
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public void update(Map<String, Object> certMap) {

		Integer certId = FieldUtil.getInteger("资质id", certMap.get("certificateId"));
		Certificate oldCert = this.get(certId);

		oldCert.setName(FieldUtil.getString("资质名称", certMap.get("name"), FieldUtil.lengthIn(255)));
		oldCert.setUpdateTime(new Date());

		certificateDao.update(oldCert);
	}

	/**
	 * 获取商家资质信息
	 */
	public Map<String,Object> getCertificate(Integer certificateId) {

		Map<String, Object> data = new HashMap<String, Object>();

		Certificate cert= this.get(certificateId);

		// 根据结束时间是否为长期有效, 设置结束时间
		data.put("endTime", cert.getEndTime().getTime() != ENDLESS ?  cert.getEndTime().getTime() : 0);
		data.put("certificateId", cert.getCertificateId());
		data.put("name", cert.getName());
		data.put("startTime", cert.getStartTime().getTime());
		data.put("type", cert.getType());

		List imageList = new ArrayList();
		String imagesUrl = cert.getImages();
		if (!StringUtils.isEmpty(imagesUrl)) {
			imageList = Arrays.asList(imagesUrl.split(","));
		}

		data.put("images", imageList);

		return data;
	}
}
