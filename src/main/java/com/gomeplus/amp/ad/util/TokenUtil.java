package com.gomeplus.amp.ad.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * java Token 加密
 * 
 * @author zhangqian-ds8
 */
public class TokenUtil {

	/**
	 * 转换成16进制
	 * 
	 * @param b
	 * @return
	 */
	public static String bin2hex(byte[] b) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			if ((b[i] & 0xff) < 0x10)
				result.append("0");
			result.append(Long.toString(b[i] & 0xff, 16));
		}
		return result.toString().toUpperCase();
	}

	public static String createToken(String appname, String appKey) throws NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

		// 初始化随机向量,这里写死
		String ivNumber = "1234567812345678";

		// 数据 data : time|appname
		Date date = new Date();
		long time = date.getTime();
		String data = String.format("%s|%s", time, appname);
		// data = "1489718887171|selfShop"; // 测试这里data

		byte[] raw = appKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

		// AES - CBC 模式加密
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// iv 向量
		IvParameterSpec iv = new IvParameterSpec(ivNumber.getBytes());

		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

		byte[] srawt = data.getBytes();

		byte[] encrypted = cipher.doFinal(srawt);

		// 16进制
		String tokenHex = bin2hex(encrypted);
		String ivHex = bin2hex(ivNumber.getBytes());

		return ivHex + tokenHex;
	}

}
