package com.app.empire.protocol.data.bulletin;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取服务器公告
 * @see AbstractData
 * @author mazheng
 */
public class GetBulletinOk extends AbstractData {
	private String bulletin;
    public GetBulletinOk(int sessionId, int serial) {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetBulletinOk, sessionId, serial);
    }

    public GetBulletinOk() {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetBulletinOk);
    }

	public String getBulletin() {
		return bulletin;
	}

	public void setBulletin(String bulletin) {
		this.bulletin = bulletin;
	}
}
