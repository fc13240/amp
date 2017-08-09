package com.gomeplus.amp.ad.service;

import com.gomeplus.adm.common.api.ApiResponse;
import com.gomeplus.adm.common.api.dmp.DmpChartOperations;
import com.gomeplus.adm.common.api.dmp.DmpEffectOperations;
import com.gomeplus.adm.common.api.dmp.DmpFlightOperations;
import com.gomeplus.adm.common.api.dmp.DmpRebateOperations;
import com.gomeplus.adm.common.api.dmp.model.DmpOrderOfRebate;
import com.gomeplus.adm.common.exception.FieldException;
import com.gomeplus.amp.ad.security.shiro.util.PrincipalUtil;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * 报表相关 的 service
 * @author DengBin
 */
@Service
public class ReportService {

	/**
	 * 根据广告主id和起止时间 查询首页图表数据
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReportsByAdvertiserId(Long startTime, Long endTime) throws Exception {
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> xAxisData = new ArrayList<String>();
		List<Integer> yAxisForImpressionNumber = new ArrayList<Integer>();
		List<Integer> yAxisForClickNumber = new ArrayList<Integer>();
		List<Double> yAxisForAvgOfClickNumber = new ArrayList<Double>();
		List<Double> yAxisForCost = new ArrayList<Double>();
		Double totalImpressionNumber = 0.0;
		Double totalClickNumber = 0.0;
		Double totalCost = 0.0;

		// 处理起止时间
		LocalDate startDate = new Date(startTime).toLocalDate();
		LocalDate endDate = new Date(endTime).toLocalDate();

		if (startDate.isAfter(endDate)) {
			data.put("error", "开始时间在结束时间之后");
			return data;
		}

		String startEventTime = startDate.toString() + " 00:00:00";
		String endEventTime = endDate + " 23:59:59";
		Long daysBetweenStartAndEnd = startDate.until(endDate, DAYS);


		// 调用接口
		DmpChartOperations dmpChartOperations = new DmpChartOperations();
		ApiResponse apiResponse = dmpChartOperations.getChartDataByAdvertiserId(dspAdvertiserId, startEventTime, endEventTime);

		ArrayList report = (ArrayList)apiResponse.getData().get("report");


		// 初始化 X, Y轴 数据
		if (startDate.isEqual(endDate)) {

			for (int i = 0; i < TimeConstant.HOURS_24.getValue(); i++) {
				xAxisData.add(this.formatIndex(i));
				this.yAxisDataInit(yAxisForImpressionNumber, yAxisForClickNumber, yAxisForAvgOfClickNumber, yAxisForCost);
			}

		} else{

			for (int i = 0; i < daysBetweenStartAndEnd + 1; i++) {
				xAxisData.add(endDate.minus(daysBetweenStartAndEnd - i, DAYS).toString());
				this.yAxisDataInit(yAxisForImpressionNumber, yAxisForClickNumber, yAxisForAvgOfClickNumber, yAxisForCost);
			}

		}


		// 处理 Y轴(曝光量, 点击量, 点击率) 数据 和 统计
		if (report.size() > 0) {
			if (startDate.isEqual(endDate)) {

				Integer index = null;

				for (int i = 0; i < report.size(); i++) {
					index = xAxisData.indexOf(((String)(((Map)report.get(i)).get("eventTime"))).substring(11, 16));
					this.setYAxisData(yAxisForImpressionNumber, yAxisForClickNumber, yAxisForAvgOfClickNumber, yAxisForCost, index, i, report);
				}

			} else {

				Integer index = null;

				for (int i = 0; i < report.size(); i++) {
					index = xAxisData.indexOf(((Map)report.get(i)).get("eventTime"));
					this.setYAxisData(yAxisForImpressionNumber, yAxisForClickNumber, yAxisForAvgOfClickNumber, yAxisForCost, index, i, report);
				}
			}

			for (int i = 0; i < report.size(); i++) {
				totalImpressionNumber += this.getDouble(((Map) report.get(i)).get("impressionNumber"));
				totalClickNumber += this.getDouble(((Map) report.get(i)).get("clickNumber"));
				totalCost += this.getDouble(((Map) report.get(i)).get("cost"));
			}
		}

		// 拼装ajax数据结构并返回
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("data", data);
		param.put("xAxisData", xAxisData);
		param.put("yAxisForImpressionNumber", yAxisForImpressionNumber);
		param.put("yAxisForClickNumber", yAxisForClickNumber);
		param.put("yAxisForAvgOfClickNumber", yAxisForAvgOfClickNumber);
		param.put("yAxisForCost", yAxisForCost);
		param.put("totalImpressionNumber", totalImpressionNumber);
		param.put("totalClickNumber", totalClickNumber);
		param.put("totalCost", totalCost);
		return this.createAjaxResult(param);
	}

	/**
	 * 处理起止时间
	 * @param startTime
	 * @param endTime
	 */
	private Map<String, Object> startAndEndTimeToString(Long startTime, Long endTime) {

		if (startTime == null || endTime == null) {
			throw new FieldException("开始时间或结束时间不能为空");
		}

		if (startTime < 0 || endTime < 0) {
			throw new FieldException("开始时间或结束时间不能为负数");
		}

		Map<String, Object> data = new HashMap<String, Object>();
		String startEventTime = null;
		String endEventTime = null;

		if (startTime == 0 && endTime == 0) {
			return data;
		}

		if (startTime > 0 && endTime == 0) {
			startEventTime = new Date(startTime).toLocalDate().toString() + " 00:00:00";
			data.put("startEventTime", startEventTime);
			return data;
		}

		if (startTime == 0 && endTime > 0) {
			endEventTime = new Date(endTime).toLocalDate().toString() + " 23:59:59";
			data.put("endEventTime", endEventTime);
			return data;
		}

		if (startTime > 0 && endTime > 0) {
			LocalDate startDate = new Date(startTime).toLocalDate();
			LocalDate endDate = new Date(endTime).toLocalDate();

			if (startDate.isAfter(endDate)) {
				throw new FieldException("开始时间在结束时间之后");
			}

			startEventTime = startDate.toString() + " 00:00:00";
			endEventTime = endDate.toString() + " 23:59:59";

			data.put("startEventTime", startEventTime);
			data.put("endEventTime", endEventTime);
			return data;
		}

		return data;
	}

