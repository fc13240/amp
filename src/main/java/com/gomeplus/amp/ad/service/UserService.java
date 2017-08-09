package com.gomeplus.amp.ad.service;

import com.gomeplus.adm.common.exception.ApiException;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.util.FieldUtil;
import com.gomeplus.amp.ad.dao.UserDao;
import com.gomeplus.amp.ad.dao.AccountDao;
import com.gomeplus.amp.ad.dao.AdvertiserDao;
import com.gomeplus.amp.ad.model.User;
import com.gomeplus.amp.ad.model.Account;
import com.gomeplus.amp.ad.model.Advertiser;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 用户 service
 * @author DèngBīn
 */
@Service
@Transactional(readOnly = true)
public class UserService extends BaseService<User, Integer> {

	// 7天
	static final long EXPIRATIONTIME = 604_800_000;
	// JWT密码
	static final String SECRET = "P@ssw02d";

	@Autowired
	private UserDao userDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AdvertiserDao advertiserDao;

	@Override
	public HibernateDao<User, Integer> getEntityDao() {
		return userDao;
	}

	private static final int IS_TEST_DEFAULT = 0;
	private static final long MIN_TEST_TIME = 3600000L;
	private static final long MAX_TEST_TIME = 2145888000000L;

	/**
	 * 初始化创建用户
	 * @param user
	 */
	@Transactional(readOnly = false)
	public User createUser(User user) {
		Long extUserId = user.getExtUserId();

		Date currentTime = new Date();
		User oldUser = userDao.getUniqueBy("extUserId", extUserId);
		if (oldUser != null) {
			oldUser.setLoginName(user.getLoginName());
			oldUser.setNickname(user.getNickname());
			oldUser.setAvatar(user.getAvatar() == null ? "" : user.getAvatar());
			oldUser.setLoginFrom(user.getLoginFrom());
			oldUser.setType(user.getType());
			oldUser.setLastLoginTime(currentTime);
			oldUser.setUpdateTime(currentTime);
			userDao.update(oldUser);
			//return oldUser;
			user = oldUser;
		} else {
			user.setIsTest(IS_TEST_DEFAULT);
			user.setTestStartTime(new Date(MIN_TEST_TIME));
			user.setTestEndTime(new Date(MAX_TEST_TIME));

			user.setLastLoginTime(currentTime);
			user.setCreateTime(currentTime);
			user.setUpdateTime(currentTime);
			userDao.save(user);
		}
		Integer userId = user.getUserId();

		Account adAccount = new Account();
		Account rebateAccount = new Account();

		// 创建广告账户
		List<Account> accounts = accountDao.getBy("userId", userId);
		for (Account account : accounts) {
			if (account.getType() == Account.Type.ADVERT_ACCOUNT.getValue()) {
				adAccount = account;
			} else if (account.getType() == Account.Type.REBATE_ACCOUNT.getValue()) {
				rebateAccount = account;
			}
		}

		Advertiser advertiser = advertiserDao.getUniqueBy("userId", userId);

		user.setAdAccount(adAccount);
		user.setRebateAccount(rebateAccount);
		user.setAdvertiser(advertiser);

		return user;
	}

	/**
	 * 使用 国美+ 商家账号id 获取 User
	 * @param extUserId 国美+ 商家账号id
	 * @return User类
	 */
	public User getUserByExtUserId(Integer extUserId) {
		return userDao.getUniqueBy("extUserId", extUserId);
	}

	/**
	 * 将 国美+ 商家账号的最新信息 更新到 数据库  User旧信息
	 * @param user 国美+ 商家账号最新User
	 * @param oldUser 数据库旧User
	 * @return User类
	 */
	public User updateOldUser(User user, User oldUser) {
		Date operateTime = new Date();
		oldUser.setLastLoginTime(operateTime);

		if (!oldUser.getName().equals(user.getName())) {
			oldUser.setName(user.getName());
		}

		if (!oldUser.getMobile().equals(user.getMobile())) {
			oldUser.setMobile(user.getMobile());
		}

		if (!oldUser.getEmail().equals(user.getEmail())) {
			oldUser.setEmail(user.getEmail());
		}

		if (!oldUser.getName().equals(user.getName())
				|| !oldUser.getMobile().equals(user.getMobile())
				|| !oldUser.getEmail().equals(user.getEmail())) {
			oldUser.setUpdateTime(operateTime);
		}

		return  oldUser;
	}

