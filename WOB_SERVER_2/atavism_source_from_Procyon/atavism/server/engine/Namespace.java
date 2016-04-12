// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import atavism.server.util.AORuntimeException;
import atavism.server.plugins.BillingClient;
import atavism.agis.plugins.ClassAbilityClient;
import atavism.agis.plugins.TrainerClient;
import atavism.server.plugins.InstanceClient;
import atavism.server.plugins.InventoryClient;
import atavism.agis.plugins.CombatClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.network.AOByteBuffer;
import java.util.Map;
import java.io.Serializable;
import atavism.server.marshalling.Marshallable;

public class Namespace implements Marshallable, Serializable
{
    private transient String name;
    private int number;
    private static Map<String, Namespace> namespaceStringToNamespace;
    private static Map<Integer, Namespace> namespaceIntToNamespace;
    public static Namespace TRANSIENT;
    public static Namespace OBJECT_MANAGER;
    public static Namespace WORLD_MANAGER;
    public static Namespace COMBAT;
    public static Namespace MOB;
    public static Namespace BAG;
    public static Namespace AGISITEM;
    public static Namespace QUEST;
    public static Namespace INSTANCE;
    public static Namespace WM_INSTANCE;
    public static Namespace VOICE;
    public static Namespace TRAINER;
    public static Namespace CLASSABILITY;
    public static Namespace BILLING;
    public static final int transientNamespaceNumber = 1;
    public static final long serialVersionUID = 1L;
    
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        buf.putByte((byte)this.number);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        final int b = buf.getByte();
        final Namespace ns = getNamespaceFromIntOrError(b);
        return ns;
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
    
    public static void encacheNamespaceMapping() {
        if (Log.loggingDebug) {
            Log.debug("Reading namespaces from the database");
        }
        Engine.getDatabase().encacheNamespaceMapping();
        Namespace.TRANSIENT = intern("NS.transient");
        Namespace.OBJECT_MANAGER = intern("NS.master");
        Namespace.WORLD_MANAGER = intern("NS.wmgr");
        WorldManagerClient.NAMESPACE = Namespace.WORLD_MANAGER;
        Namespace.WM_INSTANCE = intern("NS.wminstance");
        WorldManagerClient.INSTANCE_NAMESPACE = Namespace.WM_INSTANCE;
        Namespace.COMBAT = intern("NS.combat");
        CombatClient.NAMESPACE = Namespace.COMBAT;
        Namespace.MOB = intern("NS.mob");
        Namespace.BAG = intern("NS.inv");
        InventoryClient.NAMESPACE = Namespace.BAG;
        Namespace.AGISITEM = intern("NS.item");
        InventoryClient.ITEM_NAMESPACE = Namespace.AGISITEM;
        Namespace.QUEST = intern("NS.quest");
        Namespace.INSTANCE = intern("NS.instance");
        InstanceClient.NAMESPACE = Namespace.INSTANCE;
        Namespace.VOICE = intern("NS.voice");
        Namespace.TRAINER = intern("NS.trainer");
        TrainerClient.NAMESPACE = Namespace.TRAINER;
        Namespace.CLASSABILITY = intern("NS.classability");
        ClassAbilityClient.NAMESPACE = Namespace.CLASSABILITY;
        Namespace.BILLING = intern("NS.billing");
        BillingClient.NAMESPACE = Namespace.BILLING;
        if (Log.loggingDebug) {
            Log.debug("Read " + Namespace.namespaceIntToNamespace.size() + " namespaces from the database");
        }
    }
    
    public static Namespace getNamespace(final String nsString) {
        final Namespace ns = Namespace.namespaceStringToNamespace.get(nsString);
        if (ns == null) {
            throw new AORuntimeException("Database.getNamespaceInt Did not namespace int for namespace '" + nsString + "'");
        }
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
        return Engine.getDatabase().findExistingNamespace(nsInt);
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
        Log.info("Creating namespace '" + nsString + "'");
        return Engine.getDatabase().createNamespace(nsString);
    }
    
    static {
        Namespace.namespaceStringToNamespace = new HashMap<String, Namespace>();
        Namespace.namespaceIntToNamespace = new HashMap<Integer, Namespace>();
        Namespace.TRANSIENT = null;
        Namespace.OBJECT_MANAGER = null;
        Namespace.WORLD_MANAGER = null;
        Namespace.COMBAT = null;
        Namespace.MOB = null;
        Namespace.BAG = null;
        Namespace.AGISITEM = null;
        Namespace.QUEST = null;
        Namespace.INSTANCE = null;
        Namespace.WM_INSTANCE = null;
        Namespace.VOICE = null;
        Namespace.TRAINER = null;
        Namespace.CLASSABILITY = null;
        Namespace.BILLING = null;
    }
}
