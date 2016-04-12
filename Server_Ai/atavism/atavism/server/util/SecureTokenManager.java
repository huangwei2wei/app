// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.security.KeyPair;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.SignatureException;
import java.security.Signature;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.Mac;
import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.nio.BufferUnderflowException;
import java.io.Serializable;
import java.util.TreeMap;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import java.util.Map;

public class SecureTokenManager
{
    protected static SecureTokenManager instance;
    protected long lastTokenId;
    protected long domainKeyId;
    protected Map<Long, SecretKey> domainKeys;
    protected Map<Long, PublicKey> masterPublicKeys;
    protected PrivateKey masterPrivateKey;
    protected long masterKeyId;
    protected Map<String, IssuerHistory> issuerHistories;
    
    protected SecureTokenManager() {
        this.lastTokenId = 1L;
        this.domainKeyId = -1L;
        this.domainKeys = new HashMap<Long, SecretKey>();
        this.masterPublicKeys = new HashMap<Long, PublicKey>();
        this.masterPrivateKey = null;
        this.masterKeyId = -1L;
        this.issuerHistories = new HashMap<String, IssuerHistory>();
    }
    
    public static SecureTokenManager getInstance() {
        if (SecureTokenManager.instance == null) {
            SecureTokenManager.instance = new SecureTokenManager();
        }
        return SecureTokenManager.instance;
    }
    
    public SecureToken importToken(final byte[] encodedToken) {
        final AOByteBuffer buf = new AOByteBuffer(encodedToken);
        byte version = 0;
        byte type = 0;
        String issuerId = null;
        long tokenId = 0L;
        long keyId = 0L;
        long expiry = 0L;
        TreeMap<String, Serializable> properties = new TreeMap<String, Serializable>();
        byte[] authenticator = null;
        boolean valid = true;
        int authedLength = 0;
        Log.debug("Importing Token: " + buf.toString());
        try {
            version = buf.getByte();
            type = buf.getByte();
            issuerId = buf.getString();
            tokenId = buf.getLong();
            keyId = buf.getLong();
            expiry = buf.getLong();
            properties = (TreeMap<String, Serializable>)buf.getEncodedObject();
            authedLength = buf.position();
            authenticator = new byte[buf.remaining()];
            buf.getBytes(authenticator, 0, authenticator.length);
        }
        catch (BufferUnderflowException e) {
            Log.exception("SecureTokenManager.importToken: caught exception when decoding token.", e);
            valid = false;
        }
        catch (RuntimeException e2) {
            Log.exception("SecureTokenManager.importToken: caught exception when decoding token.", e2);
            valid = false;
        }
        if (version != 1) {
            Log.error("SecureTokenManager.importToken: token version mismatch tokenId=0x" + Long.toHexString(tokenId) + " version=" + version);
            valid = false;
        }
        if (valid && expiry <= System.currentTimeMillis()) {
            valid = false;
            Log.error("SecureTokenManager.importToken: token expired tokenId=0x" + Long.toHexString(tokenId) + " expiry=<" + DateFormat.getInstance().format(new Date(expiry)) + ">");
        }
        if (valid) {
            synchronized (this) {
                if (this.issuerAlreadyUsed(issuerId, tokenId)) {
                    valid = false;
                    Log.error("SecureTokenManager.importToken: token already used tokenId=0x" + Long.toHexString(tokenId));
                }
            }
        }
        final SecureTokenSpec spec = new SecureTokenSpec(type, issuerId, expiry, properties);
        if (authenticator == null || authenticator.length == 0) {
            Log.info("SecureTokenManager.importToken: token has no authenticator tokenId=0x" + Long.toHexString(tokenId));
            valid = false;
        }
        if (valid) {
            buf.rewind();
            final byte[] authedData = new byte[authedLength];
            buf.getBytes(authedData, 0, authedData.length);
            switch (type) {
                case 1: {
                    final PublicKey pubKey;
                    synchronized (this) {
                        pubKey = this.masterPublicKeys.get(keyId);
                    }
                    valid = this.validateMasterAuthenticator(pubKey, authedData, authenticator);
                    break;
                }
                case 2: {
                    final SecretKey secretKey;
                    synchronized (this) {
                        secretKey = this.domainKeys.get(keyId);
                    }
                    valid = this.validateDomainAuthenticator(secretKey, authedData, authenticator);
                    break;
                }
                default: {
                    Log.error("SecureTokenManager.importToken: invalid type=" + type);
                    valid = false;
                    break;
                }
            }
        }
        if (valid) {
            synchronized (this) {
                if (this.issuerAlreadyUsed(issuerId, tokenId)) {
                    valid = false;
                    Log.error("SecureTokenManager.importToken: token already used tokenId=0x" + Long.toHexString(tokenId));
                }
                else {
                    this.issuerAddToken(issuerId, tokenId, expiry);
                }
                Log.debug("SecureTokenManager - cleaning up token");
                this.issuerCleanup(issuerId, System.currentTimeMillis());
            }
        }
        final SecureToken token = new SecureToken(spec, version, tokenId, keyId, authenticator, valid);
        return token;
    }
    
