// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class Fog implements Marshallable
{
    Color color;
    int fogStart;
    int fogEnd;
    protected String name;
    private static final long serialVersionUID = 1L;
    
    public Fog() {
        this.color = null;
        this.fogStart = -1;
        this.fogEnd = -1;
        this.name = null;
    }
    
    public Fog(final String name) {
        this.color = null;
        this.fogStart = -1;
        this.fogEnd = -1;
        this.name = null;
        this.setName(name);
    }
    
    public Fog(final String name, final Color c, final int start, final int end) {
        this.color = null;
        this.fogStart = -1;
        this.fogEnd = -1;
        this.name = null;
        this.setName(name);
        this.setColor(c);
        this.setStart(start);
        this.setEnd(end);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "[Fog: color=" + this.color + ", start=" + this.fogStart + ", end=" + this.fogEnd + "]";
    }
    
    public void setColor(final Color c) {
        this.color = c;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setStart(final int start) {
        this.fogStart = start;
    }
    
    public int getStart() {
        return this.fogStart;
    }
    
    public void setEnd(final int end) {
        this.fogEnd = end;
    }
    
    public int getEnd() {
        return this.fogEnd;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.color != null) {
            flag_bits = 1;
        }
        if (this.name != null && this.name != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.color != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.color);
        }
        buf.putInt(this.fogStart);
        buf.putInt(this.fogEnd);
        if (this.name != null && this.name != "") {
            buf.putString(this.name);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.color = (Color)MarshallingRuntime.unmarshalObject(buf);
        }
        this.fogStart = buf.getInt();
        this.fogEnd = buf.getInt();
        if ((flag_bits0 & 0x2) != 0x0) {
            this.name = buf.getString();
        }
        return this;
    }
}
