package com.app.protocol.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.app.net.ProtocolFactory;
import com.app.protocol.INetData;

public class DataBeanDecoder {
	private static Logger log = Logger.getLogger(DataBeanDecoder.class);

	public AbstractData decode(INetData data) throws Exception {
		short type = data.getType();
		short subType = data.getSubType();
		// byte flag = data.getFlag();
		AbstractData d = null;
		// System.out.println("type:"+type+",subType:"+subType);
		// if ((flag & 0x1) != 0) {
		// d = ProtocolFactory.getProtocolDataBean((byte) -1, (byte) -1);
		// d.setType(type);
		// d.setSubType(subType);
		// log.debug("flag不为0，数据异常:" + ProtocolFactory.getProtocolDataBean(type, subType).getClass().getName());
		// return d;
		// } else {
		if (data.getProType() == 0) {
			d = ProtocolFactory.getProtocolDataBean(type, subType);
		} else {
			d = new PbAbstractData(type, subType);
			d.setProType(data.getProType());
		}
		if (d == null)
			log.error("***未定义的类型：" + Integer.toHexString(type) + ".0x" + Integer.toHexString(subType));
		else {
			log.debug("***接收消息： " + d.getClass().getName());
		}
		// }
		if (d != null) {
			try {
				d.setSerial(data.getSerial());
				d.setSessionId(data.getSessionId());
				Field[] fs = d.getClass().getDeclaredFields();
				for (Field f : fs) {
					PropertyUtils.setProperty(d, f.getName(), getValue(data, f));
				}
				System.out.println(System.currentTimeMillis() + " 收到数据：" + data.toBytes().length + " byte，  " + data.toString());
			} catch (Exception ex) {
				System.out.println("解码错误，type:" + type + ",SubType:" + subType);
				log.error("解码错误，type:" + type + ",SubType:" + subType);
				throw new Exception(ex);
			}
		}
		return d;
	}

	public static Object getValue(INetData data, Field field) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		// String fieldName = field.getName();// 字段名
		String type = field.getType().getSimpleName();// 类型
		switch (type) {
			case "byte":
				return Byte.valueOf(data.readByte());
			case "byte[]":
				return data.readBytes();
			case "short":
				return Short.valueOf(data.readShort());
			case "short[]":
				return data.readShorts();
			case "int":
				return Integer.valueOf(data.readInt());
			case "int[]":
				return data.readInts();
			case "long":
				return Long.valueOf(data.readLong());
			case "long[]":
				return data.readLongs();
			case "boolean":
				return Boolean.valueOf(data.readBoolean());
			case "boolean[]":
				return data.readBooleans();
			case "String":
				return data.readString();
			case "String[]":
				return data.readStrings();
			case "List":
				return data.readList(field);
			default:// 其他类型(自定义)
				return data.readObj(field);
		}

		// if (type.equals("byte"))
		// return Byte.valueOf(data.readByte());
		// if (type.equals("byte[]"))
		// return data.readBytes();
		// if (type.equals("short"))
		// return Short.valueOf(data.readShort());
		// if (type.equals("short[]"))
		// return data.readShorts();
		// if (type.equals("int"))
		// return Integer.valueOf(data.readInt());
		// if (type.equals("int[]"))
		// return data.readInts();
		// if (type.equals("long"))
		// return Long.valueOf(data.readLong());
		// if (type.equals("long[]"))
		// return data.readLongs();
		// if (type.equals("boolean"))
		// return Boolean.valueOf(data.readBoolean());
		// if (type.equals("boolean[]"))
		// return data.readBooleans();
		// if (type.endsWith("String"))
		// return data.readString();
		// if (type.endsWith("String[]"))
		// return data.readStrings();
		// if (type.endsWith("List")) // 列表结构
		// return data.readList(field);

		// throw new IllegalAccessException("fieldName:" + fieldName + ",type:" + type + ",data:" + data);
	}

}
