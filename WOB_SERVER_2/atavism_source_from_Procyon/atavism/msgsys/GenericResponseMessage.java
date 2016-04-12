// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.HashMap;
import java.util.Map;

public class GenericResponseMessage extends ResponseMessage
{
    protected Object data;
    protected Map<String, Object> properties;
    private static final long serialVersionUID = 1L;
    
    public GenericResponseMessage() {
    }
    
    public GenericResponseMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public GenericResponseMessage(final Message requestMessage) {
        super(requestMessage);
    }
    
    public GenericResponseMessage(final Message requestMessage, final Object data) {
        super(requestMessage);
        this.setData(data);
    }
    
    public Object getProperty(final String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
    
    public void setProperty(final String key, final Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(key, value);
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Object> props) {
        this.properties = props;
    }
    
    public void addProperties(final Map<String, Object> props) {
        this.properties.putAll(props);
    }
    
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
        this.data = data;
    }
}