    public SecureToken importToken(final AOByteBuffer tokenBuf) {
        final byte[] encodedToken = new byte[tokenBuf.remaining()];
        tokenBuf.getBytes(encodedToken, 0, encodedToken.length);
        if (Log.loggingDebug) {
            Log.debug("SecureTokenManager.importToken: token=" + Arrays.toString(encodedToken));
        }
        return this.importToken(encodedToken);
    }
    
    public byte[] generateToken(final SecureTokenSpec spec) {
        final AOByteBuffer buf = new AOByteBuffer(512);
        SecretKey domainKey = null;
        PrivateKey masterKey = null;
        final byte type = spec.getType();
        long keyId = 0L;
        synchronized (this) {
            switch (type) {
                case 1: {
                    if (this.masterKeyId == -1L) {
                        Log.error("SecureTokenManager.generateToken: master key not initialized");
                        throw new RuntimeException("master key not initialized");
                    }
                    keyId = this.masterKeyId;
                    masterKey = this.masterPrivateKey;
                    break;
                }
                case 2: {
                    if (this.domainKeyId == -1L) {
                        Log.error("SecureTokenManager.generateToken: domain key not initialized");
                        throw new RuntimeException("domain key not initialized");
                    }
                    keyId = this.domainKeyId;
                    domainKey = this.domainKeys.get(keyId);
                    break;
                }
                default: {
                    Log.error("SecureTokenManager.generateToken: invalid token type=" + type);
                    throw new RuntimeException("invalid token type=" + type);
                }
            }
        }
        buf.putByte((byte)1);
        buf.putByte(type);
        buf.putString(spec.getIssuerId());
        buf.putLong(this.nextTokenId());
        buf.putLong(keyId);
        buf.putLong(spec.getExpiry());
        final TreeMap<String, Serializable> properties = spec.getPropertyMap();
        buf.putEncodedObject(properties);
        final int authedDataLen = buf.position();
        buf.flip();
        final byte[] authedData = new byte[authedDataLen];
        buf.getBytes(authedData, 0, authedData.length);
        byte[] authenticator = null;
        switch (type) {
            case 1: {
                authenticator = this.generateMasterAuthenticator(masterKey, authedData);
                break;
            }
            case 2: {
                authenticator = this.generateDomainAuthenticator(domainKey, authedData);
                break;
            }
            default: {
                Log.error("SecureTokenManager.generateToken: invalid token type=" + type);
                throw new RuntimeException("invalid token type=" + type);
            }
        }
        if (authenticator == null) {
            Log.error("SecureTokenManager.generateToken: null authenticator");
            return null;
        }
        buf.putBytes(authenticator, 0, authenticator.length);
        final byte[] token = new byte[buf.position()];
        buf.flip();
        buf.getBytes(token, 0, token.length);
        return token;
    }
    
