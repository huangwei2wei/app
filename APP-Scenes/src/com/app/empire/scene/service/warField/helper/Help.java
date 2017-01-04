package com.app.empire.scene.service.warField.helper;

import java.util.List;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.empire.scene.util.Vector3;

public class Help {

	/** 寻找复活点 */
	public static Vector3 getRevivalNode(Vector3 v3, List<FieldSpawn> nodes) {
		if (nodes == null) {
			return null;
		}
		int distance = 0;
		Vector3 findV = null;
		for (FieldSpawn info : nodes) {
			Vector3 v = new Vector3(info.getBoundX()/ Vector3.Accuracy, info.getBoundY()/ Vector3.Accuracy, info.getBoundZ()/ Vector3.Accuracy);
			int nids = getDistance(v, v3);
			if (findV == null) {
				findV = v;
				distance = nids;
				continue;
			}
			if (nids < distance) {
				findV = v;
			}
		}
		return findV;
	}

	private static int getDistance(Vector3 v1, Vector3 v2) {
		return (int) Vector3.distance(new Vector3(v1.getX() / Vector3.Accuracy, 0, v1.getZ() / Vector3.Accuracy), new Vector3(v2.getX() / Vector3.Accuracy, 0, v2.getZ()
				/ Vector3.Accuracy));
	}
}
