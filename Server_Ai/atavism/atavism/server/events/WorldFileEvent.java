// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.math.Point;
import atavism.server.engine.Event;

public class WorldFileEvent extends Event
{
    private String worldFile;
    private Point loc;
    
    public WorldFileEvent() {
        this.worldFile = null;
        this.loc = null;
    }
    
    public WorldFileEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.worldFile = null;
        this.loc = null;
    }
    
    public WorldFileEvent(final String worldFileName) {
        this.worldFile = null;
        this.loc = null;
        this.setWorldFile(worldFileName);
    }
    
    public WorldFileEvent(final String worldFileName, final Point loc) {
        this.worldFile = null;
        this.loc = null;
        this.setWorldFile(worldFileName);
        this.setLoc(loc);
    }
    
    public void setWorldFile(final String worldFileName) {
        this.worldFile = worldFileName;
    }
    
    public String getWorldFile() {
        return this.worldFile;
    }
    
    public void setLoc(final Point loc) {
        this.loc = loc;
    }
    
    public Point getLoc() {
        return this.loc;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putString(this.getWorldFile());
        if (this.loc != null) {
            buf.putBoolean(true);
            buf.putPoint(this.loc);
        }
        else {
            buf.putBoolean(false);
        }
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setWorldFile(buf.getString());
    }
    
    @Override
    public String getName() {
        return "WorldFileEvent";
    }
}
