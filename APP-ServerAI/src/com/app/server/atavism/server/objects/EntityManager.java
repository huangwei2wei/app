// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.util.Iterator;
//import atavism.server.util.Log;
import java.util.HashMap;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.Namespace;
import java.util.Map;

import org.apache.log4j.Logger;

public class EntityManager
{
    static Map<Namespace, Map<OID, Entity>> entitiesByNamespace;
    public static Logger log = Logger.getLogger("navmesh");
    
    public static Entity getEntityByNamespace(final OID oid, final Namespace namespace) {
        synchronized (EntityManager.entitiesByNamespace) {
            final Map<OID, Entity> namespaceEntities = EntityManager.entitiesByNamespace.get(namespace);
            if (namespaceEntities == null) {
                return null;
            }
            return namespaceEntities.get(oid);
        }
    }
    
    public static void registerEntityByNamespace(final Entity entity, final Namespace namespace) {
        entity.setNamespace(namespace);
        synchronized (EntityManager.entitiesByNamespace) {
            Map<OID, Entity> namespaceEntities = EntityManager.entitiesByNamespace.get(namespace);
            if (namespaceEntities == null) {
                namespaceEntities = new HashMap<OID, Entity>();
                EntityManager.entitiesByNamespace.put(namespace, namespaceEntities);
            }
            final OID oid = entity.getOid();
            if (oid == null) {
            	log.error("Entity.registerEntityByNamespace: entity " + entity + ", namespace " + namespace + " oid is null");
            }
            else {
                namespaceEntities.put(oid, entity);
            }
        }
    }
    
    public static boolean removeEntityByNamespace(final Entity entity, final Namespace namespace) {
        final OID oid = entity.getOid();
        return removeEntityByNamespace(oid, entity, namespace);
    }
    
    public static boolean removeEntityByNamespace(final OID oid, final Namespace namespace) {
        return removeEntityByNamespace(oid, null, namespace);
    }
    
    private static boolean removeEntityByNamespace(final OID oid, final Entity entity, final Namespace namespace) {
        synchronized (EntityManager.entitiesByNamespace) {
            final Map<OID, Entity> namespaceEntities = EntityManager.entitiesByNamespace.get(namespace);
            if (namespaceEntities == null) {
            	log.error("Entity.removeEntityByNamespace: there are no entities for namespace " + namespace + ", entity oid is " + oid);
                return false;
            }
            if (oid == null) {
            	log.error("Entity.removeEntityByNamespace: entity " + entity + ", namespace " + namespace + " oid is null");
                return false;
            }
            final Entity previousEntity = namespaceEntities.get(oid);
            if (previousEntity == null) {
            	log.error("Entity.removeEntityByNamespace: entity " + oid + ", namespace " + namespace + " is not registered");
                return false;
            }
            if (entity != null && previousEntity != entity) {
            	log.error("Entity.removeEntityByNamespace: entity " + oid + ", namespace " + namespace + " is not the same as the registered entity");
                return false;
            }
            namespaceEntities.remove(oid);
            return true;
        }
    }
    
    public static Entity[] getAllEntitiesByNamespace(final Namespace namespace) {
        synchronized (EntityManager.entitiesByNamespace) {
            final Map<OID, Entity> namespaceEntities = EntityManager.entitiesByNamespace.get(namespace);
            if (namespaceEntities == null) {
                return new Entity[0];
            }
            final Entity[] entities = new Entity[namespaceEntities.size()];
            int i = 0;
            for (final Entity entity : namespaceEntities.values()) {
                entities[i++] = entity;
            }
            return entities;
        }
    }
    
    public static int getEntityCount() {
        synchronized (EntityManager.entitiesByNamespace) {
            int size = 0;
            for (final Map<OID, Entity> namespaceEntities : EntityManager.entitiesByNamespace.values()) {
                size += namespaceEntities.size();
            }
            return size;
        }
    }
    
    static {
        EntityManager.entitiesByNamespace = new HashMap<Namespace, Map<OID, Entity>>();
    }
}
