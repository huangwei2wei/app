// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class BinaryState extends ObjState implements Marshallable
{
    private String name;
    private Boolean value;
    private static final long serialVersionUID = 1L;
    
    public BinaryState() {
        this.name = null;
        this.value = null;
    }
    
    public BinaryState(final String stateName, final Boolean value) {
        this.name = null;
        this.value = null;
        this.setStateName(stateName);
        this.value = value;
    }
    
    @Override
    public Integer getIntValue() {
        return ((boolean)this.value) ? 1 : 0;
    }
    
    @Override
    public String getStateName() {
        return this.name;
    }
    
    public void setStateName(final String name) {
        this.name = name;
    }
    
    public Boolean getValue() {
        return this.value;
    }
    
    public void setValue(final Boolean val) {
        this.value = val;
    }
    
    public Boolean isSet() {
        return this.getValue();
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.name != null && this.name != "") {
            flag_bits = 1;
        }
        if (this.value != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.name != null && this.name != "") {
            buf.putString(this.name);
        }
        if (this.value != null) {
            buf.putByte((byte)(byte)(((boolean)this.value) ? 1 : 0));
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.name = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.value = (buf.getByte() != 0);
        }
        return this;
    }
}