	/**
	 * 查询 返利报表--投放计划 列表数据
	 * @param startTime
	 * @param endTime
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getReportOfCampaignOfRebate(Long startTime, Long endTime,
														Integer pageIndex, Integer pageSize, Integer campaignId, Integer rebateType) {

		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		List dataList = new ArrayList();
		List headerList = new ArrayList();

		// 处理起止时间
		Map<String, Object> timeDealResult = this.startAndEndTimeToString(startTime, endTime);
		if (timeDealResult != null && timeDealResult.get("error") != null) {
			return timeDealResult;
		}
		String startEventTime = timeDealResult.get("startEventTime") == null ? null : (String) timeDealResult.get("startEventTime");
		String endEventTime = timeDealResult.get("endEventTime") == null ? null : (String) timeDealResult.get("endEventTime");

		// 初始化接口参数, 并调用接口
		DmpOrderOfRebate orderOfRebate = new DmpOrderOfRebate(dspAdvertiserId, startEventTime,  endEventTime, pageIndex, pageSize, campaignId);
		DmpRebateOperations rebateOperations = new DmpRebateOperations();
		ApiResponse apiResponse = rebateOperations.getOrderOfRebate(orderOfRebate, rebateType);
		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		// 拼装符合前端的数据格式
		if (report != null && report.size() > 0) {
			for (int i = 0; i < report.size(); i++) {
				List rowDataList = new ArrayList();
				Map rowData = (Map) report.get(i);
				rowDataList.add(this.getString(rowData.get("eventTime")).length() >= 10
						? this.getString(rowData.get("eventTime")).substring(0, 10)
						: "");
				rowDataList.add(this.getString(rowData.get("campaignName")));
				rowDataList.add(this.getInteger(rowData.get("landPv")).toString());
				rowDataList.add(this.getInteger(rowData.get("landUv")).toString());
				rowDataList.add(this.getInteger(rowData.get("sharedPv")).toString());
				rowDataList.add(this.getInteger(rowData.get("sharedUv")).toString());
				if (rebateType == 0) {
					rowDataList.add(this.getInteger(rowData.get("chargeShareNumber")).toString());
					double avgShare = 0;
					if(this.getInteger(rowData.get("chargeShareNumber")).intValue() != 0) {
						avgShare =  this.getDouble(rowData.get("sharefee")) / (1000000 * (long)this.getInteger(rowData.get("chargeShareNumber")).intValue());
					}
					rowDataList.add(this.round(avgShare, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("sharefee")) / 1000000, 2).toString());
					rowDataList.add(this.getInteger(rowData.get("videoviewnumber")).toString());
					double avgVideo = 0;
					if(this.getInteger(rowData.get("videoviewnumber")).intValue() != 0) {
						avgVideo =  this.getDouble(rowData.get("videoviewfee")) / (1000000 * (long)this.getInteger(rowData.get("videoviewnumber")).intValue());
					}
					rowDataList.add(this.round(avgVideo, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("videoviewfee")) / 1000000, 2).toString());
					rowDataList.add(this.getInteger(rowData.get("questionnairenumber")).toString());
					double avgQues = 0;
					if(this.getInteger(rowData.get("questionnairenumber")).intValue() != 0) {
						avgQues =  this.getDouble(rowData.get("questionnairefee")) / (1000000 * (long)this.getInteger(rowData.get("questionnairenumber")).intValue());
					}
					rowDataList.add(this.round(avgQues, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("questionnairefee")) / 1000000, 2).toString());
				}
				if (rebateType == 1) {
					rowDataList.add(this.getInteger(rowData.get("chargeShareNumber")).toString());
					double avgShare = 0;
					if(this.getInteger(rowData.get("chargeShareNumber")) != 0) {
						avgShare =  this.getDouble(rowData.get("sharefee")) / (1000000 * (long)this.getInteger(rowData.get("chargeShareNumber")).intValue());
					}
					rowDataList.add(this.round(avgShare, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("sharefee")) / 1000000, 2).toString());
				}
				if (rebateType == 2) {
					rowDataList.add(this.getInteger(rowData.get("videoviewnumber")).toString());
					double avgVideo = 0;
					if(this.getInteger(rowData.get("videoviewnumber")) != 0) {
						avgVideo =  this.getDouble(rowData.get("videoviewfee")) / (1000000 * (long)this.getInteger(rowData.get("videoviewnumber")).intValue());
					}
					rowDataList.add(this.round(avgVideo, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("videoviewfee")) / 1000000, 2).toString());
				}
				if (rebateType == 3) {
					rowDataList.add(this.getInteger(rowData.get("questionnairenumber")).toString());
					double avgQues = 0;
					if(this.getInteger(rowData.get("questionnairenumber")) != 0) {
						avgQues =  this.getDouble(rowData.get("questionnairefee")) / (1000000 * (long)this.getInteger(rowData.get("questionnairenumber")).intValue());
					}
					rowDataList.add(this.round(avgQues, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("questionnairefee")) / 1000000, 2).toString());
				}
				rowDataList.add(this.round(this.getDouble(rowData.get("totalFee")) / 1000000, 2).toString());

				dataList.add(rowDataList);
			}
		}


		headerList.add("点击日期");
		headerList.add("投放计划名称");
		headerList.add("着陆页PV");
		headerList.add("着陆页UV");
		headerList.add("分享页PV");
		headerList.add("分享页UV");
		if (rebateType == 0) {
			headerList.add("计费分享次数");
			headerList.add("平均分享单价(元)");
			headerList.add("分享费用(元)");
			headerList.add("视频计费浏览次数");
			headerList.add("平均浏览单价(元)");
			headerList.add("视频浏览费用(元)");
			headerList.add("问卷提交次数");
			headerList.add("问卷平均返利单价(元)");
			headerList.add("问卷费用(元)");
		}
		if (rebateType == 1) {
			headerList.add("计费分享次数");
			headerList.add("平均分享单价(元)");
			headerList.add("分享费用(元)");
		}
		if (rebateType == 2) {
			headerList.add("视频计费浏览次数");
			headerList.add("平均浏览单价(元)");
			headerList.add("视频浏览费用(元)");
		}
		if (rebateType == 3) {
			headerList.add("问卷提交次数");
			headerList.add("问卷平均返利单价(元)");
			headerList.add("问卷费用(元)");
		}
		headerList.add("总费用(元)");
		list.put("header", headerList);
		list.put("data", dataList);

		data.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));
		data.put("page", orderOfRebate.getPageIndex());
		data.put("number", orderOfRebate.getPageSize());
		data.put("list", list);

		return data;
	}

	/**
	 * 查询 返利报表--投放单元 列表数据
	 * @param startTime
	 * @param endTime
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getReportOfFlightOfRebate(Long startTime, Long endTime, Integer pageIndex, Integer pageSize
																		, Integer campaignId, Integer flightId, Integer rebateType) {

		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		List dataList = new ArrayList();
		List headerList = new ArrayList();

		// 处理起止时间
		Map<String, Object> timeDealResult = this.startAndEndTimeToString(startTime, endTime);
		if (timeDealResult != null && timeDealResult.get("error") != null) {
			return timeDealResult;
		}
		String startEventTime = timeDealResult.get("startEventTime") == null ? null : (String) timeDealResult.get("startEventTime");
		String endEventTime = timeDealResult.get("endEventTime") == null ? null : (String) timeDealResult.get("endEventTime");

		// 初始化接口参数, 并调用接口
		DmpOrderOfRebate orderOfRebate = new DmpOrderOfRebate(dspAdvertiserId, startEventTime,  endEventTime, pageIndex, pageSize, campaignId, flightId);
		DmpRebateOperations rebateOperations = new DmpRebateOperations();
		ApiResponse apiResponse = rebateOperations.getUnitOfRebate(orderOfRebate,rebateType);
		ArrayList report = (ArrayList) apiResponse.getData().get("report");

		// 拼装符合前端的数据格式
		if (report != null && report.size() > 0) {
			for (int i = 0; i < report.size(); i++) {
				List rowDataList = new ArrayList();
				Map rowData = (Map) report.get(i);
				rowDataList.add(this.getString(rowData.get("eventTime")).length() >= 10
						? this.getString(rowData.get("eventTime")).substring(0, 10)
						: "");
				rowDataList.add(this.getString(rowData.get("campaignName")));
				rowDataList.add(this.getString(rowData.get("flightName")));
				rowDataList.add(this.getInteger(rowData.get("landPv")).toString());
				rowDataList.add(this.getInteger(rowData.get("landUv")).toString());
				rowDataList.add(this.getInteger(rowData.get("sharedPv")).toString());
				rowDataList.add(this.getInteger(rowData.get("sharedUv")).toString());
				if (rebateType == 0) {
					rowDataList.add(this.getInteger(rowData.get("chargeShareNumber")).toString());
					double avgShare = 0;
					if(this.getInteger(rowData.get("chargeShareNumber")) != 0) {
						avgShare =  this.getDouble(rowData.get("sharefee")) / (1000000 * (long)this.getInteger(rowData.get("chargeShareNumber")).intValue());
					}
					rowDataList.add(this.round(avgShare, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("sharefee")) / 1000000, 2).toString());
					rowDataList.add(this.getInteger(rowData.get("videoviewnumber")).toString());
					double avgVideo = 0;
					if(this.getInteger(rowData.get("videoviewnumber")) != 0) {
						avgVideo =  this.getDouble(rowData.get("videoviewfee")) / (1000000 * (long)this.getInteger(rowData.get("videoviewnumber")).intValue());
					}
					rowDataList.add(this.round(avgVideo, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("videoviewfee")) / 1000000, 2).toString());
					rowDataList.add(this.getInteger(rowData.get("questionnairenumber")).toString());
					double avgQues = 0;
					if(this.getInteger(rowData.get("questionnairenumber")) != 0) {
						avgQues =  this.getDouble(rowData.get("questionnairefee")) / (1000000 * (long)this.getInteger(rowData.get("questionnairenumber")).intValue());
					}
					rowDataList.add(this.round(avgQues, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("questionnairefee")) / 1000000, 2).toString());
				}
				if (rebateType == 1) {
					rowDataList.add(this.getInteger(rowData.get("chargeShareNumber")).toString());
					double avgShare = 0;
					if(this.getInteger(rowData.get("chargeShareNumber")) != 0) {
						avgShare =  this.getDouble(rowData.get("sharefee")) / (1000000 *(long)this.getInteger(rowData.get("chargeShareNumber")).intValue());
					}
					rowDataList.add(this.round(avgShare, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("sharefee")) / 1000000, 2).toString());
				}
				if (rebateType == 2) {
					rowDataList.add(this.getInteger(rowData.get("videoviewnumber")).toString());
					double avgVideo = 0;
					if(this.getInteger(rowData.get("videoviewnumber")) != 0) {
						avgVideo =  this.getDouble(rowData.get("videoviewfee")) / (1000000 * (long)this.getInteger(rowData.get("videoviewnumber")).intValue());
					}
					rowDataList.add(this.round(avgVideo, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("videoviewfee")) / 1000000, 2).toString());
				}
				if (rebateType == 3) {
					rowDataList.add(this.getInteger(rowData.get("questionnairenumber")).toString());
					double avgQues = 0;
					if(this.getInteger(rowData.get("questionnairenumber")) != 0) {
						avgQues =  this.getDouble(rowData.get("questionnairefee")) / (1000000 * (long)this.getInteger(rowData.get("questionnairenumber")).intValue());
					}
					rowDataList.add(this.round(avgQues, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("questionnairefee")) / 1000000, 2).toString());
				}
				rowDataList.add(this.round(this.getDouble(rowData.get("totalFee")) / 1000000, 2).toString());

				dataList.add(rowDataList);
			}
		}


		headerList.add("点击日期");
		headerList.add("投放计划名称");
		headerList.add("投放单元名称");
		headerList.add("着陆页PV");
		headerList.add("着陆页UV");
		headerList.add("分享页PV");
		headerList.add("分享页UV");
		if (rebateType == 0) {
			headerList.add("计费分享次数");
			headerList.add("平均分享单价(元)");
			headerList.add("分享费用(元)");
			headerList.add("视频计费浏览次数");
			headerList.add("平均浏览单价(元)");
			headerList.add("视频浏览费用(元)");
			headerList.add("问卷提交次数");
			headerList.add("问卷平均返利单价(元)");
			headerList.add("问卷费用(元)");
		}
		if (rebateType == 1) {
			headerList.add("计费分享次数");
			headerList.add("平均分享单价(元)");
			headerList.add("分享费用(元)");
		}
		if (rebateType == 2) {
			headerList.add("视频计费浏览次数");
			headerList.add("平均浏览单价(元)");
			headerList.add("视频浏览费用(元)");
		}
		if (rebateType == 3) {
			headerList.add("问卷提交次数");
			headerList.add("问卷平均返利单价(元)");
			headerList.add("问卷费用(元)");
		}
		headerList.add("总费用(元)");
		list.put("header", headerList);
		list.put("data", dataList);

		data.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));
		data.put("page", orderOfRebate.getPageIndex());
		data.put("number", orderOfRebate.getPageSize());
		data.put("list", list);

		return data;
	}

	/**
	 * 查询 返利报表--创意 列表数据
	 * @param startTime
	 * @param endTime
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getReportOfMaterialOfRebate(Long startTime, Long endTime, Integer pageIndex, Integer pageSize
														, Integer campaignId, Integer flightId, Integer materialId,Integer rebateType) {
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		List dataList = new ArrayList();
		List headerList = new ArrayList();

		// 处理起止时间
		Map<String, Object> timeDealResult = this.startAndEndTimeToString(startTime, endTime);
		if (timeDealResult != null && timeDealResult.get("error") != null) {
			return timeDealResult;
		}
		String startEventTime = timeDealResult.get("startEventTime") == null ? null : (String) timeDealResult.get("startEventTime");
		String endEventTime = timeDealResult.get("endEventTime") == null ? null : (String) timeDealResult.get("endEventTime");

		// 初始化接口参数, 并调用接口
		DmpOrderOfRebate orderOfRebate = new DmpOrderOfRebate(dspAdvertiserId, startEventTime,  endEventTime, pageIndex, pageSize, campaignId, flightId, materialId);
		DmpRebateOperations rebateOperations = new DmpRebateOperations();
		ApiResponse apiResponse = rebateOperations.getMaterialOfRebate(orderOfRebate,rebateType);
		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		// 拼装符合前端的数据格式
		if (report != null && report.size() > 0) {
			for (int i = 0; i < report.size(); i++) {
				List rowDataList = new ArrayList();
				Map rowData = (Map) report.get(i);
				rowDataList.add(this.getString(rowData.get("eventTime")).length() >= 10
						? this.getString(rowData.get("eventTime")).substring(0, 10)
						: "");
				rowDataList.add(this.getString(rowData.get("materialName")));
				rowDataList.add(this.getInteger(rowData.get("landPv")).toString());
				rowDataList.add(this.getInteger(rowData.get("landUv")).toString());
				rowDataList.add(this.getInteger(rowData.get("sharedPv")).toString());
				rowDataList.add(this.getInteger(rowData.get("sharedUv")).toString());
				if (rebateType == 0) {
					rowDataList.add(this.getInteger(rowData.get("chargeShareNumber")).toString());
					double avgShare = 0;
					if(this.getInteger(rowData.get("chargeShareNumber")) != 0) {
						avgShare =  this.getDouble(rowData.get("sharefee")) / (1000000 * (long)this.getInteger(rowData.get("chargeShareNumber")).intValue());
					}
					rowDataList.add(this.round(avgShare, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("sharefee")) / 1000000, 2).toString());
					rowDataList.add(this.getInteger(rowData.get("videoviewnumber")).toString());
					double avgVideo = 0;
					if(this.getInteger(rowData.get("videoviewnumber")) != 0) {
						avgVideo =  this.getDouble(rowData.get("videoviewfee")) / (1000000 * (long)this.getInteger(rowData.get("videoviewnumber")).intValue());
					}
					rowDataList.add(this.round(avgVideo, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("videoviewfee")) / 1000000, 2).toString());
					rowDataList.add(this.getInteger(rowData.get("questionnairenumber")).toString());
					double avgQues = 0;
					if(this.getInteger(rowData.get("questionnairenumber")) != 0) {
						avgQues =  this.getDouble(rowData.get("questionnairefee")) / (1000000 * (long)this.getInteger(rowData.get("questionnairenumber")).intValue());
					}
					rowDataList.add(this.round(avgQues, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("questionnairefee")) / 1000000, 2).toString());
				}
				if (rebateType == 1) {
					rowDataList.add(this.getInteger(rowData.get("chargeShareNumber")).toString());
					double avgShare = 0;
					if(this.getInteger(rowData.get("chargeShareNumber")) != 0) {
						avgShare =  this.getDouble(rowData.get("sharefee")) / (1000000 * (long)this.getInteger(rowData.get("chargeShareNumber")).intValue());
					}
					rowDataList.add(this.round(avgShare, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("sharefee")) / 1000000, 2).toString());
				}
				if (rebateType == 2) {
					rowDataList.add(this.getInteger(rowData.get("videoviewnumber")).toString());
					double avgVideo = 0;
					if(this.getInteger(rowData.get("videoviewnumber")) != 0) {
						avgVideo =  this.getDouble(rowData.get("videoviewfee")) / (1000000 * (long)this.getInteger(rowData.get("videoviewnumber")).intValue());
					}
					rowDataList.add(this.round(avgVideo, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("videoviewfee")) / 1000000, 2).toString());
				}
				if (rebateType == 3) {
					rowDataList.add(this.getInteger(rowData.get("questionnairenumber")).toString());
					double avgQues = 0;
					if(this.getInteger(rowData.get("questionnairenumber")) != 0) {
						avgQues =  this.getDouble(rowData.get("questionnairefee")) / (1000000 * (long)this.getInteger(rowData.get("questionnairenumber")).intValue());
					}
					rowDataList.add(this.round(avgQues, 2).toString());
					rowDataList.add(this.round(this.getDouble(rowData.get("questionnairefee")) / 1000000, 2).toString());
				}
				rowDataList.add(this.round(this.getDouble(rowData.get("totalFee")) / 1000000, 2).toString());

				dataList.add(rowDataList);
			}
		}


		headerList.add("点击日期");
		headerList.add("创意名称");
		headerList.add("着陆页PV");
		headerList.add("着陆页UV");
		headerList.add("分享页PV");
		headerList.add("分享页UV");
		if (rebateType == 0) {
			headerList.add("计费分享次数");
			headerList.add("平均分享单价(元)");
			headerList.add("分享费用(元)");
			headerList.add("视频计费浏览次数");
			headerList.add("平均浏览单价(元)");
			headerList.add("视频浏览费用(元)");
			headerList.add("问卷提交次数");
			headerList.add("问卷平均返利单价(元)");
			headerList.add("问卷费用(元)");
		}
		if (rebateType == 1) {
			headerList.add("计费分享次数");
			headerList.add("平均分享单价(元)");
			headerList.add("分享费用(元)");
		}
		if (rebateType == 2) {
			headerList.add("视频计费浏览次数");
			headerList.add("平均浏览单价(元)");
			headerList.add("视频浏览费用(元)");
		}
		if (rebateType == 3) {
			headerList.add("问卷提交次数");
			headerList.add("问卷平均返利单价(元)");
			headerList.add("问卷费用(元)");
		}
		headerList.add("总费用(元)");
		list.put("header", headerList);
		list.put("data", dataList);

		data.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));
		data.put("page", orderOfRebate.getPageIndex());
		data.put("number", orderOfRebate.getPageSize());
		data.put("list", list);

		return data;
	}

	/**
	 * 查询 效果报表--订单效果 列表数据
	 * @param startTime
	 * @param endTime
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getReportOfOrderOfEffect(Long startTime, Long endTime, Integer pageIndex, Integer pageSize, Integer productLine) {
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		List dataList = new ArrayList();
		List headerList = new ArrayList();

		// 处理起止时间
		Map<String, Object> timeDealResult = this.startAndEndTimeToString(startTime, endTime);
		if (timeDealResult != null && timeDealResult.get("error") != null) {
			return timeDealResult;
		}
		String startEventTime = timeDealResult.get("startEventTime") == null ? null : (String) timeDealResult.get("startEventTime");
		String endEventTime = timeDealResult.get("endEventTime") == null ? null : (String) timeDealResult.get("endEventTime");

		// 初始化接口参数, 并调用接口
		DmpOrderOfRebate orderOfRebate = new DmpOrderOfRebate(startEventTime,  endEventTime, dspAdvertiserId, pageIndex, pageSize);
		orderOfRebate.setSellType(productLine);
		DmpEffectOperations effectOperations = new DmpEffectOperations();
		ApiResponse apiResponse = effectOperations.getOrderOfEffect(orderOfRebate);

		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		// 拼装符合前端的数据格式
		if (report != null && report.size() > 0) {
			for (int i = 0; i < report.size(); i++) {
				List rowDataList = new ArrayList();
				Map rowData = (Map) report.get(i);
				rowDataList.add(this.getString(rowData.get("orderTime")));
				rowDataList.add(this.getString(rowData.get("clickTime")));
				rowDataList.add(rowData.get("orderId"));
				rowDataList.add(rowData.get("skuId"));
				rowDataList.add(this.round(this.getDouble(rowData.get("skuPrice")), 2).toString());
				rowDataList.add(this.getInteger(rowData.get("skuQuantity")).toString());
				rowDataList.add(this.getNameOfOrderType(rowData.get("orderType")));
				rowDataList.add(rowData.get("orderStatus"));
				rowDataList.add(this.getString(rowData.get("deviceType")));
				rowDataList.add(this.getString(rowData.get("orderName")));
				rowDataList.add(this.getString(rowData.get("flightName")));
				rowDataList.add(this.getString(rowData.get("materialName")));

				dataList.add(rowDataList);
			}
		}


		headerList.add("下单时间");
		headerList.add("点击时间");
		headerList.add("订单号");
		headerList.add("SKU ID");
		headerList.add("SKU金额(元)");
		headerList.add("SKU 数量");
		headerList.add("订单类型");
		headerList.add("订单状态");
		headerList.add("设备类型");
		headerList.add("投放计划名称");
		headerList.add("投放单元名称");
		headerList.add("创意名称");


		list.put("header", headerList);
		list.put("data", dataList);

		data.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));
		data.put("page", orderOfRebate.getPageIndex());
		data.put("number", orderOfRebate.getPageSize());
		data.put("list", list);

		return data;
	}


	/**
	 * 查询 效果报表--订单汇总效果 列表数据
	 * @param startTime
	 * @param endTime
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getReportOfOrderSummaryOfEffect(Long startTime, Long endTime, Integer orderDays, Integer pageIndex, Integer pageSize,Integer sellType) {
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		// 数据容器初始化
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> list = new HashMap<String, Object>();
		List dataList = new ArrayList();
		List headerList = new ArrayList();

		// 处理起止时间
		Map<String, Object> timeDealResult = this.startAndEndTimeToString(startTime, endTime);
		if (timeDealResult != null && timeDealResult.get("error") != null) {
			return timeDealResult;
		}
		String startEventTime = timeDealResult.get("startEventTime") == null ? null : (String) timeDealResult.get("startEventTime");
		String endEventTime = timeDealResult.get("endEventTime") == null ? null : (String) timeDealResult.get("endEventTime");

		// 初始化接口参数, 并调用接口
		DmpOrderOfRebate orderOfRebate = new DmpOrderOfRebate(dspAdvertiserId, startEventTime, orderDays, endEventTime, pageIndex, pageSize);
		orderOfRebate.setSellType(sellType);
		DmpEffectOperations effectOperations = new DmpEffectOperations();
		ApiResponse apiResponse = effectOperations.getOrderSummaryOfEffect(orderOfRebate);

		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		// 拼装符合前端的数据格式
		if (report != null && report.size() > 0) {
			for (int i = 0; i < report.size(); i++) {
				List rowDataList = new ArrayList();
				Map rowData = (Map) report.get(i);
				rowDataList.add(this.getString(rowData.get("clickTime")).length() >= 10
						? this.getString(rowData.get("clickTime")).substring(0, 10)
						: "");
				rowDataList.add(this.getString(rowData.get("deviceType")));
				rowDataList.add(this.round(this.getDouble(rowData.get("totalFee")) / 1000000, 2).toString());
				rowDataList.add((this.getDouble(rowData.get("totalFee")) == 0
						? 0.00
						: this.round((this.getDouble(rowData.get("totalProductLineAmount")) / this.getDouble(rowData.get("totalFee"))) * 100, 2)) + "%");
				rowDataList.add(this.getInteger(rowData.get("impressionNumber")).toString());
				rowDataList.add(this.getInteger(rowData.get("clickNumber")).toString());
				rowDataList.add((this.getDouble(rowData.get("impressionNumber")) == 0
						? 0.00
						: this.round((this.getDouble(rowData.get("clickNumber"))/this.getInteger(rowData.get("impressionNumber"))) * 100, 2)) + "%");
				rowDataList.add(this.getInteger(rowData.get("directProductLineNumber")).toString());
				rowDataList.add(this.round(this.getDouble(rowData.get("directProductLineAmount")), 2).toString());
				rowDataList.add(this.getInteger(rowData.get("indirectProductLineNumber")).toString());
				rowDataList.add(this.round(this.getDouble(rowData.get("indirectProductLineAmount")), 2).toString());
				rowDataList.add(this.getInteger(rowData.get("totalProductLineNumber")).toString());
				rowDataList.add(this.round(this.getDouble(rowData.get("totalProductLineAmount")), 2).toString());
				rowDataList.add((this.getDouble(rowData.get("clickNumber")) == 0
						? 0
						: this.round((this.getDouble(rowData.get("totalProductLineNumber")) / this.getDouble(rowData.get("clickNumber"))) * 100, 2)) + "%");

				dataList.add(rowDataList);
			}
		}


		headerList.add("点击日期");
		headerList.add("设备类型");
		headerList.add("总费用(元)");
		headerList.add("ROI");
		headerList.add("展现量");
		headerList.add("点击量");
		headerList.add("点击率");
		headerList.add("直接订单量");
		headerList.add("直接订单金额(元)");
		headerList.add("间接订单量");
		headerList.add("间接订单金额(元)");
		headerList.add("总订单量");
		headerList.add("总订单金额(元)");
		headerList.add("转化率");
		list.put("header", headerList);
		list.put("data", dataList);

		data.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));
		data.put("page", orderOfRebate.getPageIndex());
		data.put("number", orderOfRebate.getPageSize());
		data.put("list", list);

		return data;
	}


	/**
	 * 时间常量
	 */
	private enum TimeConstant {
		HOURS_24(24), DAYS_7(7), DAYS_30(30);

