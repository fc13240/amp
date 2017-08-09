package com.gomeplus.amp.ad.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.ads.AdsAccountOperations;
import com.gomeplus.adm.common.api.ads.model.AdsAccount;
import com.gomeplus.adm.common.api.ads.model.AdsSyncAccount;
import com.gomeplus.adm.common.persistence.HibernateDao;
import com.gomeplus.adm.common.service.BaseService;
import com.gomeplus.adm.common.web.Pagination;
import com.gomeplus.amp.ad.dao.AccountDao;
import com.gomeplus.amp.ad.dao.AccountRemindDao;
import com.gomeplus.amp.ad.dao.CampaignDao;
import com.gomeplus.amp.ad.dao.ChargeDao;
import com.gomeplus.amp.ad.dao.ExpenseDao;
import com.gomeplus.amp.ad.model.Account;
import com.gomeplus.amp.ad.model.AccountRemind;
import com.gomeplus.amp.ad.model.Campaign;
import com.gomeplus.amp.ad.model.Charge;
import com.gomeplus.amp.ad.model.Expense;

/**
 * 账号service
 * 
 * @author suna01
 *
 */
@Service
@Transactional(readOnly = true)
public class AccountService extends BaseService<Account, Integer>{

	private static Logger logger = LoggerFactory.getLogger(AccountService.class);
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private ExpenseDao expenseDao;
	@Autowired
	private ChargeDao chargeDao;
	@Autowired
	private CampaignDao campaignDao;
	@Autowired
	private AccountRemindDao accountRemindDao;
	
	@Override
	public HibernateDao<Account, Integer> getEntityDao() {
		return accountDao;
	}
	
