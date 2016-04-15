// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

import java.util.Arrays;
import com.app.server.atavism.server.pathing.recast.Helper;
/**
 * 感应格
 * 
 * @author doter
 * 
 */
public class ProximityGrid {
	private int _maxItems;
	private float _cellSize;
	private float _invCellSize;
	private Item[] _pool;
	private int _poolHead;
	private int _poolSize;
	private int[] _buckets;
	private int _bucketSize;
	private int[] _bounds;

	public ProximityGrid() {
		this._bounds = new int[4];
		this._maxItems = 0;
		this._cellSize = 0.0f;
		this._pool = null;
		this._poolHead = 0;
		this._poolSize = 0;
		this._buckets = null;
		this._bucketSize = 0;
	}

	public Boolean Init(final int poolSize, final float cellSize) {
		try {
			if (poolSize <= 0) {
				throw new Exception("Pool Size must be greater than 0");
			}
			if (cellSize <= 0.0f) {
				throw new Exception("Cell Size must be greater than 0");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this._cellSize = cellSize;
		this._invCellSize = 1.0f / this._cellSize;
		this._bucketSize = (int) Helper.NextPow2(poolSize);
		this._buckets = new int[this._bucketSize];
		this._poolSize = poolSize;
		this._poolHead = 0;
		this._pool = new Item[this._poolSize];
		for (int i = 0; i < this._poolSize; ++i) {
			this._pool[i] = new Item();
		}
		this.Clear();
		return true;
	}

	public void Clear() {
		for (int i = 0; i < this._bucketSize; ++i) {
			this._buckets[i] = 65535;
		}
		this._poolHead = 0;
		this._bounds[0] = 65535;
		this._bounds[1] = 65535;
		this._bounds[2] = -65535;
		this._bounds[3] = -65535;
	}

	public void AddItem(final int id, final float minx, final float miny, final float maxx, final float maxy) {
		final int iminx = (int) Math.floor(minx * this._invCellSize);
		final int iminy = (int) Math.floor(miny * this._invCellSize);
		final int imaxx = (int) Math.floor(maxx * this._invCellSize);
		final int imaxy = (int) Math.floor(maxy * this._invCellSize);
		this._bounds[0] = Math.min(this._bounds[0], iminx);
		this._bounds[1] = Math.min(this._bounds[1], iminy);
		this._bounds[2] = Math.min(this._bounds[2], imaxx);
		this._bounds[3] = Math.min(this._bounds[3], imaxy);
		for (int y = iminy; y <= imaxy; ++y) {
			for (int x = iminx; x <= imaxx; ++x) {
				if (this._poolHead < this._poolSize) {
					final int h = this.HashPos2(x, y, this._bucketSize);
					final int idx = this._poolHead;
					++this._poolHead;
					final Item item = this._pool[idx];
					item.x = (short) x;
					item.y = (short) y;
					item.id = id;
					item.next = this._buckets[h];
					this._buckets[h] = idx;
				}
			}
		}
	}

	public int QueryItems(final float minx, final float miny, final float maxx, final float maxy, final int[] ids, final int maxIds) {
		final int iminx = (int) Math.floor(minx * this._invCellSize);
		final int iminy = (int) Math.floor(miny * this._invCellSize);
		final int imaxx = (int) Math.floor(maxx * this._invCellSize);
		final int imaxy = (int) Math.floor(maxy * this._invCellSize);
		int n = 0;
		for (int y = iminy; y <= imaxy; ++y) {
			for (int x = iminx; x <= imaxx; ++x) {
				final int h = this.HashPos2(x, y, this._bucketSize);
				Item item;
				for (int idx = this._buckets[h]; idx != 65535; idx = item.next) {
					item = this._pool[idx];
					if (item.x == x && item.y == y && Arrays.toString(ids).matches(".*[\\[ ]" + item.id + "[\\],].*")) {
						if (n >= maxIds) {
							return n;
						}
						ids[n++] = item.id;
					}
				}
			}
		}
		return n;
	}

	public int GetItemCountAt(final int x, final int y) {
		int n = 0;
		final int h = this.HashPos2(x, y, this._bucketSize);
		Item item;
		for (int idx = this._buckets[h]; idx != 65535; idx = item.next) {
			item = this._pool[idx];
			if (item.x == x && item.y == y) {
				++n;
			}
		}
		return n;
	}

	public int[] Bounds() {
		return this._bounds;
	}

	public float CellSize() {
		return this._cellSize;
	}

	public int HashPos2(final int x, final int y, final int n) {
		return (x * 73856093 ^ y * 19349663) & n - 1;
	}

	class Item {
		public int id;
		public short x;
		public short y;
		public int next;
	}
}
