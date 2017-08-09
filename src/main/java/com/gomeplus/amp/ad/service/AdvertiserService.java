package com.gomeplus.amp.ad.service;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.bs.BsMerchantInfoOperations;
import com.gomeplus.adm.common.exception.ApiException;
import com.gomeplus.adm.common.exception.FieldException;
import com.gomeplus.adm.common.util.FieldUtil;
import com.gomeplus.amp.ad.dao.AdvertiserDao;
import com.gomeplus.amp.ad.dao.AccountDao;
import com.gomeplus.amp.ad.dao.UserDao;
import com.gomeplus.amp.ad.model.Advertiser;
import com.gomeplus.amp.ad.model.User;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.gomeplus.adm.common.web.Pagination;

/**
 * 广告主 service
 * @author DèngBīn
 */
@Service
@Transactional(readOnly = true)
public class AdvertiserService extends BaseService<Advertiser, Integer> {
	@Autowired
	private AdvertiserDao advertiserDao;
	@Autowired
	private AccountDao accountDao;
	private Gson gson = new Gson();
	
	@Override
	public HibernateDao<Advertiser, Integer> getEntityDao() {
		return advertiserDao;
	}

	@Autowired
	UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(AdvertiserService.class);
	private static final String BLANK_FIELD = "未填写";
	private static final String BLANK_IMAGE = "https://i7.meixincdn.com/v1/img/T1RwJTBmAg1R4cSCrK.png";
	private static final String DEFAULT_PHONE = "010-88888888";
	private static final String DEFAULT_MOBILE = "18888888888";
	private static final String DEFAULT_EMAIL = "example@example.com";

	// 默认长期有效的毫秒值 2701584429_10447943080598
	private static final String DEDAULT_MIN_MILLISECOND = "2701584429";
	private static final String DEDAULT_MAX_MILLISECOND = "10447943080598";

	/**
	 * 按照用户id查找一一对应的广告主
	 * @return
	 */
	public Advertiser getAdvertiserByUserId(){
		return advertiserDao.getUniqueBy("userId", PrincipalUtil.getUserId());
	}

	/**
	 * 新建或更新广告主
	 */
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public void save(Map<String, Object> advertiserMap) throws Exception {

		Date operateTime = new Date();

		Integer userId = PrincipalUtil.getUserId();
		if (userId == null) {
			throw new Exception("用户信息获取失败");
		}

		Advertiser oldAdvertiser = advertiserDao.getUniqueBy("userId", userId);
		if (ObjectUtils.isEmpty(oldAdvertiser)) {
			// 新建广告主
			Advertiser advertiser = new Advertiser();
			this.checkAndSetAdvertiser(advertiserMap, advertiser);

			advertiser.setUserId(PrincipalUtil.getUserId());
			advertiser.setApproveStatus(Advertiser.ApproveStatus.PENDING.getValue());
			advertiser.setStatus(Advertiser.Status.ONLINE.getValue());
			advertiser.setUpdateTime(operateTime);
			advertiser.setCreateTime(operateTime);

			advertiserDao.save(advertiser);
		} else {
			// 修改广告主
			this.checkAndSetAdvertiser(advertiserMap, oldAdvertiser);

			// 修改审核状态(未通过 -> 待审核)
			oldAdvertiser.setApproveStatus(Advertiser.ApproveStatus.PENDING.getValue());
			oldAdvertiser.setUpdateTime(operateTime);

			advertiserDao.update(oldAdvertiser);
		}

		// 修改用户信息
		User oldUser = userDao.get(PrincipalUtil.getUserId());

		oldUser.setName(FieldUtil.getString("联系人姓名", advertiserMap.get("contactName"), FieldUtil.lengthIn(20)));
		oldUser.setGender(FieldUtil.getInteger("联系人称呼", advertiserMap.get("contactSex")));
		oldUser.setMobile(FieldUtil.getString("联系人手机号", advertiserMap.get("contactMobile"), FieldUtil.lengthIn(20), FieldUtil.REGEX_MOBILE_PHONE));
		oldUser.setEmail(FieldUtil.getString("联系人电子邮箱", advertiserMap.get("contactEmail"), FieldUtil.lengthIn(100), FieldUtil.REGEX_EMAIL));
		oldUser.setAddress(FieldUtil.getString("联系人地址", advertiserMap.get("contactAddress"), FieldUtil.lengthIn(255)));
		oldUser.setUpdateTime(operateTime);

		userDao.save(oldUser);
	}

