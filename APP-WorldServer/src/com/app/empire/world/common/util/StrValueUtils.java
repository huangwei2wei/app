package com.app.empire.world.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 读取配置文件Value
 * 
 * @author Administrator
 */
public class StrValueUtils {
	private static StrValueUtils instance;
	private Properties prop;

	/**
	 * 构造函数，初始化相关值
	 */
	private StrValueUtils() {
		try {
			initProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化文件
	 * 
	 * @throws IOException
	 */
	public void initProperties() throws IOException {
		BufferedInputStream is = null;
		try {
			System.out.println("load str2value.properties ...");
			if (prop == null) {
				prop = new Properties();
			}
			is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("str2value.properties"));
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 根据KEY值，返回对应VALUE
	 * 
	 * @param key
	 *            KEY值
	 * @return 对应VALUE
	 */
	public String getValueByKey(String key) {
		return prop.getProperty(key);
	}

	/**
	 * 获取StrValueUtils Object
	 * 
	 * @return StrValueUtils Object
	 */
	public static StrValueUtils getInstance() {
		if (instance == null) {
			instance = new StrValueUtils();
		}
		return instance;
	}

	/**
	 * 写入
	 * 
	 * @param keyname
	 * @param keyvalue
	 */
	public void writeProperties(String keyname, String keyvalue) {
		OutputStream fos = null;
		try {
			// 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			fos = new FileOutputStream(System.getProperty("user.dir") + File.separator + "str2value.properties");
			prop.setProperty(keyname, keyvalue);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			prop.store(fos, "Update '" + keyname + "' value");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedInputStream is = null;
		try {
			Properties prop = null;
			if (prop == null) {
				prop = new Properties();
			}
			is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("str2valueVI.properties"));
			prop.load(is);
			System.out.println(prop.getProperty("HP"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
