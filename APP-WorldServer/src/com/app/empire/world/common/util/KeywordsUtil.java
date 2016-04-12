package com.app.empire.world.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 类 <code>KeywordsUtil</code>关键字以及无效名称过滤基类
 * 
 * @since JDK 1.6
 */
public class KeywordsUtil {
	private static Logger log = Logger.getLogger(KeywordsUtil.class);
	private static Pattern[] patterns = null;
	/**
	 * 静态块，加载文件
	 */
	static {
		try {
			loadKeywords(new File(Thread.currentThread().getContextClassLoader().getResource("keywords.xml").getPath()));
			patterns = loadPatterns(new File(Thread.currentThread().getContextClassLoader().getResource("invalidname.txt").getPath()));
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex, ex);
		}
	}

	/**
	 * 加载无效名称文件
	 * 
	 * @param file 无效名称文件
	 * @return Pattern[]
	 * @throws Exception 当无效文件不存在时抛出异常
	 */
	private static Pattern[] loadPatterns(File file) throws Exception {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			Vector<Pattern> retList = new Vector<Pattern>();
			String line;
			while ((line = br.readLine()) != null) {
				retList.add(Pattern.compile(line));
			}
			Pattern[] ret = new Pattern[retList.size()];
			retList.toArray(ret);
			return ret;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			br.close();
			fr.close();
		}
	}

	/**
	 * 判断名称是否合法
	 * 
	 * @param name 名称
	 * @return <tt>true</tt>不合法名称<br>
	 *         <tt>false</tt>合法名称
	 */
	public static boolean isInvalidName(String name) {
		// System.out.println("判断名称是否合法: " + name);
		for (int i = 0; i < patterns.length; ++i) {
			if (patterns[i].matcher(name).matches()) {
				// System.out.println(patterns[i]);
				return true;
			}
		}
		return false;
	}

	/**
	 * 加载并解析关键字文件
	 * 
	 * @param file 关键字文件
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void loadKeywords(File file) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(file);
		Element root = doc.getRootElement();
		Vector<Keyword> keywordList = new Vector<Keyword>();
		Iterator<Element> elementIterator = root.elementIterator("keyword");
		KeyWordsState.root = new KeyWordsState();
		while (elementIterator.hasNext()) {
			Element keyElem = elementIterator.next();
			String keyword = keyElem.getText();
			String replace = keyElem.attributeValue("replacement");
			keywordList.add(new Keyword(keyword, replace));
			KeyWordsState.addString(keyword);
		}
		KeyWordsState.init();
	}

	/**
	 * 过滤并把字符串中包含的关键字代替成X符号
	 * 
	 * @param str 字符串
	 * @return 完成过滤代替后的字符串
	 */
	public static String filterKeywords(String str) {
		HashMap<Integer, Integer> map = KeyWordsState.match(str);
		Object[] keys = map.keySet().toArray();
		int size = keys.length;
		char[] chars = str.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int length = chars.length;
		int j = 0;
		if (size != 1) {
			// Modify by sunzx 序号排序
			// List list = Arrays.asList(keys);
			// Collections.sort(list);
			List<Object> list = Arrays.asList(keys);
			Collections.sort(list, new Comparator<Object>() {
				public int compare(Object o1, Object o2) {
					Integer order1 = (Integer) o1;
					Integer order2 = (Integer) o2;
					return (order1 - order2);
				}
			});
			keys = list.toArray();
		}
		if (size > 0) {
			Integer index = (Integer) keys[j];
			Integer strLength = map.get(index);
			for (int i = 0; i < length; ++i) {
				if ((i == index.intValue()) && (j < size)) {
					for (int m = 0; m < strLength.intValue(); ++m) {
						buffer.append('X');
					}
					i = i + strLength.intValue() - 1;
					++j;
					if (j < size) {
						index = (Integer) keys[j];
					}
					strLength = map.get(index);
				} else {
					buffer.append(chars[i]);
				}
			}
			str = buffer.toString();
		}
		return str;
	}
}