	private void checkAndSetAdvertiser(Map<String, Object> advertiserMap, Advertiser advertiser) {

		// 营业执照信息 相关字段
		advertiser.setCompanyNature(FieldUtil.getInteger("公司性质", advertiserMap.get("companyNature")));
		advertiser.setCompanyName(FieldUtil.getString("公司名称", advertiserMap.get("companyName"), FieldUtil.lengthIn(255)));
		advertiser.setBizLicenseNumber(FieldUtil.getString("注册号（营业执照号）", advertiserMap.get("bizLicenseNumber"), FieldUtil.lengthIn(255)));
		advertiser.setLegalReprName(FieldUtil.getString("法人代表姓名", advertiserMap.get("legalReprName"), FieldUtil.lengthIn(100)));
		advertiser.setLegalReprIdNumber(FieldUtil.getString("法人代表身份证号", advertiserMap.get("legalReprIdNumber"), FieldUtil.lengthIn(18)));
		advertiser.setLegalReprIdImage(FieldUtil.getString("法人身份证电子版(正面)", advertiserMap.get("legalReprIdImage"), FieldUtil.lengthIn(255)));
		advertiser.setLegalReprIdBackImage(FieldUtil.getString("法人身份证电子版(背面)", advertiserMap.get("legalReprIdBackImage"), FieldUtil.lengthIn(255)));
		advertiser.setBizLicenseAddress(FieldUtil.getString("营业执照详细地址", advertiserMap.get("bizLicenseAddress"), FieldUtil.lengthIn(255)));
		advertiser.setCompanyFoundDate(FieldUtil.getDate("成立日期", advertiserMap.get("companyFoundDate")));

		Date bizStartDate = FieldUtil.getDate("营业开始日期", advertiserMap.get("bizStartDate"));
		Date bizEndDate = FieldUtil.getDate("营业结束日期", advertiserMap.get("bizEndDate"));
		if (bizStartDate.getTime() > bizEndDate.getTime()) {
			throw new FieldException("营业开始日期不能大于结束日期");
		}
		advertiser.setBizStartDate(bizStartDate);
		advertiser.setBizEndDate(bizEndDate);

		advertiser.setRegCapital(FieldUtil.getString("注册资本", advertiserMap.get("regCapital"), FieldUtil.lengthIn(20)));
		advertiser.setBizScope(FieldUtil.getString("经营范围", advertiserMap.get("bizScope"), FieldUtil.lengthIn(255)));
		advertiser.setBizLicenseImage(FieldUtil.getString("营业执照副本电子版", advertiserMap.get("bizLicenseImage"), FieldUtil.lengthIn(255)));
		advertiser.setCompanyAddress(FieldUtil.getString("公司详细地址", advertiserMap.get("companyAddress"), FieldUtil.lengthIn(255)));

		advertiser.setCompanyPhone(FieldUtil.getString("公司电话", advertiserMap.get("companyPhone"), FieldUtil.lengthIn(20)));
		if (advertiser.getCompanyPhone().trim().equals(DEFAULT_PHONE)) {
			advertiser.setCompanyPhone(BLANK_FIELD);
		}

		advertiser.setEmerContact(FieldUtil.getString("公司紧急联系人", advertiserMap.get("emerContact"), FieldUtil.lengthIn(20)));

		advertiser.setEmerContactPhone(FieldUtil.getString("公司紧急联系人手机号", advertiserMap.get("emerContactPhone"), FieldUtil.lengthIn(20), FieldUtil.REGEX_MOBILE_PHONE));
		if (advertiser.getEmerContactPhone().trim().equals(DEFAULT_MOBILE)) {
			advertiser.setEmerContactPhone(BLANK_FIELD);
		}

		advertiser.setCompanyEmail(FieldUtil.getString("公司邮箱", advertiserMap.get("companyEmail"), FieldUtil.lengthIn(100), FieldUtil.REGEX_EMAIL));
		if (advertiser.getCompanyEmail().trim().equals(DEFAULT_EMAIL)) {
			advertiser.setCompanyEmail(BLANK_FIELD);
		}

		// 组织机构代码证 相关字段
		advertiser.setOrgCode(FieldUtil.getString("组织机构代码", advertiserMap.get("orgCode"), FieldUtil.lengthIn(100)));
		advertiser.setOrgCodeImage(FieldUtil.getString("组织机构代码副本电子版", advertiserMap.get("orgCodeImage"), FieldUtil.lengthIn(255)));

		// 税务登记证 相关字段
		advertiser.setTaxNumber(FieldUtil.getString("税号", advertiserMap.get("taxNumber"), FieldUtil.lengthIn(100)));
		advertiser.setTaxpayerType(FieldUtil.getInteger("纳税人类型", advertiserMap.get("taxpayerType")));
		advertiser.setTaxCode(FieldUtil.getInteger("纳税类型税码", advertiserMap.get("taxCode")));
		advertiser.setTaxRegCertImage(FieldUtil.getString("税务登记证电子版", advertiserMap.get("taxRegCertImage"), FieldUtil.lengthIn(255)));
		advertiser.setTaxpayerQualificationImage(FieldUtil.getString("一般纳税人资格电子版", advertiserMap.get("taxpayerQualificationImage"), FieldUtil.lengthIn(255)));

		// 开户银行许可证
		advertiser.setBankAccountName(FieldUtil.getString("银行开户名", advertiserMap.get("bankAccountName"), FieldUtil.lengthIn(255)));
		advertiser.setBankAccount(FieldUtil.getString("公司银行账号", advertiserMap.get("bankAccount"), FieldUtil.lengthIn(20)));
		advertiser.setBankBranchName(FieldUtil.getString("开户银行支行名称", advertiserMap.get("bankBranchName"), FieldUtil.lengthIn(255)));
		advertiser.setBankBranchLineNumber(FieldUtil.getString("开户银行支行联行号", advertiserMap.get("bankBranchLineNumber"), FieldUtil.lengthIn(50)));
		advertiser.setBankBranchAddress(FieldUtil.getString("开户银行支行地址", advertiserMap.get("bankBranchAddress"), FieldUtil.lengthIn(255)));
		advertiser.setBankAccountPermissionImage(FieldUtil.getString("银行开户许可证电子版", advertiserMap.get("bankAccountPermissionImage"), FieldUtil.lengthIn(255)));
	}

