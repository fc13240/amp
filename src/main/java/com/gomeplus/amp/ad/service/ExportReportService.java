package com.gomeplus.amp.ad.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellValue;
import org.springframework.stereotype.Service;
/**
 * 导出表的服务
 * @author xiaogengen
 *
 */
@Service
public class ExportReportService {
	/**
	 * 生成excel表格
	 * @param data
	 * @param res
	 * @throws Exception
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void exportExcel(Map<String, Object> data,HttpServletResponse  res,String fileName) throws Exception{
		// 第一步，创建一个webbook，对应一个Excel文件  
		HSSFWorkbook wb = new HSSFWorkbook();  
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
		HSSFSheet sheet = wb.createSheet("一");  
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
		HSSFRow row = sheet.createRow((int) 0);  
		// 第四步，创建单元格，并设置值表头 设置表头居中  
		HSSFCellStyle style = wb.createCellStyle();  
		// 创建一个居中格式 
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
		style.setWrapText(true);
		HSSFCell cell = null;
		Map list = (Map) data.get("list");
		System.out.println(list);
		List header = (List) list.get("header");
		for (int i = 0; i < header.size(); i++) {
			cell = row.createCell((short) i);  
			cell.setCellValue((String)header.get(i));  
			cell.setCellStyle(style);            
		}
		
		List datas = (List) list.get("data");
		for (int i = 0; i < datas.size(); i++) {  
		row = sheet.createRow((int) i + 1);  
		// 第四步，创建单元格，并设置值  
		List datass = (List) datas.get(i);
		for (int j = 0; j < datass.size(); j++) {
			 row.createCell((short) j).setCellValue(String.valueOf(datass.get(j)) );  
		}
		}
		for (int i=0; i < header.size(); i++) {
			sheet.autoSizeColumn((short)i); 
		}
		
		String tmpPath = System.getProperty("java.io.tmpdir");
		File file = new File(tmpPath + File.separator + fileName);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		// 写入文件
		wb.write(fileOut);
		fileOut.close();
		
		fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
		InputStream is = new ByteArrayInputStream(getBytes(file));
		// 设置response参数，可以打开下载页面
		
		res.setContentType("application/x-excel;charset=utf-8");
		res.addHeader("Content-Disposition","attachment;filename="
				+ new String((fileName + ".xls").getBytes(), "utf-8"));
		res.setHeader("Content-Transfer-Encoding", "binary");
		ServletOutputStream out = res.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}

	} 
	/**
	 * 获取字节流数组
	 * @param file
	 * @return
	 */
	private  byte[] getBytes(File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
