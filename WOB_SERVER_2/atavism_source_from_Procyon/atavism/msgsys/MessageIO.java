// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Set;
import java.util.Iterator;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ClosedChannelException;
import atavism.server.util.AORuntimeException;
import atavism.server.network.AOByteBuffer;
import java.io.IOException;
import atavism.server.util.Log;
import java.util.LinkedList;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.List;

public class MessageIO implements Runnable
{
    private Callback callback;
    private List<AgentInfo> newAgents;
    private Selector ioSelector;
    private ByteBuffer readBuf;
    private boolean scanForWrite;
    private int messageLengthByteCount;
    
    public MessageIO() {
        this.newAgents = new LinkedList<AgentInfo>();
        this.scanForWrite = false;
        this.messageLengthByteCount = 4;
    }
    
    public MessageIO(final int messageLengthByteCount) {
        this.newAgents = new LinkedList<AgentInfo>();
        this.scanForWrite = false;
        this.messageLengthByteCount = 4;
        this.setMessageLengthByteCount(messageLengthByteCount);
    }
    
    public MessageIO(final Callback callback) {
        this.newAgents = new LinkedList<AgentInfo>();
        this.scanForWrite = false;
        this.messageLengthByteCount = 4;
        this.initialize(callback);
    }
    
    protected void initialize(final Callback callback) {
        this.readBuf = ByteBuffer.allocate(8192);
        this.callback = callback;
        try {
            this.ioSelector = Selector.open();
        }
        catch (IOException ex) {
            Log.exception("MessageHandler selector failed", ex);
            System.exit(1);
        }
    }
    
    public void start() {
        this.start("MessageIO");
    }
    
