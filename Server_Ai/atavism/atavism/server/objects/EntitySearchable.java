// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import atavism.server.engine.Matcher;
import atavism.server.engine.OID;
import java.util.Map;
import java.util.LinkedList;
import atavism.server.engine.SearchManager;
import java.util.Collection;
import atavism.server.engine.SearchSelection;
import atavism.server.engine.SearchClause;
import atavism.server.engine.Searchable;

public class EntitySearchable implements Searchable
{
    private ObjectType objectType;
    
    public EntitySearchable(final ObjectType objectType) {
        this.objectType = objectType;
    }
    
    @Override
    public Collection runSearch(final SearchClause search, final SearchSelection selection) {
        final Matcher matcher = SearchManager.getMatcher(search, Entity.class);
        if (matcher == null) {
            return null;
        }
        final List<Object> resultList = new LinkedList<Object>();
        synchronized (EntityManager.entitiesByNamespace) {
            for (final Map<OID, Entity> namespaceEntities : EntityManager.entitiesByNamespace.values()) {
                for (final Map.Entry<OID, Entity> entry : namespaceEntities.entrySet()) {
                    boolean rc = false;
                    final Entity entity = entry.getValue();
                    if (entity.getType() != this.objectType) {
                        continue;
                    }
                    entity.lock();
                    if (entity.getTransientDataRef() != null) {
                        rc = matcher.match(entity.getTransientDataRef());
                    }
                    if (!rc) {
                        rc = matcher.match(entity.getPropertyMapRef());
                    }
                    if (rc) {
                        this.selectProperties(entry.getKey(), entity, selection, resultList);
                    }
                    entity.unlock();
                }
            }
        }
        return resultList;
    }
    
    void selectProperties(final OID oid, final Entity entity, final SearchSelection selection, final List<Object> resultList) {
        if (selection.getResultOption() == 2) {
            resultList.add(oid);
            return;
        }
        Map<String, Serializable> result;
        if (selection.getAllProperties()) {
            result = new HashMap<String, Serializable>(entity.getPropertyMapRef());
            if (entity.getTransientDataRef() != null) {
                result.putAll(entity.getTransientDataRef());
            }
        }
        else {
            result = new HashMap<String, Serializable>();
            for (final String key : selection.getProperties()) {
                final Serializable value = entity.getProperty(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
        }
        if (selection.getResultOption() == 1) {
            resultList.add(new SearchEntry(oid, result));
        }
        else if (result.size() > 0) {
            resultList.add(result);
        }
    }
}
