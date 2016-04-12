// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import atavism.server.network.AOByteBuffer;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecureTokenUtil
{
    public static SecretKey generateDomainKey() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA1");
        }
        catch (NoSuchAlgorithmException e) {
            Log.exception("SecureTokenManager.generateDomainKey: could not get KeyGenerator instance.", e);
            throw new RuntimeException(e);
        }
        keyGen.init(160);
        final SecretKey key = keyGen.generateKey();
        return key;
    }
    
    public static byte[] encodeDomainKey(final long keyId, final SecretKey key) {
        final AOByteBuffer buf = new AOByteBuffer(256);
        buf.putLong(keyId);
        buf.putString(key.getAlgorithm());
        final byte[] encodedKey = key.getEncoded();
        buf.putBytes(encodedKey, 0, encodedKey.length);
        buf.flip();
        final byte[] outKey = new byte[buf.remaining()];
        buf.getBytes(outKey, 0, outKey.length);
        return outKey;
    }
    
    public static KeyPair generateMasterKeyPair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("DSA");
        }
        catch (NoSuchAlgorithmException e) {
            Log.exception("SecureTokenManager.generateMasterKeyPair: could not get DSA KeyPairGenerator instance.", e);
            throw new RuntimeException(e);
        }
        keyGen.initialize(1024);
        final KeyPair pair = keyGen.generateKeyPair();
        return pair;
    }
    
    public static byte[] encodeMasterPrivateKey(final long keyId, final PrivateKey privKey) {
        final AOByteBuffer buf = new AOByteBuffer(1024);
        buf.putLong(keyId);
        buf.putString(privKey.getAlgorithm());
        final byte[] encodedKey = privKey.getEncoded();
        buf.putBytes(encodedKey, 0, encodedKey.length);
        buf.flip();
        final byte[] outKey = new byte[buf.remaining()];
        buf.getBytes(outKey, 0, outKey.length);
        return outKey;
    }
    
    public static byte[] encodeMasterPublicKey(final long keyId, final PublicKey pubKey) {
        final AOByteBuffer buf = new AOByteBuffer(1024);
        buf.putLong(keyId);
        buf.putString(pubKey.getAlgorithm());
        final byte[] encodedKey = pubKey.getEncoded();
        buf.putBytes(encodedKey, 0, encodedKey.length);
        buf.flip();
        final byte[] outKey = new byte[buf.remaining()];
        buf.getBytes(outKey, 0, outKey.length);
        return outKey;
    }
    
    public static void main(final String[] args) {
        Log.init();
        if (args.length != 1) {
            System.exit(-1);
        }
        final Integer keyId = Integer.parseInt(args[0]);
        final KeyPair pair = generateMasterKeyPair();
        final PrivateKey priv = pair.getPrivate();
        final PublicKey pub = pair.getPublic();
        System.out.println("master key id = " + keyId);
        System.out.println("");
        final byte[] encodedPrivKey = encodeMasterPrivateKey(keyId, pair.getPrivate());
        System.out.println("encoded private key:");
        System.out.println(Base64.encodeBytes(encodedPrivKey));
        System.out.println("");
        final byte[] encodedPubKey = encodeMasterPublicKey(keyId, pair.getPublic());
        System.out.println("encoded public key:");
        System.out.println(Base64.encodeBytes(encodedPubKey));
        System.out.println("");
        System.exit(0);
    }
}
