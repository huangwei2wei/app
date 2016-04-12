package com.app.empire.protocol.data.bulletin;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取服务器公告
 * @see AbstractData
 * @author mazheng
 */
public class GetHelpOk extends AbstractData {
	private String help;
    public GetHelpOk(int sessionId, int serial) {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetHelpOk, sessionId, serial);
    }

    public GetHelpOk() {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetHelpOk);
    }

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
}
