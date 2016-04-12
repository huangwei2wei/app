// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Iterator;
import java.util.Date;
import java.text.DateFormat;
import java.util.Map;
import java.io.Serializable;
import java.util.TreeMap;

public final class SecureTokenSpec
{
    private final byte type;
    private final String issuerId;
    private final long expiry;
    private TreeMap<String, Serializable> properties;
    public static final byte TOKEN_TYPE_MASTER = 1;
    public static final byte TOKEN_TYPE_DOMAIN = 2;
    
    public SecureTokenSpec(final byte type, final String issuerId, final long expiry) {
        this.properties = new TreeMap<String, Serializable>();
        this.type = type;
        this.issuerId = issuerId;
        this.expiry = expiry;
    }
    
    public SecureTokenSpec(final byte type, final String issuerId, final long expiry, final Map<String, Serializable> properties) {
        this.properties = new TreeMap<String, Serializable>();
        this.type = type;
        this.issuerId = issuerId;
        this.expiry = expiry;
        this.properties.putAll(properties);
    }
    
    @Override
    public String toString() {
        String str = "type=" + this.type + " issuerId=" + this.issuerId + " expiry=<" + DateFormat.getInstance().format(new Date(this.expiry)) + "> props:";
        for (final String key : this.properties.keySet()) {
            str = str + " " + key + ":" + this.properties.get(key).toString();
        }
        return str;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public String getIssuerId() {
        return this.issuerId;
    }
    
    public long getExpiry() {
        return this.expiry;
    }
    
    public Serializable getProperty(final String key) {
        return this.properties.get(key);
    }
    
    public void setProperty(final String key, final Serializable value) {
        this.properties.put(key, value);
    }
    
    public TreeMap<String, Serializable> getPropertyMap() {
        return this.properties;
    }
}
