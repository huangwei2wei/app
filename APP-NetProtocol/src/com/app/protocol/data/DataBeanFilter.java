package com.app.protocol.data;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;

import com.app.protocol.INetData;
import com.app.protocol.INetSegment;
import com.app.protocol.NetPacket;
public class DataBeanFilter extends IoFilterAdapter {
    private DataBeanDecoder decoder;
    private DataBeanEncoder encoder;

	public DataBeanFilter() {
        this.decoder = new DataBeanDecoder();
        this.encoder = new DataBeanEncoder();
    }

    public DataBeanFilter(DataBeanDecoder decoder, DataBeanEncoder encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        AbstractData msg = (AbstractData) writeRequest.getMessage();
        INetSegment seg = this.encoder.encode(msg);
        if (seg != null) nextFilter.filterWrite(session, new DefaultWriteRequest(seg, writeRequest.getFuture()));
    }

    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
        NetPacket packet = (NetPacket) message;
        for(INetData data:packet.datas){
	        AbstractData msg = this.decoder.decode(data);
	        nextFilter.messageReceived(session, msg);
        }
    }
}
