package com.gomeplus.amp.ad.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.gomeplus.adm.common.api.AbstractOperations;
import com.gomeplus.adm.common.util.PropertiesUtil;
import com.gomeplus.amp.ad.dao.VideoDao;
import com.gomeplus.amp.ad.model.Video;
import com.gomeplus.amp.ad.util.TokenUtil;

@Service
public class VideoService {
	
	@Autowired
	VideoDao videoDao;

	//资源文件路径
	private static final String TOKEN_PATH = "/" + PropertiesUtil.getEnvironment() + "/token.properties";
	
	// 配置属性
	public static final Properties properties;
	
	private static final Logger logger = Logger.getLogger(VideoService.class);
	
	static {
		try {
			logger.info("TOKEN PATH is " + TOKEN_PATH);
			ClassPathResource resource = new ClassPathResource(TOKEN_PATH, AbstractOperations.class);
			properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load 'api.properties': " + ex.getMessage());
		}
	}
	/**
	 * 返回token信息
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getToken() throws Exception {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		
		String appname = properties.getProperty("appname");
		String appKey = properties.getProperty("appKey");
		String env = properties.getProperty("env");
		String token = TokenUtil.createToken(appname, appKey);
		
		data.put("appname", appname);
		data.put("token", token);
		data.put("env", env);
		return data;
	}
	
	/**
	 * 拉取视频详情接口
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getVideoByVideoId(String videoId) throws Exception {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		
		Video video = videoDao.getVideoByVideoId(Long.parseLong(videoId));
		if (null != video) {
			data.put("videoId", video.getVideoId());
			data.put("title", video.getTitle());
			data.put("fileName", video.getFileName());
			data.put("description", video.getDescription());
			data.put("image", video.getImage());
			data.put("length", video.getLength());
			data.put("ratio", video.getRatio());
			data.put("hash", video.getHashcolumn());
			data.put("appname", video.getAppname());
			data.put("convertStatus", video.getConvertStatus());
			data.put("distributeStatus", video.getDistributeStatus());
			data.put("approveStatus", video.getApproveStatus());
			data.put("isApprove", video.getIsApprove());
			data.put("isPublished", video.getIsPublished());
			data.put("ip", video.getIp());
			data.put("userId", video.getUserId());
			data.put("pageStatus", video.getPageStatus());
			data.put("status", video.getStatus());
			data.put("uploadTime", video.getUploadTime());
			data.put("createTime", video.getCreateTime());
			data.put("updateTime", video.getUpdateTime());
		}
		return data;
	}
	
}
