package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 领取付费包奖励
 * @author zengxc
 *
 */
public class GetPayAppReward extends AbstractData {
    private String code;

    public GetPayAppReward(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppReward, sessionId, serial);
    }

    public GetPayAppReward() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppReward);
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