	/**
	 * 获取入驻商户信息
	 * @return
	 */
	public Map<String, Object> getSettledMerchantInfo() throws Exception {
		
		// 数据容器初始化
		Map<String, Object> originalData = new HashMap<String, Object>();
		Map<String, Object> wrappedData = new HashMap<String, Object>();
		ApiResponse merchantInfo = null;

		// 获取 user
		User user = userDao.get(PrincipalUtil.getUserId());

		// 调用 bs 入驻商家 接口, 获取 入驻商家 信息
		BsMerchantInfoOperations merchantInfoOperations = new BsMerchantInfoOperations();

		// 校验是否是商家用户
		if (user.getType() != 1) {
			throw new Exception("不是商家用户");
		}

		// 校验shopId
		if (!ObjectUtils.isEmpty(user.getExtShopId()) && user.getExtShopId() != 0) {
			merchantInfo = merchantInfoOperations.getMerchantInfoByShopId(user.getExtShopId());
		} else {
			throw new Exception("查找不到店铺信息");
		}

		// 包装数据给前端
		if (merchantInfo != null && merchantInfo.getMessage().equals("") && !ObjectUtils.isEmpty(merchantInfo.getData())) {

			originalData = merchantInfo.getData();

			// 公司名称
			String corpName = originalData.get("corpName").toString();
			wrappedData.put("corpName", corpName.equals("") ? BLANK_FIELD : corpName);

			// 注册号
			String corpLicenseNumber = originalData.get("corpLicenseNumber").toString();
			wrappedData.put("corpLicenseNumber", corpLicenseNumber.equals("") ? BLANK_FIELD : corpLicenseNumber);

			// 法人代表姓名
			String legalPerson = originalData.get("legalPerson").toString();
			wrappedData.put("legalPerson", legalPerson.equals("") ? BLANK_FIELD : legalPerson);

			// 法人代表身份证号
			String identityCard = originalData.get("identityCard").toString();
			wrappedData.put("idCard", identityCard.equals("") ? BLANK_FIELD : identityCard);

			// 法人代表身份证正反面图片
			String legalPersonFile = originalData.get("legalPersonFile").toString();
			if (legalPersonFile.equals("")) {
				wrappedData.put("legalPersonFileFront", BLANK_IMAGE);
				wrappedData.put("legalPersonFileReverse", BLANK_IMAGE);
			} else {
				String[] arrLegalPersonFile = legalPersonFile.replace("[", "").replace("]", "").replace("\"", "").split(",");
				if (arrLegalPersonFile.length == 1) {
					wrappedData.put("legalPersonFileFront", arrLegalPersonFile[0]);
					wrappedData.put("legalPersonFileReverse", arrLegalPersonFile[0]);
				}

				if (arrLegalPersonFile.length > 1) {
					wrappedData.put("legalPersonFileFront", arrLegalPersonFile[0]);
					wrappedData.put("legalPersonFileReverse", arrLegalPersonFile[1]);
				}
			}

			// 营业执照详细地址
			String businessAddress = originalData.get("businessAddress").toString();
			wrappedData.put("businessAddress", businessAddress.equals("") ? BLANK_FIELD : businessAddress);

			// 成立日期 !!!
			if (ObjectUtils.isEmpty(originalData.get("regTime"))) {
				wrappedData.put("foundDate", Long.valueOf(DEDAULT_MIN_MILLISECOND));
			} else {
				wrappedData.put("foundDate", Long.valueOf(originalData.get("regTime").toString()));
			}

			// 营业期限 !!!
			String businessDate = originalData.get("businessDate").toString();
			if (StringUtils.isEmpty(businessDate)) {
				wrappedData.put("businessDateStart", Long.valueOf(DEDAULT_MIN_MILLISECOND));
				wrappedData.put("businessDateEnd", Long.valueOf(DEDAULT_MAX_MILLISECOND));
			} else {
				String[] arrBusinessDate = businessDate.split("_");
				if (arrBusinessDate.length != 2) {
					wrappedData.put("businessDateStart", Long.valueOf(DEDAULT_MIN_MILLISECOND));
					wrappedData.put("businessDateEnd", Long.valueOf(DEDAULT_MAX_MILLISECOND));
				} else {
					wrappedData.put("businessDateStart", Long.valueOf(arrBusinessDate[0]));
					wrappedData.put("businessDateEnd", Long.valueOf(arrBusinessDate[1]));
				}
			}

			// 注册资本
			String regCapital = originalData.get("regCapital").toString();
			wrappedData.put("registerCapital", regCapital.equals("") ? BLANK_FIELD : regCapital);

			// 经营范围
			String busineScope = originalData.get("busineScope").toString();
			wrappedData.put("busineScope", busineScope.equals("") ? BLANK_FIELD : busineScope);

			// 营业执照副本电子版
			String corpLicenseFile = originalData.get("corpLicenseFile").toString();
			if (corpLicenseFile.equals("")) {
				wrappedData.put("corpLicenceFilePath", BLANK_IMAGE);
			} else {
				String[] arrCorpLicenseFile = corpLicenseFile.replace("[", "").replace("]", "").replace("\"", "").split(",");
				wrappedData.put("corpLicenceFilePath", arrCorpLicenseFile[0]);
			}

			// 公司详细地址
			String corpAddress = originalData.get("corpAddress").toString();
			wrappedData.put("corpAddress", corpAddress.equals("") ? BLANK_FIELD : corpAddress);

			// 公司固定电话
			String corpPhone = originalData.get("corpPhone").toString();
			wrappedData.put("corpPhone", corpPhone.equals("") ? DEFAULT_PHONE : corpPhone);

			// 公司紧急联系人
			String contacterName = originalData.get("contacterName").toString();
			wrappedData.put("manageName", contacterName.equals("") ? BLANK_FIELD : contacterName);

			// 公司紧急联系人手机
			String contacterMobile = originalData.get("contacterMobile").toString();
			wrappedData.put("managePhone", contacterMobile.equals("") ? DEFAULT_MOBILE : contacterMobile);

			// 公司邮箱
			String email = originalData.get("email").toString();
			wrappedData.put("corpEmail", email.equals("") ? DEFAULT_EMAIL : email);

			// 组织机构代码
			String orgnizationCode = originalData.get("orgnizationCode").toString();
			wrappedData.put("orgnizationCode",  orgnizationCode.equals("") ? BLANK_FIELD : orgnizationCode);

			// 组织机构代码证副本电子版
			String orgnizationCodeFile = originalData.get("orgnizationCodeFile").toString();
			if (orgnizationCodeFile.equals("")) {
				wrappedData.put("orgnizationFilePath", BLANK_IMAGE);
			} else {
				String[] arrOrgnizationCodeFile = orgnizationCodeFile.replace("[", "").replace("]", "").replace("\"", "").split(",");
				wrappedData.put("orgnizationFilePath", arrOrgnizationCodeFile[0]);
			}

			// 税号
			String registNumber = originalData.get("registNumber").toString();
			wrappedData.put("valueAddedTaxno", registNumber.equals("") ? BLANK_FIELD : registNumber);

			// 纳税人类型 !!
			wrappedData.put("taxpayer", originalData.get("taxpayer"));

			// 纳税类型税码 !!
			String taxRateKey = originalData.get("taxRate").toString();
			String taxRateValue = "0";
			if ("3".equals(taxRateKey)) {
				taxRateValue = "1";
			} else if ("6".equals(taxRateKey)) {
				taxRateValue = "2";
			} else if ("7".equals(taxRateKey)) {
				taxRateValue = "3";
			} else if ("11".equals(taxRateKey)) {
				taxRateValue = "4";
			} else if ("13".equals(taxRateKey)) {
				taxRateValue = "5";
			} else if ("T13".equals(taxRateKey)) {
				taxRateValue = "7";
			} else if ("17".equals(taxRateKey)) {
				taxRateValue = "6";
			}
			wrappedData.put("taxRate", taxRateValue);

			// 税务登记证电子版
			String registNumberFile = originalData.get("registNumberFile").toString();
			if (registNumberFile.equals("")) {
				wrappedData.put("taxpayerLicenseFile", BLANK_IMAGE);
			} else {
				String[] arrRegistNumberFile = registNumberFile.replace("[", "").replace("]", "").replace("\"", "").split(",");
				wrappedData.put("taxpayerLicenseFile", arrRegistNumberFile[0]);
			}

			// 一般纳税人资格电子版
			String taxpayerFile = originalData.get("taxpayerFile").toString();
			if (taxpayerFile.equals("")) {
				wrappedData.put("taxpayerPersonFile", BLANK_IMAGE);
			} else {
				String[] arrTaxpayerFile = taxpayerFile.replace("[", "").replace("]", "").replace("\"", "").split(",");
				wrappedData.put("taxpayerPersonFile", arrTaxpayerFile[0]);
			}

			// 银行开户名
			String bankAccountName = originalData.get("bankAccountName").toString();
			wrappedData.put("bankAccountName", bankAccountName.equals("") ? BLANK_FIELD : bankAccountName);

			// 公司银行账号
			String bankAccount = originalData.get("bankAccount").toString();
			wrappedData.put("bankAccount", bankAccount.equals("") ? BLANK_FIELD : bankAccount);

			// 开户银行支行名称
			String bankName = originalData.get("bankName").toString();
			wrappedData.put("bankName", bankName.equals("") ? BLANK_FIELD : bankName);

			// 开户银行支行联行号
			String bankCode = originalData.get("bankCode").toString();
			wrappedData.put("bankCode", bankCode.equals("") ? BLANK_FIELD : bankCode);

			// 开户银行支行地址
			String bankAddress = originalData.get("bankAddress").toString();
			wrappedData.put("bankAddress", bankAddress.equals("") ? BLANK_FIELD : bankAddress);

			// 银行开户许可证电子版
			String accountPermitFile = originalData.get("accountPermitFile").toString();
			if (accountPermitFile.equals("")) {
				wrappedData.put("bankImage", BLANK_IMAGE);
			} else {
				String[] arrAccountPermitFile = accountPermitFile.replace("[", "").replace("]", "").replace("\"", "").split(",");
				wrappedData.put("bankImage", arrAccountPermitFile[0]);
			}

			return wrappedData;
		} else {
			throw new Exception("查找不到商家信息");
		}
	}

