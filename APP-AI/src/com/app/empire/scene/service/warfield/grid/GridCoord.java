package com.app.empire.scene.service.warfield.grid;


public class GridCoord {
	@Override
	public String toString() {
		return "GridCoord [X=" + X + ", Z=" + Z + "]";
	}

	public int X;
	public int Z;
	
	public GridCoord(int X, int Z)
	{
		this.X = X;
		this.Z = Z;
	}
}
