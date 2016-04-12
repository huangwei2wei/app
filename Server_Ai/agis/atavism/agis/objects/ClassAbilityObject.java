// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.server.objects.ObjectType;
import atavism.server.engine.OID;
import atavism.server.engine.Namespace;
import atavism.server.objects.Entity;

public class ClassAbilityObject extends Entity
{
    String playerclass;
    private static final long serialVersionUID = 1L;
    
    public ClassAbilityObject() {
        this.setNamespace(Namespace.CLASSABILITY);
    }
    
    public ClassAbilityObject(final OID objOid) {
        super(objOid);
        this.setNamespace(Namespace.CLASSABILITY);
    }
    
    public String toString() {
        return "[Entity: " + this.getName() + ":" + this.getOid() + "]";
    }
    
    public ObjectType getType() {
        return ObjectType.intern((short)11, "ClassAbilityObject");
    }
    
    public String getPlayerClass() {
        return this.playerclass;
    }
    
    public void setPlayerClass(final String playerclassname) {
        this.playerclass = playerclassname;
    }
    
    public void updateBaseStat(final int id, final int modifier) {
        final AgisStat stat = (AgisStat)this.getProperty(String.valueOf(id) + "_exp");
        final AgisStat rank = (AgisStat)this.getProperty(String.valueOf(id) + "_rank");
        if (stat == null || rank == null) {
            ClassAbilityObject.log.warn("ClassAbilityObject.updateBaseStat - player does nt have the skill/ability " + id);
            return;
        }
        if (rank.base < rank.max) {
            stat.modifyBaseValue(modifier);
            ClassAbilityClient.sendXPUpdate(this.getOid(), stat.getName(), stat.getCurrentValue());
            this.statSendUpdate(false);
        }
    }
    
    public void statSendUpdate(final boolean sendAll) {
        this.statSendUpdate(sendAll, null);
    }
    
    public void statSendUpdate(final boolean sendAll, final OID targetOid) {
        this.lock.lock();
        try {
            PropertyMessage propMsg = null;
            WorldManagerClient.TargetedPropertyMessage targetPropMsg = null;
            if (targetOid == null) {
                propMsg = new PropertyMessage(this.getOid());
            }
            else {
                targetPropMsg = new WorldManagerClient.TargetedPropertyMessage(targetOid, this.getOid());
            }
            int count = 0;
            for (final Object value : this.getPropertyMap().values()) {
                if (value instanceof AgisStat) {
                    final AgisStat stat = (AgisStat)value;
                    if (!sendAll && !stat.isDirty()) {
                        continue;
                    }
                    if (propMsg != null) {
                        propMsg.setProperty(stat.getName(), (Serializable)stat.getCurrentValue());
                    }
                    else {
                        targetPropMsg.setProperty(stat.getName(), (Serializable)stat.getCurrentValue());
                    }
                    if (!sendAll) {
                        stat.setDirty(false);
                    }
                    ++count;
                }
            }
            if (count > 0) {
                Engine.getPersistenceManager().setDirty((Entity)this);
                if (propMsg != null) {
                    Engine.getAgent().sendBroadcast((Message)propMsg);
                }
                else {
                    Engine.getAgent().sendBroadcast((Message)targetPropMsg);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
}
