package com.app.server.util;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.app.server.bean.ServerConfigBean;
/**
 * 管理random.xml文件
 * 
 * @author Administrator
 */
public class RandomUtils {
    private static final String fileName = Thread.currentThread().getContextClassLoader().getResource("serviceinfo.xml").getPath();


    /**
     * 更新指定服务器的公告
     * 
     * @param push
     */
    @SuppressWarnings({ "rawtypes"})
    public static void updateBulletin(String area, String machine, String value) {
        try {
            File inputXml = new File(fileName);
            SAXReader saxReader = new SAXReader();
            saxReader.setEncoding("UTF-8");
            Document document = saxReader.read(inputXml);
            Element employees = document.getRootElement();
            for (Iterator areaIterator = employees.elementIterator(); areaIterator.hasNext();) {
                Element areaEmployee = (Element) areaIterator.next();
                if (area.equals(areaEmployee.attributeValue("id"))) {
                    for (Iterator machineIterator = areaEmployee.elementIterator(); machineIterator.hasNext();) {
                        Element machineEmployee = (Element) machineIterator.next();
                        if (machine.equals(machineEmployee.attributeValue("id"))) {
                            machineEmployee.attributeValue("bulletin", value);
                            break;
                        }
                    }
                    break;
                }
            }
            OutputFormat outFmt = new OutputFormat("\t", true);
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
}
