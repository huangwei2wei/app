// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.agis.objects.AgisMob;
import atavism.agis.objects.AgisObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Logger;

public class StatusUpdateEvent extends AgisEvent
{
    private int stun;
    private int body;
    private int end;
    private int pd;
    private int current_stun;
    private int current_body;
    private int current_end;
    static Logger log;
    
    static {
        StatusUpdateEvent.log = new Logger("StatusUpdateEvent");
    }
    
    public StatusUpdateEvent() {
        this.stun = 0;
        this.body = 0;
        this.end = 0;
        this.pd = 0;
        this.current_stun = 0;
        this.current_body = 0;
        this.current_end = 0;
    }
    
    public StatusUpdateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.stun = 0;
        this.body = 0;
        this.end = 0;
        this.pd = 0;
        this.current_stun = 0;
        this.current_body = 0;
        this.current_end = 0;
    }
    
    public StatusUpdateEvent(final AgisObject obj) {
        super(obj);
        this.stun = 0;
        this.body = 0;
        this.end = 0;
        this.pd = 0;
        this.current_stun = 0;
        this.current_body = 0;
        this.current_end = 0;
        this.setBody(obj.getBody());
        this.setCurrentBody(obj.getCurrentBody());
        this.setStun(obj.getStun());
        this.setCurrentStun(obj.getCurrentStun());
        if (obj instanceof AgisMob) {
            final AgisMob mob = (AgisMob)obj;
            this.setEndurance(mob.getEndurance());
            this.setCurrentEndurance(mob.getCurrentEndurance());
            this.setPD(obj.getPD());
        }
        else {
            this.setEndurance(0);
            this.setCurrentEndurance(0);
            this.setPD(0);
        }
    }
    
    public String getName() {
        return "StatusUpdateEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(4000);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putInt(7);
        buf.putString("stun");
        buf.putInt(this.getStun());
        buf.putString("stun_cur");
        buf.putInt(this.getCurrentStun());
        buf.putString("body");
        buf.putInt(this.getBody());
        buf.putString("body_cur");
        buf.putInt(this.getCurrentBody());
        buf.putString("end");
        buf.putInt(this.getEndurance());
        buf.putString("end_cur");
        buf.putInt(this.getCurrentEndurance());
        buf.putString("pd");
        buf.putInt(this.getPD());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        for (int numAttr = buf.getInt(); numAttr > 0; --numAttr) {
            this.processAttribute(buf);
        }
    }
    
    private void processAttribute(final AOByteBuffer buf) {
        final String attr = buf.getString();
        if (attr.equals("stun")) {
            this.setStun(buf.getInt());
        }
        else if (attr.equals("stun_cur")) {
            this.setCurrentStun(buf.getInt());
        }
        else if (attr.equals("body")) {
            this.setBody(buf.getInt());
        }
        else if (attr.equals("body_cur")) {
            this.setCurrentBody(buf.getInt());
        }
        else if (attr.equals("end")) {
            this.setEndurance(buf.getInt());
        }
        else if (attr.equals("end_cur")) {
            this.setCurrentEndurance(buf.getInt());
        }
        else if (attr.equals("pd")) {
            this.setPD(buf.getInt());
        }
        else {
            final int val = buf.getInt();
            StatusUpdateEvent.log.warn("unknown attr: " + attr + ", val=" + val);
        }
    }
    
    public void setStun(final int stun) {
        this.stun = stun;
    }
    
    public int getStun() {
        return this.stun;
    }
    
    public void setCurrentStun(final int stun) {
        this.current_stun = stun;
    }
    
    public int getCurrentStun() {
        return this.current_stun;
    }
    
    public void setBody(final int body) {
        this.body = body;
    }
    
    public int getBody() {
        return this.body;
    }
    
    public void setCurrentBody(final int body) {
        this.current_body = body;
    }
    
    public int getCurrentBody() {
        return this.current_body;
    }
    
    public void setEndurance(final int end) {
        this.end = end;
    }
    
    public int getEndurance() {
        return this.end;
    }
    
    public void setCurrentEndurance(final int end) {
        this.current_end = end;
    }
    
    public int getCurrentEndurance() {
        return this.current_end;
    }
    
    public void setPD(final int pd) {
        this.pd = pd;
    }
    
    public int getPD() {
        return this.pd;
    }
}
