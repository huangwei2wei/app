// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.engine.OID;
import atavism.server.util.Logger;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class PermissionCallback implements Serializable, Marshallable
{
    protected AOObject thisObj;
    protected static final Logger log;
    private static final long serialVersionUID = 1L;
    
    public PermissionCallback() {
        this.thisObj = null;
        this.setupTransient();
    }
    
    public PermissionCallback(final AOObject obj) {
        this.thisObj = null;
        this.thisObj = obj;
        this.setupTransient();
    }
    
    private void setupTransient() {
    }
    
    public boolean acquire(final AOObject acquirer) {
        return true;
    }
    
    public boolean acquireFrom(final AOObject acquirer, final AOObject obj) {
        return true;
    }
    
    public boolean drop(final AOObject dropInto) {
        return true;
    }
    
    public boolean use(final OID activatorOid) {
        return true;
    }
    
    public boolean destroy(final AOObject destroyer) {
        return true;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    static {
        log = new Logger("AgisPermissionCallback");
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.thisObj != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.thisObj != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.thisObj);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.thisObj = (AOObject)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
