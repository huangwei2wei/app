package com.app.protocol.s2s;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.app.protocol.INetSegment;
/**
 * 类 <code>S2SEncoder</code>对服务器间的数据进行编码
 * 
 * @see org.apache.mina.filter.codec.ProtocolEncoderAdapter
 * @since JDK 1.6
 */
public class S2SEncoder extends ProtocolEncoderAdapter {
    private static final Logger log = Logger.getLogger(S2SEncoder.class);

    /**
     * 对服务器间的数据进行编码
     */
    public void encode(IoSession session, Object data, ProtocolEncoderOutput out) throws Exception {
        INetSegment segment = (INetSegment) data;
        IoBuffer buffer = IoBuffer.allocate(128);
        buffer.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前　默认大头序
        
        buffer.setAutoExpand(true);
        //buffer.put(INetSegment.HEAD);//4
        buffer.putInt(segment.getSessionId());//4
        buffer.putInt(segment.getSerial());//4
        buffer.putInt(14 + segment.size());//4
        buffer.putShort((short) 1);//2
        buffer.put(segment.getByteArray());
        //buffer.put((byte) 0);//1
        buffer.flip();
        out.write(buffer);
        if ((segment.getFlag() & 0x1) != 0)
            log.debug("***Send Error: " + segment.getType() + "." + segment.getSubType());
        else
            log.debug("***Send Protocol: " + segment.getType() + "." + segment.getSubType());
    }
}
