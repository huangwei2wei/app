// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.objects.AOObject;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface AOEventListener extends Remote
{
    String getName() throws RemoteException;
    
    void handleEvent(final Event p0, final AOObject p1) throws RemoteException;
}
