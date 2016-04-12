package com.app.empire.protocol.data.copymap;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetListOK extends AbstractData {
	private int[] id;// 流水id
	private int[] copyMapId;// 副本id
	private int[] copyType;// 副本类型
	private int[] status;// 状态0未完成，1完成
	private int[] star;// 星数

	public GetListOK(int sessionId, int serial) {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetListOK, sessionId, serial);
	}
	public GetListOK() {
		super(Protocol.MAIN_COPYMAP, Protocol.COPYMAP_GetListOK);
	}
	public int[] getId() {
		return id;
	}
	public void setId(int[] id) {
		this.id = id;
	}
	public int[] getCopyType() {
		return copyType;
	}
	public void setCopyType(int[] copyType) {
		this.copyType = copyType;
	}
	public int[] getStatus() {
		return status;
	}
	public void setStatus(int[] status) {
		this.status = status;
	}
	public int[] getCopyMapId() {
		return copyMapId;
	}
	public void setCopyMapId(int[] copyMapId) {
		this.copyMapId = copyMapId;
	}
	public int[] getStar() {
		return star;
	}
	public void setStar(int[] star) {
		this.star = star;
	}

}
