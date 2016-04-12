package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取付费包奖励列表
 * @author zengxc
 *
 */
public class GetPayAppRewardList extends AbstractData {
    private String code;

    public GetPayAppRewardList(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppRewardList, sessionId, serial);
    }

    public GetPayAppRewardList() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppRewardList);
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
