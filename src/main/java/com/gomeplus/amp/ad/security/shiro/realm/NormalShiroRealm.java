package com.gomeplus.amp.ad.security.shiro.realm;

import com.gomeplus.amp.ad.model.User;
import com.gomeplus.amp.ad.model.Account;
import com.gomeplus.amp.ad.model.Advertiser;
import com.gomeplus.amp.ad.security.shiro.model.Principal;
import com.gomeplus.amp.ad.service.GomeLoginService;
import com.gomeplus.amp.ad.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

/**
 * Realm认证
 *
 * @author DèngBīn
 */
public class NormalShiroRealm extends AuthorizingRealm {

	private static Logger logger=LoggerFactory.getLogger(NormalShiroRealm.class);

	@Autowired
	private UserService userService;

	@Autowired
	private GomeLoginService gomeLoginService;

	public NormalShiroRealm() {
		super();
	}

	/**
	 * 是否支持普通用户认证
	 * 通过host判断
	 * @param authcToken 登录token
	 * @return
	 */
	@Override
	public boolean supports(AuthenticationToken authcToken) {

		UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
		String host = token.getHost();

		if (host.equals("normalUser")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 认证回调函数 登录时调用
	 * @param authcToken 登录token
	 * @return
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {

		Principal principal = null;
		UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
		String name = (String) token.getUsername();
		String password = new String((char[]) token.getPassword());

		try {
			gomeLoginService.setName(name);
			gomeLoginService.setPassword(password);
			gomeLoginService.execute();
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage());
		}

		if (gomeLoginService.getExtUserId() == null) {
			throw new AuthenticationException("服务器繁忙");
		}

		User user = new User();
		user.setExtUserId(gomeLoginService.getExtUserId());
		user.setLoginName(name);
		user.setNickname(gomeLoginService.getNickName());
		user.setName(gomeLoginService.getNickName());
		user.setMobile(gomeLoginService.getMobile());
		user.setEmail(gomeLoginService.getEmail());
		user.setRegisterTime(new Date(gomeLoginService.getRegisterTime()));
		user.setType(gomeLoginService.getType());
		user.setLoginFrom(User.Gome.ONLINE.getValue());
		User newUser = userService.createUser(user);

		// @todo 参数检查
		Integer userId = newUser.getUserId();
		Long extUserId = newUser.getExtUserId();

		Account adAccount = newUser.getAdAccount();
		Account rebateAccount = newUser.getRebateAccount();
		Advertiser advertiser = newUser.getAdvertiser();

		Integer adAccountId = (adAccount.getAccountId() == null ? 0 : adAccount.getAccountId());
		Integer dspAdAccountId = (adAccount.getDspAccountId() == null ? 0 : adAccount.getDspAccountId());
		Integer rebateAccountId = (rebateAccount.getAccountId() == null ? 0 : rebateAccount.getAccountId());
		Integer dspRebateAccountId = (rebateAccount.getDspAccountId() == null ? 0 : rebateAccount.getDspAccountId());
		Integer advertiserId = (advertiser == null ? 0 : (advertiser.getAdvertiserId() == null ? 0 : advertiser.getAdvertiserId()));
		Integer dspAdvertiserId = (advertiser == null ? 0 : (advertiser.getDspAdvertiserId() == null ? 0 : advertiser.getDspAdvertiserId()));

		//principal = new Principal(newUser.getUserId(), newUser.getName());

		principal = new Principal(userId, extUserId, name, advertiserId, dspAdvertiserId,
			adAccountId, dspAdAccountId, rebateAccountId, dspRebateAccountId);

		return new SimpleAuthenticationInfo(principal, password, getName());
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 * @param principals 登录对象
	 * @return
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		Principal principal = (Principal) principals.getPrimaryPrincipal();
		Integer userId = principal.getUserId();

		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

		return authorizationInfo;
	}

	/**
	 * update user info in cache
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * clear user info in cache
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}


}
