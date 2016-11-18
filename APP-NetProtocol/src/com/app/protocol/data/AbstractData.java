package com.app.protocol.data;

import java.util.concurrent.atomic.AtomicInteger;

import com.app.net.IConnector;
import com.app.session.Session;

/**
 * 抽象类 AbstractData对应前后台协议接口常量定义类Protocol传输时产生的对应协议抽象bean数据
 * 
 * @author doter
 *
 */
public abstract class AbstractData {
	/** 消息的目的地 */
	protected byte					target;
	/** 协议类型 **/
	protected byte					proType;
	protected short					type;
	protected short					subType;
	protected IConnector			source;
	protected int					serial;
	protected int					sessionId;
	protected Session				handlerSource;
	private static AtomicInteger	staticSerial	= new AtomicInteger(0);

	public AbstractData(short type, short subType, int sessionId, int serial) {
		this.type = type;
		this.subType = subType;
		this.serial = serial;
		this.sessionId = sessionId;
	}

	public AbstractData(short type, short subType) {
		this.type = type;
		this.subType = subType;
		this.source = null;
		this.serial = getIncrementSerial();
		this.sessionId = -1;
	}

	public AbstractData(short type, short subType, int sessionId, int serial, byte target) {
		this.type = type;
		this.subType = subType;
		this.sessionId = sessionId;
		this.serial = serial;
		this.target = target;
	}

	public AbstractData(short type, short subType, byte target) {
		this.type = type;
		this.subType = subType;
		this.source = null;
		this.serial = getIncrementSerial();
		this.sessionId = -1;
		this.target = target;
	}

	public byte getTarget() {
		return target;
	}

	public void setTarget(byte target) {
		this.target = target;
	}

	public byte getProType() {
		return proType;
	}

	public void setProType(byte proType) {
		this.proType = proType;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public short getSubType() {
		return subType;
	}

	public void setSubType(short subType) {
		this.subType = subType;
	}

	public IConnector getSource() {
		return source;
	}

	public void setSource(IConnector source) {
		this.source = source;
	}

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public Session getHandlerSource() {
		return handlerSource;
	}

	public void setHandlerSource(Session handlerSource) {
		this.handlerSource = handlerSource;
	}

	public static AtomicInteger getStaticSerial() {
		return staticSerial;
	}

	public static void setStaticSerial(AtomicInteger staticSerial) {
		AbstractData.staticSerial = staticSerial;
	}

	public String getTypeString() {
		return this.type + "." + this.subType;
	}

	public static final int getIncrementSerial() {
		int i = staticSerial.incrementAndGet();
		if (i >= Integer.MAX_VALUE)
			staticSerial.set(0);
		return i;
	}

	public enum EnumTarget {
		WORLDSERVER((byte) 1, "发向world逻辑服务器"), SCENESSERVER((byte) 2, "发向场景服务器"), CLIENT((byte) 3, "发向场景服务器");

		private byte	value;
		private String	desc;

		private EnumTarget(byte v, String desc) {
			this.value = v;
			this.desc = desc;
		}

		public byte getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}
	}

	public enum EnumProType {
		COMMON((byte) 0, "自研协议"), PROBUFFER((byte) 1, "probuffer协议");

		private byte	value;
		private String	desc;

		private EnumProType(byte v, String desc) {
			this.value = v;
			this.desc = desc;
		}

		public byte getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}
	}

}
