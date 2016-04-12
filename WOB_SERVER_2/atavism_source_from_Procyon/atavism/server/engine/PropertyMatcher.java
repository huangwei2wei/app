// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.Iterator;
import java.util.Map;

public class PropertyMatcher implements Matcher
{
    private Map queryProps;
    
    public PropertyMatcher(final SearchClause query) {
        this.queryProps = ((PropertySearch)query).getProperties();
    }
    
    @Override
    public boolean match(final Object object) {
        final Map target = (Map)object;
        if (target == null) {
            return this.queryProps.size() == 0;
        }
        for (final Map.Entry queryProp : this.queryProps.entrySet()) {
            final Object queryKey = queryProp.getKey();
            final Object queryValue = queryProp.getValue();
            final Object targetValue = target.get(queryKey);
            if (targetValue == null && (!target.containsKey(queryKey) || queryValue != null)) {
                return false;
            }
            if (queryValue == null) {
                return false;
            }
            if (!targetValue.equals(queryValue)) {
                return false;
            }
        }
        return true;
    }
    
    public static class Factory implements MatcherFactory
    {
        @Override
        public Matcher createMatcher(final SearchClause query) {
            return new PropertyMatcher(query);
        }
    }
}