	/**
	 * 校验用户名和密码
	 * @param name 用户名
	 * @param password 密码
	 * @return
	 */
	public boolean checkParams(String name, String password, String userType) {
		return (this.checkName(name) && this.checkPassword(password) && this.checkUserType(userType));
	}

	/**
	 * 校验用户名
	 * @param name 用户名
	 * @return
	 */
	private boolean checkName(String name) {
		return !(name == null || name.trim().equals(""));
	}

	/**
	 * 校验密码
	 * @param password 密码
	 * @return
	 */
	private boolean checkPassword(String password) {
		return !(password == null || password.trim().equals(""));
	}

	/**
	 * 校验密码
	 * @param userType 用户类型
	 * @return
	 */
	private boolean checkUserType(String userType) {
		return !(userType == null || userType.trim().equals(""))
				&& (userType.trim().equals("1") || userType.trim().equals("2") || userType.trim().equals("3"));
	}

	/**
	 * 获取联系人信息
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getContactInfo() throws Exception {

		Map<String, Object> data = new LinkedHashMap<String, Object>();

		User user = this.get(PrincipalUtil.getUserId());

		if (user != null) {
			data.put("name", user.getName() == null ? "" : user.getName());
			data.put("gender", user.getGender() == null ? "" : user.getGender());
			data.put("mobile", user.getMobile() == null ? "" : user.getMobile());
			data.put("email", user.getEmail() == null ? "" : user.getEmail());
			data.put("address", user.getAddress() == null ? "" : user.getAddress());
		} else {
			throw new Exception("用户不存在!");
		}

		return data;
	}

	/**
	 * 修改联系人信息
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public void saveContactInfo(Map<String, Object> contactMap) throws Exception {

		User oldUser = this.get(PrincipalUtil.getUserId());

		oldUser.setName(FieldUtil.getString("联系人姓名", contactMap.get("name"), FieldUtil.lengthIn(20)));
		oldUser.setGender(FieldUtil.getInteger("联系人称呼", contactMap.get("gender")));
		oldUser.setMobile(FieldUtil.getString("联系人手机号", contactMap.get("mobile"), FieldUtil.lengthIn(20), FieldUtil.REGEX_MOBILE_PHONE));
		oldUser.setEmail(FieldUtil.getString("联系人电子邮箱", contactMap.get("email"), FieldUtil.lengthIn(100), FieldUtil.REGEX_EMAIL));

		// 新建广告主时, 联系人地址为必填字段;
		// 但在修改联系人时, 联系人地址为非必填字段, 所以要先进行非null非空验证;
		if (contactMap.get("address") != null && !StringUtils.isEmpty(contactMap.get("address").toString().trim())) {
			oldUser.setAddress(FieldUtil.getString("联系人地址", contactMap.get("address"), FieldUtil.lengthIn(255)));
		}

		oldUser.setUpdateTime(new Date());

		this.save(oldUser);
	}

	/**
	 * 获取用户信息
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getUserInfo() throws Exception {

		Map<String, Object> data = new LinkedHashMap<String, Object>();

		Integer userId = PrincipalUtil.getUserId();
		User user = userDao.get(userId);

		if (user != null) {

			data.put("userId", user.getUserId());
			data.put("name", user.getName());
			data.put("gender", user.getGender());
			data.put("mobile", user.getMobile());
			data.put("email", user.getEmail());
			data.put("loginName", user.getLoginName());
			data.put("nickname", user.getNickname());
			data.put("avatar", user.getAvatar());

			data.put("isTest", user.getIsTest());
			data.put("testStartTime", user.getTestStartTime().getTime());
			data.put("testEndTime", user.getTestEndTime().getTime());

			data.put("isRegistered", 0);
			data.put("isApproved", 0);
			Advertiser advertiser = advertiserDao.getUniqueBy("userId", userId);
			if (advertiser != null) {
				data.put("isRegistered", 1);
				data.put("isApproved", advertiser.getApproveStatus());
			}

			String name = user.getName();

			// 兼容新老amp版本，刷新时更新token
			String token = Jwts.builder()
				.setId(String.valueOf(userId))
				.setSubject(name)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();

			data.put("token", token);

		} else {
			throw new Exception("用户不存在!");
		}

		return data;
	}
}
