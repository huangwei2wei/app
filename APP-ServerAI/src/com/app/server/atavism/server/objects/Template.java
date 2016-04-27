// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

//import atavism.server.util.AORuntimeException;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import com.app.server.atavism.server.plugins.ObjectManagerPlugin;
import java.util.HashMap;
import java.io.Serializable;
import com.app.server.atavism.server.engine.Namespace;
import java.util.Map;

public class Template extends NamedPropertyClass implements Cloneable {
	protected int templateID = -1;
	protected String templateType = "Base";
	private Map<Namespace, Map<String, Serializable>> propMap;
	private static final long serialVersionUID = 1L;

	public Template() {
		this.propMap = new HashMap<Namespace, Map<String, Serializable>>();
	}

	public Template(final String name) {
		this.propMap = new HashMap<Namespace, Map<String, Serializable>>();
		this.setName(name);
		this.setTemplateID(ObjectManagerPlugin.getNextFreeTemplateID());
		this.setTemplateType("BaseTemplate");
	}

	public Template(final String name, final int id, final String type) {
		this.propMap = new HashMap<Namespace, Map<String, Serializable>>();
		this.setName(name);
		this.setTemplateID(id);
		this.setTemplateType(type);
	}

	public String getType() {
		return "Template";
	}

//	@Override
//	public String toString() {
//		String s = "[Template: name=" + this.getName() + ", id=" + this.getTemplateID() + ", type=" + this.getTemplateType() + "] ";
//		this.lock.lock();
//		try {
//			for (final Map.Entry<Namespace, Map<String, Serializable>> entry : this.propMap.entrySet()) {
//				final Namespace ns = entry.getKey();
//				final Map<String, Serializable> subMap = entry.getValue();
//				for (final Map.Entry<String, Serializable> sEntry : subMap.entrySet()) {
//					final String key = sEntry.getKey();
//					final Serializable val = sEntry.getValue();
//					s = s + "(ns=" + ns.getName() + ", key=" + key + ", val=" + val + ")";
//				}
//			}
//			return s;
//		} finally {
//			this.lock.unlock();
//		}
//	}

	public Object clone() throws CloneNotSupportedException {
		this.lock.lock();
		try {
			final Template res = (Template) super.clone();
			res.propMap = new HashMap<Namespace, Map<String, Serializable>>();
			for (final Map.Entry<Namespace, Map<String, Serializable>> entry : this.propMap.entrySet()) {
				res.propMap.put(entry.getKey(), new HashMap<String, Serializable>(entry.getValue()));
			}
			return res;
		} finally {
			this.lock.unlock();
		}
	}

	public void put(final Namespace namespace, final String key, final Serializable value) {
		this.lock.lock();
		try {
			Map<String, Serializable> subMap = this.propMap.get(namespace);
			if (subMap == null) {
				subMap = new HashMap<String, Serializable>();
				this.propMap.put(namespace, subMap);
			}
			subMap.put(key, value);
		} finally {
			this.lock.unlock();
		}
	}

	public void put(final String namespaceString, final String key, final Serializable value) {
		this.put(Namespace.intern(namespaceString), key, value);
	}

	public Serializable get(final Namespace namespace, final String key) {
		this.lock.lock();
		try {
			final Map<String, Serializable> subMap = this.propMap.get(namespace);
			if (subMap == null) {
				return null;
			}
			return subMap.get(key);
		} finally {
			this.lock.unlock();
		}
	}

	public Set<Namespace> getNamespaces() {
		this.lock.lock();
		try {
			final Set<Namespace> ns = new HashSet<Namespace>();
			for (final Namespace namespace : this.propMap.keySet()) {
				ns.add(namespace);
			}
			return ns;
		} finally {
			this.lock.unlock();
		}
	}

	public Map<String, Serializable> getSubMap(final Namespace namespace) {
		this.lock.lock();
		try {
			final Map<String, Serializable> subMap = this.propMap.get(namespace);
			if (subMap != null) {
				return new HashMap<String, Serializable>(subMap);
			}
			return null;
		} finally {
			this.lock.unlock();
		}
	}

	public Template restrict(final Namespace namespace) {
		this.lock.lock();
		try {
			final Template t = new Template(this.getName(), this.getTemplateID(), this.getTemplateType());
			final Map<String, Serializable> subMap = this.getSubMap(namespace);
			t.propMap.put(namespace, subMap);
			return t;
		} finally {
			this.lock.unlock();
		}
	}

	public Template merge(final Template overrideTemplate) {
		Template newTempl;
		try {
			newTempl = (Template) this.clone();
		} catch (CloneNotSupportedException e1) {
			throw new RuntimeException("merge", e1);
		}
		for (final Namespace namespace : overrideTemplate.getNamespaces()) {
			final Map<String, Serializable> subMap = overrideTemplate.getSubMap(namespace);
			for (final Map.Entry<String, Serializable> entry : subMap.entrySet()) {
				final String key = entry.getKey();
				final Serializable val = entry.getValue();
				newTempl.put(namespace, key, val);
			}
		}
		return newTempl;
	}

	public boolean equals(final Serializable other) {
		if (!(other instanceof Template)) {
			return false;
		}
		final Template oTempl = (Template) other;
		return this.getName() != null && this.getName().equals(oTempl.getName());
	}

//	@Override
//	public int hashCode() {
//		if (this.getName() == null) {
//			throw new RuntimeException("hashCode fails for null name");
//		}
//		return this.getName().hashCode();
//	}

	// public Entity generate() {
	// throw new AORuntimeException("generate not implemented");
	// }

	public int getTemplateID() {
		return this.templateID;
	}

	public void setTemplateID(final int templateID) {
		this.templateID = templateID;
	}

	public String getTemplateType() {
		return this.templateType;
	}

	public void setTemplateType(final String templateType) {
		this.templateType = templateType;
	}
}
