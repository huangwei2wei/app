// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.objects.AOObject;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class AbstractEventListener extends UnicastRemoteObject implements AOEventListener
{
    protected String name;
    
    public AbstractEventListener() throws RemoteException {
        this.name = "";
    }
    
    public AbstractEventListener(final String name) throws RemoteException {
        this.name = "";
        this.name = name;
    }
    
    @Override
    public String getName() throws RemoteException {
        return this.name;
    }
    
    @Override
    public abstract void handleEvent(final Event p0, final AOObject p1) throws RemoteException;
}
