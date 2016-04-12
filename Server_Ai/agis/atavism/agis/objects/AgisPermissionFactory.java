// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.PermissionCallback;
import atavism.server.objects.AOObject;
import atavism.server.objects.PermissionFactory;

public class AgisPermissionFactory implements PermissionFactory
{
    public PermissionCallback createPermission(final AOObject obj) {
        return new AgisPermissionCallback(obj);
    }
}