	/**
	 * 分页获取所有广告主
	 * @param pagination
	 * @param keyword
	 * @param approvalStatus
	 * @return 广告主集合
	 */
	public List<Advertiser> getAllAdvertisersByPagination(Pagination pagination, String keyword, Integer approvalStatus) {
		List<Advertiser> advertisers = advertiserDao.getAllAdvertisetsByPagination(pagination, keyword, approvalStatus);
		return advertisers;
	}

	/**
	 * 通过广告主
	 * @param advertiserId
	 */
	@Transactional(readOnly = false)
	public void passByAdvertiserId(Integer advertiserId) {
		Advertiser advertiser = advertiserDao.get(advertiserId);
		advertiser.setApproveStatus(Advertiser.ApproveStatus.PASS.getValue());
		advertiser.setUpdateTime(new Date());
		advertiserDao.update(advertiser);
	}

	/**
	 * 不通过广告主
	 * @param advertiserId
	 * @param remark
	 */
	@Transactional(readOnly = false)
	public void revokeByAdvertiserId(Integer advertiserId, String remark) {
		Advertiser advertiser = advertiserDao.getUniqueBy("advertiserId", advertiserId);
		advertiser.setApproveStatus(Advertiser.ApproveStatus.FAIL.getValue());
		advertiser.setUpdateTime(new Date());
		advertiser.setRemark(remark);
		advertiserDao.update(advertiser);
	}
	
