package com.app.protocol;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract interface INetData {
	public abstract boolean needCompress();

	public abstract int getSessionId();

	public abstract int getSerial();

	public abstract byte getType();

	public abstract byte getSubType();

	public abstract int getNumOfParameter();

	public abstract boolean readBoolean() throws IllegalAccessException;

	public abstract boolean[] readBooleans() throws IllegalAccessException;

	public abstract byte readByte() throws IllegalAccessException;

	public abstract byte[] readBytes() throws IllegalAccessException;

	public abstract short readShort() throws IllegalAccessException;

	public abstract short[] readShorts() throws IllegalAccessException;

	public abstract int readInt() throws IllegalAccessException;

	public abstract int[] readInts() throws IllegalAccessException;

	public abstract long readLong() throws IllegalAccessException;

	public abstract long[] readLongs() throws IllegalAccessException;

	public abstract String readString() throws IllegalAccessException;

	public abstract String[] readStrings() throws IllegalAccessException;

	public abstract Object readObj(Field field) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,ClassNotFoundException;

	public abstract List<Object> readList(Field field) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,ClassNotFoundException;

//	public abstract byte getFlag();

	public abstract byte[] toBytes();
}
