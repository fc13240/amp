package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.util.FileUtil;
import com.gomeplus.adm.common.util.RandomStringUtil;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * 图片 controller
 * 
 * @author wangwei01
 */
@Controller
public class ImageController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ImageController.class);

	private Gson gson = new Gson();

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
	@RequestMapping(value = "/api/image/upload", method = RequestMethod.POST)
	public FeAjaxResponse upload(HttpServletRequest request, @RequestParam(value="file", required=true) MultipartFile file) {
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
				imageUrl = FileUtil.sendFileToNewCDN(image, file.getOriginalFilename());
                //imageUrl = FileUtil.sendFileToCDN(size, image);
				// 将生成的临时文件删除
				image.delete();

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
}
