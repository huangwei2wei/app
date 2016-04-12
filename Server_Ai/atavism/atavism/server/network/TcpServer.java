// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.network;

import java.nio.channels.SocketChannel;
import atavism.server.util.Log;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import atavism.server.util.Logger;
import java.nio.channels.ServerSocketChannel;

public class TcpServer
{
    protected TcpAcceptCallback acceptCallback;
    protected ServerSocketChannel ssChannel;
    protected Thread thread;
    protected static final Logger log;
    
    public TcpServer() {
        this.acceptCallback = null;
        this.ssChannel = null;
    }
    
    public TcpServer(final int port) {
        this.acceptCallback = null;
        this.ssChannel = null;
        try {
            this.bind(port);
        }
        catch (IOException e) {
            throw new RuntimeException("TcpServer contructor bind failed", e);
        }
    }
    
    public void bind() throws IOException {
        this.bind(0);
    }
    
    public void bind(final int port) throws IOException {
        this.bind(new InetSocketAddress(port));
    }
    
    public void bind(final String hostname, final int port) throws IOException {
        this.bind(new InetSocketAddress(hostname, port));
    }
    
    public void bind(final InetAddress address, final int port) throws IOException {
        this.bind(new InetSocketAddress(address, port));
    }
    
    public void bind(final InetSocketAddress address) throws IOException {
        this.ssChannel = ServerSocketChannel.open();
        this.ssChannel.socket().bind(address);
        if (Log.loggingDebug) {
            TcpServer.log.debug("bound to " + this.getAddress() + ":" + this.getPort());
        }
    }
    
    public int getPort() {
        return this.ssChannel.socket().getLocalPort();
    }
    
    public InetAddress getAddress() {
        return this.ssChannel.socket().getInetAddress();
    }
    
    public void registerAcceptCallback(final TcpAcceptCallback cb) {
        this.acceptCallback = cb;
    }
    
    public Thread getThread() {
        return this.thread;
    }
    
    public void start() {
        if (this.acceptCallback == null) {
            throw new RuntimeException("no registered accept callback");
        }
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final SocketChannel sc = TcpServer.this.ssChannel.accept();
                        sc.configureBlocking(false);
                        TcpServer.this.acceptCallback.onTcpAccept(sc);
                    }
                }
                catch (Exception e) {
                    TcpServer.log.exception("TcpServer.run caught exception", e);
                }
            }
        };
        (this.thread = new Thread(run, "TcpAccept")).setDaemon(true);
        this.thread.start();
    }
    
    static {
        log = new Logger("TcpServer");
    }
}