    protected byte[] generateDomainAuthenticator(final SecretKey key, final byte[] data) {
        if (key == null) {
            return null;
        }
        try {
            final Mac mac = Mac.getInstance(key.getAlgorithm());
            mac.init(key);
            return mac.doFinal(data);
        }
        catch (NoSuchAlgorithmException e3) {
            return null;
        }
        catch (InvalidKeyException e) {
            Log.exception("SecureTokenManager.generateDomainAuthenticator: invalid key", e);
            throw new RuntimeException(e);
        }
        catch (IllegalStateException e2) {
            Log.exception("SecureTokenManager.generateDomainAuthenticator: illegal state", e2);
            throw new RuntimeException(e2);
        }
    }
    
    protected boolean validateDomainAuthenticator(final SecretKey key, final byte[] data, final byte[] authenticator) {
        final byte[] newAuthenticator = this.generateDomainAuthenticator(key, data);
        return Arrays.equals(newAuthenticator, authenticator);
    }
    
    protected byte[] generateMasterAuthenticator(final PrivateKey key, final byte[] data) {
        if (key == null) {
            Log.error("SecureTokenManager.generateMasterAuthenticator: null key");
            return null;
        }
        try {
            final Signature sig = Signature.getInstance(key.getAlgorithm());
            sig.initSign(key);
            sig.update(data);
            return sig.sign();
        }
        catch (NoSuchAlgorithmException e) {
            Log.exception("SecureTokenManager.generateMasterAuthenticator: bad key", e);
            return null;
        }
        catch (InvalidKeyException e2) {
            Log.exception("SecureTokenManager.generateMasterAuthenticator: invalid key", e2);
            throw new RuntimeException(e2);
        }
        catch (SignatureException e3) {
            Log.exception("SecureTokenManager.generateMasterAuthenticator: illegal signature state", e3);
            throw new RuntimeException(e3);
        }
    }
    
    protected boolean validateMasterAuthenticator(final PublicKey key, final byte[] data, final byte[] authenticator) {
        if (key == null) {
            Log.error("SecureTokenManager.validateMasterAuthenticator: key is null");
            return false;
        }
        try {
            final Signature sig = Signature.getInstance(key.getAlgorithm());
            sig.initVerify(key);
            sig.update(data);
            final boolean rv = sig.verify(authenticator);
            if (Log.loggingDebug) {
                Log.debug("SecureTokenManager.validateMasterAuthenticator rv=" + rv);
            }
            return rv;
        }
        catch (NoSuchAlgorithmException e) {
            Log.exception("SecureTokenManager.validateMasterAuthenticator: bad key", e);
            return false;
        }
        catch (InvalidKeyException e2) {
            Log.exception("SecureTokenManager.validateMasterAuthenticator: invalid key", e2);
            throw new RuntimeException(e2);
        }
        catch (SignatureException e3) {
            Log.exception("SecureTokenManager.validateMasterAuthenticator: bad signature", e3);
            return false;
        }
    }
    
