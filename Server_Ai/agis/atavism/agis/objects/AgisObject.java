// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import atavism.agis.core.AgisAbility;
import atavism.agis.core.Cooldown;
import atavism.agis.core.DCMap;
import atavism.server.engine.OID;
import atavism.server.objects.AOObject;
import atavism.server.objects.BinaryState;
import atavism.server.objects.DisplayContext;
import atavism.server.objects.Entity;
import atavism.server.objects.ObjState;
import atavism.server.util.AORuntimeException;
import atavism.server.util.Log;

public class AgisObject extends AOObject {
	protected int templateID;
	public static String baseDCKey = "agisobj.basedc";
	public static String dcMapKey = "item_dcmap";
	int stun;
	int body;
	int currentStun;
	int currentBody;
	OID ownerOID;
	protected Map<Integer, AgisAbility.Entry> abilityMap;
	protected Map<String, Cooldown.State> cooldownStateMap;
	private int stunCounter;
	private static final long serialVersionUID = 1L;

	public AgisObject() {
		this.templateID = -1;
		this.ownerOID = null;
		this.abilityMap = new HashMap<Integer, AgisAbility.Entry>();
		this.cooldownStateMap = new HashMap<String, Cooldown.State>();
		this.stunCounter = 0;
	}

	public AgisObject(final OID oid) {
		super(oid);
		this.templateID = -1;
		this.ownerOID = null;
		this.abilityMap = new HashMap<Integer, AgisAbility.Entry>();
		this.cooldownStateMap = new HashMap<String, Cooldown.State>();
		this.stunCounter = 0;
	}

	public static AgisObject convert(final Entity obj) {
		if (!(obj instanceof AgisObject)) {
			throw new AORuntimeException("AgisObject.convert: obj is not a agisobject: " + obj);
		}
		return (AgisObject) obj;
	}

	public void setTemplateID(final int templateID) {
		this.templateID = templateID;
	}

	public int getTemplateID() {
		return this.templateID;
	}

	public DisplayContext baseDC() {
		return (DisplayContext) this.getProperty(AgisObject.baseDCKey);
	}

	public void baseDC(final DisplayContext dc) {
		this.setProperty(AgisObject.baseDCKey, (Serializable) dc);
	}

	public DCMap dcMap() {
		this.lock.lock();
		try {
			DCMap map = (DCMap) this.getProperty(AgisObject.dcMapKey);
			if (map == null) {
				map = new DCMap();
				this.dcMap(map);
			}
			return map;
		} finally {
			this.lock.unlock();
		}
	}

	public void dcMap(final DCMap dcMap) {
		this.setProperty(AgisObject.dcMapKey, (Serializable) dcMap);
	}

	public void addDCMapping(final DisplayContext base, final DisplayContext target) {
		final DCMap dcMap = this.dcMap();
		dcMap.add(base, target);
	}

	public DisplayContext getDCMapping(final DisplayContext base) {
		return (DisplayContext) this.dcMap().get(base).clone();
	}

	public int getDCV() {
		return 0;
	}

	public int getResistantPD() {
		return 0;
	}

	public int getPD() {
		return 0;
	}

