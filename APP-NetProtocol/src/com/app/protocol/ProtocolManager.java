package com.app.protocol;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.DataBeanEncoder;
import com.app.protocol.s2s.S2SSegment;
public class ProtocolManager {
    public static INetSegment getNetSegmentInstance(byte type, byte subType, int sessionId, int serial, byte flag) {
        return new S2SSegment(type, subType, serial, sessionId, flag);
    }

    public static INetSegment makeSegment(AbstractData data) {
        try {
            DataBeanEncoder encoder = new DataBeanEncoder();
            INetSegment segment = encoder.encode(data);
            return segment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