    public void registerMasterPublicKey(final byte[] encodedPubKey) {
        final AOByteBuffer buf = new AOByteBuffer(encodedPubKey);
        final long keyId = buf.getLong();
        final String algorithm = buf.getString();
        if (Log.loggingDebug) {
            Log.debug("SecureTokenManager.registerMasterPublicKey: decoding public key keyId=0x" + Long.toHexString(keyId) + " algorithm=" + algorithm);
        }
        final byte[] keyData = new byte[buf.remaining()];
        buf.getBytes(keyData, 0, keyData.length);
        final EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
        if (this.masterPublicKeys.containsKey(keyId)) {
            Log.error("SecureTokenManager.registerMasterPublicKey: key already exists in table keyId=0x" + Long.toHexString(keyId));
            throw new IllegalArgumentException("master public already exists in table keyId=0x" + Long.toHexString(keyId));
        }
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            Log.exception("SecureTokenManager.registerMasterPublicKey: could not get KeyFactory instance. keyId=0x" + Long.toHexString(keyId) + " algorithm=" + algorithm, e);
            throw new RuntimeException(e);
        }
        PublicKey pubKey;
        try {
            pubKey = factory.generatePublic(keySpec);
        }
        catch (InvalidKeySpecException e2) {
            Log.exception("SecureTokenManager.registerMasterPublicKey: invalid master public key. keyId=0x" + Long.toHexString(keyId), e2);
            throw new RuntimeException(e2);
        }
        this.masterPublicKeys.put(keyId, pubKey);
    }
    
    public void initMaster(final byte[] encodedPrivKey) {
        final AOByteBuffer buf = new AOByteBuffer(encodedPrivKey);
        final long keyId = buf.getLong();
        final String algorithm = buf.getString();
        if (Log.loggingDebug) {
            Log.debug("SecureTokenManager.initMaster: master key keyId=0x" + Long.toHexString(keyId) + " algorithm=" + algorithm);
        }
        final byte[] keyData = new byte[buf.remaining()];
        buf.getBytes(keyData, 0, keyData.length);
        final EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
        KeyFactory factory;
        try {
            factory = KeyFactory.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            Log.exception("SecureTokenManager.initMaster: could not get KeyFactory instance. algorithm=" + algorithm + " for keyId=0x" + Long.toHexString(keyId), e);
            throw new RuntimeException(e);
        }
        try {
            synchronized (this) {
                this.masterPrivateKey = factory.generatePrivate(keySpec);
                this.masterKeyId = keyId;
            }
        }
        catch (InvalidKeySpecException e2) {
            Log.exception("SecureTokenManager.initMaster: invalid master private key. keyId=0x" + Long.toHexString(keyId), e2);
            throw new RuntimeException(e2);
        }
    }
    
    public synchronized void initDomain(final byte[] domainKey) {
        final AOByteBuffer buf = new AOByteBuffer(domainKey);
        final long domainKeyId = buf.getLong();
        final String algorithm = buf.getString();
        if (Log.loggingDebug) {
            Log.debug("SecureTokenManager.initDomain: reading domain key. keyId=0x" + Long.toHexString(domainKeyId) + " algorithm=" + algorithm);
        }
        final byte[] keyData = new byte[buf.remaining()];
        buf.getBytes(keyData, 0, buf.remaining());
        if (this.domainKeys.containsKey(domainKeyId)) {
            Log.error("SecureTokenManager.initDomain: domain key already exists in table keyId=0x" + Long.toHexString(domainKeyId));
            throw new IllegalArgumentException("domain key already exists in table keyId=0x" + Long.toHexString(domainKeyId));
        }
        final SecretKeySpec keySpec = new SecretKeySpec(keyData, algorithm);
        this.domainKeyId = domainKeyId;
        this.domainKeys.put(domainKeyId, keySpec);
    }
    
    protected synchronized long nextTokenId() {
        return ++this.lastTokenId;
    }
    
    public byte[] getEncodedDomainKey() {
        return SecureTokenUtil.encodeDomainKey(this.domainKeyId, this.domainKeys.get(this.domainKeyId));
    }
    
    public boolean hasDomainKey() {
        boolean result = true;
        if (this.domainKeyId == -1L) {
            result = false;
        }
        return result;
    }
    
    protected boolean issuerAlreadyUsed(final String issuerId, final long tokenId) {
        final IssuerHistory issuer = this.issuerHistories.get(issuerId);
        return issuer != null && issuer.alreadyUsed(tokenId);
    }
    
    protected void issuerAddToken(final String issuerId, final long tokenId, final long expiry) {
        IssuerHistory issuer = this.issuerHistories.get(issuerId);
        if (issuer == null) {
            issuer = new IssuerHistory(issuerId);
            this.issuerHistories.put(issuerId, issuer);
        }
        issuer.addToken(tokenId, expiry);
    }
    
    protected void issuerCleanup(final String issuerId, final long time) {
        final IssuerHistory issuer = this.issuerHistories.get(issuerId);
        if (issuer == null) {
            return;
        }
        issuer.cleanup(time);
    }
    
    public static void main(final String[] args) {
        Log.init();
        final SecretKey key = SecureTokenUtil.generateDomainKey();
        System.out.println("domain key:");
        System.out.println(key.getFormat() + ", " + key.getAlgorithm());
        System.out.println(Base64.encodeBytes(key.getEncoded()));
        System.out.println("");
        final KeyPair pair = SecureTokenUtil.generateMasterKeyPair();
        final PrivateKey priv = pair.getPrivate();
        System.out.println("private key:");
        System.out.println(priv.getFormat() + ", " + priv.getAlgorithm());
        System.out.println(Base64.encodeBytes(priv.getEncoded()));
        System.out.println("");
        final PublicKey pub = pair.getPublic();
        System.out.println("public key:");
        System.out.println(pub.getFormat() + ", " + pub.getAlgorithm());
        System.out.println(Base64.encodeBytes(pub.getEncoded()));
        System.out.println("");
        final byte[] encodedPrivKey = SecureTokenUtil.encodeMasterPrivateKey(12L, pair.getPrivate());
        System.out.println("encoded private key:");
        System.out.println(Base64.encodeBytes(encodedPrivKey));
        System.out.println("");
        final byte[] encodedPubKey = SecureTokenUtil.encodeMasterPublicKey(12L, pair.getPublic());
        System.out.println("encoded public key:");
        System.out.println(Base64.encodeBytes(encodedPubKey));
        System.out.println("");
        final byte[] encodedDomainKey = SecureTokenUtil.encodeDomainKey(24L, key);
        System.out.println("encoded domain key:");
        System.out.println(Base64.encodeBytes(encodedDomainKey));
        System.out.println("");
        getInstance().registerMasterPublicKey(encodedPubKey);
        getInstance().initMaster(encodedPrivKey);
        getInstance().initDomain(encodedDomainKey);
        final SecureTokenSpec masterSpec = new SecureTokenSpec((byte)1, "test", System.currentTimeMillis() + 10000L);
        masterSpec.setProperty("prop1", "value1");
        final byte[] masterTokenData = getInstance().generateToken(masterSpec);
        System.out.println("master token data:");
        System.out.println(Base64.encodeBytes(masterTokenData));
        System.out.println("");
        final SecureToken masterToken = getInstance().importToken(masterTokenData);
        System.out.println("imported master token:");
        System.out.println(masterToken.toString());
        final SecureTokenSpec domainSpec = new SecureTokenSpec((byte)2, "test", System.currentTimeMillis() + 10000L);
        domainSpec.setProperty("prop1", "value1");
        final byte[] domainTokenData = getInstance().generateToken(domainSpec);
        System.out.println("domain token data:");
        System.out.println(Base64.encodeBytes(domainTokenData));
        System.out.println("");
        final SecureToken domainToken = getInstance().importToken(domainTokenData);
        System.out.println("imported domain token:");
        System.out.println(domainToken.toString());
    }
    
    static {
        SecureTokenManager.instance = null;
    }
    
    protected class IssuerHistory
    {
        protected final String issuerId;
        protected final Set<Long> usedTokenIds;
        protected final TreeMap<Long, Set<Long>> usedTokens;
        
        protected IssuerHistory(final String issuerId) {
            this.usedTokenIds = new HashSet<Long>();
            this.usedTokens = new TreeMap<Long, Set<Long>>();
            this.issuerId = issuerId;
        }
        
        public boolean alreadyUsed(final long tokenId) {
            return this.usedTokenIds.contains(tokenId);
        }
        
        protected void addToken(final long tokenId, final long expiry) {
            this.usedTokenIds.add(tokenId);
            Set<Long> tokenIdList = this.usedTokens.get(expiry);
            if (tokenIdList == null) {
                tokenIdList = new HashSet<Long>();
                this.usedTokens.put(expiry, tokenIdList);
            }
            tokenIdList.add(tokenId);
        }
        
        protected void cleanup(final long time) {
            Log.debug("IssuerHistory - cleaning up tokens");
            while (this.usedTokens.size() != 0) {
                final Long expiry = this.usedTokens.firstKey();
                if (expiry >= time) {
                    Log.debug("IssuerHistory - no tokens older than time");
                    return;
                }
                for (final Long tokenId : this.usedTokens.get(expiry)) {
                    Log.debug("IssuerHistory - removing usedtokenId: " + tokenId);
                    this.usedTokenIds.remove(tokenId);
                }
                Log.debug("IssuerHistory - removing usedtoken: " + expiry);
                this.usedTokens.remove(expiry);
            }
            Log.debug("IssuerHistory - no tokens");
        }
    }
}