	public void setStun(final int stun) {
		this.lock.lock();
		try {
			this.stun = stun;
			if (this.currentStun > stun) {
				this.currentStun = stun;
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public int getStun() {
		return this.stun;
	}

	public void modifyStun(final int delta) {
		this.lock.lock();
		try {
			final int stun = this.getStun();
			this.setStun(stun + delta);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void setBody(final int body) {
		this.lock.lock();
		try {
			this.body = body;
			if (this.currentBody > body) {
				this.currentBody = body;
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public int getBody() {
		return this.body;
	}

	public void modifyBody(final int delta) {
		this.lock.lock();
		try {
			final int body = this.getBody();
			this.setBody(body + delta);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void setCurrentStun(final int stun) {
		this.currentStun = stun;
	}

	public int getCurrentStun() {
		return this.currentStun;
	}

	public void modifyCurrentStun(final int delta) {
		this.lock.lock();
		try {
			final int stun = this.getCurrentStun();
			this.setCurrentStun(stun + delta);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void setCurrentBody(final int body) {
		this.currentBody = body;
	}

	public int getCurrentBody() {
		return this.currentBody;
	}

	public void modifyCurrentBody(final int delta) {
		this.lock.lock();
		try {
			final int body = this.getCurrentBody();
			this.setCurrentBody(body + delta);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void attackable(final boolean val) {
		final String stateName = AgisStates.Attackable.toString();
		this.setState(stateName, (ObjState) new BinaryState(stateName, val));
	}

	public boolean attackable() {
		if (this.isDead()) {
			return false;
		}
		final BinaryState attackable = (BinaryState) this.getState(AgisStates.Attackable.toString());
		return attackable != null && attackable.isSet();
	}

	public void isDead(final boolean val) {
		this.setState(AgisStates.Dead.toString(), (ObjState) new BinaryState(AgisStates.Dead.toString(), val));
	}

	public boolean isDead() {
		final BinaryState dead = (BinaryState) this.getState(AgisStates.Dead.toString());
		return dead != null && dead.isSet();
	}

	public void setSound(final String name, final String value) {
		this.setProperty("agis.sound." + name, (Serializable) value);
	}

	public String getSound(final String name) {
		return (String) this.getProperty("agis.sound." + name);
	}

	public OID getOwnerOID() {
		return this.ownerOID;
	}

	public void setOwnerOID(final OID ownerOID) {
		this.ownerOID = ownerOID;
	}

	public void addAbility(final AgisAbility ability, final String category) {
		this.lock.lock();
		try {
			final AgisAbility.Entry entry = new AgisAbility.Entry(ability, category);
			this.abilityMap.put(entry.getAbilityID(), entry);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public boolean hasAbility(final AgisAbility ability) {
		this.lock.lock();
		try {
			final AgisAbility.Entry entry = this.abilityMap.get(ability.getID());
			return entry != null;
		} finally {
			this.lock.unlock();
		}
	}

	public boolean hasAbilities() {
		this.lock.lock();
		try {
			return this.abilityMap.size() > 0;
		} finally {
			this.lock.unlock();
		}
	}

	public Set<AgisAbility.Entry> findAbilitiesByCategory(final String category) {
		this.lock.lock();
		try {
			final Set<AgisAbility.Entry> abilities = new HashSet<AgisAbility.Entry>();
			for (final AgisAbility.Entry entry : this.abilityMap.values()) {
				if (entry.getCategory().equals(category)) {
					abilities.add(entry);
				}
			}
			return abilities;
		} finally {
			this.lock.unlock();
		}
	}

	public Map<Integer, AgisAbility.Entry> getAbilityMap() {
		this.lock.lock();
		try {
			return new HashMap<Integer, AgisAbility.Entry>(this.abilityMap);
		} finally {
			this.lock.unlock();
		}
	}

	public void setAbilityMap(final Map<Integer, AgisAbility.Entry> map) {
		this.lock.lock();
		try {
			this.abilityMap = new HashMap<Integer, AgisAbility.Entry>(map);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void addCooldownState(final Cooldown.State cd) {
		this.lock.lock();
		try {
			if (Log.loggingDebug) {
				Log.debug("AgisObject.addCooldownState id=" + cd.getID());
			}
			final Cooldown.State oldcd = this.cooldownStateMap.get(cd.getID());
			if (oldcd != null) {
				oldcd.cancel();
			}
			this.cooldownStateMap.put(cd.getID(), cd);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public Cooldown.State removeCooldownState(final Cooldown.State cd) {
		this.lock.lock();
		try {
			return this.cooldownStateMap.remove(cd.getID());
		} finally {
			this.lock.unlock();
		}
	}

	public Cooldown.State getCooldownState(final String id) {
		this.lock.lock();
		try {
			return this.cooldownStateMap.get(id);
		} finally {
			this.lock.unlock();
		}
	}

	public Map<String, Cooldown.State> getCooldownStateMap() {
		this.lock.lock();
		try {
			return new HashMap<String, Cooldown.State>(this.cooldownStateMap);
		} finally {
			this.lock.unlock();
		}
	}

	public void setCooldownStateMap(final Map<String, Cooldown.State> map) {
		this.lock.lock();
		try {
			this.cooldownStateMap = new HashMap<String, Cooldown.State>(map);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public int getStunCounter() {
		return this.stunCounter;
	}

	protected void setStunCounter(final int cnt) {
		this.stunCounter = cnt;
	}

	public void addStun() {
		++this.stunCounter;
	}

	public void removeStun() {
		--this.stunCounter;
	}

	public boolean isStunned() {
		return this.stunCounter > 0;
	}
}
