// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.LinkedList;
import atavism.msgsys.ResponseMessage;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import java.util.Map;
import atavism.server.objects.Template;
import atavism.server.engine.Namespace;
import java.util.Iterator;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.agis.objects.SocialInfo;
import atavism.server.engine.OID;
import atavism.server.engine.Hook;
import atavism.server.messages.LoginMessage;
import atavism.server.util.Log;
import atavism.server.util.Logger;
import atavism.agis.database.AccountDatabase;
import atavism.server.engine.EnginePlugin;

public class SocialPlugin extends EnginePlugin
{
    protected AccountDatabase aDB;
    private static final Logger log;
    
    static {
        log = new Logger("SocialPlugin");
    }
    
    public SocialPlugin() {
        super("Social");
        this.setPluginType("Social");
    }
    
    public void onActivate() {
        this.registerHooks();
        if (Log.loggingDebug) {
            SocialPlugin.log.debug("SocialPlugin activated");
        }
        this.registerLoadHook(SocialClient.NAMESPACE, (EnginePlugin.LoadHook)new SocialStateLoadHook());
        this.registerSaveHook(SocialClient.NAMESPACE, (EnginePlugin.SaveHook)new SocialStateSaveHook());
        this.registerUnloadHook(SocialClient.NAMESPACE, (EnginePlugin.UnloadHook)new SocialStateUnloadHook());
        this.registerPluginNamespace(SocialClient.NAMESPACE, (EnginePlugin.GenerateSubObjectHook)new SocialSubObjectHook());
        this.aDB = new AccountDatabase();
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook)new LoginHook());
    }
    
    public static SocialInfo getSocialInfo(final OID oid) {
        return (SocialInfo)EntityManager.getEntityByNamespace(oid, SocialClient.NAMESPACE);
    }
    
    public static void registerSocialInfo(final SocialInfo sInfo) {
        EntityManager.registerEntityByNamespace((Entity)sInfo, SocialClient.NAMESPACE);
    }
    
    class SocialStateLoadHook implements EnginePlugin.LoadHook
    {
        public void onLoad(final Entity e) {
            final SocialInfo sInfo = (SocialInfo)e;
            Log.debug("CHANNEL: got channelson Load:" + sInfo.getChannels());
            for (String s : sInfo.getChannels()) {}
        }
    }
    
    class SocialStateSaveHook implements EnginePlugin.SaveHook
    {
        public void onSave(final Entity e, final Namespace namespace) {
        }
    }
    
    class SocialStateUnloadHook implements EnginePlugin.UnloadHook
    {
        public void onUnload(final Entity e) {
            final SocialInfo sInfo = (SocialInfo)e;
            for (String s : sInfo.getChannels()) {}
        }
    }
    
    public class SocialSubObjectHook extends EnginePlugin.GenerateSubObjectHook
    {
        public SocialSubObjectHook() {
            super((EnginePlugin)SocialPlugin.this);
        }
        
        public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace name, final OID masterOid) {
            if (Log.loggingDebug) {
                Log.debug("SocialPlugin::GenerateSubObjectHook::generateSubObject()");
            }
            if (masterOid == null) {
                Log.error("GenerateSubObjectHook: no master oid");
                return null;
            }
            if (Log.loggingDebug) {
                Log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(SocialClient.NAMESPACE);
            final SocialInfo sInfo = new SocialInfo(masterOid);
            sInfo.setName(template.getName());
            Boolean persistent = (Boolean)template.get(Namespace.OBJECT_MANAGER, ":persistent");
            if (persistent == null) {
                persistent = false;
            }
            sInfo.setPersistenceFlag((boolean)persistent);
            if (props != null) {
                for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                    final String key = entry.getKey();
                    final Serializable value = entry.getValue();
                    if (!key.startsWith(":")) {
                        sInfo.setProperty(key, value);
                    }
                }
            }
            if (Log.loggingDebug) {
                Log.debug("GenerateSubObjectHook: created entity " + sInfo);
            }
            SocialPlugin.registerSocialInfo(sInfo);
            if (persistent) {
                Engine.getPersistenceManager().persistEntity((Entity)sInfo);
            }
            return new EnginePlugin.SubObjData();
        }
    }
    
    class LoginHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LoginMessage message = (LoginMessage)msg;
            final OID playerOid = message.getSubject();
            final OID instanceOid = message.getInstanceOid();
            Log.debug("LoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
            final LinkedList<OID> friendsOf = SocialPlugin.this.aDB.getFriendsOf(playerOid);
            WorldManagerClient.sendObjChatMsg(playerOid, 2, "Welcome to Smoo Online!");
            Engine.getAgent().sendResponse(new ResponseMessage((Message)message));
            return true;
        }
    }
    
    public class ChannelChangeHook implements Hook
    {
        public boolean processMessage(final Message m, final int flags) {
            return true;
        }
    }
}
