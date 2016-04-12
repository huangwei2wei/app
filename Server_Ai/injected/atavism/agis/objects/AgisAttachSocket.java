// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class AgisAttachSocket implements Serializable, Marshallable
{
    private String name;
    private static Map<String, AgisAttachSocket> socketNameMapping;
    private static Lock mapLock;
    public static AgisAttachSocket PRIMARYWEAPON;
    public static AgisAttachSocket HEAD;
    public static AgisAttachSocket LSHOULDER;
    public static AgisAttachSocket RSHOULDER;
    public static AgisAttachSocket SECONDARYWEAPON;
    public static AgisAttachSocket SHIELD;
    public static AgisAttachSocket BACK;
    public static AgisAttachSocket PRIMARYWEAPONIDLE;
    public static AgisAttachSocket SECONDARYWEAPONIDLE;
    private static final long serialVersionUID = 1L;
    
    public AgisAttachSocket() {
    }
    
    public AgisAttachSocket(final String socketName) {
        this.name = socketName;
        AgisAttachSocket.mapLock.lock();
        try {
            AgisAttachSocket.socketNameMapping.put(socketName, this);
        }
        finally {
            AgisAttachSocket.mapLock.unlock();
        }
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "[AgisAttachSocket name=" + this.getName() + "]";
    }
    
    public static AgisAttachSocket getSocketByName(final String socketName) {
        AgisAttachSocket.mapLock.lock();
        try {
            return AgisAttachSocket.socketNameMapping.get(socketName);
        }
        finally {
            AgisAttachSocket.mapLock.unlock();
        }
    }
    
    static {
        AgisAttachSocket.socketNameMapping = new HashMap<String, AgisAttachSocket>();
        AgisAttachSocket.mapLock = LockFactory.makeLock("AgisAttachSocketLock");
        AgisAttachSocket.PRIMARYWEAPON = new AgisAttachSocket("primaryWeapon");
        AgisAttachSocket.HEAD = new AgisAttachSocket("head");
        AgisAttachSocket.LSHOULDER = new AgisAttachSocket("shoulderL");
        AgisAttachSocket.RSHOULDER = new AgisAttachSocket("shoulderR");
        AgisAttachSocket.SECONDARYWEAPON = new AgisAttachSocket("secondaryWeapon");
        AgisAttachSocket.SHIELD = new AgisAttachSocket("shield");
        AgisAttachSocket.BACK = new AgisAttachSocket("back");
        AgisAttachSocket.PRIMARYWEAPONIDLE = new AgisAttachSocket("primaryWeaponIdle");
        AgisAttachSocket.SECONDARYWEAPONIDLE = new AgisAttachSocket("secondaryWeaponIdle");
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.name != null && this.name != "") {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.name != null && this.name != "") {
            buf.putString(this.name);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.name = buf.getString();
        }
        return this;
    }
}
