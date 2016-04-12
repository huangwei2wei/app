// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.LinkedList;
import atavism.server.util.Log;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;

public class MessageCatalog
{
    private static List<MessageCatalog> catalogList;
    private String name;
    private int firstMsgNumber;
    private int lastMsgNumber;
    private int nextMsgNumber;
    private static HashMap<String, Integer> stringToMsgNumberMap;
    private static HashMap<Integer, MessageType> numberToMsgTypeMap;
    
    public static Integer getMessageNumber(final String msgTypeString) {
        for (final MessageCatalog catalog : MessageCatalog.catalogList) {
            final Integer num = catalog.getMsgNumberFromString(msgTypeString);
            if (num != null) {
                return num;
            }
        }
        return null;
    }
    
    public static MessageType getMessageType(final Integer msgTypeNumber) {
        if (msgTypeNumber == null) {
            return null;
        }
        return MessageCatalog.numberToMsgTypeMap.get(msgTypeNumber);
    }
    
    public static MessageType getMessageType(final String msgTypeString) {
        return getMessageType(getMessageNumber(msgTypeString));
    }
    
    public static MessageCatalog addMsgCatalog(final String name, final int firstMsgNumber, final int msgNumberCount) {
        final MessageCatalog catalog = new MessageCatalog(name, firstMsgNumber, msgNumberCount);
        addMsgCatalog(catalog);
        return catalog;
    }
    
    public static MessageCatalog addMsgCatalog(final MessageCatalog catalog) {
        assert catalog != null;
        final int first = catalog.firstMsgNumber;
        final int last = catalog.lastMsgNumber;
        for (final MessageCatalog existingCatalog : MessageCatalog.catalogList) {
            if ((first >= existingCatalog.firstMsgNumber && first <= existingCatalog.lastMsgNumber) || (last <= existingCatalog.lastMsgNumber && last >= existingCatalog.firstMsgNumber)) {
                throw new RuntimeException("MessageCatalog.addMsgCatalog: Numbers for catalog '" + catalog.name + "' overlap with number for catalog '" + existingCatalog.name + "'");
            }
        }
        MessageCatalog.catalogList.add(catalog);
        return catalog;
    }
    
    public MessageCatalog(final String name, final int firstMsgNumber, final int msgNumberCount) {
        this.name = name;
        this.firstMsgNumber = firstMsgNumber;
        this.nextMsgNumber = firstMsgNumber;
        this.lastMsgNumber = firstMsgNumber + msgNumberCount - 1;
    }
    
    public Integer getMsgNumberFromString(final String msgTypeString) {
        return MessageCatalog.stringToMsgNumberMap.get(msgTypeString);
    }
    
    public void addMsgTypeTranslation(final String msgTypeString) {
        final MessageType msgType = MessageType.intern(msgTypeString);
        this.addMsgTypeTranslation(msgType);
    }
    
    public void addMsgTypeTranslation(final MessageType msgType) {
        this.addMsgTypeTranslation(msgType, this.nextMsgNumber++);
    }
    
    public void addMsgTypeTranslation(final MessageType msgType, final int msgNumber) {
        if (msgNumber < this.firstMsgNumber || msgNumber > this.lastMsgNumber) {
            throw new RuntimeException("MessageCatalog.addMsgTypeTranslation: the message number " + msgNumber + " is outside the range of numbers handled by catalog '" + this.name + "'");
        }
        final String msgTypeString = msgType.getMsgTypeString();
        if (MessageCatalog.stringToMsgNumberMap.get(msgTypeString) != null) {
            Log.debug("MessageCatalog.addMsgTypeTranslation: skipping, a translation for msg type '" + msgTypeString + "' already exists in catalog '" + this.name + "'");
        }
        else {
            msgType.setMsgTypeNumber(msgNumber);
            MessageCatalog.stringToMsgNumberMap.put(msgTypeString, msgNumber);
            MessageCatalog.numberToMsgTypeMap.put(msgNumber, msgType);
            Log.debug("Adding msg type '" + msgTypeString + "', msgNumber " + msgNumber + "/0x" + Integer.toHexString(msgNumber));
        }
    }
    
    @Override
    public String toString() {
        return "MessageCatalog [name=" + this.name + "; firstMsgNumber=" + this.firstMsgNumber + "; lastMsgNumber=" + this.lastMsgNumber + "]";
    }
    
    public int getFirstMsgNumber() {
        return this.firstMsgNumber;
    }
    
    public int getLastMsgNumber() {
        return this.lastMsgNumber;
    }
    
    static {
        MessageCatalog.catalogList = new LinkedList<MessageCatalog>();
        MessageCatalog.stringToMsgNumberMap = new HashMap<String, Integer>();
        MessageCatalog.numberToMsgTypeMap = new HashMap<Integer, MessageType>();
    }
}
