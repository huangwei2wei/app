package com.app.empire.scene.service.warfield.helper;

//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import com.app.empire.scene.service.warfield.navi.seeker.NavmeshSeeker;
import com.app.empire.scene.service.warfield.navi.seeker.NavmeshTriangle;
import com.app.empire.scene.util.Rect;
import com.app.empire.scene.util.Vector3;

//import com.chuangyou.common.util.Rect;
//import com.chuangyou.common.util.Vector3;
//import com.chuangyou.xianni.warfield.navi.seeker.NavmeshSeeker;
//import com.chuangyou.xianni.warfield.navi.seeker.NavmeshTriangle;

public class ParseMapDataHelper {

	/**
	 * 解析成Seeker
	 * 
	 * @param data
	 * @return
	 */
	public static NavmeshSeeker parse2Seeker(byte[] data) {
		IoBuffer buf = IoBuffer.wrap(data);// Unpooled.buffer(data.length).order(ByteOrder.LITTLE_ENDIAN);
		buf.order(ByteOrder.LITTLE_ENDIAN);// 设置小头在前　默认大头序

//		buf.writeBytes(data);
		int step = buf.getInt();
		Rect rect = new Rect();
		rect.setxMax(buf.getFloat());
		rect.setyMax(buf.getFloat());
		rect.setxMin(buf.getFloat());
		rect.setyMin(buf.getFloat());
		// _mapBounds.put(configName, rect);
		List<NavmeshTriangle> triangles = new ArrayList<NavmeshTriangle>();
		int size = buf.getInt();
		for (int j = 0; j < size; j++) {
			int id = buf.getInt();
			Vector3 pos1 = new Vector3(buf.getFloat(), buf.getFloat(), buf.getFloat());
			Vector3 pos2 = new Vector3(buf.getFloat(), buf.getFloat(), buf.getFloat());
			Vector3 pos3 = new Vector3(buf.getFloat(), buf.getFloat(), buf.getFloat());
			NavmeshTriangle tri = new NavmeshTriangle(id, pos1, pos2, pos3);
			tri.setNeigbhor(0, buf.getInt());
			tri.setNeigbhor(1, buf.getInt());
			tri.setNeigbhor(2, buf.getInt());
			triangles.add(tri);
		}
		Map<Integer, List<Integer>> grids = new HashMap<Integer, List<Integer>>();
		int gridCount = buf.getInt();
		for (int g = 0; g < gridCount; g++) {
			int index = buf.getInt();
			int tirSize = buf.getInt();
			List<Integer> triIds = new ArrayList<Integer>();
			for (int t = 0; t < tirSize; t++) {
				triIds.add(buf.getInt());
			}
			grids.put(index, triIds);
		}
		NavmeshSeeker seeker = new NavmeshSeeker();
		seeker.SetData(triangles, grids, rect, step);
		return seeker;
	}
}
