// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import atavism.server.objects.Instance;
import java.util.LinkedList;
import java.io.Serializable;

public class WorldCollectionLoaderContext implements Serializable
{
    private static final long serialVersionUID = 1L;
    private LinkedList<String> worldCollectionFiles;
    private LinkedList<String> worldCollectionDatabaseKeys;
    
    public WorldCollectionLoaderContext() {
        this.worldCollectionFiles = new LinkedList<String>();
        this.worldCollectionDatabaseKeys = new LinkedList<String>();
    }
    
    public boolean load(final Instance instance) {
        return this.load(instance, instance.getWorldLoaderOverride());
    }
    
    public boolean load(final Instance instance, final WorldLoaderOverride worldLoaderOverride) {
        boolean rv = true;
        for (final WorldCollectionLoader loader : this.getWorldCollectionLoaders(instance, worldLoaderOverride)) {
            rv &= loader.load(instance);
        }
        return rv;
    }
    
    public List<WorldCollectionLoader> getWorldCollectionLoaders(final Instance instance, final WorldLoaderOverride worldLoaderOverride) {
        final List<WorldCollectionLoader> worldCollections = new LinkedList<WorldCollectionLoader>();
        for (final String fileName : this.getWorldCollectionFiles()) {
            worldCollections.add(new WorldCollectionFileLoader(fileName, worldLoaderOverride));
        }
        for (final String persistenceKey : this.getWorldCollectionDatabaseKeys()) {
            worldCollections.add(new WorldCollectionDatabaseLoader(persistenceKey, worldLoaderOverride));
        }
        return worldCollections;
    }
    
    public synchronized void setWorldCollectionFiles(final List<String> fileNames) {
        this.worldCollectionFiles.clear();
        this.worldCollectionFiles.addAll(fileNames);
    }
    
    public synchronized void addWorldCollectionFile(final String fileName) {
        this.worldCollectionFiles.add(fileName);
    }
    
    public synchronized List<String> getWorldCollectionFiles() {
        return new LinkedList<String>(this.worldCollectionFiles);
    }
    
    public synchronized void setWorldCollectionDatabaseKeys(final List<String> persistenceKeys) {
        this.worldCollectionDatabaseKeys.clear();
        this.worldCollectionDatabaseKeys.addAll(persistenceKeys);
    }
    
    public synchronized void addWorldCollectionDatabaseKey(final String persistenceKey) {
        this.worldCollectionDatabaseKeys.add(persistenceKey);
    }
    
    public synchronized List<String> getWorldCollectionDatabaseKeys() {
        return new LinkedList<String>(this.worldCollectionDatabaseKeys);
    }
}
