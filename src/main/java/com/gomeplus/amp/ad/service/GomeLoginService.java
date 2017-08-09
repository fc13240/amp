package com.gomeplus.amp.ad.service;

import com.gome.userCenter.facade.login.IUserLoginFacade;
import com.gome.userCenter.model.RequestParams;
import com.gome.userCenter.model.UserInfo;
import com.gome.userCenter.model.UserLoginResult;
import com.gome.userCenter.model.enu.CompanyNameEnum;
import com.gomeplus.amp.ad.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录 service
 * @author DèngBīn
 */
@Service
public class GomeLoginService {

	private static Logger logger = LoggerFactory.getLogger(GomeLoginService.class);

	private static final String LOGIN_SITE = "amp.gomeplus.com";
	private static final String HOST_NAME = "login.gome.com.cn";
	private static final String INVOKE_FROM = "gomeAmpWeb";
	private static final String HOST_PORT = "80";
	private static final String REMOTE_PORT = "80";

	private String name;

	private String password;

	private Long extUserId;

	private String nickName;

	private String email;

	private String mobile;

	private Long registerTime;

	private Integer type;

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Long getRegisterTime() {
		return registerTime;
	}

	public Integer getType() {
		return type;
	}

	@Autowired
	private IUserLoginFacade userLoginFacade;

	/**
	 * dubbo调用 国美在线 登录
	 * @throws Exception
	 */
	public void execute() throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		// 统一登录系统会为每个公司分配各自的站点
		map.put("loginSite", LOGIN_SITE);
		// 统一登录系统会为每个公司分配自己的公司名称
		map.put("companyName", CompanyNameEnum.gomeOnLine);
		// 是否是授权登录, 是传true, 每个公司只能授权自己的用户, 授权了以后该公司的用户将能在其他渠道登录
		map.put("isAuthorized", true);

		RequestParams requestParams = new RequestParams();

		// 调用渠道, 统一登录系统分配invokeChannel
		requestParams.setInvokeChannel(INVOKE_FROM);
		// 服务调用方ip, 为用户ip
		requestParams.setClientIp(InetAddress.getLocalHost().toString());
		// 服务调用方远程端口号
		requestParams.setRemotePort(REMOTE_PORT);
		// 服务器名称
		requestParams.setHostName(HOST_NAME);
		// 服务器部署占用端口号
		requestParams.setHostPort(HOST_PORT);

		// dubbo调用国美在线登录
		UserLoginResult<UserInfo> result = userLoginFacade.doLogin(name, password, requestParams, map);
		if (result.isSuccess()) {
			logger.info("国美在线登录成功!用户信息: " + result.getBuessObj());
			logger.info("SCN需要调用方保存到cookie: " + result.getExtraInfoMap().get("SCN"));

			UserInfo userInfo = result.getBuessObj();

			this.extUserId = Long.valueOf(userInfo.getId());
			logger.info("extUserId: " + this.extUserId);

			this.mobile = userInfo.getMobile();
			logger.info("mobile: " + this.mobile);

			this.email = "";

			this.nickName = userInfo.getNikename();
			logger.info("nickName: " + this.nickName);

			this.registerTime = userInfo.getRegistrationDate().getTime();
			logger.info("registerTime: " + this.registerTime);

			this.type = User.Type.COMMON.getValue();
			logger.info("type: " + this.type);

		} else {

			logger.error("国美在线登录失败: " + result.getMessage());
			throw new Exception(result.getMessage());
		}

	}
}
