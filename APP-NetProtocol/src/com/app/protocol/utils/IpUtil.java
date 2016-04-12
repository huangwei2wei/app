package com.app.protocol.utils;
import java.net.InetAddress;
import java.net.UnknownHostException;
public class IpUtil {
    public static String toServerID(int ip, short port) {
        long ipInt = int2long(ip) << 16;
        long serverId = ipInt | port;
        return Long.toHexString(serverId);
    }

    public static String toServerID(String ip, short port) {
        long ipInt = int2long(ip2Int(ip)) << 16;
        long serverId = ipInt | port;
        return Long.toHexString(serverId);
    }

    public static int ip2Int(String ip) {
        try {
            InetAddress add = InetAddress.getByName(ip);
            byte[] adds = add.getAddress();
            int a = byte2int(adds[0]);
            int b = byte2int(adds[1]);
            int c = byte2int(adds[2]);
            int d = byte2int(adds[3]);
            int result = a << 24 | b << 16 | c << 8 | d;
            return result;
        } catch (UnknownHostException e) {
        }
        return 0;
    }

    public static String ip2HexString(String ip) {
        int ipInt = ip2Int(ip);
        return Integer.toHexString(ipInt);
    }

    public static String hexString2Ip(String hex) {
        return int2Ip(Integer.parseInt(hex, 16));
    }

    public static String int2Ip(int ip) {
        byte[] b = new byte[4];
        b[0] = (byte) (ip >> 24 & 0xFF);
        b[1] = (byte) (ip >> 16 & 0xFF);
        b[2] = (byte) (ip >> 8 & 0xFF);
        b[3] = (byte) (ip & 0xFF);
        try {
            InetAddress add = InetAddress.getByAddress(b);
            return add.getHostAddress();
        } catch (UnknownHostException e) {
        }
        return null;
    }

    public static long int2long(int i) {
        long l = i & 0x7FFFFFFF;
        if (i < 0) {
            l |= 2147483648L;
        }
        return l;
    }

    public static int byte2int(byte b) {
        int l = b & 0x7F;
        if (b < 0) {
            l |= 128;
        }
        return l;
    }
}
