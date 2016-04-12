package com.app.empire.protocol.data.errorcode;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 返回短代项目列表
 * @author cj
 *
 */
public class GetSmsCodeNewListOk extends AbstractData {
	private int[]    id;          //id            
    private int[]    price;	  	  //短代价格      
    private String[] smsCode;	  //短代号        
    private int[]    itemId;	  //物品id        
    private String[] itemName;	  //物品名称      
    private String[] itemIcon;	  //物品ICON      
    private int[]    countType;	  //数量类型0:天数 ,1:个数,2:月卡
    private int[]    count;	  //数量          
    private String[] remark1;	  //短代说明支付  
    private String[] remark2;	  //非短代支付说明
    private String[] extensionInfo; //扩展参数【默认空字符串】格式如：CPID:741511,CPServiceID:651110072600
    private String	 qualificationInfo; //公司资质信息【默认空字符串】格式如：CPID:741511,CPServiceID:651110072600


    public GetSmsCodeNewListOk(int sessionId, int serial) {
        super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetSmsCodeNewListOk, sessionId, serial);
    }

    public GetSmsCodeNewListOk() {
        super(Protocol.MAIN_ERRORCODE, Protocol.ERRORCODE_GetSmsCodeNewListOk);
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

	public String[] getExtensionInfo() {
		return extensionInfo;
	}

	public void setExtensionInfo(String[] extensionInfo) {
		this.extensionInfo = extensionInfo;
	}

	public String getQualificationInfo() {
		return qualificationInfo;
	}

	public void setQualificationInfo(String qualificationInfo) {
		this.qualificationInfo = qualificationInfo;
	}
    
    
}
