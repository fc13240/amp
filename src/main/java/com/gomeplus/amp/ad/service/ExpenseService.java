package com.gomeplus.amp.ad.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gomeplus.amp.ad.dao.ExpenseDao;
import com.gomeplus.amp.ad.model.Expense;

/**
 * 
 * @author sunyunlong
 * @description 消费记录service
 * @parameter
 */
@Service
@Transactional(readOnly = true)
public class ExpenseService {

	@Autowired
	private ExpenseDao expenseDao;

	/**
	 * 获取投放计划当天对应的消费金额
	 * 
	 * @param dspCampaignIds
	 * @return
	 */
	public Map<Integer, BigInteger> getExpenseAmount(List<Integer> dspCampaignIds) throws Exception {

		// 构造投放计划对应消费金额集合
		Map<Integer, BigInteger> expenseMap = new HashMap<Integer, BigInteger>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date time = format.parse(format.format(new Date()));
		// 遍历并封装数据集
		List<Expense> getExpense = expenseDao.getExpensesByCampaignId(dspCampaignIds, time);
		if (!CollectionUtils.isEmpty(getExpense)) {
			for (Expense expense : getExpense) {
				if (expenseMap.containsKey(expense.getDspCampaignId())) {
					BigInteger amount = expenseMap.get(expense.getDspCampaignId()).add(expense.getAmount());
					expenseMap.put(expense.getDspCampaignId(), amount);
				} else {
					expenseMap.put(expense.getDspCampaignId(), expense.getAmount());
				}
			}

		}
		return expenseMap;
	}
	/**
	 * 统计某时间段内投放计划的消费金额
	 * @param dspCampaignIds
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<String, Map<Integer, BigInteger>> getAmountByDspCampaignIds(List<Integer> dspCampaignIds,
			Date startTime, Date endTime) throws Exception {
		//构造广告账户类型Map
		Map<Integer, BigInteger> AdExpenseMap = new HashMap<Integer, BigInteger>();
		//构造返利账户类型Map
		Map<Integer, BigInteger> rebateExpenseMap = new HashMap<Integer, BigInteger>();
		//构造类型集合，统计消费金额
		Map<String, Map<Integer, BigInteger>> dspCampaignsMap = new HashMap<String, Map<Integer, BigInteger>>();
		// 查询统计
		List<Expense> getExpenseAmount = expenseDao.getAmountByDspCampaignIds(dspCampaignIds, startTime, endTime);
		if (!CollectionUtils.isEmpty(getExpenseAmount)) {
			for (Expense expense : getExpenseAmount) {
				if(expense.getType().compareTo(Expense.Type.ADVERT_ACCOUNT.getValue()) == 0){
					if (AdExpenseMap.containsKey(expense.getDspCampaignId())) {
						BigInteger amount = AdExpenseMap.get(expense.getDspCampaignId()).add(expense.getAmount());
						AdExpenseMap.put(expense.getDspCampaignId(), amount);
					} else {
						AdExpenseMap.put(expense.getDspCampaignId(), expense.getAmount());
					}
				}else if(expense.getType().compareTo(Expense.Type.REBATE_ACCOUNT.getValue()) == 0){
					if (rebateExpenseMap.containsKey(expense.getDspCampaignId())) {
						BigInteger amount = rebateExpenseMap.get(expense.getDspCampaignId()).add(expense.getAmount());
						rebateExpenseMap.put(expense.getDspCampaignId(), amount);
					} else {
						rebateExpenseMap.put(expense.getDspCampaignId(), expense.getAmount());
					}
				}
			}
			dspCampaignsMap.put("adAmount", AdExpenseMap);
			dspCampaignsMap.put("rebateAmount", rebateExpenseMap);
		}
		return dspCampaignsMap;
	}
	/**
	 * 统计某时间段内投放单元的消费金额
	 * @param dspFlightIds
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<Integer, BigInteger>> getAmountByDspFlightIds(List<Integer> dspFlightIds,
			Date startTime, Date endTime) throws Exception {
		//构造广告账户类型Map
		Map<Integer, BigInteger> AdExpenseMap = new HashMap<Integer, BigInteger>();
		//构造返利账户类型Map
		Map<Integer, BigInteger> rebateExpenseMap = new HashMap<Integer, BigInteger>();
		//构造类型集合，统计消费金额
		Map<String, Map<Integer, BigInteger>> DspFlightsMap = new HashMap<String, Map<Integer, BigInteger>>();
		// 查询统计
		List<Expense> getExpenseAmount = expenseDao.getAmountByDspFlightIds(dspFlightIds, startTime, endTime);
		if (!CollectionUtils.isEmpty(getExpenseAmount)) {
			for (Expense expense : getExpenseAmount) {
				if(expense.getType().compareTo(Expense.Type.ADVERT_ACCOUNT.getValue()) == 0){
					if (AdExpenseMap.containsKey(expense.getDspFlightId())) {
						BigInteger amount = AdExpenseMap.get(expense.getDspFlightId()).add(expense.getAmount());
						AdExpenseMap.put(expense.getDspFlightId(), amount);
					} else {
						AdExpenseMap.put(expense.getDspFlightId(), expense.getAmount());
					}
				}else if(expense.getType().compareTo(Expense.Type.REBATE_ACCOUNT.getValue()) == 0){
					if (rebateExpenseMap.containsKey(expense.getDspFlightId())) {
						BigInteger amount = rebateExpenseMap.get(expense.getDspFlightId()).add(expense.getAmount());
						rebateExpenseMap.put(expense.getDspFlightId(), amount);
					} else {
						rebateExpenseMap.put(expense.getDspFlightId(), expense.getAmount());
					}
				}
			}
			DspFlightsMap.put("adAmount", AdExpenseMap);
			DspFlightsMap.put("rebateAmount", rebateExpenseMap);
		}
		return DspFlightsMap;
	}
}