	/**
	 * 获取账号概况信息
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> getBasicAccount() throws Exception {
		Integer userId = PrincipalUtil.getUserId();
		// 广告账户和返利账户余额
		Map<String, Object> data = new HashMap<String, Object>();
		List<Account> accounts = accountDao.getBy("userId", userId);

		BigInteger rebateBalance = BigInteger.valueOf(0);
		BigInteger adBalance = BigInteger.valueOf(0);
		for (Account account : accounts) {
			if (account.getType() == Account.Type.ADVERT_ACCOUNT.getValue()) {
				adBalance = adBalance.add(account.getBalance());
			} else if (account.getType() == Account.Type.REBATE_ACCOUNT.getValue()) {
				rebateBalance = rebateBalance.add(account.getBalance());
			}

		}
		
		data.put("adBalance", adBalance);
		data.put("rebateBalance", rebateBalance);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date time = format.parse(format.format(new Date()));
		// 获取今日广告花费，返利花费
		List<Expense> expenses = expenseDao.getAllExpenses(userId, time);
		BigInteger adTodayCost = BigInteger.valueOf(0);
		BigInteger rebateTodayCost = BigInteger.valueOf(0);
		for (Expense expense : expenses) {
			BigInteger amount = expense.getAmount();
			if (expense.getType().compareTo(Expense.Type.ADVERT_ACCOUNT.getValue()) == 0) {
				adTodayCost = adTodayCost.add(amount); 
			} else if (expense.getType().compareTo(Expense.Type.REBATE_ACCOUNT.getValue()) == 0) {
				rebateTodayCost = rebateTodayCost.add(amount);
			}
		}
		data.put("adTodayCost", adTodayCost);
		data.put("rebateTodayCost", rebateTodayCost);
		
		return data;
	}
	
	/**
	 * 获取所有充值记录
	 * @param state
	 * @param time
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getCharge(Pagination pagination, Integer state, Long time) throws ParseException {
		// 数据容器初始化
		Map<String, Object> list = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> headerList = new ArrayList<String>();
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		// 拼装头部文件
		headerList.add("充值日期");
		headerList.add("名称");
		headerList.add("状态");
		headerList.add("流水单号");
		headerList.add("金额(元)");
		list.put("header", headerList);
		// 拼装数据

		Integer userId = PrincipalUtil.getUserId();

		List<Charge> charges = new ArrayList<Charge>();
		Long totalCount = Long.valueOf(0);
		Integer page = 1;
		Integer number = 0;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 分页获取	
		if (time == 0) {
			charges = chargeDao.getChargesByPagination(userId, pagination, state);

			totalCount = pagination.getTotalCount();
			page = pagination.getCurrentPage();
			number = pagination.getNumber();
		} else {
			Date timeDate = format.parse(format.format(time));

			charges = chargeDao.getChargesByTime(userId, timeDate, state);

			totalCount = Long.valueOf(charges.size());
			page = 1;
			number = charges.size();
		}

		for (Charge charge : charges) {
			List<Object> rowDatas = new ArrayList<Object>();
			rowDatas.add(format.format(charge.getCreateTime()));
			rowDatas.add(charge.getName());
			rowDatas.add(charge.getStatusString());
			rowDatas.add(charge.getOrderNo());
			rowDatas.add((new BigDecimal(charge.getPayMoney())).divide(new BigDecimal(100)));
			dataList.add(rowDatas);
		}

		list.put("data", dataList);
		
		data.put("totalCount", totalCount);
		data.put("page", page);
		data.put("number", number);
		data.put("list", list);
		return data;
	}
	
	/**
	 * 消费记录
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> getExpense(Pagination pagination, Long time) throws ParseException {
		// 数据容器初始化
		Map<String, Object> list = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> headerList = new ArrayList<String>();
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		// 拼装头部文件
		headerList.add("日期");
		headerList.add("账户");
		headerList.add("支出(元)");
		list.put("header", headerList);

		Integer userId = PrincipalUtil.getUserId();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date timeDate = format.parse(format.format(time));

		List<Expense> expenses = expenseDao.getExpensesByPagination(userId, timeDate, pagination);
		Long totalCount = pagination.getTotalCount();
		Integer page = pagination.getCurrentPage();
		Integer number = pagination.getNumber();

		Map<String, List<Object>> expensesMap = new LinkedHashMap<String, List<Object>>();
		for (Expense expense : expenses) {
			// 按时间和账户类型显示消费记录
			String key = expense.getTime() + "_" + expense.getType();

			if (expensesMap.containsKey(key)) {
				List<Object> expensesData = expensesMap.get(key);
				BigDecimal amount = (BigDecimal) expensesData.get(2);
				amount = amount.add((new BigDecimal(expense.getAmount())).divide(new BigDecimal(100)));
				expensesData.set(2, amount);

				expensesMap.put(key, expensesData);
			} else {
				List<Object> expensesData = new ArrayList<Object>();
				expensesData.add(format.format(expense.getTime()));
				expensesData.add(expense.getTypeString());
				expensesData.add((new BigDecimal(expense.getAmount())).divide(new BigDecimal(100)));

				expensesMap.put(key, expensesData);
			}
		}
		dataList = new ArrayList<List<Object>>(expensesMap.values());
	
		list.put("data", dataList);
		data.put("totalCount", totalCount);
		data.put("page", page);
		data.put("number", number);
		data.put("list", list);
		return data;
	}
	
	/**
	 * 单日明细
	 * @param time
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getDailyExpense(Long time) throws ParseException {
		// 数据容器初始化
		Map<String, Object> list = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> headerList = new ArrayList<String>();
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		// 拼装头部文件
		headerList.add("计划名称");
		headerList.add("账户");
		headerList.add("支出(元)");
		list.put("header", headerList);
		// 拼装数据
		// @todo
		Integer userId = PrincipalUtil.getUserId();
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		date=format.parse(format.format(time));
		// 查询单日明细数据
		List<Expense> expenses = expenseDao.getAllExpenses(userId, date);
		List<Integer> dspCampaignIds = new ArrayList<Integer>();
		Map<String, String> campaignMap = new HashMap<String, String>();
		// 获取投放计划Id,消费记录Map
		Map<String, List<BigInteger>> expenseMap = new HashMap<String, List<BigInteger>>();
		for (Expense expense : expenses) {
			Integer dspCampaignId = expense.getDspCampaignId();
			if (!dspCampaignIds.contains(dspCampaignId)) {
				dspCampaignIds.add(dspCampaignId);
			}
			
			BigInteger amount = expense.getAmount();
			String key = dspCampaignId + "_" + expense.getType();
			List<BigInteger> expenseLists = new ArrayList<BigInteger>();
			if (!CollectionUtils.isEmpty(expenseMap.get(key))) {
				expenseLists = expenseMap.get(key);
			}
			expenseLists.add(amount);
			expenseMap.put(key, expenseLists);
		}
		// 获取投放计划Map
		List<Campaign> campaigns = campaignDao.getCampaignsByDspCampaignIds(dspCampaignIds);
		for (Campaign campaign : campaigns) {
			campaignMap.put(campaign.getDspCampaignId().toString(), campaign.getName());
		}
		// 封装列表数据data
		String typeName = null; 
		for (String key : expenseMap.keySet()) {
			BigInteger amountTotal = BigInteger.valueOf(0);
			String dspCampaignId = key.split("_")[0];
			String type = key.split("_")[1];
			if (type.equals(Expense.Type.ADVERT_ACCOUNT.getValue().toString())) {
				typeName = "广告账户";
			} else if (type.equals(Expense.Type.REBATE_ACCOUNT.getValue().toString())) {
				typeName = "返利账户";
			}
			List<BigInteger> amounts = expenseMap.get(key);
			for (int i = 0; i < amounts.size(); i++) {
				amountTotal = amountTotal.add(amounts.get(i));
			} 
			String campaignName = campaignMap.get(dspCampaignId);
			List<Object> rowDatas = new ArrayList<Object>();
			rowDatas.add(campaignName);
			rowDatas.add(typeName);
			rowDatas.add(new BigDecimal(amountTotal).divide(new BigDecimal(100)));
			dataList.add(rowDatas);
		}
		
		list.put("data", dataList);
		
		data.put("list", list);
		return data;
		
	}

	/**
	 * 获取资金划拨信息
	 * @throws ParseException
	 */
	public Map<String, Object> getAccountTransfer() throws ParseException {
		Integer userId = PrincipalUtil.getUserId();
		// 总可用余额
		BigInteger totalBalance = BigInteger.ZERO;
		// 广告账户余额
		BigInteger adBalance = BigInteger.ZERO;
		// 返利账户余额
		BigInteger rebateBalance = BigInteger.ZERO;
		// 构造数据
		Map<String, Object> data = new HashMap<String, Object>();
		List<Account> accounts = accountDao.getBy("userId", userId);
		for (Account account : accounts) {
			if (account.getType() == Account.Type.ADVERT_ACCOUNT.getValue()) {
				adBalance = account.getBalance();
			} else {
				rebateBalance = account.getBalance();
			}
		}
		totalBalance = totalBalance.add(adBalance).add(rebateBalance);
		data.put("totalBalance", totalBalance);
		data.put("adBalance", adBalance);
		data.put("rebateBalance", rebateBalance);
		return data;
	}

