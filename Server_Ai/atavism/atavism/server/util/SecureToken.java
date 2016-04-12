// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.io.Serializable;

public final class SecureToken
{
    protected final SecureTokenSpec spec;
    protected final byte version;
    protected final long tokenId;
    protected final long keyId;
    protected final byte[] authenticator;
    protected final boolean valid;
    protected static final byte TOKEN_VERSION = 1;
    
    SecureToken(final SecureTokenSpec spec, final byte version, final long tokenId, final long keyId, final byte[] authenticator, final boolean valid) {
        this.spec = spec;
        this.version = version;
        this.tokenId = tokenId;
        this.keyId = keyId;
        this.authenticator = authenticator;
        this.valid = valid;
    }
    
    @Override
    public String toString() {
        return "[SecureToken: version=" + this.version + " tokenId=0x" + Long.toHexString(this.tokenId) + " keyId=0x" + Long.toHexString(this.keyId) + " valid=" + this.valid + " " + this.spec.toString() + "]";
    }
    
    public byte getType() {
        return this.spec.getType();
    }
    
    public String getIssuerId() {
        return this.spec.getIssuerId();
    }
    
    public long getExpiry() {
        return this.spec.getExpiry();
    }
    
    public Serializable getProperty(final String key) {
        return this.spec.getProperty(key);
    }
    
    public byte getVersion() {
        return this.version;
    }
    
    public long getTokenId() {
        return this.tokenId;
    }
    
    public long getKeyId() {
        return this.keyId;
    }
    
    public boolean getValid() {
        return this.valid;
    }
    
    public byte[] getAuthenticator() {
        return this.authenticator;
    }
}
