package com.app.empire.protocol.data.bulletin;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取服务器公告
 * @see AbstractData
 * @author mazheng
 */
public class GetAbout extends AbstractData {
    public GetAbout(int sessionId, int serial) {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetAbout, sessionId, serial);
    }

    public GetAbout() {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetAbout);
    }
}
