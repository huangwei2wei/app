// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;
/**
 * 
 * @author doter
 * 
 */
public class NavMeshParams {
	public float[] Orig;// 原稿
	public float TileWidth;// 网格宽度
	public float TileHeight;// 网格高度
	public int MaxTiles;// 最大网格数
	public int MaxPolys;// 最大多边形

	public NavMeshParams() {
		this.Orig = new float[3];
	}
}
