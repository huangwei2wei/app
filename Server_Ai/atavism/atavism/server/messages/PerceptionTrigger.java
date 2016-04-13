// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import java.util.Iterator;
import atavism.server.util.Log;
import atavism.msgsys.MessageAgent;
import atavism.msgsys.Message;
import java.util.ArrayList;
import java.util.Collection;
import atavism.msgsys.IFilter;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.Map;
import atavism.msgsys.MessageType;
import java.util.List;
import atavism.msgsys.MessageTrigger;

public class PerceptionTrigger extends MessageTrigger {
	private List<MessageType> msgTypes;
	private transient Map<OID, IntHolder> objectRefs;
	private transient PerceptionFilter filter;

	public PerceptionTrigger() {
		this.objectRefs = new HashMap<OID, IntHolder>();
	}

	@Override
	public void setFilter(final IFilter filter) {
		this.filter = (PerceptionFilter) filter;
	}

	public void setTriggeringTypes(final Collection<MessageType> types) {
		(this.msgTypes = new ArrayList<MessageType>(types.size())).addAll(types);
	}

	@Override
	public boolean match(final Message message) {
		if (this.msgTypes == null) {
			return message instanceof PerceptionMessage;
		}
		return this.msgTypes.contains(message.getMsgType());
	}

	@Override
	public synchronized void trigger(final Message triggeringMessage, final IFilter triggeringFilter, final MessageAgent agent) {
		final PerceptionMessage message = (PerceptionMessage) triggeringMessage;
		final List<PerceptionMessage.ObjectNote> gainObjects = message.getGainObjects();
		final List<PerceptionMessage.ObjectNote> lostObjects = message.getLostObjects();
		if (gainObjects != null) {
			final List<PerceptionFilter.TypedSubject> newSubjects = new ArrayList<PerceptionFilter.TypedSubject>(gainObjects.size());
			for (final PerceptionMessage.ObjectNote gain : gainObjects) {
				final IntHolder refCount = this.objectRefs.get(gain.subjectOid);
				if (refCount == null) {
					this.objectRefs.put(gain.subjectOid, new IntHolder(1));
					newSubjects.add(new PerceptionFilter.TypedSubject(gain.subjectOid, gain.objectType));
				} else {
					final IntHolder intHolder = refCount;
					++intHolder.count;
				}
			}
			if (newSubjects.size() > 0) {
				if (Log.loggingDebug) {
					Log.debug("PerceptionTrigger adding " + newSubjects.size() + "; newSubjects: " + newSubjects.toString());
				}
				this.filter.addSubjects(newSubjects);
			}
		}
		if (lostObjects == null) {
			return;
		}
		final List<OID> freeOids = new ArrayList<OID>(lostObjects.size());
		for (final PerceptionMessage.ObjectNote lost : lostObjects) {
			final IntHolder refCount = this.objectRefs.get(lost.subjectOid);
			if (refCount == null) {
				Log.error("PerceptionTrigger: duplicate lost " + lost.subjectOid);
			} else if (refCount.count == 1) {
				this.objectRefs.remove(lost.subjectOid);
				freeOids.add(lost.subjectOid);
			} else {
				final IntHolder intHolder2 = refCount;
				--intHolder2.count;
			}
		}
		if (freeOids.size() > 0) {
			if (Log.loggingDebug) {
				Log.debug("PerceptionTrigger removing " + freeOids.size());
			}
			this.filter.removeSubjects(freeOids);
		}
	}

	protected class IntHolder {
		int count;

		IntHolder() {
			this.count = 0;
		}

		IntHolder(final int initial) {
			this.count = 0;
			this.count = initial;
		}
	}
}
