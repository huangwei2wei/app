// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Collection;
import java.util.Map;
import atavism.server.engine.Engine;
import atavism.server.objects.ObjectTypes;
import atavism.server.objects.ObjectType;
import atavism.agis.plugins.SocialClient;
import java.util.ArrayList;
import atavism.server.engine.OID;
import java.util.HashMap;
import atavism.server.objects.Entity;

public class SocialInfo extends Entity
{
    private int id;
    private HashMap<OID, String> friends;
    private HashMap<OID, String> ignores;
    private ArrayList<String> channels;
    private static final long serialVersionUID = 1L;
    
    public SocialInfo() {
        this.friends = new HashMap<OID, String>();
        this.ignores = new HashMap<OID, String>();
        this.channels = new ArrayList<String>();
        this.setNamespace(SocialClient.NAMESPACE);
    }
    
    public SocialInfo(final OID objOid) {
        super(objOid);
        this.friends = new HashMap<OID, String>();
        this.ignores = new HashMap<OID, String>();
        this.channels = new ArrayList<String>();
        this.setNamespace(SocialClient.NAMESPACE);
    }
    
    public String toString() {
        return "[Entity: " + this.getName() + ":" + this.getOid() + "]";
    }
    
    public ObjectType getType() {
        return ObjectTypes.unknown;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public void addFriend(final OID oid, final String name) {
        this.friends.put(oid, name);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public void removeFriend(final OID oid) {
        this.friends.remove(oid);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public HashMap<OID, String> getFriends() {
        return new HashMap<OID, String>(this.friends);
    }
    
    public void setFriends(final HashMap<OID, String> friends) {
        this.friends = new HashMap<OID, String>(friends);
    }
    
    public void addIgnore(final OID oid, final String name) {
        this.ignores.put(oid, name);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public void removeIgnore(final OID oid) {
        this.ignores.remove(oid);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public HashMap<OID, String> getIgnores() {
        return new HashMap<OID, String>(this.ignores);
    }
    
    public void setIgnores(final HashMap<OID, String> ignores) {
        this.ignores = new HashMap<OID, String>(ignores);
    }
    
    public void addChannel(final String name) {
        this.channels.add(name);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public void removeChannel(final String name) {
        this.channels.remove(name);
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public ArrayList<String> getChannels() {
        return new ArrayList<String>(this.channels);
    }
    
    public void setChannels(final ArrayList<String> channels) {
        this.channels = new ArrayList<String>(channels);
    }
}
