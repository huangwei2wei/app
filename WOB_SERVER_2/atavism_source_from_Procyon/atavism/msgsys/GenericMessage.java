// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class GenericMessage extends Message
{
    protected Serializable data;
    protected Map<String, Serializable> properties;
    private static final long serialVersionUID = 1L;
    
    public GenericMessage() {
    }
    
    public GenericMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public Serializable getProperty(final String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
    
    public void setProperty(final String key, final Serializable value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Serializable>();
        }
        this.properties.put(key, value);
    }
    
    public Map<String, Serializable> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Serializable> props) {
        this.properties = props;
    }
    
    public void addProperties(final Map<String, Serializable> props) {
        this.properties.putAll(props);
    }
    
    public Serializable getData() {
        return this.data;
    }
    
    public void setData(final Serializable data) {
        this.data = data;
    }
}
