package com.app.protocol.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.app.protocol.INetSegment;
import com.app.protocol.ProtocolManager;

public class DataBeanEncoder {
	private static Logger log = Logger.getLogger(DataBeanEncoder.class);

	// SimpleDateFormat sdf = new SimpleDateFormat("mm:ss:SSSS");
	public INetSegment encode(AbstractData data) throws Exception {
		try {
			INetSegment segment = ProtocolManager.getNetSegmentInstance(data.getType(), data.getSubType(), data.getSessionId(), data.getSerial(), data.getTarget());
			Field[] fs = data.getClass().getDeclaredFields();
			for (Field f : fs) {
				// String ftype = f.getType().getSimpleName();
				Object value = PropertyUtils.getProperty(data, f.getName());
				// if (value == null) {
				// System.out.println("type:" + data.getType() + "------------SubType:" + data.getSubType() + " PropertyName:" + f.getName() + " value is null");
				// } else if (value instanceof String[]) {
				// for (String str : (String[]) value) {
				// if (str == null) {
				// System.out.println("type:" + data.getType() + "------------SubType:" + data.getSubType() + " PropertyName:" + f.getName() + " value is null");
				// }
				// }
				// }
				setValue(segment, f, value);
			}

			// System.out.println("type:" + data.getType() + "------------SubType:"
			// + data.getSubType());
			// this.log.info("Send Msg —————— " + data.getClass().getSimpleName() +
			// " " + data.getType() + "," + data.getSubType());
			// System.out.println(sdf.format((new Date()))+"Send Msg —————— " +
			// data.getClass().getSimpleName());
			return segment;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("编码错误:" + e.getMessage());
			throw new Exception(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void setValue(INetSegment data, Field f, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// String fieldName = f.getName();
		String type = f.getType().getSimpleName();

		switch (type) {
			case "byte":
				data.write(((Byte) value).byteValue());
				break;
			case "byte[]":
				data.write((byte[]) (byte[]) value);
				break;
			case "short":
				data.writeShort(((Short) value).shortValue());
				break;
			case "short[]":
				data.writeShorts((short[]) (short[]) value);
				break;
			case "int":
				data.writeInt(((Integer) value).intValue());
				break;
			case "int[]":
				data.writeInts((int[]) (int[]) value);
				break;
			case "long":
				data.writeLong(((Long) value).longValue());
				break;
			case "long[]":
				data.writeLongs((long[]) (long[]) value);
				break;
			case "boolean":
				data.writeBoolean(((Boolean) value).booleanValue());
				break;
			case "boolean[]":
				data.writeBooleans((boolean[]) value);
				break;
			case "String":
				data.writeString((String) value);
				break;
			case "String[]":
				data.writeStrings((String[]) (String[]) value);
				break;
			case "List":
				data.writeList((List<Object>) value);
				break;
			default:// 其他类型(自定义)
				data.writeObj(value);
				break;
		}

		// if (type.equals("byte")) {
		// data.write(((Byte) value).byteValue());
		// } else if (type.equals("byte[]")) {
		// data.write((byte[]) (byte[]) value);
		// } else if (type.equals("short")) {
		// data.writeShort(((Short) value).shortValue());
		// } else if (type.equals("short[]")) {
		// data.writeShorts((short[]) (short[]) value);
		// } else if (type.equals("int")) {
		// data.writeInt(((Integer) value).intValue());
		// } else if (type.equals("int[]")) {
		// data.writeInts((int[]) (int[]) value);
		// } else if (type.equals("long")) {
		// data.writeLong(((Long) value).longValue());
		// } else if (type.equals("long[]")) {
		// data.writeLongs((long[]) (long[]) value);
		// } else if (type.equals("boolean")) {
		// data.writeBoolean(((Boolean) value).booleanValue());
		// } else if (type.equals("boolean[]")) {
		// data.writeBooleans((boolean[]) value);
		// } else if (type.endsWith("String")) {
		// data.writeString((String) value);
		// } else if (type.endsWith("String[]")) {
		// data.writeStrings((String[]) (String[]) value);
		// } else if (type.endsWith("List")) {
		// data.writeList((List<Object>) value);
		// } else {
		// log.info("类型错误，fieldName:" + fieldName + ",type:" + type + ",value:" + value);
		// throw new IllegalAccessException("类型错误，fieldName:" + fieldName + ",type:" + type + ",value:" + value);
		// }
	}
}
