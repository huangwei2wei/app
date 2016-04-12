// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.Iterator;
import java.io.Serializable;
import java.util.Map;
import atavism.server.network.AOByteBuffer;

public class DebugUtils
{
    public static String byteArrayToHexString(final AOByteBuffer buf) {
        final String bytes = byteArrayToHexString(buf.copyBytes());
        buf.rewind();
        return bytes;
    }
    
    public static String byteArrayToHexString(final byte[] in) {
        byte ch = 0;
        int i = 0;
        if (in == null || in.length <= 0) {
            return null;
        }
        final String[] pseudo = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
        final StringBuffer out = new StringBuffer(in.length * 2);
        final StringBuffer chars = new StringBuffer(in.length);
        while (i < in.length) {
            ch = (byte)(in[i] & 0xF0);
            ch >>>= 4;
            ch &= 0xF;
            out.append(pseudo[ch]);
            ch = (byte)(in[i] & 0xF);
            out.append(pseudo[ch]);
            if (in[i] >= 32 && in[i] <= 126) {
                chars.append((char)in[i]);
            }
            else {
                chars.append("*");
            }
            ++i;
        }
        return new String(out) + " == " + new String(chars);
    }
    
    public static void logDebugMap(final Map<String, Serializable> map) {
        if (!Log.loggingDebug) {
            return;
        }
        Log.debug("PRINTMAP START");
        for (final Map.Entry<String, Serializable> e : map.entrySet()) {
            final Object key = e.getKey();
            final Object val = e.getValue();
            if (Log.loggingDebug) {
                Log.debug("entry: key=" + key.toString() + ", value=" + val.toString());
            }
        }
        Log.debug("PRINTMAP END");
    }
    
    public static String mapToString(final Map<String, Serializable> map) {
        if (map == null) {
            return "null";
        }
        String result = "[";
        for (final Map.Entry<String, Serializable> entry : map.entrySet()) {
            if (result.length() > 1) {
                result += ",";
            }
            result = result + entry.getKey() + "=" + entry.getValue();
        }
        result += "]";
        return result;
    }
}
