// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.List;
import java.util.Collection;
import atavism.server.marshalling.Marshallable;

public class AdvertiseMessage extends Message implements Marshallable
{
    private Collection<MessageType> advertisements;
    private static final long serialVersionUID = 1L;
    
    public AdvertiseMessage() {
        this.advertisements = null;
    }
    
    public AdvertiseMessage(final Collection<MessageType> list) {
        this.advertisements = null;
        this.msgType = MessageTypes.MSG_TYPE_ADVERTISE;
        this.advertisements = list;
    }
    
    public void setAdvertisements(final List<MessageType> list) {
        this.advertisements = list;
    }
    
    public Collection<MessageType> getAdvertisements() {
        return this.advertisements;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.advertisements != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.advertisements != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.advertisements);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.advertisements = (Collection<MessageType>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
