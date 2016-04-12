// 
// Decompiled by Procyon v0.5.30
// 

package atavism.persistence;

class Coding
{
    private static final char[] hexDigits;
    
    static String stringEncode(final String string) {
        StringBuilder sb = null;
        for (int ii = 0; ii < string.length(); ++ii) {
            final char c = string.charAt(ii);
            if (c < '\u0080') {
                if (c < ' ' || c == ',' || c == '}' || c == '\n' || c == '%' || c == '#' || c == '\u007f') {
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append(string.substring(0, ii));
                    }
                    sb.append('%');
                    sb.append(Coding.hexDigits[(byte)c >> 4 & 0xF]);
                    sb.append(Coding.hexDigits[(byte)c >> 0 & 0xF]);
                }
                else if (sb != null) {
                    sb.append(c);
                }
            }
            else {
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(string.substring(0, ii));
                }
                final int u16 = c;
                sb.append('#');
                sb.append(Coding.hexDigits[u16 >> 12 & 0xF]);
                sb.append(Coding.hexDigits[u16 >> 8 & 0xF]);
                sb.append(Coding.hexDigits[u16 >> 4 & 0xF]);
                sb.append(Coding.hexDigits[u16 >> 0 & 0xF]);
            }
        }
        if (sb != null) {
            return new String(sb);
        }
        return string;
    }
    
    private static int decodeHex(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + '\n';
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + '\n';
        }
        return -1;
    }
    
    static String stringDecode(final String string) {
        StringBuilder sb = null;
        for (int ii = 0; ii < string.length(); ++ii) {
            final char c = string.charAt(ii);
            if (c == '%') {
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(string.substring(0, ii));
                }
                int u8 = decodeHex(string.charAt(++ii)) << 4;
                u8 |= decodeHex(string.charAt(++ii));
                sb.append((char)u8);
            }
            else if (c == '#') {
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(string.substring(0, ii));
                }
                int u9 = decodeHex(string.charAt(++ii)) << 12;
                u9 |= decodeHex(string.charAt(++ii)) << 8;
                u9 |= decodeHex(string.charAt(++ii)) << 4;
                u9 |= decodeHex(string.charAt(++ii));
                sb.append((char)u9);
            }
            else if (sb != null) {
                sb.append(c);
            }
        }
        if (sb != null) {
            return new String(sb);
        }
        return string;
    }
    
    static {
        hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
