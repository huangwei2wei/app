// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.util.Log;

public class GenericBehaviorFactory implements BehaviorFactory
{
    protected Class bhvClass;
    private static final long serialVersionUID = 1L;
    
    public GenericBehaviorFactory(final Class c) {
        this.bhvClass = null;
        this.bhvClass = c;
    }
    
    @Override
    public Behavior generate() {
        try {
            return this.bhvClass.newInstance();
        }
        catch (Exception e) {
            Log.exception("GenericBehaviorFactory.generate: could not generate behavior: ", e);
            return null;
        }
    }
}
