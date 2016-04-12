package com.app.empire.world.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class KeyProcessService {
	private static KeyProcessService instance = null;
	private List<KeyProcess> keyProcesses;
	private Document xmldoc;
	private String fileName;
	private long lastModified;
	private File file;

	public static KeyProcessService getInstance() throws DocumentException {
		if (instance == null) {
			instance = new KeyProcessService();
		}
		return instance;
	}

	public KeyProcessService() throws DocumentException {
		reloadFile();
	}

	// 重新读取文件
	public void reloadFile() throws DocumentException {
		fileName = Thread.currentThread().getContextClassLoader().getResource("keyprocess.xml").getPath();
		if (file == null) {
			file = new File(fileName);
		}
		lastModified = file.lastModified();
		SAXReader reader = new SAXReader();
		xmldoc = reader.read(file);
		loadSource();
		System.out.println("reload keyprocess");
	}

	private void loadSource() throws DocumentException {
		keyProcesses = new ArrayList<KeyProcess>();
		Element root = xmldoc.getRootElement();
		@SuppressWarnings("unchecked")
		Iterator<Element> elementIterator = root.elementIterator("process");
		while (elementIterator.hasNext()) {
			Element keyElem = elementIterator.next();
			String value = keyElem.getText();
			String name = keyElem.attributeValue("name");
			keyProcesses.add(new KeyProcess(name, value));
		}
	}

	public List<KeyProcess> getKeyProcesses() {
		return keyProcesses;
	}

	/**
	 * 增加一条外挂规则
	 * 
	 * @param pushId
	 * @param lostTime
	 * @param message
	 * @param isRepeat
	 */
	public void addProcess(String name, String value) {
		try {
			if (isFileModified()) {
				reloadFile();
			}
			Element keyprocess = xmldoc.getRootElement();
			Element process = keyprocess.addElement("process");
			process.addAttribute("name", name);
			process.addText(value);
			xmlWriter();
			loadSource();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除一条记录
	 * 
	 * @param value
	 */
	public void removeProcess(String value) {
		try {
			if (isFileModified()) {
				reloadFile();
			}
			Element keyprocess = xmldoc.getRootElement();
			for (Iterator<?> i = keyprocess.elementIterator(); i.hasNext();) {
				Element process = (Element) i.next();
				if (value.equals(process.getTextTrim())) {
					keyprocess.remove(process);
					xmlWriter();
					loadSource();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void xmlWriter() throws IOException {
		OutputFormat outFmt = new OutputFormat();
		outFmt.setEncoding("UTF-8");
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(fileName), outFmt);
		xmlWriter.write(xmldoc);
		xmlWriter.close();
	}

	public static void main(String[] args) {
		KeyProcessService s;
		try {
			s = KeyProcessService.getInstance();
			s.getKeyProcesses();
			s.removeProcess("test1");
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	private boolean isFileModified() {
		long t = file.lastModified();
		if (t != lastModified) {
			lastModified = t;
			return true;
		}
		return false;
	}
}
