package com.app.empire.protocol.data.errorcode;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
public class GetSmsCodeListOk extends AbstractData {
    private int[]    id;
    private int[]    price;
    private String[] smsCode;
    private int[]    itemId;
    private String[] itemName;
    private String[] itemIcon;
    private int[]    countType;
    private int[]    count;
    private String[] remark1;
    private String[] remark2;

    public GetSmsCodeListOk(int sessionId, int serial) {
        super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetSmsCodeListOk, sessionId, serial);
    }

    public GetSmsCodeListOk() {
        super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetSmsCodeListOk);
    }

    public int[] getId() {
        return id;
    }

    public void setId(int[] id) {
        this.id = id;
    }

    public int[] getPrice() {
        return price;
    }

    public void setPrice(int[] price) {
        this.price = price;
    }

    public String[] getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String[] smsCode) {
        this.smsCode = smsCode;
    }

    public int[] getItemId() {
        return itemId;
    }

    public void setItemId(int[] itemId) {
        this.itemId = itemId;
    }

    public String[] getItemName() {
        return itemName;
    }

    public void setItemName(String[] itemName) {
        this.itemName = itemName;
    }

    public String[] getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String[] itemIcon) {
        this.itemIcon = itemIcon;
    }

    public int[] getCountType() {
        return countType;
    }

    public void setCountType(int[] countType) {
        this.countType = countType;
    }

    public int[] getCount() {
        return count;
    }

    public void setCount(int[] count) {
        this.count = count;
    }

    public String[] getRemark1() {
        return remark1;
    }

    public void setRemark1(String[] remark1) {
        this.remark1 = remark1;
    }

    public String[] getRemark2() {
        return remark2;
    }

    public void setRemark2(String[] remark2) {
        this.remark2 = remark2;
    }
}
