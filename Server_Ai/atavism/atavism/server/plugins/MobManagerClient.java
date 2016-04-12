// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.server.engine.OID;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.objects.SpawnData;
import atavism.msgsys.MessageType;

public class MobManagerClient
{
    public static MessageType MSG_TYPE_CREATE_SPAWN_GEN;
    public static MessageType MSG_TYPE_SET_AGGRO_RADIUS;
    
    public static boolean createSpawnGenerator(final SpawnData spawnData) {
        final CreateSpawnGeneratorMessage message = new CreateSpawnGeneratorMessage(spawnData);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static void setAggroRadius(final OID mob, final OID target, final int radius) {
        final SetAggroRadiusMessage message = new SetAggroRadiusMessage(mob, target, radius);
        Engine.getAgent().sendBroadcast(message);
    }
    
    static {
        MobManagerClient.MSG_TYPE_CREATE_SPAWN_GEN = MessageType.intern("ao.CREATE_SPAWN_GEN");
        MobManagerClient.MSG_TYPE_SET_AGGRO_RADIUS = MessageType.intern("ao.SET_AGGRO_RADIUS");
    }
    
    public static class CreateSpawnGeneratorMessage extends Message
    {
        private SpawnData spawnData;
        private static final long serialVersionUID = 1L;
        
        public CreateSpawnGeneratorMessage() {
        }
        
        public CreateSpawnGeneratorMessage(final SpawnData spawnData) {
            super(MobManagerClient.MSG_TYPE_CREATE_SPAWN_GEN);
            this.setSpawnData(spawnData);
        }
        
        public SpawnData getSpawnData() {
            return this.spawnData;
        }
        
        public void setSpawnData(final SpawnData spawnData) {
            this.spawnData = spawnData;
        }
    }
    
    public static class SetAggroRadiusMessage extends Message
    {
        private OID mob;
        private OID target;
        private int radius;
        private static final long serialVersionUID = 1L;
        
        public SetAggroRadiusMessage() {
        }
        
        public SetAggroRadiusMessage(final OID mob, final OID target, final int radius) {
            super(MobManagerClient.MSG_TYPE_SET_AGGRO_RADIUS);
            this.setMob(mob);
            this.setTarget(target);
            this.setRadius(radius);
        }
        
        public OID getMob() {
            return this.mob;
        }
        
        public void setMob(final OID mob) {
            this.mob = mob;
        }
        
        public OID getTarget() {
            return this.target;
        }
        
        public void setTarget(final OID target) {
            this.target = target;
        }
        
        public int getRadius() {
            return this.radius;
        }
        
        public void setRadius(final int radius) {
            this.radius = radius;
        }
    }
}
