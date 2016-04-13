// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class DefaultFilterTable extends FilterTable {
	Map<MessageType, Map<Object, List<Subscription>>> messageTypes;

	public DefaultFilterTable() {
		this.messageTypes = new HashMap<MessageType, Map<Object, List<Subscription>>>();
	}

	@Override
	public synchronized void addFilter(final Subscription sub, final Object object) {
		final Collection<MessageType> types = sub.filter.getMessageTypes();
		if (types == null)
			return;
		for (final MessageType tt : types) {
			Map<Object, List<Subscription>> objectMap = this.messageTypes.get(tt);
			if (objectMap == null) {
				objectMap = new HashMap<Object, List<Subscription>>();
				this.messageTypes.put(tt, objectMap);
			}
			LinkedList<Subscription> subList = (LinkedList<Subscription>) objectMap.get(object);
			if (subList == null) {
				subList = new LinkedList<Subscription>();
				objectMap.put(object, subList);
			}
			if (sub.getTrigger() != null) {
				subList.addFirst(sub);
			} else {
				subList.addLast(sub);
			}
		}
	}

	@Override
	public synchronized void removeFilter(final Subscription sub, final Object object) {
		final Collection<MessageType> types = sub.filter.getMessageTypes();
		if (types == null) {
			return;
		}
		for (final MessageType tt : types) {
			final Map<Object, List<Subscription>> objectMap = this.messageTypes.get(tt);
			if (objectMap == null) {
				continue;
			}
			final List<Subscription> subList = objectMap.get(object);
			if (subList == null) {
				continue;
			}
			final ListIterator<Subscription> iterator = subList.listIterator();
			while (iterator.hasNext()) {
				final Subscription ss = iterator.next();
				if (ss.subId == sub.subId) {
					iterator.remove();
					break;
				}
			}
			if (subList.size() != 0) {
				continue;
			}
			objectMap.remove(object);
		}
	}

	@Override
	public synchronized int match(final Message message, final Set<Object> matches, final List<Subscription> triggers) {
		final MessageType type = message.getMsgType();
		final Map<Object, List<Subscription>> objectMap = this.messageTypes.get(type);
		if (objectMap == null) {
			return 0;
		}
		int count = 0;
		for (final Map.Entry<Object, List<Subscription>> entry : objectMap.entrySet()) {
			final List<Subscription> subs = entry.getValue();
			boolean matched = false;
			for (final Subscription sub : subs) {
				if (sub.filter.matchRemaining(message)) {
					if (!matched && matches.add(entry.getKey())) {
						++count;
						matched = true;
					}
					if (triggers == null || sub.getTrigger() == null || !sub.getTrigger().match(message)) {
						break;
					}
					triggers.add(sub);// ´¥·¢
				}
			}
		}
		return count;
	}
}
