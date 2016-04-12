// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.Iterator;
import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import atavism.server.engine.Event;

public class IslandManifestEvent extends Event
{
    private String worldName;
    private String worldFilesDirectory;
    private HashMap<String, String> worldFiles;
    
    public IslandManifestEvent() {
        this.worldName = null;
        this.worldFilesDirectory = null;
        this.worldFiles = null;
    }
    
    public IslandManifestEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.worldName = null;
        this.worldFilesDirectory = null;
        this.worldFiles = null;
    }
    
    public IslandManifestEvent(final String worldName) {
        this.worldName = null;
        this.worldFilesDirectory = null;
        this.worldFiles = null;
        this.setWorldName(worldName);
    }
    
    public void setWorldName(final String worldName) {
        this.worldName = worldName;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public void setWorldFilesDirectory(final String worldFilesDirectory) {
        this.worldFilesDirectory = worldFilesDirectory;
    }
    
    public String getWorldFilesDirectory() {
        return this.worldFilesDirectory;
    }
    
    public void setWorldFiles(final HashMap<String, String> worldFiles) {
        this.worldFiles = worldFiles;
    }
    
    public HashMap<String, String> getWorldFiles() {
        return this.worldFiles;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(null);
        buf.putInt(msgId);
        buf.putString(this.getWorldName());
        buf.putString(this.getWorldFilesDirectory());
        buf.putInt(this.worldFiles.size());
        for (final String worldFile : this.worldFiles.keySet()) {
            buf.putString(worldFile);
            buf.putString(this.worldFiles.get(worldFile));
        }
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.setWorldName(buf.getString());
    }
    
    @Override
    public String getName() {
        return "IslandManifestEvent";
    }
}
