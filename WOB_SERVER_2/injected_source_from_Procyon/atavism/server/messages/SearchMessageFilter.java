// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.msgsys.Message;
import atavism.server.objects.ObjectType;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.MessageTypeFilter;

public class SearchMessageFilter extends MessageTypeFilter implements Marshallable
{
    private ObjectType objectType;
    
    public SearchMessageFilter() {
    }
    
    public SearchMessageFilter(final ObjectType objectType) {
        super(SearchMessage.MSG_TYPE_SEARCH);
        this.setType(objectType);
    }
    
    public ObjectType getType() {
        return this.objectType;
    }
    
    public void setType(final ObjectType objectType) {
        this.objectType = objectType;
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        return ((SearchMessage)message).getType() == this.objectType;
    }
    
    @Override
    public String toString() {
        return "[SearchMessageFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "type=" + this.objectType + " " + super.toStringInternal();
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.objectType != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.objectType != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.objectType);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.objectType = (ObjectType)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
