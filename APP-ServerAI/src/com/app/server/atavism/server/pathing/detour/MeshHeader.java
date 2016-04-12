// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;
/**
 * 网格头
 * 
 * @author doter
 * 
 */
public class MeshHeader {
	public int Magic;// 魔法
	public int Version;// 版本
	public int X;
	public int Y;
	public int Layer;// 层
	public long UserId;
	public int PolyCount;
	public int VertCount;
	public int MaxLinkCount;
	public int DetailMeshCount;// 详细信息网目数
	public int DetailVertCount;
	public int DetailTriCount;
	public int BVNodeCount;
	public int OffMeshConCount;
	public int OffMeshBase;
	public float WalkableHeight;// 步行的高度
	public float WalkableRadius;// 适合步行半径
	public float WalkableClimb;// 适合步行攀登
	public float[] BMin;
	public float[] BMax;
	public long TileRef;
	public float BVQuantFactor;// BV定量因子

	public MeshHeader() {
	}

	public MeshHeader(final int Magic, final int Version, final int X, final int Y, final int Layer, final long UserId, final int PolyCount, final int VertCount, final int MaxLinkCount,
			final int DetailMeshCount, final int DetailVertCount, final int DetailTriCount, final float BVQuantFactor, final int OffMeshBase, final float WalkableHeight, final float WalkableRadius,
			final float WalkableClimb, final int OffMeshConCount, final int BVNodeCount, final float[] Bmin, final float[] Bmax) {
		this.Magic = Magic;
		this.Version = Version;
		this.X = X;
		this.Y = Y;
		this.Layer = Layer;
		this.UserId = UserId;
		this.PolyCount = PolyCount;
		this.VertCount = VertCount;
		this.MaxLinkCount = MaxLinkCount;
		this.DetailMeshCount = DetailMeshCount;
		this.DetailVertCount = DetailVertCount;
		this.DetailTriCount = DetailTriCount;
		this.BVNodeCount = BVNodeCount;
		this.OffMeshConCount = OffMeshConCount;
		this.OffMeshBase = OffMeshBase;
		this.WalkableHeight = WalkableHeight;
		this.WalkableRadius = WalkableRadius;
		this.WalkableClimb = WalkableClimb;
		this.BMin = Bmin;
		this.BMax = Bmax;
		this.BVQuantFactor = BVQuantFactor;
	}

	public MeshHeader(final float f, final int polyCount3, final float walkableHeight2, final float walkableRadius2, final float walkableClimb2, final int storedOffMeshConCount, final int i,
			final float[] gs, final float[] gs2) {
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PolyCount: ");
		builder.append(this.PolyCount);
		builder.append(", VertCount: ");
		builder.append(this.VertCount);
		builder.append(", DetailVertCount: ");
		builder.append(this.DetailVertCount);
		builder.append("\n");
		return builder.toString();
	}
}