	/**
	 * 资金划拨
	 * 
	 * @param adBalance
	 * @param rebateBalance
	 * @author: liuchen
	 */
	@Transactional
	public void accountTransfer(BigInteger adBalance, BigInteger rebateBalance) throws Exception {
		// @todo 待修改
		Integer userId = PrincipalUtil.getUserId();
		String userName = PrincipalUtil.getName();
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		BigInteger adsAmount = BigInteger.ZERO;
		// 需要同步的账户金额(大于0)
		BigInteger synchAccount = BigInteger.ZERO;
		// 转账账户id
		Integer from = 0;
		// 目标账户
		Integer to = 0;
		List<Account> accounts = accountDao.getBy("userId", userId);

		// 计算传入划拨资金总和
		BigInteger transferTotalBalance = BigInteger.ZERO;
		transferTotalBalance = transferTotalBalance.add(adBalance).add(rebateBalance);
		logger.info("传入划拨资金总和=" + transferTotalBalance);

		// 获取DB广告账户和返利账户总额
		BigInteger totalBalance = accounts.stream().map(Account::getBalance).reduce(BigInteger::add).get();
		logger.info("获取DB广告账户和返利账户总额=" + totalBalance);

		// 比较传入资金总和 VS DB总余额
		if (transferTotalBalance.compareTo(totalBalance) != 0) {
			throw new RuntimeException("传入资金总额与系统账户总余额不相等");
		}

		for (Account account : accounts) {
			if (account.getType().compareTo(Account.Type.ADVERT_ACCOUNT.getValue()) == 0) {
				adsAmount = adBalance.subtract(account.getBalance());
				account.setBalance(adBalance);
			} else if (account.getType().compareTo(Account.Type.REBATE_ACCOUNT.getValue()) == 0) {
				adsAmount = rebateBalance.subtract(account.getBalance());
				account.setBalance(rebateBalance);
			}
			if (adsAmount.compareTo(BigInteger.ZERO) == 1) {
				synchAccount = adsAmount;
				to = account.getDspAccountId();
			} else if (adsAmount.compareTo(BigInteger.ZERO) == -1) {
				from = account.getDspAccountId();
			}
			account.setUpdateTime(new Date());
			// 更改资金划拨的账户金额
			accountDao.update(account);
		}
		// 执行同步操作
		if (from != 0 && to != 0) {
			synchAccount(from, to, dspAdvertiserId, synchAccount, userName);
		}
	}
	/**
	 * 封装同步ADS账户
	 * @param from
	 * @param to
	 * @param advertiserId
	 * @param synchAccount
	 * @param updateUser
	 */
	private void synchAccount(Integer from, Integer to, Integer advertiserId,
			BigInteger synchAccount, String updateUser) {
		// 同步ADS账户
		AdsAccountOperations adsAccountOperations = new AdsAccountOperations();
		AdsSyncAccount adsSynAccount = new AdsSyncAccount(from, to, advertiserId, synchAccount,
				updateUser);

		ApiResponse apiResponse;
		try {
			// 同步ADS账户请求
			apiResponse = adsAccountOperations.post(adsSynAccount);
		} catch (Exception e) {
			throw new RuntimeException("同步ADS账户fromAccountId=" + from + ",toAccountId=" + to + "请求异常");
		}

		// 同步ADS账户返回错误信息抛出异常
		if (StringUtils.isNotEmpty(apiResponse.getMessage())) {
			throw new RuntimeException(
					"同步ADS账户fromAccountId=" + from + ",toAccountId=" + to + "返回错误信息，Message是" + apiResponse.getMessage
							());
		}
	}

