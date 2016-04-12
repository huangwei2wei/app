// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import java.io.Serializable;

public class PathArc implements Serializable, Cloneable
{
    public static final byte Illegal = 0;
    public static final byte CVToCV = 1;
    public static final byte TerrainToTerrain = 2;
    public static final byte CVToTerrain = 3;
    int poly1Index;
    int poly2Index;
    byte arcKind;
    PathEdge edge;
    private static final long serialVersionUID = 1L;
    
    public PathArc() {
    }
    
    public PathArc(final byte arcKind, final int poly1Index, final int poly2Index, final PathEdge edge) {
        this.arcKind = arcKind;
        this.poly1Index = poly1Index;
        this.poly2Index = poly2Index;
        this.edge = edge;
    }
    
    public String formatArcKind(final byte val) {
        switch (val) {
            case 0: {
                return "Illegal";
            }
            case 1: {
                return "CVToCV";
            }
            case 2: {
                return "TerrainToTerrain";
            }
            case 3: {
                return "CVToTerrain";
            }
            default: {
                return "Unknown ArcKind " + val;
            }
        }
    }
    
    public static byte parseArcKind(final String s) {
        if (s.equals("Illegal")) {
            return 0;
        }
        if (s.equals("CVToCV")) {
            return 1;
        }
        if (s.equals("TerrainToTerrain")) {
            return 2;
        }
        if (s.equals("CVToTerrain")) {
            return 3;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "[PathArc kind=" + this.formatArcKind(this.arcKind) + ",poly1Index=" + this.getPoly1Index() + ",poly2Index=" + this.getPoly2Index() + ",edge=" + this.getEdge() + "]";
    }
    
    public String shortString() {
        return this.getPoly1Index() + ":" + this.getPoly2Index();
    }
    
    public Object clone() {
        return new PathArc(this.getKind(), this.getPoly1Index(), this.getPoly2Index(), this.getEdge());
    }
    
    public byte getKind() {
        return this.arcKind;
    }
    
    public int getPoly1Index() {
        return this.poly1Index;
    }
    
    public void setPoly1Index(final int poly1Index) {
        this.poly1Index = poly1Index;
    }
    
    public int getPoly2Index() {
        return this.poly2Index;
    }
    
    public void setPoly2Index(final int poly2Index) {
        this.poly2Index = poly2Index;
    }
    
    public PathEdge getEdge() {
        return this.edge;
    }
}
