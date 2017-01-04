package com.app.empire.scene.service.warField.navi.exector;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.service.role.objects.ActiveLiving;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshSeeker;
import com.app.empire.scene.service.warField.navi.seeker.NavmeshSeekerStatuCode;

public class NavigationTask implements Runnable {
	private Logger log = Logger.getLogger(NavigationTask.class);
	private int fieldId;

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public long getLivingID() {
		return livingID;
	}

	public void setLivingID(long livingID) {
		this.livingID = livingID;
	}

	public Vector3 getStart() {
		return start;
	}

	public void setStart(Vector3 start) {
		this.start = start;
	}

	public Vector3 getEnd() {
		return end;
	}

	public void setEnd(Vector3 end) {
		this.end = end;
	}

	private long livingID;
	private Vector3 start;
	private Vector3 end;

	private TasksQueue<NavigationTask> tasksQueue;

	public NavigationTask(int fieldId, long livingID, Vector3 start, Vector3 end) {
		// TODO Auto-generated constructor stub
		this.fieldId = fieldId;
		this.livingID = livingID;
		this.start = start;
		this.end = end;
	}

	public TasksQueue<NavigationTask> getTasksQueue() {
		return tasksQueue;
	}

	public void setTasksQueue(TasksQueue<NavigationTask> tasksQueue) {
		this.tasksQueue = tasksQueue;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		try {
			Field f = FieldMgr.getIns().getField(getFieldId());
			if (f == null) {
				return;
			}
			NavmeshSeeker seeker = f.getSeeker();
			List<Vector3> path = new ArrayList<Vector3>();
			NavmeshSeekerStatuCode code = seeker.seek(start, end, path, 0);
			// System.out.println("start = " + start + " end = " + end);
			Living l = f.getLiving(livingID);
			if (l == null) {
				return;
			}
			if (l instanceof ActiveLiving) {
				if (path.size() > 0)
					path.remove(0);
				((ActiveLiving) l).navigateComplete(code, path);
			}
			// long s = System.currentTimeMillis();
			// NavmeshSeeker seeker =
			// FieldMgr.getIns().GetSeeker("");//NavigationExector.GetSeeker(mapid);
			// List<Vector3> path = new ArrayList<Vector3>();
			// NavmeshSeekerStatuCode code = seeker.seek(start, end, path, 0);
			// //// 回调寻路数据
			//
			//
			// System.out.println("rid = " + rid);
			// System.out.println(path.size());
			// System.out.println(code);
			// System.out.println((System.currentTimeMillis() - s));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			log.error("findPath Cost = " + (System.currentTimeMillis() - startTime));
		}
	}

}