		private Integer value;

		TimeConstant(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}


	/**
	 * 截取任意位小数
	 * @param src
	 * @param count
	 * @return
	 */
	private String round(Double src, Integer count) {
		DecimalFormat formater = new DecimalFormat("##0.0000");
		//保留几位小数
		formater.setMaximumFractionDigits(count);
		//模式  四舍五入
		formater.setRoundingMode(RoundingMode.HALF_UP);
		//return Double.parseDouble(formater.format(src));
		return formater.format(src);
	}

	/**
	 * 数字时间格式化
	 * @param i
	 * @return
	 */
	private String formatIndex(Integer i) {
		if (i >=0 && i < 10) {
			return ("0" + i + ":00");
		} else if (i >=10 && i < 24) {
			return (i + ":00");
		}

		return "";
	}

	/**
	 * Y轴数据初始化
	 * @param yAxisForImpressionNumber
	 * @param yAxisForClickNumber
	 * @param yAxisForAvgOfClickNumber
	 */
	private void yAxisDataInit(List<Integer> yAxisForImpressionNumber, List<Integer> yAxisForClickNumber,
							   List<Double> yAxisForAvgOfClickNumber, List<Double> yAxisForCost) {
		yAxisForImpressionNumber.add(0);
		yAxisForClickNumber.add(0);
		yAxisForAvgOfClickNumber.add(0.0);
		yAxisForCost.add(0.0);
	}

