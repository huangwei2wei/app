package com.app.empire.protocol.data.backpack;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class UseGoodsOk extends AbstractData {
	
	private String[] key; //使用获得的资源或者物品(物品对应goodsId的数值，资源对应资源名:"heroExp","Exp","gold",等)
	private int[] value;  //物品的數量或者資源的值
	
	public UseGoodsOk(int sessionId, int serial) {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_UseGoodsOk, sessionId, serial);
	}
	public UseGoodsOk() {
		super(Protocol.MAIN_BACKPACK, Protocol.BACKPACK_UseGoodsOk);
	}
	
	public String[] getKey() {
		return key;
	}
	public void setKey(String[] key) {
		this.key = key;
	}
	public int[] getValue() {
		return value;
	}
	public void setValue(int[] value) {
		this.value = value;
	}
	
}
