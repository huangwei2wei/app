// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

public class BinaryState extends ObjState
{
    private String name;
    private Boolean value;
    private static final long serialVersionUID = 1L;
    
    public BinaryState() {
        this.name = null;
        this.value = null;
    }
    
    public BinaryState(final String stateName, final Boolean value) {
        this.name = null;
        this.value = null;
        this.setStateName(stateName);
        this.value = value;
    }
    
    @Override
    public Integer getIntValue() {
        return ((boolean)this.value) ? 1 : 0;
    }
    
    @Override
    public String getStateName() {
        return this.name;
    }
    
    public void setStateName(final String name) {
        this.name = name;
    }
    
    public Boolean getValue() {
        return this.value;
    }
    
    public void setValue(final Boolean val) {
        this.value = val;
    }
    
    public Boolean isSet() {
        return this.getValue();
    }
}
