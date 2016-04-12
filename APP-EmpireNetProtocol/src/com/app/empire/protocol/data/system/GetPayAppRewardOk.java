package com.app.empire.protocol.data.system;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 领取付费包奖励
 * @author zengxc
 *
 */
public class GetPayAppRewardOk extends AbstractData {

    public GetPayAppRewardOk(int sessionId, int serial) {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppRewardOk, sessionId, serial);
    }

    public GetPayAppRewardOk() {
        super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetPayAppRewardOk);
    }


}