	/**
	 * 设置Y轴数据
	 * @param yAxisForImpressionNumber
	 * @param yAxisForClickNumber
	 * @param yAxisForAvgOfClickNumber
	 * @param index
	 * @param i
	 * @param report
	 */
	private void setYAxisData(List<Integer> yAxisForImpressionNumber, List<Integer> yAxisForClickNumber,
							  List<Double> yAxisForAvgOfClickNumber, List<Double> yAxisForCost,
							  Integer index, Integer i, ArrayList report) {
		yAxisForImpressionNumber.set(index, this.getInteger(((Map) report.get(i)).get("impressionNumber")));
		yAxisForClickNumber.set(index, this.getInteger(((Map) report.get(i)).get("clickNumber")));
		yAxisForAvgOfClickNumber.set(index, this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
				? 0
				: Double.parseDouble(this.round(this.getDouble(((Map) report.get(i)).get("clickNumber")) / this.getDouble(((Map) report.get(i)).get("impressionNumber")), 4)));
		// 图表数据中的花费做了除以100的处理, 但整体总花费是前端做的处理
		yAxisForCost.set(index, Double.parseDouble(this.round(this.getDouble(((Map) report.get(i)).get("cost")) / 1000000, 2)));

	}

	/**
	 * 创建ajax返回结果
	 * @param param
	 * @return
	 */
	private Map<String, Object> createAjaxResult(Map<String, Object> param) {
		Map<String, Object> overall = new HashMap<String, Object>();
		overall.put("impression", this.getInteger(param.get("totalImpressionNumber")));
		overall.put("click", this.getInteger(param.get("totalClickNumber")));
		overall.put("ctr", (this.getDouble(param.get("totalImpressionNumber")) == 0)
				? 0
				:(this.round(this.getDouble(param.get("totalClickNumber")) / this.getDouble(param.get("totalImpressionNumber")), 4)));
		overall.put("cost", this.round(this.getDouble(param.get("totalCost")) / 1000000, 2));


		Map<String, Object> chart = new HashMap<String, Object>();

		Map<String, Object> xAxis = new HashMap<String, Object>();
		xAxis.put("data", param.get("xAxisData"));
		chart.put("xAxis", xAxis);

		List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();

		Map<String, Object> dataOfImpression = new HashMap<String, Object>();
		dataOfImpression.put("name", "impression");
		dataOfImpression.put("data", param.get("yAxisForImpressionNumber"));

		Map<String, Object> dataOfClick = new HashMap<String, Object>();
		dataOfClick.put("name", "click");
		dataOfClick.put("data", param.get("yAxisForClickNumber"));

		Map<String, Object> dataOfAvgOfClick = new HashMap<String, Object>();
		dataOfAvgOfClick.put("name", "ctr");
		dataOfAvgOfClick.put("data", param.get("yAxisForAvgOfClickNumber"));

		Map<String, Object> dataOfCost = new HashMap<String, Object>();
		dataOfCost.put("name", "cost");
		dataOfCost.put("data", param.get("yAxisForCost"));

		series.add(dataOfImpression);
		series.add(dataOfClick);
		series.add(dataOfAvgOfClick);
		series.add(dataOfCost);
		chart.put("series", series);

		((Map)param.get("data")).put("overall", overall);
		((Map)param.get("data")).put("chart", chart);

		return (Map)param.get("data");
	}

