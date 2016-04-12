package com.app.empire.world.common.util;

import java.io.File;
import java.io.FileOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 服务器版本配置
 * 
 * @author Administrator
 */
public class VersionUtils {
	private static final String fileName = Thread.currentThread().getContextClassLoader().getResource("version.xml").getPath();

	/**
	 * 更新知道的值
	 * 
	 * @param push
	 */
	public synchronized static void update(String key, String value) {
		try {
			File inputXml = new File(fileName);
			SAXReader saxReader = new SAXReader();
			saxReader.setEncoding("UTF-8");
			Document document = saxReader.read(inputXml);
			Element employee = document.getRootElement();
			employee.element(key).setText(value);
			OutputFormat outFmt = new OutputFormat("", true);
			outFmt.setEncoding("UTF-8");
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(fileName), outFmt);
			xmlWriter.write(document);
			xmlWriter.close();
			document = null;
			inputXml = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询指定的值
	 * 
	 * @param pushId
	 * @return
	 */
	public static String select(String key) {
		File inputXml = new File(fileName);
		if (!inputXml.exists()) {
			return null;
		}
		String ret = null;
		try {
			SAXReader saxReader = new SAXReader();
			saxReader.setEncoding("UTF-8");
			Document document = saxReader.read(inputXml);
			Element employee = document.getRootElement();
			ret = employee.element(key).getText();
			document = null;
			inputXml = null;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
