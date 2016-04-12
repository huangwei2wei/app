// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.io.IOException;
import atavism.server.util.Log;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

public class ChannelUtil
{
    public static final int TIMEOUT = 30000;
    
    public static int fillBuffer(final ByteBuffer buffer, final SocketChannel socket) throws IOException {
        Selector selector = null;
        try {
            selector = Selector.open();
            socket.register(selector, 1);
            while (buffer.remaining() > 0) {
                final int nReady = selector.select(30000L);
                if (nReady != 1) {
                    Log.debug("Connection timeout while reading");
                    break;
                }
                selector.selectedKeys().clear();
                final int nBytes = socket.read(buffer);
                if (nBytes == -1) {
                    break;
                }
            }
        }
        finally {
            selector.close();
        }
        buffer.flip();
        return buffer.limit();
    }
    
    public static boolean writeBuffer(final AOByteBuffer buffer, final SocketChannel socket) throws IOException {
        Selector selector = null;
        try {
            selector = Selector.open();
            socket.register(selector, 4);
            while (buffer.hasRemaining()) {
                final int nReady = selector.select(30000L);
                if (nReady != 1) {
                    Log.debug("Connection timeout while writing");
                    break;
                }
                selector.selectedKeys().clear();
                if (socket.write(buffer.getNioBuf()) == 0) {
                    break;
                }
            }
        }
        finally {
            selector.close();
        }
        return !buffer.hasRemaining();
    }
    
    public static void patchLengthAndFlip(final AOByteBuffer messageBuf) {
        final int len = messageBuf.position();
        messageBuf.getNioBuf().rewind();
        messageBuf.putInt(len - 4);
        messageBuf.position(len);
        messageBuf.getNioBuf().flip();
    }
}
