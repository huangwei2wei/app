// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;
import atavism.server.pathing.recast.Helper;
/**
 * 查询过滤器
 * 
 * @author doter
 * 
 */
public class QueryFilter {
	private float[] _areaCost;// 地区成本
	public int IncludeFlags;// 包括标志
	public int ExcludeFlags;// 排除标志

	public QueryFilter() {
		this._areaCost = new float[NavMeshBuilder.MaxAreas];
		for (int i = 0; i < NavMeshBuilder.MaxAreas; ++i) {
			this._areaCost[i] = 1.0f;
		}
		this.ExcludeFlags = 0;
		this.IncludeFlags = 65535;
	}
	/**
	 * 过滤
	 * 
	 * @param refId
	 * @param tile
	 * @param poly
	 * @return
	 */
	public Boolean PassFilter(final long refId, final MeshTile tile, final Poly poly) {
		return (poly.Flags & this.IncludeFlags) != 0x0 && (poly.Flags & this.ExcludeFlags) == 0x0;
	}
	/**
	 * 获取成本
	 * 
	 * @param pax
	 * @param pay
	 * @param paz
	 * @param pbx
	 * @param pby
	 * @param pbz
	 * @param prevRef
	 * @param prevTile
	 * @param prevPoly
	 * @param curRef
	 * @param curTile
	 * @param curPoly
	 * @param nextRef
	 * @param nextTile
	 * @param nextPoly
	 * @return
	 */
	public float GetCost(final float pax, final float pay, final float paz, final float pbx, final float pby, final float pbz, final long prevRef, final MeshTile prevTile, final Poly prevPoly,
			final long curRef, final MeshTile curTile, final Poly curPoly, final long nextRef, final MeshTile nextTile, final Poly nextPoly) {
		return Helper.VDist(pax, pay, paz, pbx, pby, pbz) * this._areaCost[curPoly.getArea()];
	}
	/**
	 * 获取地区成本
	 * 
	 * @param i
	 * @return
	 */
	public float GetAreaCost(final int i) {
		return this._areaCost[i];
	}
	/**
	 * 设置地区成本
	 * 
	 * @param i
	 * @return
	 */
	public void SetAreaCost(final int i, final float cost) {
		this._areaCost[i] = cost;
	}
}
