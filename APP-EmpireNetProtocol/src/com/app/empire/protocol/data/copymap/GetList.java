package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 获取过关副本记录
 * 
 * @author doter
 * 
 */
public class GetList extends AbstractData {
	private int copyType;// 获取副本类型，1主线副本
	private int skip;// 开始位置,下标0开始
	private int limit;// 返回条数

	public GetList(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetList, sessionId, serial);
	}

	public GetList() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetList);
	}

	public int getCopyType() {
		return copyType;
	}

	public void setCopyType(int copyType) {
		this.copyType = copyType;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