	/**
	 * 获取余额提醒
	 */
	public Map<String, Object> getBalanceRemind() throws Exception {

		Map<String, Object> data = new LinkedHashMap<String, Object>();

		Integer userId = PrincipalUtil.getUserId();

		AccountRemind accountRemind = accountRemindDao.getUniqueBy("userId", userId);

		if (accountRemind != null) {
			data.put("isOpen", accountRemind.getIsOpen());
			data.put("remindAmount", accountRemind.getRemindAmount());
			data.put("isSms", accountRemind.getIsSms());
			data.put("mobile", accountRemind.getMobile());
			data.put("isEmail", accountRemind.getIsEmail());
			data.put("email", accountRemind.getEmail());
		} else {
			data.put("isOpen", 0);
			data.put("remindAmount", 0);
			data.put("isSms", 0);
			data.put("mobile", "");
			data.put("isEmail", 0);
			data.put("email", "");
		}

		return data;
	}

	/**
	 * 保存余额提醒
	 * @param remindMap
	 */
	@Transactional(readOnly = false)
	public void saveBalanceRemind(Map<String, Object> remindMap) throws Exception {
		
		Integer userId = PrincipalUtil.getUserId();

		Integer isOpen = ((Double)remindMap.get("isOpen")).intValue();
		Long remindAmount = ((Double)remindMap.get("remindAmount")).longValue();
		Integer isSms = ((Double)remindMap.get("isSms")).intValue();
		String mobile = (String) remindMap.get("mobile");
		Integer isEmail = ((Double)remindMap.get("isEmail")).intValue();
		String email = (String) remindMap.get("email");

		Date currentTime = new Date();
		AccountRemind oldAccountRemind = accountRemindDao.getUniqueBy("userId", userId);

		if (oldAccountRemind != null) {
			oldAccountRemind.setIsOpen(isOpen);
			oldAccountRemind.setRemindAmount(remindAmount);
			oldAccountRemind.setIsSms(isSms);
			oldAccountRemind.setMobile(mobile);
			oldAccountRemind.setIsEmail(isEmail);
			oldAccountRemind.setEmail(email);
			oldAccountRemind.setUpdateTime(currentTime);

			accountRemindDao.update(oldAccountRemind);
		} else {
			AccountRemind accountRemind = new AccountRemind(isOpen, remindAmount, isSms,
				mobile, isEmail, email, userId, currentTime, currentTime);

			accountRemindDao.save(accountRemind);
		}
	}
}
