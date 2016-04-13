// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class SoundData implements Serializable
{
    private String fileName;
    private String type;
    private Map<String, String> properties;
    private static final long serialVersionUID = 1L;
    
    public SoundData() {
        this.fileName = null;
        this.type = null;
        this.properties = null;
    }
    
    public SoundData(final String fileName, final String type, final Map<String, String> properties) {
        this.fileName = null;
        this.type = null;
        this.properties = null;
        this.setFileName(fileName);
        this.setType(type);
        this.setProperties(properties);
    }
    
    @Override
    public String toString() {
        return "[SoundData: FileName=" + this.getFileName() + ", Type=" + this.getType() + ", Properties=" + this.getProperties() + "]";
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    public void addProperty(final String key, final String value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(key, value);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}
