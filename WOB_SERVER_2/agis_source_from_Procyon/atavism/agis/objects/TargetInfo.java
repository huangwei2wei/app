// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.OID;
import java.io.Serializable;

public class TargetInfo implements Serializable
{
    protected OID oid;
    protected String species;
    private static final long serialVersionUID = 1L;
    
    public TargetInfo() {
    }
    
    public TargetInfo(final OID oid, final String species) {
        this.oid = oid;
        this.species = species;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setOid(final OID oid) {
        this.oid = oid;
    }
    
    public String getSpecies() {
        return this.species;
    }
    
    public void setSpecies(final String species) {
        this.species = species;
    }
}
