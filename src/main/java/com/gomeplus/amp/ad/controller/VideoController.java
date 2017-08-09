package com.gomeplus.amp.ad.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.service.VideoService;

@Controller
public class VideoController {

	private static Logger logger = LoggerFactory.getLogger(VideoController.class);

	@Autowired
	private VideoService videoService;

	/**
	 * 获取token参数
	 *
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value = "/api/video/token", method = RequestMethod.GET)
	public FeAjaxResponse getToken() {

		Map<String, Object> data = null;
		try {
			data = videoService.getToken();
		} catch (Exception exception) {
			logger.error("get token failed ",exception);
			return FeAjaxResponse.error(400, "获取token参数失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "获取token参数成功！");
	}
	
	/**
	 * 根据视频id拉取视频详情接口
	 *
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value = "/api/video/detail", method = RequestMethod.GET)
	public FeAjaxResponse getVideoDetail(@RequestParam String videoId) {

		Map<String, Object> data = null;
		try {
			data = videoService.getVideoByVideoId(videoId);
		} catch (Exception exception) {
			logger.error("get video detail failed ",exception);
			return FeAjaxResponse.error(400, "拉取视频详情失败！" + exception.getMessage());
		}

		return FeAjaxResponse.success(data, "拉取视频详情成功！");
	}

}
