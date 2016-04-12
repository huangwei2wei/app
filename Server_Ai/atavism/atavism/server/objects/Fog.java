// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

public class Fog
{
    Color color;
    int fogStart;
    int fogEnd;
    protected String name;
    private static final long serialVersionUID = 1L;
    
    public Fog() {
        this.color = null;
        this.fogStart = -1;
        this.fogEnd = -1;
        this.name = null;
    }
    
    public Fog(final String name) {
        this.color = null;
        this.fogStart = -1;
        this.fogEnd = -1;
        this.name = null;
        this.setName(name);
    }
    
    public Fog(final String name, final Color c, final int start, final int end) {
        this.color = null;
        this.fogStart = -1;
        this.fogEnd = -1;
        this.name = null;
        this.setName(name);
        this.setColor(c);
        this.setStart(start);
        this.setEnd(end);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "[Fog: color=" + this.color + ", start=" + this.fogStart + ", end=" + this.fogEnd + "]";
    }
    
    public void setColor(final Color c) {
        this.color = c;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setStart(final int start) {
        this.fogStart = start;
    }
    
    public int getStart() {
        return this.fogStart;
    }
    
    public void setEnd(final int end) {
        this.fogEnd = end;
    }
    
    public int getEnd() {
        return this.fogEnd;
    }
}
