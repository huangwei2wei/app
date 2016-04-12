// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.msgsys.Message;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.SubjectFilter;
import atavism.server.objects.Entity;
import atavism.msgsys.NoRecipientsException;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.objects.ObjectStub;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.plugins.AgisMobPlugin;
import atavism.server.objects.SpawnData;
import atavism.agis.behaviors.CombatPetBehavior;
import atavism.server.engine.Behavior;
import atavism.server.engine.BaseBehavior;
import java.util.Iterator;
import atavism.server.objects.Template;
import atavism.server.util.Log;
import atavism.agis.plugins.CombatClient;
import java.util.Random;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import java.util.HashMap;
import java.util.Map;
import atavism.server.engine.OID;
import atavism.server.util.Logger;
import atavism.msgsys.MessageCallback;
import java.io.Serializable;

public class TamedPet extends Pet implements Serializable, MessageCallback
{
    static final Logger log;
    private String mobName;
    private OID mobObj;
    private boolean isSpawned;
    private OID ownerOid;
    private int attitude;
    private int currentCommand;
    private int skillType;
    private int currentLevel;
    private Map<String, Serializable> propMapWM;
    private Map<String, Serializable> propMapC;
    private Long sub;
    private Long sub2;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("CombatPet");
    }
    
    public TamedPet() {
        this.attitude = 2;
        this.currentCommand = -2;
        this.skillType = 0;
        this.currentLevel = 0;
        this.propMapWM = new HashMap<String, Serializable>();
        this.propMapC = new HashMap<String, Serializable>();
    }
    
    public TamedPet(final String entityName, final int mobTemplateID, final String mobName, final OID ownerOid, final int skillType) {
        super(entityName, mobTemplateID, mobName, ownerOid);
        this.attitude = 2;
        this.currentCommand = -2;
        this.skillType = 0;
        this.currentLevel = 0;
        this.propMapWM = new HashMap<String, Serializable>();
        this.propMapC = new HashMap<String, Serializable>();
        this.mobTemplateID = mobTemplateID;
        this.mobName = mobName;
        this.ownerOid = ownerOid;
        this.skillType = skillType;
        this.createNewTemplate();
    }
    
    private void createNewTemplate() {
        final Template mobTemplate = ObjectManagerClient.getTemplate(this.mobTemplateID, ObjectManagerPlugin.MOB_TEMPLATE);
        String ownerName = WorldManagerClient.getObjectInfo(this.ownerOid).name;
        if (ownerName.endsWith("s")) {
            ownerName = ownerName.concat("'");
        }
        else {
            ownerName = ownerName.concat("'s");
        }
        final String petSpecies = (String)mobTemplate.get(WorldManagerClient.NAMESPACE, "subSpecies");
        this.mobName = String.valueOf(ownerName) + " " + petSpecies;
        mobTemplate.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_NAME, (Serializable)this.mobName);
        mobTemplate.setName(this.mobName);
        final String playersRace = (String)EnginePlugin.getObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "race");
        mobTemplate.put(WorldManagerClient.NAMESPACE, "attitude", (Serializable)0);
        mobTemplate.put(WorldManagerClient.NAMESPACE, "faction", (Serializable)playersRace);
        ObjectManagerClient.registerTemplate(mobTemplate);
        this.propMapWM = (Map<String, Serializable>)mobTemplate.getSubMap(WorldManagerClient.NAMESPACE);
        String gender = this.propMapWM.get("genderOptions");
        if (gender.equals("Either")) {
            final Random random = new Random();
            if (random.nextInt(2) == 0) {
                gender = "Male";
            }
            else {
                gender = "Female";
            }
        }
        this.propMapWM.put("gender", gender);
        this.propMapWM.put("objectKey", this.getName());
        (this.propMapC = (Map<String, Serializable>)mobTemplate.getSubMap(CombatClient.NAMESPACE)).put("petOwner", (Serializable)this.ownerOid);
        Log.debug("PET: mobName: " + this.mobName + " has props: " + this.propMapWM);
        this.saveObject();
    }
    
    public void loadPetData() {
        Log.debug("PET: registering pet template: " + this.mobName + " with props: " + this.propMapWM);
        final Template tmpl = new Template(this.mobName);
        for (final String key : this.propMapWM.keySet()) {
            tmpl.put(WorldManagerClient.NAMESPACE, key, (Serializable)this.propMapWM.get(key));
        }
        for (final String key : this.propMapC.keySet()) {
            tmpl.put(CombatClient.NAMESPACE, key, (Serializable)this.propMapC.get(key));
        }
        ObjectManagerClient.registerTemplate(tmpl);
    }
    
    public void summonPet() {
        Log.debug("PET: summon pet hit");
        if (this.isSpawned) {
            Log.debug("PET: pet is already spawned");
            this.despawnPet();
        }
        this.currentLevel = 1;
        this.loadPetData();
        Log.debug("PET: summoning pet with props: " + this.propMapWM);
        final MobFactory mobFactory = new MobFactory(this.mobTemplateID);
        mobFactory.addBehav((Behavior)new BaseBehavior());
        final CombatPetBehavior ncpBehav = new CombatPetBehavior();
        ncpBehav.setOwnerOid(this.ownerOid);
        mobFactory.addBehav(ncpBehav);
        final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(this.ownerOid);
        final SpawnData spawnData = new SpawnData();
        ObjectStub obj = null;
        obj = mobFactory.makeObject(spawnData, bwNode.getInstanceOid(), bwNode.getLoc());
        obj.spawn();
        final InterpolatedWorldNode iwNode = obj.getWorldNode();
        Log.debug("PET: pet " + this.mobName + " spawned at: " + iwNode.getLoc() + " in instance: " + iwNode.getInstanceOid());
        Log.debug("PET: owner is at: " + bwNode.getLoc() + " in instance: " + bwNode.getInstanceOid());
        final String gender = this.propMapWM.get("gender");
        this.isSpawned = true;
        AgisMobPlugin.setDisplay(this.mobObj = obj.getOid(), gender);
        AgisMobClient.petCommandUpdate(this.mobObj, this.attitude, null);
        AgisMobClient.petCommandUpdate(this.mobObj, this.currentCommand, null);
        final boolean activated = this.activate();
        this.saveObject();
        EnginePlugin.setObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "activePet", (Serializable)this.mobObj);
        EnginePlugin.setObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "combatPet", (Serializable)this.getName());
        final String faction = (String)EnginePlugin.getObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "faction");
        final String tempFaction = (String)EnginePlugin.getObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "temporaryFaction");
        EnginePlugin.setObjectProperty(this.mobObj, WorldManagerClient.NAMESPACE, "faction", (Serializable)faction);
        EnginePlugin.setObjectProperty(this.mobObj, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)tempFaction);
        this.updatePetCombatStats();
    }
    
    private void updatePetCombatStats() {
        AgisMobClient.updatePetStats(this.ownerOid, this.mobObj, this.currentLevel, 80);
    }
    
    @Override
    public boolean despawnPet() {
        boolean wasSpawned = false;
        Log.debug("PET: despawn hit with isSpawned: " + this.isSpawned);
        if (this.isSpawned) {
            try {
                Log.debug("PET: despawning pet: " + this.mobObj);
                WorldManagerClient.despawn(this.mobObj);
                Log.debug("PET: despawned pet: " + this.mobObj);
            }
            catch (NoRecipientsException e) {
                Log.debug("PET: no recipients found for despawn pet.");
            }
            this.isSpawned = false;
            final boolean deactivated = this.deactivate();
            this.saveObject();
            EnginePlugin.setObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "activePet", (Serializable)null);
            EnginePlugin.setObjectProperty(this.ownerOid, WorldManagerClient.NAMESPACE, "combatPet", (Serializable)null);
            wasSpawned = true;
        }
        return wasSpawned;
    }
    
    public void saveObject() {
        Log.debug("PET: saving pet object: " + this.getName());
        ObjectManagerClient.saveObjectData(this.getName(), (Entity)this, WorldManagerClient.NAMESPACE);
    }
    
    public boolean activate() {
        Log.debug("PET: in activate: this " + this);
        this.sub = null;
        this.sub2 = null;
        final SubjectFilter filter = new SubjectFilter(this.mobObj);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(AgisMobClient.MSG_TYPE_PET_TARGET_LOST);
        this.sub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        if (this.sub == null) {
            Log.debug("PET: sub is null");
        }
        final SubjectFilter filter2 = new SubjectFilter(this.ownerOid);
        filter2.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter2.addType(AgisMobClient.MSG_TYPE_SEND_PET_COMMAND);
        this.sub2 = Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this);
        Log.debug("PET: set up subscription for pet owner: " + this.ownerOid);
        return true;
    }
    
    public boolean deactivate() {
        Log.debug("PET: deactivating");
        if (this.sub != null) {
            Engine.getAgent().removeSubscription((long)this.sub);
            this.sub = null;
            Log.debug("PET: removing sub");
        }
        if (this.sub2 != null) {
            Engine.getAgent().removeSubscription((long)this.sub2);
            this.sub2 = null;
            Log.debug("PET: removing sub 2");
        }
        return true;
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof PropertyMessage) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final OID subject = propMsg.getSubject();
            if (subject.equals((Object)this.mobObj)) {
                this.handlePetPropertyUpdate(propMsg);
            }
            else if (subject.equals((Object)this.ownerOid)) {
                this.handleOwnerPropertyUpdate(propMsg);
            }
        }
        else if (msg.getMsgType() == AgisMobClient.MSG_TYPE_SEND_PET_COMMAND) {
            Log.debug("PET: got send pet command message");
            final AgisMobClient.sendPetCommandMessage spcMsg = (AgisMobClient.sendPetCommandMessage)msg;
            final String command = spcMsg.getCommand();
            final OID targetOid = spcMsg.getTargetOid();
            this.handleCommand(command, targetOid);
        }
        else if (msg.getMsgType() == AgisMobClient.MSG_TYPE_PET_TARGET_LOST) {
            Log.debug("PET: got send pet command message");
            final AgisMobClient.petTargetLostMessage spcMsg2 = (AgisMobClient.petTargetLostMessage)msg;
            this.handleTargetLost();
        }
        else {
            TamedPet.log.error("PET: unknown msg: " + msg);
        }
    }
    
    public void handleCommand(final String command, final OID targetOid) {
        if (!this.isSpawned) {
            Log.debug("PET: command issued to pet that is not spawned");
            return;
        }
        Log.debug("PET: issuing pet command: " + command);
        if (command.equals("passive")) {
            this.updateAttitude(1);
        }
        else if (command.equals("defensive")) {
            this.updateAttitude(2);
        }
        else if (command.equals("aggressive")) {
            this.updateAttitude(3);
        }
        else if (command.equals("stay")) {
            this.updateCommand(-1, targetOid);
        }
        else if (command.equals("follow")) {
            this.updateCommand(-2, targetOid);
        }
        else if (command.equals("attack")) {
            if (targetOid.equals((Object)this.ownerOid) || targetOid.equals((Object)this.mobObj)) {
                ExtendedCombatMessages.sendErrorMessage(this.ownerOid, "Your pet cannot attack that target");
            }
            else {
                this.updateCommand(-3, targetOid);
            }
        }
    }
    
    private void handleTargetLost() {
        Log.debug("PET: pet has lost target, checking current command: " + this.currentCommand);
        if (this.currentCommand == -3) {
            this.updateCommand(-2, null);
        }
    }
    
    protected void handlePetPropertyUpdate(final PropertyMessage propMsg) {
        final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
        if (dead != null && dead) {
            Log.debug("PET: got pet death, despawning");
        }
    }
    
    protected void handleOwnerPropertyUpdate(final PropertyMessage propMsg) {
        Log.debug("");
        final Boolean dead = (Boolean)propMsg.getProperty("deadstate");
        if (dead != null && dead) {
            Log.debug("PET: got owner death, despawning");
            final DespawnPet despawnPet = new DespawnPet();
            Engine.getExecutor().schedule(despawnPet, 2000L, TimeUnit.MILLISECONDS);
            return;
        }
        final LinkedList<Integer> skillValues = (LinkedList<Integer>)propMsg.getProperty("skillValue");
        if (skillValues != null) {
            Log.debug("PET: got skill value update");
        }
    }
    
    public void updateAttitude(final int attitude) {
        this.attitude = attitude;
        AgisMobClient.petCommandUpdate(this.mobObj, attitude, null);
    }
    
    public void updateCommand(final int command, final OID target) {
        this.currentCommand = command;
        AgisMobClient.petCommandUpdate(this.mobObj, this.currentCommand, target);
    }
    
    @Override
    public String getMobName() {
        return this.mobName;
    }
    
    @Override
    public void setMobName(final String mobName) {
        this.mobName = mobName;
    }
    
    @Override
    public OID getMobObj() {
        return this.mobObj;
    }
    
    @Override
    public void setMobObj(final OID mobObj) {
        this.mobObj = mobObj;
    }
    
    @Override
    public boolean getSpawned() {
        return this.isSpawned;
    }
    
    @Override
    public void setSpawned(final boolean isSpawned) {
        this.isSpawned = isSpawned;
    }
    
    @Override
    public OID getOwnerOid() {
        return this.ownerOid;
    }
    
    @Override
    public void setOwnerOid(final OID ownerOid) {
        this.ownerOid = ownerOid;
    }
    
    public int getAttitude() {
        return this.attitude;
    }
    
    public void setAttitude(final int attitude) {
        this.attitude = attitude;
    }
    
    public int getCurrentCommand() {
        return this.currentCommand;
    }
    
    public void setCurrentCommand(final int currentCommand) {
        this.currentCommand = currentCommand;
    }
    
    public int getSkillType() {
        return this.skillType;
    }
    
    public void setSkillType(final int skillType) {
        this.skillType = skillType;
    }
    
    public int getCurrentLevel() {
        return this.currentLevel;
    }
    
    public void setCurrentLevel(final int currentLevel) {
        this.currentLevel = currentLevel;
    }
    
    public Map<String, Serializable> getPropMapWM() {
        return this.propMapWM;
    }
    
    public void setPropMapWM(final Map<String, Serializable> propMapWM) {
        this.propMapWM = propMapWM;
    }
    
    public Map<String, Serializable> getPropMapC() {
        return this.propMapC;
    }
    
    public void setPropMapC(final Map<String, Serializable> propMapC) {
        this.propMapC = propMapC;
    }
    
    public Long getSub() {
        return this.sub;
    }
    
    public void setSub(final Long sub) {
        this.sub = sub;
    }
    
    public Long getSub2() {
        return this.sub2;
    }
    
    public void setSub2(final Long sub2) {
        this.sub2 = sub2;
    }
    
    static /* synthetic */ void access$2(final TamedPet tamedPet, final boolean isSpawned) {
        tamedPet.isSpawned = isSpawned;
    }
    
    class DespawnPet implements Runnable, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void run() {
            if (TamedPet.this.isSpawned) {
                try {
                    Log.debug("PET: despawning pet: " + TamedPet.this.mobObj);
                    WorldManagerClient.despawn(TamedPet.this.mobObj);
                    Log.debug("PET: despawned pet: " + TamedPet.this.mobObj);
                }
                catch (NoRecipientsException e) {
                    Log.debug("PET: no recipients found for despawn pet.");
                }
                TamedPet.access$2(TamedPet.this, false);
                final boolean deactivated = TamedPet.this.deactivate();
                TamedPet.this.saveObject();
                EnginePlugin.setObjectProperty(TamedPet.this.ownerOid, WorldManagerClient.NAMESPACE, "activePet", (Serializable)null);
                EnginePlugin.setObjectProperty(TamedPet.this.ownerOid, WorldManagerClient.NAMESPACE, "combatPet", (Serializable)null);
            }
        }
    }
}
