package com.app.empire.protocol.data.bulletin;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取服务器公告
 * @see AbstractData
 * @author mazheng
 */
public class GetBulletin extends AbstractData {
    public GetBulletin(int sessionId, int serial) {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetBulletin, sessionId, serial);
    }

    public GetBulletin() {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetBulletin);
    }
}
