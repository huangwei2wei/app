package com.app.empire.world.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 自定义加解密算法
 * 
 * @author Administrator
 */
public class CryptionUtil {
	/**
	 * 加密
	 * 
	 * @param txt
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] Encrypt(String txt, String key) throws UnsupportedEncodingException {
		byte[] bs = txt.getBytes("utf-8"); // 原字符串转换成字节数组
		byte[] keys = key.getBytes("utf-8"); // 密钥转换成字节数组
		int iRandom = (int) (Math.random() * bs.length) & 0xff;// 获取随机key的位置
		byte KRandom = (byte) (Math.random() * 255);// 获取随机key
		byte[] rb = new byte[bs.length + 2];
		// 异或
		for (int i = 0; i < bs.length; i++) {
			if (i < iRandom) {
				bs[i] = (byte) (bs[i] ^ keys[i % keys.length]);
				rb[i] = (byte) (bs[i] ^ KRandom);
			} else {
				if (i == iRandom)
					rb[i] = KRandom;
				bs[i] = (byte) (bs[i] ^ keys[i % keys.length]);
				rb[i + 1] = (byte) (bs[i] ^ KRandom);
			}
		}
		rb[rb.length - 1] = (byte) iRandom;
		return rb;
	}

	/**
	 * 解密
	 * 
	 * @param txt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String Decrypt(byte[] bs, String key) throws UnsupportedEncodingException {
		byte[] keys = key.getBytes("utf-8"); // 密钥转换成字节数组
		int iRandom = bs[bs.length - 1] & 0xff;
		byte KRandom = bs[iRandom];
		byte[] rb = new byte[bs.length - 2];
		// 异或
		for (int i = 0; i < rb.length; i++) {
			if (i < iRandom) {
				bs[i] = (byte) (bs[i] ^ KRandom);
				rb[i] = (byte) (bs[i] ^ keys[i % keys.length]);
			} else {
				bs[i + 1] = (byte) (bs[i + 1] ^ KRandom);
				rb[i] = (byte) (bs[i + 1] ^ keys[i % keys.length]);
			}
		}
		// byte数组还原成字符串
		return new String(rb, "utf-8");
	}

	/**
	 * 将byte[]转成16进制String
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte getByteFromHexString(char c) {
		char[] mark = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		for (byte i = 0; i < mark.length; i++) {
			if (mark[i] == c) {
				return i;
			}
		}
		return 0;
	}

	public static byte[] getByteFromHexString(String data) {
		String[] datas = data.split(",");
		byte[] logData = new byte[datas.length];
		for (int i = 0; i < logData.length; i++) {
			byte a = getByteFromHexString(datas[i].charAt(0));
			byte b = getByteFromHexString(datas[i].charAt(1));
			logData[i] = (byte) (((a & 0xf) << 4) | (b & 0xf));
		}
		return logData;
	}

	public static final byte[] inputStream2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
}
