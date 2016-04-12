// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import com.app.server.atavism.server.engine.OID;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class PermissionCallback implements Serializable
{
    protected AOObject thisObj;
//    protected static final Logger log;
    protected Logger log = Logger.getLogger("navmesh");
    private static final long serialVersionUID = 1L;
    
    public PermissionCallback() {
        this.thisObj = null;
        this.setupTransient();
    }
    
    public PermissionCallback(final AOObject obj) {
        this.thisObj = null;
        this.thisObj = obj;
        this.setupTransient();
    }
    
    private void setupTransient() {
    }
    
    public boolean acquire(final AOObject acquirer) {
        return true;
    }
    
    public boolean acquireFrom(final AOObject acquirer, final AOObject obj) {
        return true;
    }
    
    public boolean drop(final AOObject dropInto) {
        return true;
    }
    
    public boolean use(final OID activatorOid) {
        return true;
    }
    
    public boolean destroy(final AOObject destroyer) {
        return true;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
 
}
