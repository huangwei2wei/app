/**
 * Copyright 1999-2002,2004 The Apache Software Foundation. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.wyd.channel.utils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/***
 * format validation
 *
 * This class encodes/decodes hexadecimal data
 * 
 * @xerces.internal  
 * 
 * @author Jeffrey Rodriguez
 * @version $Id: HexBin.java,v 1.2.6.1 2005/09/06 11:44:41 neerajbj Exp $
 */
public final class HexBin {
    static private final int    BASELENGTH        = 128;
    static private final int    LOOKUPLENGTH      = 16;
    static final private byte[] hexNumberTable    = new byte[BASELENGTH];
    static final private char[] lookUpHexAlphabet = new char[LOOKUPLENGTH];
    private static final String MAC_NAME          = "HmacSHA1";
    private static final String ENCODING          = "UTF-8";
    static {
        for (int i = 0; i < BASELENGTH; i++) {
            hexNumberTable[i] = -1;
        }
        for (int i = '9'; i >= '0'; i--) {
            hexNumberTable[i] = (byte) (i - '0');
        }
        for (int i = 'F'; i >= 'A'; i--) {
            hexNumberTable[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'f'; i >= 'a'; i--) {
            hexNumberTable[i] = (byte) (i - 'a' + 10);
        }
        for (int i = 0; i < 10; i++) {
            lookUpHexAlphabet[i] = (char) ('0' + i);
        }
        for (int i = 10; i <= 15; i++) {
            lookUpHexAlphabet[i] = (char) ('A' + i - 10);
        }
    }

    /***
     * Encode a byte array to hex string
     *
     * @param binaryData array of byte to encode
     * @return return encoded string
     */
    static public String encode(byte[] binaryData) {
        if (binaryData == null) return null;
        int lengthData = binaryData.length;
        int lengthEncode = lengthData * 2;
        char[] encodedData = new char[lengthEncode];
        int temp;
        for (int i = 0; i < lengthData; i++) {
            temp = binaryData[i];
            if (temp < 0) temp += 256;
            encodedData[i * 2] = lookUpHexAlphabet[temp >> 4];
            encodedData[i * 2 + 1] = lookUpHexAlphabet[temp & 0xf];
        }
        return new String(encodedData);
    }

    /***
     * Decode hex string to a byte array
     *
     * @param encoded encoded string
     * @return return array of byte to encode
     */
    static public byte[] decode(String encoded) {
        if (encoded == null) return null;
        int lengthData = encoded.length();
        if (lengthData % 2 != 0) return null;
        char[] binaryData = encoded.toCharArray();
        int lengthDecode = lengthData / 2;
        byte[] decodedData = new byte[lengthDecode];
        byte temp1, temp2;
        char tempChar;
        for (int i = 0; i < lengthDecode; i++) {
            tempChar = binaryData[i * 2];
            temp1 = (tempChar < BASELENGTH) ? hexNumberTable[tempChar] : -1;
            if (temp1 == -1) return null;
            tempChar = binaryData[i * 2 + 1];
            temp2 = (tempChar < BASELENGTH) ? hexNumberTable[tempChar] : -1;
            if (temp2 == -1) return null;
            decodedData[i] = (byte) ((temp1 << 4) | temp2);
        }
        return decodedData;
    }

    public static String HashToMD5Hex(String sourceStr) {
        String signStr = null;
        try {
            byte[] bytes = sourceStr.getBytes("utf-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            byte[] md5Byte = md5.digest();
            if (md5Byte != null) {
                signStr = HexBin.encode(md5Byte);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signStr;
    }

    /**
    * 使用HMAC-SHA1 签名方法对对encryptText进行签名
    * @param encryptText 被签名的字符串
    * @param encryptKey 密钥
    * @return 返回被加密后的字符串
    * @throws Exception
    */
    public static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定Mac 算法的Mac 对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化Mac 对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成Mac 操作
        byte[] digest = mac.doFinal(text);
        StringBuilder sBuilder = bytesToHexString(digest);
        return sBuilder.toString();
    }

    /**
    * 转换成Hex
    *
    * @param bytesArray
    */
    public static StringBuilder bytesToHexString(byte[] bytesArray) {
        if (bytesArray == null) {
            return null;
        }
        StringBuilder sBuilder = new StringBuilder();
        for (byte b : bytesArray) {
            String hv = String.format("%02x", b);
            sBuilder.append(hv);
        }
        return sBuilder;
    }

    /**
     * 利用MD5进行加密
     * 
     * @param str
     *            待加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException
     *             没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public static String getMD5(String str) {
        String reStr = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");// 创建具有指定算法名称的信息摘要
            md.update(str.getBytes());// 使用指定的字节更新摘要。
            byte ss[] = md.digest();// 通过执行诸如填充之类的最终操作完成哈希计算
            reStr = bytes2String(ss);
        } catch (NoSuchAlgorithmException e) {
        }
        return reStr;
    }

    private static String bytes2String(byte[] aa) {// 将字节数组转换为字符串
        String hash = "";
        for (int i = 0; i < aa.length; i++) {// 循环数组
            int temp;
            if (aa[i] < 0) // 如果小于零，将其变为正数
                temp = 256 + aa[i];
            else
                temp = aa[i];
            if (temp < 16) hash += "0";
            hash += Integer.toString(temp, 16);// 转换为16进制
        }
        hash = hash.toUpperCase();// 全部转换为大写
        return hash;
    }
}
