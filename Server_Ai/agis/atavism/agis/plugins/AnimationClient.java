// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.LockFactory;
import atavism.server.network.AOByteBuffer;
import java.util.Set;
import java.util.Iterator;
import atavism.server.engine.Engine;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.EventParser;
import atavism.msgsys.SubjectMessage;
import java.io.Serializable;
import atavism.agis.objects.CoordinatedEffect;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;

public class AnimationClient
{
    public static final String TEMPL_ANIM = ":tmpl.anim";
    public static final MessageType MSG_TYPE_INVOKE_EFFECT;
    
    static {
        MSG_TYPE_INVOKE_EFFECT = MessageType.intern("ao.INVOKE_EFFECT");
    }
    
    public static void playSingleAnimation(final OID oid, final String animName) {
        if (Log.loggingDebug) {
            Log.debug("AnimationClient.playSingleAnimation: playing anim " + animName);
        }
        final CoordinatedEffect effect = new CoordinatedEffect("PlayAnimation");
        effect.sendSourceOid(true);
        effect.putArgument("animName", animName);
        effect.invoke(oid, null);
    }
    
    public static class InvokeEffectMessage extends SubjectMessage implements EventParser
    {
        protected String effectName;
        protected OID effectOid;
        protected transient Lock lock;
        protected Map<String, Serializable> propertyMap;
        private static final long serialVersionUID = 1L;
        
        public InvokeEffectMessage() {
            this.lock = null;
            this.propertyMap = new HashMap<String, Serializable>();
            this.setMsgType(AnimationClient.MSG_TYPE_INVOKE_EFFECT);
            this.setupTransient();
        }
        
        public InvokeEffectMessage(final OID oid, final String effectName) {
            super(AnimationClient.MSG_TYPE_INVOKE_EFFECT, oid);
            this.lock = null;
            this.propertyMap = new HashMap<String, Serializable>();
            this.setupTransient();
            this.setEffectName(effectName);
            this.setEffectOid(Engine.getOIDManager().getNextOid());
        }
        
        public String toString() {
            String s = "[InvokeEffectMessage super=" + super.toString();
            s = String.valueOf(s) + " effectName=" + this.effectName + " effectOid=" + this.effectOid;
            for (final Map.Entry<String, Serializable> entry : this.propertyMap.entrySet()) {
                final String key = entry.getKey();
                final Serializable val = entry.getValue();
                s = String.valueOf(s) + " key=" + key + ",value=" + val;
            }
            return String.valueOf(s) + "]";
        }
        
        public void setEffectName(final String effectName) {
            this.effectName = effectName;
        }
        
        public String getEffectName() {
            return this.effectName;
        }
        
        public void setEffectOid(final OID oid) {
            this.effectOid = oid;
        }
        
        public OID getEffectOid() {
            return this.effectOid;
        }
        
        public void put(final String key, final Serializable val) {
            this.setProperty(key, val);
        }
        
        public void setProperty(final String key, final Serializable val) {
            this.lock.lock();
            try {
                this.propertyMap.put(key, val);
            }
            finally {
                this.lock.unlock();
            }
            this.lock.unlock();
        }
        
        public Serializable get(final String key) {
            return this.getProperty(key);
        }
        
        public Serializable getProperty(final String key) {
            this.lock.lock();
            try {
                return this.propertyMap.get(key);
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public Set<String> keySet() {
            this.lock.lock();
            try {
                return this.propertyMap.keySet();
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public Map<String, Serializable> getPropertyMap() {
            return this.propertyMap;
        }
        
        public AOByteBuffer toBuffer(final String version) {
            this.lock.lock();
            try {
                final AOByteBuffer buf = new AOByteBuffer(400);
                buf.putOID(this.getEffectOid());
                buf.putInt(71);
                buf.putString(this.effectName);
                if (Log.loggingDebug) {
                    Log.debug("InvokeEventMessage: oid=" + this.getSubject());
                }
                buf.putPropertyMap((Map)this.propertyMap);
                buf.flip();
                return buf;
            }
            finally {
                this.lock.unlock();
            }
        }
        
        public void parseBytes(final AOByteBuffer buf) {
            buf.rewind();
            this.setEffectOid(buf.getOID());
            buf.getInt();
            this.setEffectName(buf.getString());
            this.propertyMap = (Map<String, Serializable>)buf.getPropertyMap();
        }
        
        void setupTransient() {
            this.lock = LockFactory.makeLock("InvokeEffectMessageLock");
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            this.setupTransient();
        }
    }
}
