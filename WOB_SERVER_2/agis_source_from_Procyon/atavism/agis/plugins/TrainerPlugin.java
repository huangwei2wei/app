// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.Iterator;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.objects.Entity;
import atavism.msgsys.Message;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.objects.EntityManager;
import atavism.server.engine.OID;
import atavism.server.engine.Hook;
import atavism.server.util.Log;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class TrainerPlugin extends EnginePlugin
{
    private static final Logger log;
    
    static {
        log = new Logger("TrainerPlugin");
    }
    
    public TrainerPlugin() {
        super("Trainer");
        this.setPluginType("Trainer");
    }
    
    public void onActivate() {
        super.onActivate();
        this.registerHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(TrainerClient.MSG_TYPE_REQ_TRAINER_INFO);
        filter.addType(TrainerClient.MSG_TYPE_REQ_SKILL_TRAINING);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        this.registerPluginNamespace(TrainerClient.NAMESPACE, (EnginePlugin.GenerateSubObjectHook)new TrainerSubObjectHook());
        if (Log.loggingDebug) {
            TrainerPlugin.log.debug("TrainerPlugin activated");
        }
    }
    
    public void registerHooks() {
        this.getHookManager().addHook(TrainerClient.MSG_TYPE_REQ_TRAINER_INFO, (Hook)new ReqTrainerInfoHook());
        this.getHookManager().addHook(TrainerClient.MSG_TYPE_REQ_SKILL_TRAINING, (Hook)new ReqSkillTrainingHook());
    }
    
    protected void sendTrainerInfo(final OID playerOid, final OID trainerOid) {
        final Entity e = EntityManager.getEntityByNamespace(trainerOid, TrainerClient.NAMESPACE);
        String skills = (String)e.getProperty("skills");
        skills = ((skills == null) ? "" : skills);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.TRAINING_INFO");
        props.put("trainerOid", (Serializable)trainerOid);
        props.put("playerOid", (Serializable)playerOid);
        props.put("skills", skills);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(TrainerClient.MSG_TYPE_TRAINING_INFO, playerOid, trainerOid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public class TrainerSubObjectHook extends EnginePlugin.GenerateSubObjectHook
    {
        public TrainerSubObjectHook() {
            super((EnginePlugin)TrainerPlugin.this);
        }
        
        public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
            if (Log.loggingDebug) {
                TrainerPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            if (masterOid == null) {
                TrainerPlugin.log.error("GenerateSubObjectHook: no master oid");
                return null;
            }
            if (Log.loggingDebug) {
                TrainerPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(TrainerClient.NAMESPACE);
            if (props == null) {
                Log.warn("GenerateSubObjectHook: no props in ns " + TrainerClient.NAMESPACE);
                return null;
            }
            final Entity tinfo = new Entity(masterOid);
            tinfo.setName(template.getName());
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                final String key = entry.getKey();
                final Serializable value = entry.getValue();
                if (!key.startsWith(":")) {
                    tinfo.setProperty(key, value);
                }
            }
            if (Log.loggingDebug) {
                TrainerPlugin.log.debug("GenerateSubObjectHook: created entity " + tinfo);
            }
            EntityManager.registerEntityByNamespace(tinfo, TrainerClient.NAMESPACE);
            return new EnginePlugin.SubObjData();
        }
    }
    
    public class ReqTrainerInfoHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            if (Log.loggingDebug) {
                TrainerPlugin.log.debug("Processing ReqTrainerInfoHook - Send SKill Info");
            }
            final WorldManagerClient.ExtensionMessage reqMsg = (WorldManagerClient.ExtensionMessage)msg;
            TrainerPlugin.this.sendTrainerInfo((OID)reqMsg.getProperty("playerOid"), (OID)reqMsg.getProperty("npcOid"));
            return true;
        }
    }
    
    public class ReqSkillTrainingHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            if (Log.loggingDebug) {
                TrainerPlugin.log.debug("Processing ReqTrainerInfoHook - Send SKill Info");
            }
            final WorldManagerClient.ExtensionMessage reqMsg = (WorldManagerClient.ExtensionMessage)msg;
            final Map<String, Serializable> props = new HashMap<String, Serializable>();
            props.put("skill", reqMsg.getProperty("skill"));
            props.put("playerOid", reqMsg.getProperty("playerOid"));
            final WorldManagerClient.ExtensionMessage addSkillMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_ADD_SKILL, (OID)reqMsg.getProperty("playerOid"), (Map)props);
            Engine.getAgent().sendBroadcast((Message)addSkillMsg);
            return true;
        }
    }
}
