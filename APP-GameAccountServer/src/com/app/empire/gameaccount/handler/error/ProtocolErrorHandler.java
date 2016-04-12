package com.app.empire.gameaccount.handler.error;
import com.app.empire.gameaccount.session.AcceptSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
public class ProtocolErrorHandler implements IDataHandler {
    public AbstractData handle(AbstractData data) throws Exception {
        AcceptSession session = (AcceptSession) data.getSource();
        session.send(data);
        return null;
    }
}
