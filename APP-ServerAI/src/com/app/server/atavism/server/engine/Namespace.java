// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import atavism.server.engine.Namespace;

public class Namespace implements Serializable {
	private transient String name;
	private int number;
	private static Map<String, Namespace> namespaceStringToNamespace;
	private static Map<Integer, Namespace> namespaceIntToNamespace;
	public static final int transientNamespaceNumber = 1;
	public static final long serialVersionUID = 1L;
	public static AtomicInteger id = new AtomicInteger(0);
	public static Namespace WORLD_MANAGER;
	public static Namespace MOB;
	public static Namespace TRANSIENT = null;
	public static Namespace OBJECT_MANAGER = null;

	public Namespace() {
		this.number = 0;
	}

	public Namespace(final String name, final int number) {
		this.number = 0;
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return this.name;
	}

	public int getNumber() {
		return this.number;
	}

	@Override
	public String toString() {
		return "[Namespace " + this.name + ":" + this.number + "]";
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.number = in.readInt();
	}

	private Object readResolve() throws ObjectStreamException {
		final Namespace ns = getNamespaceFromIntOrError(this.number);
		return ns;
	}

	private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
		out.writeInt(this.number);
	}

	public static Namespace intern(final String name) {
		return getOrCreateNamespace(name);
	}

	public static Namespace addDBNamespace(final String name, final int number) {
		final Namespace ns = new Namespace(name, number);
		Namespace.namespaceStringToNamespace.put(name, ns);
		Namespace.namespaceIntToNamespace.put(number, ns);
		return ns;
	}

	public static Namespace getNamespace(final String nsString) {
		final Namespace ns = Namespace.namespaceStringToNamespace.get(nsString);
		return ns;
	}

	public static Namespace getNamespaceIfExists(final String nsString) {
		return Namespace.namespaceStringToNamespace.get(nsString);
	}

	public static Namespace getNamespaceFromInt(final Integer nsInt) {
		return Namespace.namespaceIntToNamespace.get(nsInt);
	}

	protected static Namespace getNamespaceFromIntOrError(final Integer nsInt) {
		final Namespace ns = Namespace.namespaceIntToNamespace.get(nsInt);
		if (ns != null) {
			return ns;
		}
		return null;
	}

	public static Integer compressNamespaceList(final Set<Namespace> namespaces) {
		if (namespaces == null || namespaces.size() == 0) {
			return null;
		}
		int result = 0;
		for (final Namespace n : namespaces) {
			result |= 1 << n.number;
		}
		return result;
	}

	public static List<Namespace> decompressNamespaceList(final Integer namespacesInteger) {
		final List<Namespace> namespaces = new LinkedList<Namespace>();
		if (namespacesInteger == null) {
			return namespaces;
		}
		int n = namespacesInteger;
		for (int i = 0; i < 32; ++i) {
			if ((n & 0x1) != 0x0) {
				namespaces.add(getNamespaceFromInt(i));
			}
			n >>= 1;
			if (n == 0) {
				break;
			}
		}
		return namespaces;
	}

	private static Namespace getOrCreateNamespace(final String nsString) {
		final Namespace ns = Namespace.namespaceStringToNamespace.get(nsString);
		if (ns != null) {
			return ns;
		}
		return createNamespace(nsString);
	}

	private static Namespace createNamespace(final String nsString) {
		// Log.info("Creating namespace '" + nsString + "'");
		int number = id.incrementAndGet();
		return addDBNamespace(nsString, number);
	}
	public static void encacheNamespaceMapping() {
		Namespace.WORLD_MANAGER = intern("NS.wmgr");
		Namespace.MOB = intern("NS.mob");
		Namespace.TRANSIENT = intern("NS.transient");
		Namespace.OBJECT_MANAGER = intern("NS.master");
	}
	static {
		Namespace.namespaceStringToNamespace = new HashMap<String, Namespace>();
		Namespace.namespaceIntToNamespace = new HashMap<Integer, Namespace>();
	}
}