	/**
	 * 投放计划报表
	 * @param deviceType 投放平台类型
	 * @param campaignId 投放计划id
	 * @param startTime 开始时间戳
	 * @param endTime 结束时间戳
	 * @param page 页码
	 * @param number 每页行数
	 * @param productLine(售卖方式，支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReportOfCampaign(String deviceType, Integer campaignId, long startTime, long endTime, Integer orderDays,Integer page, Integer number , Integer productLine) throws Exception {
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		Map<String,Object> returnData = new HashMap<String,Object>();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = "";
		String endTimeStr = "";
		if (startTime != 0){
			startTimeStr = format.format(startTime);
		}
		if (endTime != 0){
			endTimeStr = format.format(endTime).substring(0, 10) + " 23:59:59";
		}

		Map<String, Object> list = new HashMap<String, Object>();

		List header = new ArrayList();

		header.add("点击日期");
		header.add("计划名称");
		header.add("设备类型");
		header.add("展现数");
		header.add("点击数");
		header.add("点击率");
		header.add("总费用(元)");
		header.add("ROI");
		if (3 == productLine) {
			header.add("平均点击单价");
			header.add("千次展现成本");
		}
		header.add("直接商品行");
		header.add("直接商品金额(元)");
		header.add("间接商品行");
		header.add("间接商品金额(元)");
		header.add("总商品行");
		header.add("总商品金额(元)");
		header.add("转化率");

		list.put("header", header);

		// 调用接口
		DmpFlightOperations dmpFlightOperations = new DmpFlightOperations();
		ApiResponse apiResponse = dmpFlightOperations.getFlightOrder(dspAdvertiserId.toString(), deviceType, campaignId, startTimeStr, endTimeStr, orderDays,page, number, productLine);

		List datas= new ArrayList();

		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		for (int i = 0; i < report.size(); i++) {

			List data= new ArrayList();
			data.add(this.getString(((Map) report.get(i)).get("clickTime")).length() >= 10
					? this.getString(((Map) report.get(i)).get("clickTime")).substring(0, 10)
					: "");
			data.add(this.getString(((Map) report.get(i)).get("orderName")));
			data.add(this.getString(((Map) report.get(i)).get("deviceType")));
			data.add(this.getInteger(((Map) report.get(i)).get("impressionNumber")).toString());
			data.add(this.getInteger(((Map) report.get(i)).get("clickNumber")).toString());
			data.add((this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map) report.get(i)).get("clickNumber")) / this.getDouble(((Map) report.get(i)).get("impressionNumber"))) * 100, 2)) + "%");
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalFee")) / 1000000, 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("totalFee")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")) / this.getDouble(((Map)report.get(i)).get("totalFee"))), 2)));
			if(3 == productLine){
				data.add(this.getDouble(((Map) report.get(i)).get("clickNumber")) == 0
						? 0
						:this.round(this.getDouble(((Map) report.get(i)).get("totalFee"))/(this.getDouble(((Map) report.get(i)).get("clickNumber"))*1000000),2));
				data.add(this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
						? 0
						:this.round((this.getDouble(((Map) report.get(i)).get("totalFee"))/(this.getDouble(((Map) report.get(i)).get("impressionNumber"))*1000000))*1000,2));
			}
			data.add(this.getInteger(((Map)report.get(i)).get("directProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("directProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("indirectProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("indirectProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("totalProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")), 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("clickNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineNumber")) / this.getDouble(((Map)report.get(i)).get("clickNumber"))) * 100, 2)) + "%");

			datas.add(data);
		}
		list.put("data", datas);

		returnData.put("list", list);
		returnData.put("page", page);
		returnData.put("number", number);
		returnData.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));

		return returnData;
	}

	/**
	 * 投放单元报表
	 * @param deviceType 投放平台类型
	 * @param campaignId 投放计划id
	 * @param flightId 投放单元id 
	 * @param startTime 开始时间戳
	 * @param endTime 结束时间戳
	 * @param page 页码
	 * @param number 每页行数
	 * @param productLine(售卖方式，支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReportOfFlight(String deviceType, Integer campaignId, Integer flightId, long startTime, long endTime, Integer orderDays, Integer page, Integer number, Integer sellType) throws Exception {
		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		Map<String,Object> returnData = new HashMap<String,Object>();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = "";
		String endTimeStr = "";
		if (startTime != 0){
			startTimeStr = format.format(startTime);
		}
		if (endTime != 0){
			endTimeStr = format.format(endTime).substring(0, 10) + " 23:59:59";
		}

		Map<String, Object> list = new HashMap<String, Object>();

		List header = new ArrayList();

		header.add("点击日期");
		header.add("计划名称");
		header.add("单元名称");
		header.add("设备类型");
		header.add("展现数");
		header.add("点击数");
		header.add("点击率");
		header.add("总费用(元)");
		header.add("ROI");
		if(3 == sellType){
			header.add("平均点击单价");
			header.add("千次展现成本");
		}
		header.add("直接商品行");
		header.add("直接商品金额(元)");
		header.add("间接商品行");
		header.add("间接商品金额(元)");
		header.add("总商品行");
		header.add("总商品金额(元)");
		header.add("转化率");

		list.put("header", header);

		// 调用接口
		DmpFlightOperations dmpFlightOperations = new DmpFlightOperations();
		ApiResponse apiResponse = dmpFlightOperations.getFlightUnit(dspAdvertiserId.toString(), deviceType, campaignId, flightId, startTimeStr, endTimeStr,orderDays, page, number,sellType);

		List datas= new ArrayList();

		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		for (int i = 0; i < report.size(); i++) {
			List data= new ArrayList();
			data.add(this.getString(((Map) report.get(i)).get("clickTime")).length() >= 10
					? this.getString(((Map) report.get(i)).get("clickTime")).substring(0, 10)
					: "");
			data.add(this.getString(((Map) report.get(i)).get("orderName")));
			data.add(this.getString(((Map) report.get(i)).get("flightName")));
			data.add(this.getString(((Map) report.get(i)).get("deviceType")));
			data.add(this.getInteger(((Map) report.get(i)).get("impressionNumber")).toString());
			data.add(this.getInteger(((Map) report.get(i)).get("clickNumber")).toString());
			data.add((this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map) report.get(i)).get("clickNumber")) / this.getDouble(((Map) report.get(i)).get("impressionNumber"))) * 100, 2)) + "%");
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalFee")) / 1000000, 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("totalFee")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")) / this.getDouble(((Map)report.get(i)).get("totalFee"))), 2)));
			if (3 == sellType) {
				data.add(this.getDouble(((Map) report.get(i)).get("clickNumber")) == 0
						? 0
						:this.round(this.getDouble(((Map) report.get(i)).get("totalFee"))
						/ (this.getDouble(((Map) report.get(i)).get("clickNumber")) * 1000000), 2));
				data.add(this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
						? 0
						:this.round((this.getDouble(((Map) report.get(i)).get("totalFee"))
						/ (this.getDouble(((Map) report.get(i)).get("impressionNumber")) * 1000000)) * 1000, 2));
			}
			data.add(this.getInteger(((Map)report.get(i)).get("directProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("directProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("indirectProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("indirectProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("totalProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")), 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("clickNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineNumber")) / this.getDouble(((Map)report.get(i)).get("clickNumber"))) * 100, 2)) + "%");

			datas.add(data);
		}
		list.put("data", datas);

		returnData.put("list", list);
		returnData.put("page", page);
		returnData.put("number", number);
		returnData.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));

		return returnData;
	}

	/**
	 * 投放素材报表
	 * @param deviceType 投放平台类型
	 * @param campaignId 投放计划id
	 * @param flightId 投放单元id
	 * @param materialId 素材id  
	 * @param startTime 开始时间戳
	 * @param endTime 结束时间戳
	 * @param page 页码
	 * @param number 每页行数
	 * @param productLine(售卖方式，支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReportOfMaterialId(String deviceType, Integer campaignId, Integer flightId, Integer materialId, long startTime, long endTime,int orderDays, Integer page, Integer number, Integer productLine) throws Exception {
		Map<String,Object> returnData = new HashMap<String,Object>();

		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = "";
		String endTimeStr = "";
		if (startTime != 0){
			startTimeStr = format.format(startTime);
		}
		if (endTime != 0){
			endTimeStr = format.format(endTime).substring(0, 10) + " 23:59:59";
		}

		Map<String, Object> list = new HashMap<String, Object>();

		List header = new ArrayList();

		header.add("点击日期");
		header.add("计划名称");
		header.add("单元名称");
		header.add("创意名称");
		header.add("设备类型");
		header.add("展现数");
		header.add("点击数");
		header.add("点击率");
		header.add("总费用(元)");
		header.add("ROI");
		if (3 == productLine) {
			header.add("平均点击单价");
			header.add("千次展现成本");
		}
		header.add("直接商品行");
		header.add("直接商品金额(元)");
		header.add("间接商品行");
		header.add("间接商品金额(元)");
		header.add("总商品行");
		header.add("总商品金额(元)");
		header.add("转化率");

		list.put("header", header);



		// 调用接口
		DmpFlightOperations dmpFlightOperations = new DmpFlightOperations();
		ApiResponse apiResponse = dmpFlightOperations.getFlightMaterial(dspAdvertiserId.toString(), deviceType, campaignId, flightId, materialId, startTimeStr, endTimeStr, orderDays, page, number,productLine);

		List datas= new ArrayList();

		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		for (int i = 0; i < report.size(); i++) {
			List data= new ArrayList();
			data.add(this.getString(((Map) report.get(i)).get("clickTime")).length() >= 10
					? this.getString(((Map) report.get(i)).get("clickTime")).substring(0, 10)
					: "");
			data.add(this.getString(((Map) report.get(i)).get("orderName")));
			data.add(this.getString(((Map) report.get(i)).get("flightName")));
			data.add(this.getString(((Map) report.get(i)).get("materialName")));
			data.add(this.getString(((Map) report.get(i)).get("deviceType")));
			data.add(this.getInteger(((Map) report.get(i)).get("impressionNumber")).toString());
			data.add(this.getInteger(((Map) report.get(i)).get("clickNumber")).toString());
			data.add((this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map) report.get(i)).get("clickNumber")) / this.getDouble(((Map) report.get(i)).get("impressionNumber"))) * 100, 2)) + "%");
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalFee")) / 1000000, 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("totalFee")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")) / this.getDouble(((Map)report.get(i)).get("totalFee"))), 2)));
			if (3 == productLine) {
				data.add(this.getDouble(((Map) report.get(i)).get("clickNumber")) == 0
						? 0
						:this.round(this.getDouble(((Map) report.get(i)).get("totalFee"))
						/ (this.getDouble(((Map) report.get(i)).get("clickNumber")) * 1000000), 2));
				data.add(this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
						? 0
						:this.round((this.getDouble(((Map) report.get(i)).get("totalFee"))
						/ (this.getDouble(((Map) report.get(i)).get("impressionNumber")) * 1000000)) * 1000, 2));
			}
			data.add(this.getInteger(((Map)report.get(i)).get("directProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("directProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("indirectProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("indirectProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("totalProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")), 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("clickNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineNumber")) / this.getDouble(((Map)report.get(i)).get("clickNumber"))) * 100, 2)) + "%");

			datas.add(data);
		}
		list.put("data", datas);

		returnData.put("list", list);
		returnData.put("page", page);
		returnData.put("number", number);
		returnData.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));

		return returnData;
	}

	/**
	 * 投放报表--关键词
	 * @param keywordId
	 * @param deviceType
	 * @param campaignId
	 * @param flightId
	 * @param materialId
	 * @param startTime
	 * @param endTime
	 * @param page
	 * @param number
	 * @param productLine(售卖方式，支持CPD:0,CPM:1,CPC:2，BID_CPC = 3)
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReportOfKeyword(Integer keywordId,String deviceType, Integer campaignId, Integer flightId, long startTime, long endTime, int orderDays, Integer page, Integer number) throws Exception {
		Map<String,Object> returnData = new HashMap<String,Object>();

		Integer dspAdvertiserId = PrincipalUtil.getDspAdvertiserId();;
		if (dspAdvertiserId <= 0) {
			throw new InvalidSessionException("dspAdvertiserId非法, 小于等于零");
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startTimeStr = "";
		String endTimeStr = "";
		if (startTime != 0){
			startTimeStr = format.format(startTime);
		}
		if (endTime != 0){
			endTimeStr = format.format(endTime).substring(0, 10) + " 23:59:59";
		}

		Map<String, Object> list = new HashMap<String, Object>();

		List header = new ArrayList();

		header.add("点击日期");
		header.add("关键词");
		header.add("设备类型");
		header.add("展现数");
		header.add("点击数");
		header.add("点击率");
		header.add("总费用(元)");
		header.add("ROI");
		header.add("平均点击单价");
		header.add("千次展现成本");
		header.add("直接商品行");
		header.add("直接商品金额(元)");
		header.add("间接商品行");
		header.add("间接商品金额(元)");
		header.add("总商品行");
		header.add("总商品金额(元)");

		list.put("header", header);
		// 调用接口
		DmpFlightOperations dmpFlightOperations = new DmpFlightOperations();
		ApiResponse apiResponse = dmpFlightOperations.getFlightKeyword(dspAdvertiserId.toString(), keywordId, deviceType, campaignId, flightId, startTimeStr, endTimeStr,orderDays, page, number);

		List datas= new ArrayList();

		ArrayList report = (ArrayList)apiResponse.getData().get("report");

		for (int i = 0; i < report.size(); i++) {
			List data= new ArrayList();
			data.add(this.getString(((Map) report.get(i)).get("clickTime")).length() >= 10
					? this.getString(((Map) report.get(i)).get("clickTime")).substring(0, 10)
					: "");
			data.add(this.getString(((Map) report.get(i)).get("keywordName")));
			data.add(this.getString(((Map) report.get(i)).get("deviceType")));
			data.add(this.getInteger(((Map) report.get(i)).get("impressionNumber")).toString());
			data.add(this.getInteger(((Map) report.get(i)).get("clickNumber")).toString());
			data.add((this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
					? 0
					: this.round((this.getDouble(((Map) report.get(i)).get("clickNumber")) / this.getDouble(((Map) report.get(i)).get("impressionNumber"))) * 100, 2)) + "%");
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalFee")) / 1000000, 2).toString());
			data.add((this.getDouble(((Map)report.get(i)).get("totalFee")) == 0
					? 0
					: this.round((this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")) / this.getDouble(((Map)report.get(i)).get("totalFee"))), 2)));
			data.add(this.getDouble(((Map) report.get(i)).get("clickNumber")) == 0
					? 0:this.round(this.getDouble(((Map) report.get(i)).get("totalFee"))/(this.getDouble(((Map) report.get(i)).get("clickNumber")) * 1000000),2));
			data.add(this.getDouble(((Map) report.get(i)).get("impressionNumber")) == 0
					? 0
					:this.round((this.getDouble(((Map) report.get(i)).get("totalFee"))/(this.getDouble(((Map) report.get(i)).get("impressionNumber")) * 1000000))*1000,2));
			data.add(this.getInteger(((Map)report.get(i)).get("directProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("directProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("indirectProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("indirectProductLineAmount")), 2).toString());
			data.add(this.getInteger(((Map)report.get(i)).get("totalProductLineNumber")).toString());
			data.add(this.round(this.getDouble(((Map)report.get(i)).get("totalProductLineAmount")), 2).toString());
			datas.add(data);
		}
		list.put("data", datas);

		returnData.put("list", list);
		returnData.put("page", page);
		returnData.put("number", number);
		returnData.put("totalCount", this.getInteger(apiResponse.getData().get("totalNumber")));

		return returnData;
	}
	
	/**
	 * 从object中获取Integer
	 * @param object
	 * @return
	 */
	private Integer getInteger(Object object) {
		if (object == null) {
			return 0;
		}

		Integer target = 0;

		try {
			Double origin = Double.parseDouble(object.toString().trim());
			target = origin.intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return target;
	}

	/**
	 * 从object中获取Double
	 * @param object
	 * @return
	 */
	private Double getDouble(Object object) {
		if (object == null) {
			return 0.0;
		}

		Double target = 0.0;

		try {
			target = Double.parseDouble(object.toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return target;
	}

	/**
	 * 从object中获取String
	 * @param object
	 * @return
	 */
	private String getString(Object object) {
		if (object == null) {
			return "";
		}

		return object.toString().trim();
	}

	/**
	 * 从object中获取orderType的状态名称
	 * @return
	 */
	private String getNameOfOrderType(Object object) {
		if (object == null) {
			return "";
		}

		String target = "";

		try {
			Double origin = Double.parseDouble(object.toString().trim());
			Integer orderType = origin.intValue();

			if (orderType == 1) {
				target = "影响订单";
			} else if (orderType == 2) {
				target = "间接订单";
			} else if (orderType == 3) {
				target = "直接订单";
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return target;
	}

	/**
	 * 从object中获取orderStatus的状态名称
	 * @param object
	 * @return
	 */
	private String getNameOfOrderStatus(Object object) {
		if (object == null) {
			return "待付款";
		}

		String target = "";

		try {
			Double origin = Double.parseDouble(object.toString().trim());
			Integer orderStatus = origin.intValue();

			if (orderStatus == 0) {
				target = "待付款";
			} else if (orderStatus == 1) {
				target = "买家已付款";
			} else if (orderStatus == 11) {
				target = "参团待确认";
			} else if (orderStatus == 2) {
				target = "卖家已发货";
			} else if (orderStatus == 3) {
				target = "交易成功";
			} else if (orderStatus == -1) {
				target = "取消待处理";
			} else if (orderStatus == -2 || orderStatus == -6) {
				target = "交易关闭";
			} else if (orderStatus == -5) {
				target = "拒收入库";
			} else if (orderStatus == -10) {
				target = "退款中";
			} else if (orderStatus == -12) {
				target = "用户拒收";
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return target;
	}
}