	/**
	 * 获取广告主审核拒绝原因
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = { ApiException.class })
	public Map<String, Object> getRejectReason(Integer userId) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Advertiser advertiser = advertiserDao.getAdvertisersByUserId(userId);
		if (!ObjectUtils.isEmpty(advertiser)) {
			String rejectResons = advertiser.getRejectReasons();
			if (!StringUtils.isEmpty(rejectResons)) {
				List<String> reasons = gson.fromJson(rejectResons, new ArrayList<String>().getClass());
				data.put("rejectReasons", String.join("、", reasons));
			}
		}
		return data;
	}
	
	/**
	 * 获取广告主信息
	 * @return
	 */
	public Map<String, Object> getAdvertiser() throws Exception {
		
		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		// 获取 user
		User user = userDao.get(PrincipalUtil.getUserId());
		if (!ObjectUtils.isEmpty(user)) {
			Advertiser advertiser = advertiserDao.getAdvertisersByUserId(user.getUserId());
			// 公司名称
			data.put("corpName", advertiser.getCompanyName());
			// 注册号
			data.put("corpLicenseNumber", advertiser.getBizLicenseNumber());
			// 法人代表姓名
			data.put("legalPerson", advertiser.getLegalReprName());
			// 法人代表身份证号
			data.put("idCard", advertiser.getLegalReprIdNumber());
			// 法人代表身份证正反面图片
			data.put("legalPersonFileFront", advertiser.getLegalReprIdImage());
			data.put("legalPersonFileReverse", advertiser.getLegalReprIdBackImage());
			// 营业执照详细地址
			data.put("businessAddress", advertiser.getBizLicenseAddress());
			// 成立日期 
			data.put("foundDate", advertiser.getCompanyFoundDate());
			// 营业期限 
			data.put("businessDateStart", advertiser.getBizStartDate());
			data.put("businessDateEnd", advertiser.getBizEndDate());
			// 注册资本
			data.put("registerCapital", advertiser.getRegCapital());
			// 经营范围
			data.put("busineScope", advertiser.getBizScope());
			// 营业执照副本电子版
			data.put("corpLicenceFilePath", advertiser.getBizLicenseImage());
			// 公司详细地址
			data.put("corpAddress", advertiser.getCompanyAddress());
			// 公司固定电话
			data.put("corpPhone", advertiser.getCompanyPhone());
			// 公司紧急联系人
			data.put("manageName", advertiser.getCompanyName());
			// 公司紧急联系人手机
			data.put("managePhone", advertiser.getCompanyPhone());
			// 公司邮箱
			data.put("corpEmail", advertiser.getCompanyEmail());
			// 组织机构代码
			data.put("orgnizationCode", advertiser.getOrgCode());
			// 组织机构代码证副本电子版
			data.put("orgnizationFilePath", advertiser.getOrgCodeImage());
			// 税号
			data.put("valueAddedTaxno", advertiser.getTaxNumber());
			// 纳税人类型 !!
			data.put("taxpayer", advertiser.getTaxpayerType());
			// 纳税类型税码 !!
			data.put("taxRate", advertiser.getTaxCode());
			// 税务登记证电子版
			data.put("taxpayerLicenseFile", advertiser.getTaxRegCertImage());
			// 一般纳税人资格电子版
			data.put("taxpayerPersonFile", advertiser.getTaxpayerQualificationImage());
			// 银行开户名
			data.put("bankAccountName", advertiser.getBankAccountName());
			// 公司银行账号
			data.put("bankAccount", advertiser.getBankAccount());
			// 开户银行支行名称
			data.put("bankName", advertiser.getBankBranchName());
			// 开户银行支行联行号
			data.put("bankCode", advertiser.getBankBranchLineNumber());
			// 开户银行支行地址
			data.put("bankAddress", advertiser.getBankBranchAddress());
			// 银行开户许可证电子版
			data.put("bankImage", advertiser.getBankAccountPermissionImage());
		} else {
			throw new Exception("查找不到广告主信息");
		}
		return data;
	}
	
}