    public void start(final String threadName) {
        if (Log.loggingNet) {
            Log.net("MessageIO.start: Starting MessageIO thread");
        }
        final Thread thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void addAgent(final AgentInfo agentInfo) {
        synchronized (this.newAgents) {
            this.newAgents.add(agentInfo);
        }
        this.ioSelector.wakeup();
    }
    
    public void removeAgent(final AgentInfo agentInfo) {
        agentInfo.socket.keyFor(this.ioSelector).cancel();
        this.ioSelector.wakeup();
    }
    
    public void outputReady() {
        this.scanForWrite = true;
        this.ioSelector.wakeup();
    }
    
    public void addToOutputWithLength(final AOByteBuffer buf, final AgentInfo agentInfo) {
        boolean needNotify = true;
        synchronized (agentInfo.outputBuf) {
            needNotify = (agentInfo.outputBuf.position() == 0);
            this.putMessageLength(buf, agentInfo);
            final byte[] data = buf.array();
            agentInfo.outputBuf.putBytes(data, 0, buf.limit());
        }
        if (needNotify) {
            this.outputReady();
        }
    }
    
    public void addToOutput(final AOByteBuffer buf, final AgentInfo agentInfo) {
        boolean needNotify = true;
        synchronized (agentInfo.outputBuf) {
            needNotify = (agentInfo.outputBuf.position() == 0);
            final byte[] data = buf.array();
            agentInfo.outputBuf.putBytes(data, 0, buf.limit());
        }
        if (needNotify) {
            this.outputReady();
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    this.doMessageIO();
                }
            }
            catch (IOException ex) {
                Log.exception("MessageIO thread got", ex);
                continue;
            }
            catch (Exception ex2) {
                Log.exception("MessageIO thread got", ex2);
                continue;
            }
            break;
        }
    }
    
    private void doMessageIO() throws IOException {
        this.ioSelector.select();
        synchronized (this.newAgents) {
            for (final AgentInfo agentInfo : this.newAgents) {
                try {
                    agentInfo.socket.register(this.ioSelector, 5, agentInfo);
                }
                catch (ClosedChannelException ex) {
                    Log.exception("addNewAgent", ex);
                    try {
                        this.callback.handleMessageData(-1, null, agentInfo);
                    }
                    catch (AORuntimeException ex3) {}
                }
            }
            this.newAgents.clear();
        }
        final Set<SelectionKey> readyKeys = this.ioSelector.selectedKeys();
        for (final SelectionKey key : readyKeys) {
            final SocketChannel socket = (SocketChannel)key.channel();
            final AgentInfo agentInfo2 = (AgentInfo)key.attachment();
            try {
                this.handleReadyChannel(socket, key, agentInfo2);
            }
            catch (CancelledKeyException ex2) {
                Log.debug("Connection closed (cancelled) " + socket);
                try {
                    this.callback.handleMessageData(-1, null, agentInfo2);
                }
                catch (Exception ex4) {}
            }
        }
        if (this.scanForWrite) {
            final Set<SelectionKey> allKeys = this.ioSelector.keys();
            for (final SelectionKey key2 : allKeys) {
                final AgentInfo agentInfo2 = (AgentInfo)key2.attachment();
                try {
                    synchronized (agentInfo2.outputBuf) {
                        if (agentInfo2.outputBuf.position() <= 0) {
                            continue;
                        }
                        key2.interestOps(5);
                    }
                }
                catch (CancelledKeyException ex5) {}
            }
            this.scanForWrite = false;
        }
    }
    
    private void handleReadyChannel(final SocketChannel socket, final SelectionKey key, final AgentInfo agentInfo) throws IOException {
        if (key.isWritable()) {
            synchronized (agentInfo.outputBuf) {
                if (agentInfo.outputBuf.position() > 0) {
                    agentInfo.outputBuf.flip();
                    try {
                        socket.write(agentInfo.outputBuf.getNioBuf());
                    }
                    catch (IOException ex) {
                        Log.debug("Connection closed (exception on write) " + socket + " exception=" + ex);
                        return;
                    }
                    agentInfo.outputBuf.getNioBuf().compact();
                }
            }
        }
        if (key.isReadable()) {
            int nRead = -1;
            this.readBuf.clear();
            try {
                nRead = socket.read(this.readBuf);
            }
            catch (IOException ex) {
                Log.debug("Connection closed (exception) " + socket);
                socket.close();
                try {
                    this.callback.handleMessageData(-1, null, agentInfo);
                }
                catch (Exception e) {
                    Log.exception("Exception handling closed connection", e);
                }
                return;
            }
            if (nRead == -1) {
                Log.debug("Connection closed (-1) " + socket);
                socket.close();
                try {
                    this.callback.handleMessageData(-1, null, agentInfo);
                }
                catch (Exception e2) {
                    Log.exception("Exception handling closed connection", e2);
                }
                return;
            }
            this.readBuf.flip();
            this.addAgentData(this.readBuf, agentInfo);
        }
        synchronized (agentInfo.outputBuf) {
            if (agentInfo.outputBuf.position() > 0) {
                key.interestOps(5);
            }
            else {
                key.interestOps(1);
                agentInfo.outputBuf.clear();
            }
        }
    }
    
    private void addAgentData(final ByteBuffer buf, final AgentInfo agentInfo) {
        AOByteBuffer inputBuf = agentInfo.inputBuf;
        if (inputBuf.remaining() < buf.limit()) {
            int additional;
            for (additional = inputBuf.capacity(); inputBuf.remaining() + additional < buf.limit(); additional *= 2) {}
            final AOByteBuffer newBuf = new AOByteBuffer(inputBuf.capacity() + additional);
            final byte[] bytes = inputBuf.array();
            newBuf.putBytes(bytes, 0, bytes.length);
            newBuf.position(inputBuf.position());
            newBuf.limit(inputBuf.limit());
            agentInfo.inputBuf = newBuf;
            inputBuf = newBuf;
        }
        final byte[] bytes2 = buf.array();
        inputBuf.putBytes(bytes2, 0, buf.limit());
        inputBuf.flip();
        while (inputBuf.remaining() >= 4) {
            final int currentPos = inputBuf.position();
            final int messageLen = this.getMessageLength(inputBuf);
            if (inputBuf.remaining() < messageLen) {
                inputBuf.position(currentPos);
                break;
            }
            try {
                this.callback.handleMessageData(messageLen, inputBuf, agentInfo);
            }
            catch (Exception ex) {
                Log.exception("handleMessageData", ex);
            }
            inputBuf.position(currentPos + this.messageLengthByteCount + messageLen);
        }
        inputBuf.getNioBuf().compact();
    }
    
    private int getMessageLength(final AOByteBuffer inputBuf) {
        switch (this.messageLengthByteCount) {
            case 4: {
                return inputBuf.getInt();
            }
            case 2: {
                return inputBuf.getShort();
            }
            case 1: {
                return inputBuf.getByte();
            }
            default: {
                throw new AORuntimeException("MessageIO.getMessageLength: messageLengthByteCount is " + this.messageLengthByteCount);
            }
        }
    }
    
    private void putMessageLength(final AOByteBuffer buf, final AgentInfo agentInfo) {
        final int dataLen = buf.limit();
        final AOByteBuffer target = agentInfo.outputBuf;
        switch (this.messageLengthByteCount) {
            case 4: {
                target.putInt(dataLen);
                break;
            }
            case 2: {
                target.putShort((short)dataLen);
                break;
            }
            case 1: {
                target.putByte((byte)dataLen);
                break;
            }
            default: {
                Log.error("MessageIO.putBufLength: messageLengthByteCount is " + this.messageLengthByteCount);
                target.putInt(dataLen);
                break;
            }
        }
    }
    
    public int getMessageLengthByteCount() {
        return this.messageLengthByteCount;
    }
    
    public void setMessageLengthByteCount(final int messageLengthByteCount) {
        this.messageLengthByteCount = messageLengthByteCount;
    }
    
    public interface Callback
    {
        void handleMessageData(final int p0, final AOByteBuffer p1, final AgentInfo p2);
    }
}
