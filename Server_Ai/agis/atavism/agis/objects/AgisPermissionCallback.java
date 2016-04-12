// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.AOObject;
import atavism.server.objects.PermissionCallback;

public class AgisPermissionCallback extends PermissionCallback
{
    private static final long serialVersionUID = 1L;
    
    public AgisPermissionCallback(final AOObject obj) {
        super(obj);
    }
    
    public boolean acquire(final AOObject acquirer) {
        return this.thisObj.isItem();
    }
    
    public boolean drop(final AOObject dropInto) {
        return true;
    }
    
    public boolean use(final AOObject user) {
        return true;
    }
    
    public boolean destroy(final AOObject destroyer) {
        return true;
    }
}
