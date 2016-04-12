// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.HashMap;
import atavism.server.util.Log;
import atavism.server.network.AOByteBuffer;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class MessageType implements Serializable, Marshallable
{
    protected static Map<String, MessageType> internedMsgTypes;
    protected transient String msgTypeString;
    protected transient Integer msgTypeNumber;
    private static final long serialVersionUID = 1L;
    
    public MessageType() {
    }
    
    protected MessageType(final String msgTypeString) {
        this.msgTypeString = msgTypeString;
        this.msgTypeNumber = -1;
    }
    
    public int getMsgTypeNumber() {
        if (this.msgTypeNumber == -1) {
            final Integer number = MessageCatalog.getMessageNumber(this.msgTypeString);
            if (number != null) {
                this.msgTypeNumber = number;
            }
            else {
                this.msgTypeNumber = 0;
            }
        }
        return this.msgTypeNumber;
    }
    
    public void setMsgTypeNumber(final int msgTypeNumber) {
        this.msgTypeNumber = msgTypeNumber;
    }
    
    public String getMsgTypeString() {
        return this.msgTypeString;
    }
    
    public static MessageType intern(final String typeName) {
        MessageType type = MessageType.internedMsgTypes.get(typeName);
        if (type == null) {
            type = new MessageType(typeName);
            MessageType.internedMsgTypes.put(typeName, type);
        }
        return type;
    }
    
    @Override
    public String toString() {
        return "MessageType['" + this.msgTypeString + "', " + this.msgTypeNumber + "]";
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final boolean b = in.readBoolean();
        if (b) {
            this.msgTypeNumber = in.readInt();
            final MessageType type = MessageCatalog.getMessageType(this.msgTypeNumber);
            this.msgTypeString = type.getMsgTypeString();
        }
        else {
            this.msgTypeNumber = -1;
            this.msgTypeString = in.readUTF();
        }
    }
    
    public static MessageType readObjectUtility(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final boolean b = in.readBoolean();
        if (b) {
            final int msgTypeNumber = in.readInt();
            return MessageCatalog.getMessageType(Integer.valueOf(msgTypeNumber));
        }
        final String msgTypeString = in.readUTF();
        return intern(msgTypeString);
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.msgTypeNumber > 0) {
            final MessageType type = MessageCatalog.getMessageType(this.msgTypeNumber);
            return type;
        }
        final MessageType type = intern(this.msgTypeString);
        return type;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
        if (this.msgTypeNumber > 0) {
            out.writeBoolean(true);
            out.writeInt(this.msgTypeNumber);
        }
        else {
            out.writeBoolean(false);
            out.writeUTF(this.msgTypeString);
        }
    }
    
    public static void writeObjectUtility(final ObjectOutputStream out, final MessageType type) throws IOException, ClassNotFoundException {
        if (type.msgTypeNumber > 0) {
            out.writeBoolean(true);
            out.writeInt(type.msgTypeNumber);
        }
        else {
            out.writeBoolean(false);
            out.writeUTF(type.msgTypeString);
        }
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        if (this.msgTypeNumber > 0) {
            buf.putByte((byte)1);
            buf.putShort((short)(int)this.msgTypeNumber);
        }
        else {
            buf.putByte((byte)0);
            buf.putString(this.msgTypeString);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        MessageType msgType = null;
        final byte b = buf.getByte();
        if (b != 0) {
            final int typeNum = buf.getShort();
            msgType = MessageCatalog.getMessageType(Integer.valueOf(typeNum));
            if (msgType == null) {
                Log.error("No MessageType number " + typeNum + " in MessageCatalog");
            }
        }
        else {
            msgType = intern(buf.getString());
        }
        return msgType;
    }
    
    static {
        MessageType.internedMsgTypes = new HashMap<String, MessageType>();
    }
}
