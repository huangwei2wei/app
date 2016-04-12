package com.app.empire.protocol.data.purchase;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetRuleListOk extends AbstractData {
    private int[] price;
    private int[] ratio;

    public GetRuleListOk(int sessionId, int serial) {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_GetRuleListOk, sessionId, serial);
    }

    public GetRuleListOk() {
        super(Protocol.MAIN_PURCHASE, Protocol.PURCHASE_GetRuleListOk);
    }

    public int[] getPrice() {
        return price;
    }

    public void setPrice(int[] price) {
        this.price = price;
    }

    public int[] getRatio() {
        return ratio;
    }

    public void setRatio(int[] ratio) {
        this.ratio = ratio;
    }
}
