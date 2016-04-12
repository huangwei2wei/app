// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import com.app.server.atavism.server.math.IntVector2;
import com.app.server.atavism.server.pathing.recast.Helper;
import java.util.EnumSet;

public class NavMesh {
	public NavMeshParams _param;// NavMesh 参数
	public float[] _orig;// 原始
	public float _tileWidth;
	public float _tileHeight;
	public int _maxTiles;
	public int _tileLutSize;// 网格切割尺寸
	public int _tileLutMask;// _tileLutSize-1
	public MeshTile[] _posLookup;
	public MeshTile _nextFree;
	public MeshTile[] _tiles;
	public long _saltBits;
	public long _tileBits;
	public long _polyBits;
	public static long NullLink;
	public static int TileFreeData;

	public NavMeshParams Param() {
		return this._param;
	}

	public NavMesh() {
		this._orig = new float[3];
	}
	/**
	 * 初始化
	 * 
	 * @param param
	 * @return
	 */
	public EnumSet<Status> Init(final NavMeshParams param) {
		this._param = param;
		System.arraycopy(param.Orig, 0, this._orig, 0, 3);
		this._tileWidth = param.TileWidth;
		this._tileHeight = param.TileHeight;
		this._maxTiles = param.MaxTiles;
		this._tileLutSize = (int) Helper.NextPow2(param.MaxTiles / 4);
		if (this._tileLutSize <= 0) {
			this._tileLutSize = 1;
		}
		this._tileLutMask = this._tileLutSize - 1;
		this._tiles = new MeshTile[this._maxTiles];
		this._posLookup = new MeshTile[this._tileLutSize];
		for (int i = 0; i < this._tileLutSize; ++i) {
			this._posLookup[i] = new MeshTile();
		}
		this._nextFree = null;
		for (int i = this._maxTiles - 1; i >= 0; --i) {
			this._tiles[i] = new MeshTile(1L, this._nextFree);
			this._nextFree = this._tiles[i];
		}
		this._tileBits = Helper.Ilog2(Helper.NextPow2(param.MaxTiles));
		this._polyBits = Helper.Ilog2(Helper.NextPow2(param.MaxPolys));
		this._saltBits = Math.min(31L, 32L - this._tileBits - this._polyBits);
		if (this._saltBits < 10L) {// 失败无效参数
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		return EnumSet.of(Status.Success);
	}

	public EnumSet<Status> Init(final NavMeshBuilder data, final int flags) {
		try {
			if (data.Header.Magic != Helper.NavMeshMagic) {
				throw new Exception("Wrong Magic Number");
			}
			if (data.Header.Version != Helper.NavMeshVersion) {
				throw new Exception("Wrong Version Number");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		final NavMeshParams param = new NavMeshParams();
		System.arraycopy(data.Header.BMin, 0, param.Orig, 0, 3);
		param.TileWidth = data.Header.BMax[0] - data.Header.BMin[0];
		param.TileHeight = data.Header.BMax[2] - data.Header.BMin[2];
		param.MaxTiles = 1;
		param.MaxPolys = data.Header.PolyCount;
		final EnumSet<Status> status = this.Init(param);
		if (status.contains(Status.Failure)) {
			return status;
		}
		final long temp = 0L;
		return this.AddTile(data, flags, 0L).status;
	}

	public DetourNumericReturn AddTile(final NavMeshBuilder data, final int flags, final long lastRef) {
		final DetourNumericReturn statusReturn = new DetourNumericReturn();
		final MeshHeader header = data.Header;
		if (header.Magic != Helper.NavMeshMagic) {// 失败
			statusReturn.status = EnumSet.of(Status.Failure, Status.WrongMagic);
			return statusReturn;
		}
		if (header.Version != Helper.NavMeshVersion) {// 失败
			statusReturn.status = EnumSet.of(Status.Failure, Status.WrongVersion);
			return statusReturn;
		}
		if (this.GetTileAt(header.X, header.Y, header.Layer) != null) {// 失败重复
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		MeshTile tile = null;
		if (lastRef == 0L) {
			if (this._nextFree != null) {
				tile = this._nextFree;
				this._nextFree = tile.Next;
				tile.Next = null;
			}
		} else {
			final int tileIndex = (int) this.DecodePolyIdTile(lastRef);
			if (tileIndex >= this._maxTiles) {
				statusReturn.status = EnumSet.of(Status.Failure, Status.OutOfMemory);
				return statusReturn;
			}
			final MeshTile target = this._tiles[tileIndex];
			MeshTile prev = null;
			for (tile = this._nextFree; tile != null && tile != target; tile = tile.Next) {
				prev = tile;
			}
			if (tile != target) {
				statusReturn.status = EnumSet.of(Status.Failure, Status.OutOfMemory);
				return statusReturn;
			}
			if (prev != null) {
				this._nextFree = tile.Next;
			} else {
				prev.Next = tile.Next;
			}
			tile.Salt = this.DecodePolyIdSalt(lastRef);
		}
		if (tile == null) {// 失败
			statusReturn.status = EnumSet.of(Status.Failure, Status.OutOfMemory);
			return statusReturn;
		}
		final int h = this.ComputeTileHash(header.X, header.Y, this._tileLutMask);
		tile.Next = this._posLookup[h];
		this._posLookup[h] = tile;
		tile.Verts = data.NavVerts;
		tile.Polys = data.NavPolys;
		tile.Links = data.NavLinks;
		tile.DetailMeshes = data.NavDMeshes;
		tile.DetailVerts = data.NavDVerts;
		tile.DetailTris = data.NavDTris;
		tile.BVTree = data.NavBvTree;
		tile.OffMeshCons = data.OffMeshCons;
		tile.LinksFreeList = 0L;
		tile.Links[header.MaxLinkCount - 1].Next = NavMesh.NullLink;
		for (int i = 0; i < header.MaxLinkCount - 1; ++i) {
			tile.Links[i].Next = i + 1;
		}
		tile.Data = data;
		tile.Header = header;
		tile.Flags = flags;
		this.ConnectIntLinks(tile);
		this.BaseOffMeshLinks(tile);
		final int MaxNeis = 32;
		final MeshTile[] neis = new MeshTile[MaxNeis];
		for (int nneis = this.GetTilesAt(header.X, header.Y, neis, MaxNeis), j = 0; j < nneis; ++j) {
			final MeshTile temp = neis[j];
			if (neis[j] != tile) {
				this.ConnectExtLinks(tile, temp, -1);
				this.ConnectExtLinks(temp, tile, -1);
			}
			this.ConnectExtOffMeshLinks(tile, temp, -1);
			this.ConnectExtOffMeshLinks(temp, tile, -1);
		}
		for (int k = 0; k < 8; ++k) {
			for (int nneis = this.GetNeighborTilesAt(header.X, header.Y, k, neis, MaxNeis), l = 0; l < nneis; ++l) {
				final MeshTile temp2 = neis[l];
				this.ConnectExtLinks(tile, temp2, k);
				this.ConnectExtLinks(temp2, tile, Helper.OppositeTile(k));
				this.ConnectExtOffMeshLinks(tile, temp2, k);
				this.ConnectExtOffMeshLinks(temp2, tile, Helper.OppositeTile(k));
			}
		}
		statusReturn.longValue = this.GetTileRef(tile);
		statusReturn.status = EnumSet.of(Status.Success);
		return statusReturn;
	}
	/**
	 * 计算哈希
	 * 
	 * @param x
	 * @param y
	 * @param mask
	 * @return
	 */
	private int ComputeTileHash(final int x, final int y, final int mask) {
		final long h1 = -1918454973L;
		final long h2 = -669632447L;
		final long n = h1 * x + h2 * y;
		return (int) (n & mask);
	}

	public EnumSet<Status> RemoveTile(final long refId, NavMeshBuilder data) {
		data = null;
		if (refId == 0L) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final long tileIndex = this.DecodePolyIdTile(refId);
		final long tileSalt = this.DecodePolyIdSalt(refId);
		if (tileIndex >= this._maxTiles) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final MeshTile tile = this._tiles[(int) tileIndex];
		if (tile.Salt != tileSalt) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final int h = this.ComputeTileHash(tile.Header.X, tile.Header.Y, this._tileLutMask);
		MeshTile prev = null;
		MeshTile cur = this._posLookup[h];
		while (cur != null) {
			if (cur == tile) {
				if (prev != null) {
					prev.Next = cur.Next;
					break;
				}
				this._posLookup[h] = cur.Next;
				break;
			} else {
				prev = cur;
				cur = cur.Next;
			}
		}
		final int MaxNeis = 32;
		final MeshTile[] neis = new MeshTile[MaxNeis];
		for (int nneis = this.GetTilesAt(tile.Header.X, tile.Header.Y, neis, MaxNeis), j = 0; j < nneis; ++j) {
			if (neis[j] != tile) {
				final MeshTile temp = neis[j];
				this.UnconnectExtLinks(temp, tile);
			}
		}
		for (int i = 0; i < 8; ++i) {
			for (int nneis = this.GetNeighborTilesAt(tile.Header.X, tile.Header.Y, i, neis, MaxNeis), k = 0; k < nneis; ++k) {
				final MeshTile temp2 = neis[k];
				this.UnconnectExtLinks(temp2, tile);
			}
		}
		if ((tile.Flags & NavMesh.TileFreeData) != 0x0) {
			tile.Data = null;
		} else {
			data = tile.Data;
		}
		tile.Header = null;
		tile.Flags = 0;
		tile.LinksFreeList = 0L;
		tile.Polys = null;
		tile.Verts = null;
		tile.Links = null;
		tile.DetailMeshes = null;
		tile.DetailVerts = null;
		tile.DetailTris = null;
		tile.BVTree = null;
		tile.OffMeshCons = null;
		tile.Salt = (tile.Salt + 1L & (1 << (int) this._saltBits) - 1);
		if (tile.Salt == 0L) {
			final MeshTile meshTile = tile;
			++meshTile.Salt;
		}
		tile.Next = this._nextFree;
		this._nextFree = tile;
		return EnumSet.of(Status.Success);
	}

	public IntVector2 CalcTileLoc(final float posx, final float posy, final float posz) {
		final IntVector2 tileLoc = new IntVector2();
		tileLoc.x = (int) Math.floor((posx - this._orig[0]) / this._tileWidth);
		tileLoc.y = (int) Math.floor((posz - this._orig[2]) / this._tileHeight);
		return tileLoc;
	}

	public MeshTile GetTileAt(final int x, final int y, final int layer) {
		final int h = this.ComputeTileHash(x, y, this._tileLutMask);
		for (MeshTile tile = this._posLookup[h]; tile != null; tile = tile.Next) {
			if (tile.Header != null && tile.Header.X == x && tile.Header.Y == y && tile.Header.Layer == layer) {
				return tile;
			}
		}
		return null;
	}

	public int GetTilesAt(final int x, final int y, final MeshTile[] tiles, final int maxTiles) {
		int n = 0;
		final int h = this.ComputeTileHash(x, y, this._tileLutMask);
		for (MeshTile tile = this._posLookup[h]; tile != null; tile = tile.Next) {
			if (tile.Header != null && tile.Header.X == x && tile.Header.Y == y && n < maxTiles) {
				tiles[n++] = tile;
			}
		}
		return n;
	}

	public long GetTileRefAt(final int x, final int y, final int layer) {
		final int h = this.ComputeTileHash(x, y, this._tileLutMask);
		for (MeshTile tile = this._posLookup[h]; tile != null; tile = tile.Next) {
			if (tile.Header != null && tile.Header.X == x && tile.Header.Y == y && tile.Header.Layer == layer) {
				return this.GetTileRef(tile);
			}
		}
		return 0L;
	}

	public long GetTileRef(final MeshTile tile) {
		if (tile == null) {
			return 0L;
		}
		long it = -1L;
		for (int i = 0; i < this._tiles.length; ++i) {
			if (this._tiles[i] == tile) {
				it = i;
			}
		}
		return this.EncodePolyId(tile.Salt, it, 0L);
	}

	public MeshTile GetTileByRef(final long refId) {
		if (refId == 0L) {
			return null;
		}
		final long tileIndex = this.DecodePolyIdTile(refId);
		final long tileSalt = this.DecodePolyIdSalt(refId);
		if ((int) tileIndex >= this._maxTiles) {
			return null;
		}
		final MeshTile tile = this._tiles[(int) tileIndex];
		if (tile.Salt != tileSalt) {
			return null;
		}
		return tile;
	}

	public int GetMaxTiles() {
		return this._maxTiles;
	}

	public MeshTile GetTile(final int i) {
		return this._tiles[i];
	}

	public DetourMeshTileAndPoly GetTileAndPolyByRef(final long refId) {
		final DetourMeshTileAndPoly tileAndPoly = new DetourMeshTileAndPoly();
		if (refId == 0L) {
			tileAndPoly.status = EnumSet.of(Status.Failure);
			return tileAndPoly;
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			tileAndPoly.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return tileAndPoly;
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (ip >= this._tiles[it].Header.PolyCount) {
			EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		tileAndPoly.tile = this._tiles[it];
		tileAndPoly.poly = this._tiles[it].Polys[ip];
		tileAndPoly.status = EnumSet.of(Status.Success);
		return tileAndPoly;
	}

	public DetourMeshTileAndPoly GetTileAndPolyByRefUnsafe(final long refId) {
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		final DetourMeshTileAndPoly meshTileAndPoly = new DetourMeshTileAndPoly();
		meshTileAndPoly.tile = this._tiles[it];
		meshTileAndPoly.poly = this._tiles[it].Polys[ip];
		return meshTileAndPoly;
	}

	public Boolean IsValidPolyRef(final long refId) {
		if (refId == 0L) {
			return false;
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			return false;
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			return false;
		}
		if (ip >= this._tiles[it].Header.PolyCount) {
			return false;
		}
		return true;
	}

	public long GetPolyRefBase(final MeshTile tile) {
		if (tile == null) {
			return 0L;
		}
		long it = -1L;
		for (int i = 0; i < this._tiles.length; ++i) {
			if (this._tiles[i] == tile) {
				it = i;
			}
		}
		return this.EncodePolyId(tile.Salt, it, 0L);
	}

	public EnumSet<Status> GetOffMeshConnectionPolyEndPoints(final long prevRef, final long polyRef, final float[] startPos, final float[] endPos) {
		if (polyRef == 0L) {
			return EnumSet.of(Status.Failure);
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(polyRef, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final MeshTile tile = this._tiles[it];
		if (ip >= this._tiles[it].Header.PolyCount) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final Poly poly = tile.Polys[ip];
		if (poly.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
			return EnumSet.of(Status.Failure);
		}
		int idx0 = 0;
		int idx2 = 1;
		long i = poly.FirstLink;
		while (i != NavMesh.NullLink) {
			if (tile.Links[(int) i].Edge == 0) {
				if (tile.Links[(int) i].Ref != prevRef) {
					idx0 = 1;
					idx2 = 0;
					break;
				}
				break;
			} else {
				i = tile.Links[(int) i].Next;
			}
		}
		System.arraycopy(tile.Verts, poly.Verts[idx0] * 3, startPos, 0, 3);
		System.arraycopy(tile.Verts, poly.Verts[idx2] * 3, endPos, 0, 3);
		return EnumSet.of(Status.Success);
	}

	public OffMeshConnection GetOffMeshConnectionByRef(final long refId) {
		if (refId == 0L) {
			return null;
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			return null;
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			return null;
		}
		final MeshTile tile = this._tiles[it];
		if (ip >= this._tiles[it].Header.PolyCount) {
			return null;
		}
		final Poly poly = tile.Polys[ip];
		if (poly.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
			return null;
		}
		final long idx = ip - tile.Header.OffMeshBase;
		return tile.OffMeshCons[(int) idx];
	}

	public EnumSet<Status> SetPolyFlags(final long refId, final int flags) {
		if (refId == 0L) {
			return EnumSet.of(Status.Failure);
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final MeshTile tile = this._tiles[it];
		if (ip >= this._tiles[it].Header.PolyCount) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final Poly poly = tile.Polys[ip];
		poly.Flags = flags;
		return EnumSet.of(Status.Success);
	}

	public DetourStatusReturn GetPolyFlags(final long refId, final int resultFlags) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		if (refId == 0L) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final MeshTile tile = this._tiles[it];
		if (ip >= this._tiles[it].Header.PolyCount) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final Poly poly = tile.Polys[ip];
		statusReturn.intValue = poly.Flags;
		statusReturn.status = EnumSet.of(Status.Success);
		return statusReturn;
	}

	public EnumSet<Status> SetPolyArea(final long refId, final short area) {
		if (refId == 0L) {
			return EnumSet.of(Status.Failure);
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final MeshTile tile = this._tiles[it];
		if (ip >= this._tiles[it].Header.PolyCount) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		final Poly poly = tile.Polys[ip];
		poly.setArea(area);
		return EnumSet.of(Status.Success);
	}

	public DetourStatusReturn GetPolyArea(final long refId, final short resultArea) {
		final DetourStatusReturn statusReturn = new DetourStatusReturn();
		if (refId == 0L) {
			statusReturn.status = EnumSet.of(Status.Failure);
			return statusReturn;
		}
		final long[] saltItIp = new long[3];
		this.DecodePolyId(refId, saltItIp);
		final int salt = (int) saltItIp[0];
		final int it = (int) saltItIp[1];
		final int ip = (int) saltItIp[2];
		if (it >= this._maxTiles) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		if (this._tiles[it].Salt != salt || this._tiles[it].Header == null) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final MeshTile tile = this._tiles[it];
		if (ip >= this._tiles[it].Header.PolyCount) {
			statusReturn.status = EnumSet.of(Status.Failure, Status.InvalidParam);
			return statusReturn;
		}
		final Poly poly = tile.Polys[ip];
		statusReturn.intValue = poly.getArea();
		statusReturn.status = EnumSet.of(Status.Success);
		return statusReturn;
	}

	public Status StoreTileState(final MeshTile tile, TileState tileState) {
		tileState = new TileState();
		tileState.Magic = Helper.NavMeshMagic;
		tileState.Version = Helper.NavMeshVersion;
		tileState.Ref = this.GetTileRef(tile);
		tileState.PolyStates = new PolyState[tile.Header.PolyCount];
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly p = tile.Polys[i];
			tileState.PolyStates[i] = new PolyState();
			tileState.PolyStates[i].Flags = p.Flags;
			tileState.PolyStates[i].Area = p.getArea();
		}
		return Status.Success;
	}

	public EnumSet<Status> RestoreTileState(final MeshTile tile, final TileState tileState) {
		if (tileState.Magic != Helper.NavMeshMagic) {
			return EnumSet.of(Status.Failure, Status.WrongMagic);
		}
		if (tileState.Version != Helper.NavMeshVersion) {
			return EnumSet.of(Status.Failure, Status.WrongVersion);
		}
		if (tileState.Ref != this.GetTileRef(tile)) {
			return EnumSet.of(Status.Failure, Status.InvalidParam);
		}
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly p = tile.Polys[i];
			final PolyState s = tileState.PolyStates[i];
			p.Flags = s.Flags;
			p.setArea(s.Area);
		}
		return EnumSet.of(Status.Success);
	}

	public long EncodePolyId(final long salt, final long it, final long ip) {
		return salt << (int) (this._polyBits + this._tileBits) | it << (int) this._polyBits | ip;
	}

	public void DecodePolyId(final long refId, final long[] saltItIp) {
		final long saltMask = (1 << (int) this._saltBits) - 1;
		final long tileMask = (1 << (int) this._tileBits) - 1;
		final long polyMask = (1 << (int) this._polyBits) - 1;
		saltItIp[0] = (refId >> (int) (this._polyBits + this._tileBits) & saltMask);
		saltItIp[1] = (refId >> (int) this._polyBits & tileMask);
		saltItIp[2] = (refId & polyMask);
	}

	public long DecodePolyIdSalt(final long refId) {
		final long saltMask = (1 << (int) this._saltBits) - 1;
		return refId >> (int) (this._polyBits + this._tileBits) & saltMask;
	}

	public long DecodePolyIdTile(final long refId) {
		final long tileMask = (1 << (int) this._tileBits) - 1;
		return refId >> (int) this._polyBits & tileMask;
	}

	public long DecodePolyIdPoly(final long refId) {
		final long polyMask = (1 << (int) this._polyBits) - 1;
		return refId & polyMask;
	}

	private int GetNeighborTilesAt(final int x, final int y, final int side, final MeshTile[] tiles, final int maxTiles) {
		int nx = x;
		int ny = y;
		switch (side) {
			case 0 : {
				++nx;
				break;
			}
			case 1 : {
				++nx;
				++ny;
				break;
			}
			case 2 : {
				++ny;
				break;
			}
			case 3 : {
				--nx;
				++ny;
				break;
			}
			case 4 : {
				--nx;
				break;
			}
			case 5 : {
				--nx;
				--ny;
				break;
			}
			case 6 : {
				--ny;
				break;
			}
			case 7 : {
				++nx;
				--ny;
				break;
			}
		}
		return this.GetTilesAt(nx, ny, tiles, maxTiles);
	}

	private int FindConnectingPolys(final float vax, final float vay, final float vaz, final float vbx, final float vby, final float vbz, final MeshTile tile, final int side, final long[] con,
			final float[] conarea, final int maxcon) {
		if (tile == null) {
			return 0;
		}
		final float[] amin = new float[2];
		final float[] amax = new float[2];
		Helper.CalcSlabEndPoints(vax, vay, vaz, vbx, vby, vbz, amin, amax, side);
		final float apos = Helper.GetSlabCoord(vax, vay, vaz, side);
		final float[] bmin = new float[2];
		final float[] bmax = new float[2];
		final int m = NavMeshBuilder.ExtLink | side;
		int n = 0;
		final long baseId = this.GetPolyRefBase(tile);
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly poly = tile.Polys[i];
			for (int nv = poly.VertCount, j = 0; j < nv; ++j) {
				if (poly.Neis[j] == m) {
					final int vc = poly.Verts[j] * 3;
					final int vd = poly.Verts[(j + 1) % nv] * 3;
					final float bpos = Helper.GetSlabCoord(tile.Verts[vc + 0], tile.Verts[vc + 1], tile.Verts[vc + 2], side);
					if (Math.abs(apos - bpos) <= 0.01f) {
						Helper.CalcSlabEndPoints(tile.Verts[vc + 0], tile.Verts[vc + 1], tile.Verts[vc + 2], tile.Verts[vd + 0], tile.Verts[vd + 1], tile.Verts[vd + 2], bmin, bmax, side);
						if (Helper.OverlapSlabs(amin, amax, bmin, bmax, 0.01f, tile.Header.WalkableClimb)) {
							if (n < maxcon) {
								conarea[n * 2 + 0] = Math.max(amin[0], bmin[0]);
								conarea[n * 2 + 1] = Math.min(amax[0], bmax[0]);
								con[n] = (baseId | i);
								++n;
								break;
							}
							break;
						}
					}
				}
			}
		}
		return n;
	}
	/**
	 * 连接诠释链接
	 * 
	 * @param tile
	 */
	private void ConnectIntLinks(final MeshTile tile) {
		if (tile == null)
			return;
		long baseId = GetPolyRefBase(tile);
		for (int i = 0; i < tile.Header.PolyCount; i++) {
			Poly poly = tile.Polys[i];
			poly.FirstLink = NullLink;
			if (poly.getType() == NavMeshBuilder.PolyTypeOffMeshConnection)
				continue;
			for (int j = poly.VertCount - 1; j >= 0; j--) {
				if ((poly.Neis[j] == 0) || ((poly.Neis[j] & NavMeshBuilder.ExtLink) != 0))
					continue;
				long idx = AllocLink(tile);
				if (idx == NullLink)
					continue;
				Link link = tile.Links[(int) idx];
				link.Ref = (baseId | poly.Neis[j] - 1);
				link.Edge = (short) j;
				link.Side = 255;
				link.BMin = (link.BMax = 0);
				link.Next = poly.FirstLink;
				poly.FirstLink = idx;
			}
		}
	}
	/**
	 * 基本的网状网链路
	 * 
	 * @param tile
	 */
	private void BaseOffMeshLinks(final MeshTile tile) {
		if (tile == null)
			return;
		long baseId = GetPolyRefBase(tile);
		for (int i = 0; i < tile.Header.OffMeshConCount; i++) {
			OffMeshConnection con = tile.OffMeshCons[i];
			Poly poly = tile.Polys[con.Poly];
			float[] ext = {con.Rad, tile.Header.WalkableClimb, con.Rad};
			int p = 0;
			float[] nearestPt = new float[3];
			long refId = FindNearestPolyInTile(tile, con.Pos[(p + 0)], con.Pos[(p + 1)], con.Pos[(p + 2)], ext[0], ext[1], ext[2], nearestPt);
			if ((refId <= 0L) || ((nearestPt[0] - con.Pos[(p + 0)]) * (nearestPt[0] - con.Pos[(p + 0)]) + (nearestPt[2] - con.Pos[(p + 2)]) * (nearestPt[2] - con.Pos[(p + 2)]) > con.Rad * con.Rad)) {
				continue;
			}
			int v = poly.Verts[0] * 3;
			System.arraycopy(nearestPt, 0, tile.Verts, v, 3);

			long idx = AllocLink(tile);
			if (idx != NullLink) {
				Link link = tile.Links[(int) idx];
				link.Ref = refId;
				link.Edge = 0;
				link.Side = 255;
				link.BMin = (link.BMax = 0);
				link.Next = poly.FirstLink;
				poly.FirstLink = idx;
			}

			long tidx = AllocLink(tile);
			if (tidx == NullLink)
				continue;
			int landPolyIdx = (int) DecodePolyIdTile(refId);
			Poly landPoly = tile.Polys[landPolyIdx];
			Link link = tile.Links[(int) tidx];
			link.Ref = (baseId | con.Poly);
			link.Edge = 255;
			link.Side = 255;
			link.BMin = (link.BMax = 0);
			link.Next = landPoly.FirstLink;
			landPoly.FirstLink = tidx;
		}
	}

	private void ConnectExtLinks(final MeshTile tile, final MeshTile target, final int side) {
		if (tile == null) {
			return;
		}
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly poly = tile.Polys[i];
			for (int nv = poly.VertCount, j = 0; j < nv; ++j) {
				if ((poly.Neis[j] & NavMeshBuilder.ExtLink) != 0x0) {
					final int dir = poly.Neis[j] & 0xFF;
					if (side == -1 || dir == side) {
						final int va = poly.Verts[j] * 3;
						final int vb = poly.Verts[(j + 1) % nv] * 3;
						final long[] nei = new long[4];
						final float[] neia = new float[8];
						for (int nnei = this.FindConnectingPolys(tile.Verts[va + 0], tile.Verts[va + 1], tile.Verts[va + 2], tile.Verts[vb + 0], tile.Verts[vb + 1], tile.Verts[vb + 2], target,
								Helper.OppositeTile(dir), nei, neia, 4), k = 0; k < nnei; ++k) {
							final long idx = this.AllocLink(tile);
							if (idx != NavMesh.NullLink) {
								final Link link = tile.Links[(int) idx];
								link.Ref = nei[k];
								link.Edge = (short) j;
								link.Side = (short) dir;
								link.Next = poly.FirstLink;
								poly.FirstLink = idx;
								if (dir == 0 || dir == 4) {
									float tmin = (neia[k * 2 + 0] - tile.Verts[va + 2]) / (tile.Verts[vb + 2] - tile.Verts[va + 2]);
									float tmax = (neia[k * 2 + 1] - tile.Verts[va + 2]) / (tile.Verts[vb + 2] - tile.Verts[va + 2]);
									if (tmin > tmax) {
										final float temp = tmin;
										tmin = tmax;
										tmax = temp;
									}
									link.BMin = (short) (Math.min(1.0f, Math.max(tmin, 0.0f)) * 255.0f);
									link.BMax = (short) (Math.min(1.0f, Math.max(tmax, 0.0f)) * 255.0f);
								} else if (dir == 2 || dir == 6) {
									float tmin = (neia[k * 2 + 0] - tile.Verts[va + 0]) / (tile.Verts[vb + 0] - tile.Verts[va + 0]);
									float tmax = (neia[k * 2 + 1] - tile.Verts[va + 0]) / (tile.Verts[vb + 0] - tile.Verts[va + 0]);
									if (tmin > tmax) {
										final float temp = tmin;
										tmin = tmax;
										tmax = temp;
									}
									link.BMin = (short) (Math.min(1.0f, Math.max(tmin, 0.0f)) * 255.0f);
									link.BMax = (short) (Math.min(1.0f, Math.max(tmax, 0.0f)) * 255.0f);
								}
							}
						}
					}
				}
			}
		}
	}

	private long AllocLink(final MeshTile tile) {
		if (tile.LinksFreeList == NavMesh.NullLink) {
			return NavMesh.NullLink;
		}
		final long link = tile.LinksFreeList;
		tile.LinksFreeList = tile.Links[(int) link].Next;
		return link;
	}

	private void ConnectExtOffMeshLinks(final MeshTile tile, final MeshTile target, final int side) {
		if (tile == null) {
			return;
		}
		final short oppositeSide = (short) ((side == -1) ? 255 : ((short) Helper.OppositeTile(side)));
		for (int i = 0; i < target.Header.OffMeshConCount; ++i) {
			final OffMeshConnection targetCon = target.OffMeshCons[i];
			if (targetCon.Side == oppositeSide) {
				final Poly targetPoly = target.Polys[targetCon.Poly];
				if (targetPoly.FirstLink != NavMesh.NullLink) {
					final float[] ext = {targetCon.Rad, target.Header.WalkableClimb, targetCon.Rad};
					final int p = 3;
					final float[] nearestPt = new float[3];
					final long refId = this.FindNearestPolyInTile(tile, targetCon.Pos[p + 0], targetCon.Pos[p + 1], targetCon.Pos[p + 2], ext[0], ext[1], ext[2], nearestPt);
					if (refId > 0L) {
						if ((nearestPt[0] - targetCon.Pos[p + 0]) * (nearestPt[0] - targetCon.Pos[p + 0]) + (nearestPt[2] - targetCon.Pos[p + 2]) * (nearestPt[2] - targetCon.Pos[p + 2]) <= targetCon.Rad
								* targetCon.Rad) {
							final int v = targetPoly.Verts[1] * 3;
							System.arraycopy(nearestPt, 0, target.Verts, v, 3);
							final long idx = this.AllocLink(target);
							if (idx != NavMesh.NullLink) {
								final Link link = target.Links[(int) idx];
								link.Ref = refId;
								link.Edge = 1;
								link.Side = oppositeSide;
								final Link link3 = link;
								final Link link4 = link;
								final boolean b = false;
								link4.BMax = (short) (b ? 1 : 0);
								link3.BMin = (short) (b ? 1 : 0);
								link.Next = targetPoly.FirstLink;
								targetPoly.FirstLink = idx;
							}
							if ((targetCon.Flags & NavMeshBuilder.OffMeshConBiDir) != 0x0) {
								final long tidx = this.AllocLink(tile);
								if (tidx != NavMesh.NullLink) {
									final int landPolyIdx = (int) this.DecodePolyIdPoly(refId);
									final Poly landPoly = tile.Polys[landPolyIdx];
									final Link link2 = tile.Links[(int) tidx];
									link2.Ref = (this.GetPolyRefBase(target) | targetCon.Poly);
									link2.Edge = 255;
									link2.Side = (short) ((side == -1) ? 255 : ((short) side));
									final Link link5 = link2;
									final Link link6 = link2;
									final boolean b2 = false;
									link6.BMax = (short) (b2 ? 1 : 0);
									link5.BMin = (short) (b2 ? 1 : 0);
									link2.Next = landPoly.FirstLink;
									landPoly.FirstLink = tidx;
								}
							}
						}
					}
				}
			}
		}
	}

	private void UnconnectExtLinks(final MeshTile tile, final MeshTile target) {
		if (tile == null || target == null) {
			return;
		}
		final long targetNum = this.DecodePolyIdTile(this.GetTileRef(target));
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly poly = tile.Polys[i];
			int j = (int) poly.FirstLink;
			int pj = (int) NavMesh.NullLink;
			while (j != NavMesh.NullLink) {
				if (tile.Links[j].Side != 255 && this.DecodePolyIdTile(tile.Links[j].Ref) == targetNum) {
					final long nj = tile.Links[j].Next;
					if (pj == NavMesh.NullLink) {
						poly.FirstLink = nj;
					} else {
						tile.Links[pj].Next = nj;
					}
					this.FreeLink(tile, j);
					j = (int) nj;
				} else {
					pj = j;
					j = (int) tile.Links[j].Next;
				}
			}
		}
	}

	private void FreeLink(final MeshTile tile, final long link) {
		tile.Links[(int) link].Next = tile.LinksFreeList;
		tile.LinksFreeList = link;
	}
	/**
	 * 查询多边形网格
	 * 
	 * @param tile
	 * @param qminx
	 * @param qminy
	 * @param qminz
	 * @param qmaxx
	 * @param qmaxy
	 * @param qmaxz
	 * @param polys
	 * @param maxPolys
	 * @return
	 */
	private int QueryPolygonsInTile(final MeshTile tile, final float qminx, final float qminy, final float qminz, final float qmaxx, final float qmaxy, final float qmaxz, final long[] polys,
			final int maxPolys) {
		if (tile.BVTree != null) {
			int node = 0;
			final int end = tile.Header.BVNodeCount;
			final float[] tbmin = tile.Header.BMin;
			final float[] tbmax = tile.Header.BMax;
			final float qfac = tile.Header.BVQuantFactor;
			final int[] bmin = new int[3];
			final int[] bmax = new int[3];
			final float minx = Math.min(tbmax[0], Math.max(qminx, tbmin[0])) - tbmin[0];
			final float miny = Math.min(tbmax[1], Math.max(qminy, tbmin[1])) - tbmin[1];
			final float minz = Math.min(tbmax[2], Math.max(qminz, tbmin[2])) - tbmin[2];
			final float maxx = Math.min(tbmax[0], Math.max(qmaxx, tbmin[0])) - tbmin[0];
			final float maxy = Math.min(tbmax[1], Math.max(qmaxy, tbmin[1])) - tbmin[1];
			final float maxz = Math.min(tbmax[2], Math.max(qmaxz, tbmin[2])) - tbmin[2];
			bmin[0] = ((int) (qfac * minx) & 0xFFFE);
			bmin[1] = ((int) (qfac * miny) & 0xFFFE);
			bmin[2] = ((int) (qfac * minz) & 0xFFFE);
			bmax[0] = ((int) (qfac * maxx + 1.0f) | 0x1);
			bmax[1] = ((int) (qfac * maxy + 1.0f) | 0x1);
			bmax[2] = ((int) (qfac * maxz + 1.0f) | 0x1);
			final long baseId = this.GetPolyRefBase(tile);
			int n = 0;
			while (node < end) {
				final Boolean overlap = Helper.OverlapQuantBounds(bmin, bmax, tile.BVTree[node].BMin, tile.BVTree[node].BMax);
				final Boolean isLeafNode = tile.BVTree[node].I >= 0;
				if (isLeafNode && overlap && n < maxPolys) {
					polys[n++] = (baseId | tile.BVTree[node].I);
				}
				if (overlap || isLeafNode) {
					++node;
				} else {
					final int escapeIndex = -tile.BVTree[node].I;
					node += escapeIndex;
				}
			}
			return n;
		}
		float[] bmin2 = new float[3];
		float[] bmax2 = new float[3];
		int n2 = 0;
		final long baseId2 = this.GetPolyRefBase(tile);
		for (int i = 0; i < tile.Header.PolyCount; ++i) {
			final Poly p = tile.Polys[i];
			if (p.getType() != NavMeshBuilder.PolyTypeOffMeshConnection) {
				int v = p.Verts[0];
				System.arraycopy(tile.Verts, v, bmin2, 0, 3);
				System.arraycopy(tile.Verts, v, bmax2, 0, 3);
				for (int j = 1; j < p.VertCount; ++j) {
					v = p.Verts[j] * 3;
					bmin2 = Helper.VMin(bmin2, tile.Verts[v + 0], tile.Verts[v + 1], tile.Verts[v + 2]);
					bmax2 = Helper.VMax(bmax2, tile.Verts[v + 0], tile.Verts[v + 1], tile.Verts[v + 2]);
				}
				if (Helper.OverlapBounds(qminx, qminy, qminz, qmaxx, qmaxy, qmaxz, bmin2[0], bmin2[1], bmin2[2], bmax2[0], bmax2[1], bmax2[2]) && n2 < maxPolys) {
					polys[n2++] = (baseId2 | i);
				}
			}
		}
		return n2;
	}
	/**
	 * 查找最近点
	 * 
	 * @param tile
	 * @param centerx
	 * @param centery
	 * @param centerz
	 * @param extentsx
	 * @param extentsy
	 * @param extentsz
	 * @param nearestPt
	 * @return
	 */
	private long FindNearestPolyInTile(final MeshTile tile, final float centerx, final float centery, final float centerz, final float extentsx, final float extentsy, final float extentsz,
			final float[] nearestPt) {
		final float[] bmin = Helper.VSub(centerx, centery, centerz, extentsx, extentsy, extentsz);
		final float[] bmax = Helper.VAdd(centerx, centery, centerz, extentsx, extentsy, extentsz);
		final long[] polys = new long[128];
		final int polyCount = this.QueryPolygonsInTile(tile, bmin[0], bmin[1], bmin[2], bmax[0], bmax[1], bmax[2], polys, 128);
		long nearest = 0L;
		float nearestDistanceSqr = Float.MAX_VALUE;
		for (final long refId : polys) {
			final float[] closestPtPoly = new float[3];
			this.ClosestPointOnPolyInTile(tile, this.DecodePolyIdPoly(refId), centerx, centery, centerz, closestPtPoly);
			final float d = Helper.VDistSqr(centerx, centery, centerz, closestPtPoly[0], closestPtPoly[1], closestPtPoly[2]);
			if (d < nearestDistanceSqr) {
				if (nearestPt != null) {
					System.arraycopy(closestPtPoly, 0, nearestPt, 0, 3);
				}
				nearestDistanceSqr = d;
				nearest = refId;
			}
		}
		return nearest;
	}
	/***
	 * 返回多边形上的最近点
	 * 
	 */
	private void ClosestPointOnPolyInTile(final MeshTile tile, final long ip, final float posx, final float posy, final float posz, float[] closestPt) {
		final Poly poly = tile.Polys[(int) ip];
		if (poly.getType() == NavMeshBuilder.PolyTypeOffMeshConnection) {
			final int v0 = poly.Verts[0] * 3;
			final int v2 = poly.Verts[1] * 3;
			final float d0 = Helper.VDist(posx, posy, posz, tile.Verts[v0 + 0], tile.Verts[v0 + 1], tile.Verts[v0 + 2]);
			final float d2 = Helper.VDist(posx, posy, posz, tile.Verts[v2 + 0], tile.Verts[v2 + 1], tile.Verts[v2 + 2]);
			final float u = d0 / (d0 + d2);
			closestPt = Helper.VLerp(closestPt, tile.Verts[v0 + 0], tile.Verts[v0 + 1], tile.Verts[v0 + 2], tile.Verts[v2 + 0], tile.Verts[v2 + 1], tile.Verts[v2 + 2], u);
			return;
		}
		final PolyDetail pd = tile.DetailMeshes[(int) ip];
		final float[] verts = new float[NavMeshBuilder.VertsPerPoly * 3];
		final float[] edged = new float[NavMeshBuilder.VertsPerPoly];
		final float[] edget = new float[NavMeshBuilder.VertsPerPoly];
		final int nv = poly.VertCount;
		for (int i = 0; i < nv; ++i) {
			System.arraycopy(tile.Verts, poly.Verts[i] * 3, verts, i * 3, 3);
		}
		closestPt[0] = posx;
		closestPt[1] = posy;
		closestPt[2] = posz;
		if (!Helper.DistancePtPolyEdgesSqr(posx, posy, posz, verts, nv, edged, edget)) {
			float dmin = Float.MAX_VALUE;
			int imin = -1;
			for (int j = 0; j < nv; ++j) {
				if (edged[j] < dmin) {
					dmin = edged[j];
					imin = j;
				}
			}
			final int va = imin * 3;
			final int vb = (imin + 1) % nv * 3;
			closestPt = Helper.VLerp(closestPt, verts[va + 0], verts[va + 1], verts[va + 2], verts[vb + 0], verts[vb + 1], verts[vb + 2], edget[imin]);
		}
		for (int k = 0; k < pd.TriCount; ++k) {
			final int t = (int) (pd.TriBase + k) * 4;
			final float[] v3 = new float[9];
			for (int l = 0; l < 3; ++l) {
				if (tile.DetailTris[t + l] < poly.VertCount) {
					System.arraycopy(tile.Verts, poly.Verts[tile.DetailTris[t + l]] * 3, v3, l * 3, 3);
				} else {
					System.arraycopy(tile.DetailVerts, (int) ((pd.VertBase + (tile.DetailTris[t + l] - poly.VertCount)) * 3L), v3, l * 3, 3);
				}
			}
			final DetourNumericReturn closestHeight = Helper.ClosestHeightPointTriangle(posx, posy, posz, v3[0], v3[1], v3[2], v3[3], v3[4], v3[5], v3[6], v3[7], v3[8]);
			if (closestHeight.boolValue) {
				closestPt[1] = closestHeight.floatValue;
				break;
			}
		}
	}

	static {
		NavMesh.NullLink = -1L;
		NavMesh.TileFreeData = 1;
	}
}
