// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.Map;

public class PropertySearch implements SearchClause
{
    private Map queryProps;
    
    public PropertySearch() {
    }
    
    public PropertySearch(final Map queryProps) {
        this.setProperties(queryProps);
    }
    
    public Map getProperties() {
        return this.queryProps;
    }
    
    public void setProperties(final Map queryProps) {
        this.queryProps = queryProps;
    }
}
