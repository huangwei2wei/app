package com.wyd.channel.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.bouncycastle.util.encoders.Base64;
/**
 * RSA加解密,RSA签名、签名验证类
 *
 */
public class RSAUtil_FL {
    private static RSAUtil_FL rsa = new RSAUtil_FL();

    // /**
    // * 加密,key可以是公钥，也可以是私钥
    // *
    // * @param message
    // * @return
    // * @throws Exception
    // */
    // public String encrypt(String message, Key key) throws Exception {
    // Cipher cipher = Cipher.getInstance("RSA");
    // cipher.init(Cipher.ENCRYPT_MODE, key);
    // byte[] encryptBytes=cipher.doFinal(message.getBytes());
    // return Base64.encode(encryptBytes);
    // }
    /**
     * 解密，key可以是公钥，也可以是私钥，如果是公钥加密就用私钥解密，反之亦然
     *
     * @param message
     * @return
     * @throws Exception
     */
    public String decrypt(String message, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        // byte[] decryptBytes = cipher.doFinal(toBytes(message));
        byte[] decryptBytes = cipher.doFinal(Base64.decode(message));
        // return Base64.encode(decryptBytes);
        return new String(decryptBytes);
    }

    /**
     * 用私钥签名
     *
     * @param message
     * @param key
     * @return
     * @throws Exception
     */
    public byte[] sign(String message, PrivateKey key) throws Exception {
        Signature signetcheck = Signature.getInstance("MD5withRSA");
        signetcheck.initSign(key);
        signetcheck.update(message.getBytes("ISO-8859-1"));
        return signetcheck.sign();
    }

    /**
     * 用公钥验证签名的正确性
     *
     * @param message
     * @param signStr
     * @return
     * @throws Exception
     */
    public boolean verifySign(String message, String signStr, PublicKey key) throws Exception {
        if (message == null || signStr == null || key == null) {
            return false;
        }
        Signature signetcheck = Signature.getInstance("MD5withRSA");
        signetcheck.initVerify(key);
        signetcheck.update(message.getBytes("ISO-8859-1"));
        return signetcheck.verify(toBytes(signStr));
    }

    /**
     * 从pem文件读取RSA PublicKey
     * filename 为pem文件访问绝对路径。
     * @param fileName  
     * @return
     * @throws Exception
     */
    public static RSAPublicKey initPublicKey(String keyFilePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(keyFilePath));
            String s = br.readLine();
            StringBuffer publicBuff = new StringBuffer();
            s = br.readLine();
            while (s.charAt(0) != '-') {
                publicBuff.append(s + "\r");
                s = br.readLine();
            }
            byte[] keybyte = Base64.decode(publicBuff.toString());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            // PublicKey publicKey = kf.generatePublic(keySpec);
            // RSAPublicKeySpec keySpec = new RSAPublicKeySpec();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keybyte);
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从pem文件读取RSA PrivateKey , 根据文件获取私钥
     * filename 为pem文件访问绝对路径。
     * @param fileName  
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey initPrivateKey(String keyFilePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(keyFilePath));
            String s = br.readLine();
            StringBuffer privateBuff = new StringBuffer();
            s = br.readLine();
            while (s.charAt(0) != '-') {
                privateBuff.append(s + "\r");
                s = br.readLine();
            }
            byte[] keybyte = Base64.decode(privateBuff.toString());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            // PublicKey publicKey = kf.generatePublic(keySpec);
            // RSAPublicKeySpec keySpec = new RSAPublicKeySpec();
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keybyte);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keybyte);
            RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            return null;
        }
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
            sb.append(HEXCHAR[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static final byte[] toBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
    private static char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    // /**
    // * RSA私钥加密
    // * 区分游戏，加载不同的私钥文件，生成密文
    // * @param gameid
    // * @param timestamp
    // * @param uuid
    // * @return
    // */
    // public static String encrypt(int gameid, String plainText){
    // String encryptText = "";
    //
    // String privateKeyFilePath = rsa.getKeyFilePath(gameid, false);
    // try {
    // if (rsa.isExistKeyFile(privateKeyFilePath)) {
    // RSAPrivateKey privateKey = RSAUtil_FL.initPrivateKey(privateKeyFilePath);
    // encryptText = rsa.encrypt(plainText, privateKey);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return encryptText;
    // }
    /**
     * RSA公钥解密
     * @param gameid
     * @param encryptText
     * @return
     */
    public static String decrypt(int gameid, String encryptText) {
        String plainText = "";
        try {
            String publicKeyFilePath = rsa.getKeyFilePath(gameid, true);
            if (rsa.isExistKeyFile(publicKeyFilePath)) {
                RSAPublicKey publickKey = RSAUtil_FL.initPublicKey(publicKeyFilePath);
                plainText = rsa.decrypt(encryptText, publickKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plainText;
    }

    /**
     * 检测密钥文件是否存在
     * @param filePath
     * @return
     */
    private boolean isExistKeyFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取公钥和私钥文件路径
     * @param gameid
     * @param isPublicKey
     * @return
     */
    private String getKeyFilePath(int gameid, boolean isPublicKey) {
        if (isPublicKey) {
            return this.getClass().getResource("/").getPath() + "rsasecret/" + gameid + "_SignKey.pub";
        } else {
            return this.getClass().getResource("/").getPath() + "rsasecret/" + gameid + ".pri";
        }
    }
    // /**
    // * 测试函数
    // * filename 为pem文件访问绝对路径。
    // * @param fileName
    // * @return
    // * @throws Exception
    // */
    // public static void main(String[] args) throws Exception {
    // System.out.println(rsa.getKeyFilePath(1234567890, true));
    // String messageStr = "走抽根烟去！111";
    //
    // //私钥加密
    // String encryptData = encrypt(1234567890,messageStr);
    // System.out.println(encryptData);
    // //公钥解密
    // String dencryptData = decrypt(1234567890,encryptData);
    // System.out.println(dencryptData);
    //
    // }
}