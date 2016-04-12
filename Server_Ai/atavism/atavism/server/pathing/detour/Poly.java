// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

public class Poly
{
    public long FirstLink;
    public int[] Verts;
    public int[] Neis;
    public int Flags;
    public short VertCount;
    public short _areaAndType;//区域和类型;
    
    public short getArea() {
        return (short)(this._areaAndType & 0x3F);
    }
    
    public void setArea(final short value) {
        this._areaAndType = (short)((this._areaAndType & 0xC0) | (value & 0x3F));
    }
    
    public short getType() {
        return (short)(this._areaAndType >> 6);
    }
    
    public void setType(final short value) {
        this._areaAndType = (short)((this._areaAndType & 0x3F) | value << 6);
    }
    
    public Poly() {
        this.Verts = new int[NavMeshBuilder.VertsPerPoly];
        this.Neis = new int[NavMeshBuilder.VertsPerPoly];
    }
}
