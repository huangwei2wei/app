// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import com.app.server.atavism.server.math.AOVector;
import java.util.List;

public class PathFinderValue
{
    protected PathSearcher.PathResult result;
    protected List<AOVector> path;
    protected String terrainString;
    
    public PathFinderValue(final PathSearcher.PathResult result, final List<AOVector> path, final String terrainString) {
        this.result = result;
        this.path = path;
        this.terrainString = terrainString;
    }
    
    public PathSearcher.PathResult getResult() {
        return this.result;
    }
    
    public void setResult(final PathSearcher.PathResult result) {
        this.result = result;
    }
    
    public List<AOVector> getPath() {
        return this.path;
    }
    
    public String getTerrainString() {
        return this.terrainString;
    }
    
    public void setTerrainString(final String terrainString) {
        this.terrainString = terrainString;
    }
    
    public void addTerrainChar(final char ch) {
    }
    
    public void addPathElement(final AOVector loc) {
        assert this.path.size() == 0;
        this.path.add(loc);
    }
    
    public int pathElementCount() {
        return this.path.size();
    }
    
    public void addPathElement(final AOVector loc, final boolean overTerrain) {
        assert this.path.size() == this.terrainString.length();
        this.path.add(loc);
        this.terrainString += (overTerrain ? 'T' : 'C');
    }
    
    public void removePathElementsAfter(final int pathSize) {
        for (int i = this.path.size() - 1; i >= pathSize; --i) {
            this.path.remove(i);
        }
        this.terrainString = this.terrainString.substring(0, pathSize);
    }
    
    String stringPath(final int firstElt) {
        String s = "";
        for (int i = firstElt; i < this.path.size(); ++i) {
            final AOVector p = this.path.get(i);
            if (s.length() > 0) {
                s += ", ";
            }
            s = s + "#" + i + ": " + this.terrainString.charAt(i) + p;
        }
        return s;
    }
}
