// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.util.AORuntimeException;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class TimerEvent extends Event
{
    public TimerEvent() {
    }
    
    public TimerEvent(final AOObject obj) {
        super(obj);
    }
    
    @Override
    public AOByteBuffer toBytes() {
        throw new AORuntimeException("TimerEvent: tobytes not implemented");
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        throw new AORuntimeException("TimerEvent: parsebytes not implemented");
    }
    
    @Override
    public String getName() {
        return "TimerEvent";
    }
}
