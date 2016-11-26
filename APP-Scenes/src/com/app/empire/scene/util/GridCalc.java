package com.app.empire.scene.util;

/**
 * 大地图格式计算工具类
 * 
 */
public class GridCalc {

	/**
	 * 计算坐标点的格子,以50 * 50来计算的
	 * 
	 * @param posX
	 * @param posY
	 * @return (posX / 50) + "_" + (posY / 50)
	 */
	public static String getGridIdStrByPoint(int posX, int posY) {
		return (posX / 50) + "_" + (posY / 50);
	}

	public static String getNinePatchId(int posX, int posY) {
		return (posX / 150) + "_" + (posY / 150);
	}

	public static int[] getPos(String pos) {
		return SplitUtil.splitToInt(pos);
	}

	public static String getPos(int x, int y) {
		return x + "," + y;
	}
	
//	public static String getGridIdStrByGrid(int gridX, int gridY) {
//		return gridX + "_" + gridY;
//	}

//	public static String filterStartPos(String pos) {
//		int[] posKey = SplitUtil.splitToInt(pos);
//		int posX = posKey[0];
//		int posY = posKey[1];
//
//		int newPosX = posX / 250;
//		int newPosY = posY / 250;
//
//		boolean isA = false;
//		if (newPosX % 2 == 0) {
//			if (newPosY % 2 == 0)
//				isA = true;
//		} else {
//			if (newPosY % 2 != 0)
//				isA = true;
//		}
//
//		int x = 0;
//		int y = 0;
//		if (isA) {
//			x = posX % 250;
//			y = posY % 250;
//		} else {
//			x = (posX % 250) + 250;
//			y = posY % 250;
//		}
//		return x + "," + y;
//	}

//	public static String filterNextPos(String starPos, String endPos, String filterStartPos) {
//		int[] startPosSet = SplitUtil.splitToInt(starPos);
//		int startX = startPosSet[0];
//		int startY = startPosSet[1];
//		int[] endPosSet = SplitUtil.splitToInt(endPos);
//		int endX = endPosSet[0];
//		int endY = endPosSet[1];
//		int[] filterPosSet = SplitUtil.splitToInt(filterStartPos);
//		int filterPosX = filterPosSet[0];
//		int filterPosY = filterPosSet[1];
//
//		int offX = startX - endX;
//		int offY = startY - endY;
//		int x = filterPosX - offX;
//		int y = filterPosY - offY;
//		return x + "," + y;
//	}
}
