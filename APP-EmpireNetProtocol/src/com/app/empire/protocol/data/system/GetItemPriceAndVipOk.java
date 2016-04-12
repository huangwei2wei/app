package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetItemPriceAndVipOk extends AbstractData {

	private int	discount; //物品购买折扣（没有折扣是100，例如：95折是95）
	private String	unitPrice; //相关物品单价，格式 "商品1Id":单价,"商品2Id":单价……


    public GetItemPriceAndVipOk(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetItemPriceAndVipOk, sessionId, serial);
    }

    public GetItemPriceAndVipOk() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetItemPriceAndVipOk);
    }

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
    
    
}
