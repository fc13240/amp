package com.gomeplus.amp.ad.controller;

import com.gomeplus.adm.common.util.DESUtil;
import com.gomeplus.adm.common.web.BaseController;
import com.gomeplus.adm.common.web.FeAjaxResponse;
import com.gomeplus.amp.ad.security.captcha.CaptchaUtil;
import com.gomeplus.amp.ad.service.ReportService;
import com.gomeplus.amp.ad.service.UserService;
import com.gomeplus.amp.ad.service.AccountService;
import com.gomeplus.amp.ad.service.MessageService;
import com.gomeplus.amp.ad.security.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 首页 Controller
 * @author DèngBīn
 */
@Controller
public class MainController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MainController.class);

	private static final int LIMITED_FAIL_LOGIN_TIMES = 3;

	private static final int SECONDS_OF_FIVE_MINUTES = 5 * 60;

	@Autowired
	private UserService userService;

	@Autowired
	private ReportService reportService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private MessageService messageService;

	private Gson gson = new Gson();

	/**
	 * 后台管理首页
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {

		String environment = System.getenv().get("ENVIRONMENT");
		if (environment == null) {
			environment = "development";
		}
		String indexPath = environment + "/index";

		logger.info("index path is " + indexPath);

		return indexPath;
	}

	/**
	 * 获取图形验证码
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/captcha", method = RequestMethod.GET)
	public FeAjaxResponse getCaptcha(@CookieValue(name = "amptoken", required = false) String ampToken,
									 HttpServletResponse response) {

		// 初始化 数据容器 和 常量名称(前缀+用户名)
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		String token;

		// 判断cookie是否已经生成token，已经有token则在新生成验证码的同时不需要再次生成
		if (StringUtils.isEmpty(ampToken)) {

			token = UUID.randomUUID().toString();
			// 生成token的同时放入cookie
			Cookie tokenCookie = new Cookie("amptoken", token);
			tokenCookie.setMaxAge(300);
			response.addCookie(tokenCookie);
			logger.info("ampToken is " + token);

		} else {
			token = ampToken;
		}

		// 验证码在redis里面对应的key
		String captchaKey = RedisUtil.CAPTCHA_KEY + token;

		//生成随机字串
		String captcha = CaptchaUtil.generateCaptcha(4);

		try {
			// @todo 宽高通过配置获取
			Integer width = 89;
			Integer height = 36;
			// 生产图片字节流, 并转换成base64格式, 再存入数据容器给前端
			String imageSrcWithBase64 = CaptchaUtil.outputImage(width, height, captcha);
			if (!StringUtils.isEmpty(imageSrcWithBase64.trim())) {
				data.put("image", "data:image/jpg;base64," + imageSrcWithBase64);

				// 成功后, 将验证码 以(前缀+用户名)为key 存入 redis
				RedisUtil.setex(captchaKey, SECONDS_OF_FIVE_MINUTES, captcha);
			}
		} catch (Exception e) {
			// 失败后, 将验证码 以(前缀+用户名)为key 删除
			RedisUtil.del(captchaKey);

			logger.error("获取图形验证码失败! 原因如下:" + e);
			return FeAjaxResponse.error(400, "获取图形验证码失败");
		}

		return FeAjaxResponse.success(data, "获取图形验证码成功");
	}

	/**
	 * 登录操作
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
	public FeAjaxResponse login(@RequestBody String loginJson, @CookieValue(name = "amptoken", required = false)
			String ampToken) {

		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> loginMap = new HashMap<String, Object>();

		// 没有cookieToken无法比对验证码，直接返回错误
		if (StringUtils.isEmpty(ampToken)) {
			return FeAjaxResponse.error(409, data, "验证码错误");
		}

		// 获取Web参数, 并校验
		loginMap = (Map<String, Object>) gson.fromJson(loginJson, loginMap.getClass());
		try {
			// @todo cookie获取验证码
			// @todo 错误三次以上需要验证码
			String captcha = (String) loginMap.get("captcha");
			String captchaKey = RedisUtil.CAPTCHA_KEY + ampToken;

			if (ampToken.length() != 36 || captcha.length() != 4) {
				return FeAjaxResponse.error(409, data, "验证码错误");
			}
			String realCaptcha = RedisUtil.get(captchaKey);

			if (!realCaptcha.equals(captcha.toUpperCase())) {
				return FeAjaxResponse.error(409, data, "验证码错误");
			}
		} catch (Exception e) {
			logger.error("check captcha error: " + e.getMessage());
			return FeAjaxResponse.error(409, data, "验证码错误");
		}

		Subject subject = SecurityUtils.getSubject();
		try {
			String name = (String) loginMap.get("name");
			String password = (String) loginMap.get("password");
			// @todo 移除userType
			Integer userType = 2;
			String host = "normalUser";

			UsernamePasswordToken token = new UsernamePasswordToken(name, password, host);
			token.setRememberMe(true);
			subject.login(token);
			data = userService.getUserInfo();
		} catch (AuthenticationException ae) {
			logger.error("login error: " + ae.getMessage());
			return FeAjaxResponse.error(400, data, "用户名或密码错误");
		} catch (Exception e) {
			logger.error("login error: " + e.getMessage());
			return FeAjaxResponse.error(400, data, "用户名或密码错误");
		}

		return FeAjaxResponse.success(data, "登录成功");
	}

	/**
	 * pop跳转登录回调
	 * @param des
	 * @return
	 */
	@RequestMapping(value = { "/callback" })
	public String callback(@RequestParam String des) {
		logger.info("callback des解密前: " + des);

		// 校验回调传来的加密字符串
		if (StringUtils.isEmpty(des)) {
			logger.error("login error: des加密字符串为空或null");
			return "redirect:/";
		}

		try {
			logger.info("callback des解密后: " + new DESUtil().decode(des));

			// 解密前: N4ePmBMP+fUbZ39ASNAH8h2BBY6ZyyCW3v4Pc8qtCFwWHQSKjVx+HA==
			// 解密后: 13633|4461|gm_nnn|1487839059|张三疯
			String[] arrUserInfo = new DESUtil().decode(des).split("\\|");

			if (arrUserInfo == null || arrUserInfo.length < 5) {
				logger.error("login error: 解密后的字符串格式有误");
				return "redirect:/";
			}

			// 校验解密后的shopId和userId
			Integer shopId = Integer.valueOf(arrUserInfo[0]);
			Integer userId = Integer.valueOf(arrUserInfo[1]);

			// 校验解密后的userName
			String userName = arrUserInfo[2];
			if (StringUtils.isEmpty(userName)) {
				throw new Exception("解密后的userName不能为空或null");
			}

			String password = des;
			String host = "popUser";

			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(userName, password, host);
			token.setRememberMe(true);
			subject.login(token);

		} catch (NumberFormatException e) {
			logger.error("login error: 解密后的shopId或userId不为整数");
			return "redirect:/";
		} catch (AuthenticationException e) {
			logger.error("login error: " + e.getMessage());
			return "redirect:/";
		} catch (Exception e) {
			logger.error("login error: " + e.getMessage());
			return "redirect:/";
		}

		return "redirect:/";
	}

	/**
	 * 退出账号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/logout", method = RequestMethod.POST)
	public FeAjaxResponse logout() {

		SecurityUtils.getSubject().logout();

		return FeAjaxResponse.success("退出成功");
	}

	/**
	 * 未登录
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/error/401", method = RequestMethod.GET)
	public FeAjaxResponse unauthorized() {

		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			return FeAjaxResponse.error(401, "未登录");
		}

		return FeAjaxResponse.success("已登录");
	}

	/**
	 * 获取首页图表数据(调用了dmp接口)
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/home/report")
	public FeAjaxResponse report(Long starttime, Long endtime) {

		try {
			Map<String, Object> data = reportService.getReportsByAdvertiserId(starttime, endtime);

			if (data.get("error") != null) {
				return FeAjaxResponse.error(400, (String) data.get("error"));
			}

			return FeAjaxResponse.success(data, "查询成功");
		} catch (InvalidSessionException e) {
			SecurityUtils.getSubject().logout();
			logger.error("获取首页图表数据失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(401, "身份过期,请重新登录");
		} catch (Exception e) {
			logger.error("获取首页图表数据失败! 原因如下:" + e.getMessage());
			return FeAjaxResponse.error(400, "获取数据失败!请稍后再试!");
		}

	}

	/**
	 * 获取广告账户,返利账户,站内信数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/api/home/summary")
	public FeAjaxResponse summary() {

		// 最近3条消息
		Integer number = 3;
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		try {
			Map<String, Object> basicAccount = accountService.getBasicAccount();
			List<Map<String, Object>> messages = messageService.getLatestMessages(number);

			Map<String, Object> adAccount = new LinkedHashMap<String, Object>();
			adAccount.put("balance", basicAccount.get("adBalance"));
			adAccount.put("todayCost", basicAccount.get("adTodayCost"));

			Map<String, Object> rebateAccount = new LinkedHashMap<String, Object>();
			rebateAccount.put("balance", basicAccount.get("rebateBalance"));
			rebateAccount.put("todayCost", basicAccount.get("rebateTodayCost"));

			data.put("adAccount", adAccount);
			data.put("rebateAccount", rebateAccount);
			data.put("messages", messages);
		} catch (Exception e) {
			logger.error("get home summary failed " + e.getMessage());
			return FeAjaxResponse.error(400, "获取首页基础信息失败");
		}

		return FeAjaxResponse.success(data, "查询成功");
	}
}