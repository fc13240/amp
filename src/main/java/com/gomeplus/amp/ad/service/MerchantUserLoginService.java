package com.gomeplus.amp.ad.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomeplus.adm.common.api.bs.BsCommonUserOperations;
import com.gomeplus.adm.common.api.bs.BsMerchantUserOperations;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录 service
 * @author DèngBīn
 */
@Service
public class MerchantUserLoginService {

	private static Logger logger = LoggerFactory.getLogger(MerchantUserLoginService.class);

	private final Integer USER_TYPE_MERCHANT = 1;
	private final Integer USER_TYPE_COMMON = 2;
	private final Integer USER_TYPE_AE = 3;

	private String name;

	private String password;

	private Integer userType;

	private Long extUserId;

	private String nickName;

	private String email;

	private String mobile;

	private String facePicUrl;

	private Long registerTime;

	private Integer type;

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Long getExtUserId() {
		return extUserId;
	}

	public String getNickName() {
		return nickName;
	}

	public String getEmail() {
		return email;
	}

	public String getMobile() {
		return mobile;
	}

	public String getFacePicUrl() {
		return facePicUrl;
	}

	public Long getRegisterTime() {
		return registerTime;
	}

	public Integer getType() {
		return type;
	}

	/**
	 * 调用国美+商家会员登录API
	 * @return User类
	 * @throws Exception
	 */
	public void execute() throws Exception {

		HttpResponse result = null;

		if (userType == this.USER_TYPE_COMMON) {
			result = (new BsCommonUserOperations()).login(name, password);
		} else if (userType == this.USER_TYPE_MERCHANT) {
			result = (new BsMerchantUserOperations()).login(name, password);
		} else if (userType == this.USER_TYPE_AE) {
			// 待定...
		}

		try {
			logger.info("==============================");
			// 成功返回数据 映射数据 到 User对象
			String strResponse = EntityUtils.toString(result.getEntity());

			logger.info("get login response: " + strResponse);

			ObjectMapper mapper = new ObjectMapper();

			Map<String,Map> mapResponse = mapper.readValue(strResponse, HashMap.class);
			logger.info("mapResponse: " + mapResponse);

			Map mapUser = (Map)(mapResponse.get("data").get("user"));
			logger.info("mapUser: " + mapUser);

			this.extUserId = Long.valueOf(mapUser.get("id").toString());
			logger.info("extUserId: " + this.extUserId);

			this.mobile = (String) mapUser.get("mobile");
			logger.info("mobile: " + this.mobile);

			//this.email = (String) mapUser.get("email");
			this.email = "";
			this.nickName = (String) mapUser.get("nickname");
			logger.info("nickName: " + this.nickName);

			this.facePicUrl = (String) mapUser.get("facePicUrl");
			logger.info("facePicUrl: " + this.facePicUrl);

			this.registerTime = (Long) mapUser.get("registerTime");
			logger.info("registerTime: " + this.registerTime);

			this.type = (Integer) mapUser.get("xpopRefereeId") > 0 ? 1 : 0;
			logger.info("type: " + this.type);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("login error " + e.getMessage());
			throw new Exception("服务器繁忙(Api-Type-04)");
		}
	}
}
