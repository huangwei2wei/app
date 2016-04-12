// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.util.Iterator;
import atavism.server.util.DebugUtils;
import atavism.msgsys.MessageType;
import atavism.server.messages.PropertyMessage;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageCatalog;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.util.Log;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.io.Serializable;
import java.util.Map;

public class MessageRegionTrigger implements RegionTrigger
{
    public static final int TARGET_MODE = 1;
    public static final int SUBJECT_MODE = 2;
    private int mode;
    private Map<String, Serializable> messageProperties;
    private Set<String> propertyExclusions;
    
    public MessageRegionTrigger() {
        this.mode = 1;
        this.messageProperties = new HashMap<String, Serializable>();
        this.propertyExclusions = new HashSet<String>();
    }
    
    public MessageRegionTrigger(final int mode) {
        this.mode = 1;
        this.messageProperties = new HashMap<String, Serializable>();
        this.propertyExclusions = new HashSet<String>();
        this.setMode(mode);
    }
    
    public MessageRegionTrigger(final int mode, final Map<String, Serializable> messageProperties, final Set<String> propertyExclusions) {
        this.mode = 1;
        this.messageProperties = new HashMap<String, Serializable>();
        this.propertyExclusions = new HashSet<String>();
        this.setMode(mode);
        this.setMessageProperties(messageProperties);
        this.setPropertyExclusions(propertyExclusions);
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setMode(final int mode) {
        this.mode = mode;
    }
    
    public Map<String, Serializable> getMessageProperties() {
        return this.messageProperties;
    }
    
    public void setMessageProperties(final Map<String, Serializable> messageProperties) {
        this.messageProperties = messageProperties;
    }
    
    public Set<String> getPropertyExclusions() {
        return this.propertyExclusions;
    }
    
    public void setPropertyExclusions(final Set<String> propertyExclusions) {
        this.propertyExclusions = propertyExclusions;
    }
    
    @Override
    public void enter(final AOObject obj, final Region region) {
        final Message message = this.makeMessage(obj, region);
        if (message == null) {
            Log.error("MessageRegionTrigger: can't build message for " + obj + " entering region " + region);
            return;
        }
        this.configureMessage(message, obj, region, "onEnter");
        Engine.getAgent().sendBroadcast(message);
    }
    
    @Override
    public void leave(final AOObject obj, final Region region) {
        final Message message = this.makeMessage(obj, region);
        if (message == null) {
            Log.error("MessageRegionTrigger: can't build message for " + obj + " leaving region " + region);
            return;
        }
        this.configureMessage(message, obj, region, "onLeave");
        Engine.getAgent().sendBroadcast(message);
    }
    
    protected Message makeMessage(final AOObject obj, final Region region) {
        MessageType type = null;
        final String typeName = (String)region.getProperty("messageType");
        if (typeName != null && !typeName.equals("")) {
            type = MessageCatalog.getMessageType(typeName);
            if (type == null) {
                Log.error("MessageRegionTrigger: unknown messageType=" + typeName);
                return null;
            }
        }
        String messageClass = (String)region.getProperty("messageClass");
        if (messageClass == null || messageClass.equals("")) {
            messageClass = "extension";
        }
        final String extensionType = (String)region.getProperty("messageExtensionType");
        Message message = null;
        if (messageClass.equals("extension")) {
            if (this.mode == 1) {
                final WorldManagerClient.TargetedExtensionMessage extMessage = new WorldManagerClient.TargetedExtensionMessage(obj.getOid(), obj.getOid());
                if (extensionType != null) {
                    extMessage.setExtensionType(extensionType);
                }
                message = extMessage;
            }
            else if (this.mode == 2) {
                final WorldManagerClient.ExtensionMessage extMessage2 = new WorldManagerClient.ExtensionMessage(obj.getOid());
                if (extensionType != null) {
                    extMessage2.setExtensionType(extensionType);
                }
                message = extMessage2;
            }
        }
        else if (messageClass.equals("property")) {
            if (this.mode == 1) {
                message = new WorldManagerClient.TargetedPropertyMessage(obj.getOid(), obj.getOid());
            }
            else if (this.mode == 2) {
                message = new PropertyMessage(type, obj.getOid());
            }
        }
        if (message != null && type != null) {
            message.setMsgType(type);
        }
        return message;
    }
    
    protected void configureMessage(final Message message, final AOObject obj, final Region region, final String action) {
        Map<String, Serializable> messageMap = null;
        if (message instanceof PropertyMessage) {
            messageMap = ((PropertyMessage)message).getPropertyMapRef();
        }
        else if (message instanceof WorldManagerClient.TargetedPropertyMessage) {
            messageMap = ((WorldManagerClient.TargetedPropertyMessage)message).getPropertyMapRef();
        }
        if (messageMap != null) {
            if (action != null) {
                messageMap.put("regionAction", action);
            }
            if (this.messageProperties != null) {
                messageMap.putAll(this.messageProperties);
            }
        }
        final String messageRegionProperties = (String)region.getProperty("messageRegionProperties");
        if (messageRegionProperties != null && messageMap != null) {
            this.copyProperties(messageRegionProperties, region.getPropertyMapRef(), messageMap);
        }
        final String objectProperties = (String)region.getProperty("messageObjectProperties");
        if (objectProperties != null && messageMap != null) {
            this.copyProperties(objectProperties, obj.getPropertyMap(), messageMap);
        }
        if (Log.loggingDebug && messageMap != null) {
            Log.debug("MessageRegionTrigger: properties=" + DebugUtils.mapToString(messageMap));
        }
    }
    
    protected void copyProperties(String propertyNames, final Map<String, Serializable> source, final Map<String, Serializable> destination) {
        propertyNames = propertyNames.trim();
        if (propertyNames.equals("ALL")) {
            for (final String prop : source.keySet()) {
                if (!this.propertyExclusions.contains(prop)) {
                    final Serializable value = source.get(prop);
                    if (value == null) {
                        continue;
                    }
                    destination.put(prop, value);
                }
            }
        }
        else {
            final String[] arr$;
            final String[] props = arr$ = propertyNames.split(",");
            for (String prop2 : arr$) {
                prop2 = prop2.trim();
                if (!this.propertyExclusions.contains(prop2)) {
                    final Serializable value2 = source.get(prop2);
                    if (value2 != null) {
                        destination.put(prop2, value2);
                    }
                }
            }
        }
    }
}
