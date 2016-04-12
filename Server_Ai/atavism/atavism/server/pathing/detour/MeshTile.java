// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;
/**
 * 平铺网格
 * 
 * @author doter
 * 
 */
public class MeshTile {
	public long Salt;// 标识
	public long LinksFreeList;// 链接空闲列表
	public MeshHeader Header;
	public Poly[] Polys;
	public float[] Verts;
	public Link[] Links;
	public PolyDetail[] DetailMeshes;
	public float[] DetailVerts;
	public short[] DetailTris;
	public BVNode[] BVTree;
	public OffMeshConnection[] OffMeshCons;
	public NavMeshBuilder Data;
	public int Flags;
	public MeshTile Next;// 下一个

	public MeshTile() {
	}

	public MeshTile(final long Salt, final MeshTile Next) {
		this.Salt = Salt;
		this.Next = Next;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Salt: ");
		builder.append(this.Salt);
		builder.append("\n");
		for (final PolyDetail pd : this.DetailMeshes) {
			builder.append("DetailMesh: ");
			builder.append(pd);
			builder.append("\n");
		}
		return builder.toString();
	}
}
