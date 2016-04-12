// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

public class SearchEntry
{
    public Object key;
    public Object value;
    
    public SearchEntry() {
    }
    
    public SearchEntry(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "[SearchEntry key=" + this.key + " value=" + this.value + "]";
    }
}
