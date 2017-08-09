package com.gomeplus.amp.ad.service;

import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.util.FieldUtil;
import com.gomeplus.amp.ad.dao.PageTemplateDao;
import com.gomeplus.amp.ad.model.PageTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面模板 service
 * @author DèngBīn
 */
@Service
@Transactional(readOnly = true)
public class PageTemplateService extends BaseService<PageTemplate, Integer> {

	@Autowired
	private PageTemplateDao pageTemplateDao;

	@Override
	public HibernateDao<PageTemplate, Integer> getEntityDao() {
		return pageTemplateDao;
	}

	/**
	 * 获取页面模板列表
	 * @param platform 平台
	 * @return
	 */
	public Map<String,Object> getPageTemplates(Integer platform) {
		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		List dataList = new ArrayList();

		// 获取页面模板列表数据
		List<PageTemplate> pageTemplateList = pageTemplateDao.getListOfPageTemplate(platform);

		// 拼装传给前端的格式
		if (!ObjectUtils.isEmpty(pageTemplateList)) {

			for (PageTemplate pageTemplate: pageTemplateList) {

				Map<String, Object> rowData = new LinkedHashMap<String, Object>();
				rowData.put("pageTemplateId", pageTemplate.getPageTemplateId());
				rowData.put("pageTemplateTitle", pageTemplate.getTitle());
				rowData.put("pageTemplateDesc", pageTemplate.getDescription());
				rowData.put("preview", pageTemplate.getPreview());

				dataList.add(rowData);
			}
		}

		data.put("list", dataList);

		return data;
	}
}
