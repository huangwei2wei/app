// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

class MessageTypes
{
    public static MessageType MSG_TYPE_AGENT_HELLO;
    public static MessageType MSG_TYPE_HELLO_RESPONSE;
    public static MessageType MSG_TYPE_ALLOC_NAME;
    public static MessageType MSG_TYPE_ALLOC_NAME_RESPONSE;
    public static MessageType MSG_TYPE_NEW_AGENT;
    public static MessageType MSG_TYPE_AGENT_STATE;
    public static MessageType MSG_TYPE_ADVERTISE;
    public static MessageType MSG_TYPE_SUBSCRIBE;
    public static MessageType MSG_TYPE_UNSUBSCRIBE;
    public static MessageType MSG_TYPE_FILTER_UPDATE;
    public static MessageType MSG_TYPE_AWAIT_PLUGIN_DEPENDENTS;
    public static MessageType MSG_TYPE_PLUGIN_AVAILABLE;
    public static MessageType MSG_TYPE_RESPONSE;
    public static MessageType MSG_TYPE_BOOLEAN_RESPONSE;
    public static MessageType MSG_TYPE_LONG_RESPONSE;
    public static MessageType MSG_TYPE_INT_RESPONSE;
    public static MessageType MSG_TYPE_STRING_RESPONSE;
    public static MessageType MSG_TYPE_OID_RESPONSE;
    public static MessageType MSG_TYPE_ALL_TYPES;
    public static MessageCatalog catalog;
    private static MessageType firstMsgType;
    private static MessageType lastMsgType;
    
    public static void initializeCatalog() {
        if (MessageTypes.catalog != null) {
            return;
        }
        (MessageTypes.catalog = MessageCatalog.addMsgCatalog("msgsysCatalog", 5000, 100)).addMsgTypeTranslation(MessageTypes.MSG_TYPE_AGENT_HELLO);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_HELLO_RESPONSE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_ALLOC_NAME);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_NEW_AGENT);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_AGENT_STATE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_ADVERTISE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_SUBSCRIBE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_UNSUBSCRIBE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_FILTER_UPDATE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_AWAIT_PLUGIN_DEPENDENTS);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_PLUGIN_AVAILABLE);
        MessageTypes.firstMsgType = MessageTypes.MSG_TYPE_AGENT_HELLO;
        MessageTypes.lastMsgType = MessageTypes.MSG_TYPE_FILTER_UPDATE;
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_RESPONSE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_BOOLEAN_RESPONSE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_LONG_RESPONSE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_INT_RESPONSE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_STRING_RESPONSE);
        MessageTypes.catalog.addMsgTypeTranslation(MessageTypes.MSG_TYPE_ALLOC_NAME_RESPONSE);
    }
    
    public static boolean isInternal(final MessageType type) {
        final Integer num = type.getMsgTypeNumber();
        return num >= MessageTypes.firstMsgType.getMsgTypeNumber() && num <= MessageTypes.lastMsgType.getMsgTypeNumber();
    }
    
    static {
        MessageTypes.MSG_TYPE_AGENT_HELLO = MessageType.intern("msgsys.AGENT_HELLO");
        MessageTypes.MSG_TYPE_HELLO_RESPONSE = MessageType.intern("msgsys.HELLO_RESPONSE");
        MessageTypes.MSG_TYPE_ALLOC_NAME = MessageType.intern("msgsys.ALLOC_NAME");
        MessageTypes.MSG_TYPE_ALLOC_NAME_RESPONSE = MessageType.intern("msgsys.ALLOC_NAME_RESPONSE");
        MessageTypes.MSG_TYPE_NEW_AGENT = MessageType.intern("msgsys.NEW_AGENT");
        MessageTypes.MSG_TYPE_AGENT_STATE = MessageType.intern("msgsys.AGENT_STATE");
        MessageTypes.MSG_TYPE_ADVERTISE = MessageType.intern("msgsys.ADVERTISE");
        MessageTypes.MSG_TYPE_SUBSCRIBE = MessageType.intern("msgsys.SUBSCRIBE");
        MessageTypes.MSG_TYPE_UNSUBSCRIBE = MessageType.intern("msgsys.UNSUBSCRIBE");
        MessageTypes.MSG_TYPE_FILTER_UPDATE = MessageType.intern("msgsys.FILTER_UPDATE");
        MessageTypes.MSG_TYPE_AWAIT_PLUGIN_DEPENDENTS = MessageType.intern("msgsys.AWAIT_PLUGIN_DEPENDENTS");
        MessageTypes.MSG_TYPE_PLUGIN_AVAILABLE = MessageType.intern("msgsys.PLUGIN_AVAILABLE");
        MessageTypes.MSG_TYPE_RESPONSE = MessageType.intern("msgsys.RESPONSE");
        MessageTypes.MSG_TYPE_BOOLEAN_RESPONSE = MessageType.intern("msgsys.BOOLEAN_RESPONSE");
        MessageTypes.MSG_TYPE_LONG_RESPONSE = MessageType.intern("msgsys.LONG_RESPONSE");
        MessageTypes.MSG_TYPE_INT_RESPONSE = MessageType.intern("msgsys.INT_RESPONSE");
        MessageTypes.MSG_TYPE_STRING_RESPONSE = MessageType.intern("msgsys.STRING_RESPONSE");
        MessageTypes.MSG_TYPE_OID_RESPONSE = MessageType.intern("msgsys.OID_RESPONSE");
        MessageTypes.MSG_TYPE_ALL_TYPES = MessageType.intern("msgsys.all");
    }
}
