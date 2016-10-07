package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.chuangyou.common.protobuf.pb.PlayerMoveBoardcastProto.PlayerMoveBoardcastMsg;
import com.chuangyou.common.protobuf.pb.PlayerStopBoardcastProto.PlayerStopBoardcastMsg;
//import com.chuangyou.common.util.Log;
import com.app.empire.scene.util.MathUtils;
import com.app.empire.scene.util.Vector3;
//import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.entity.buffer.LivingState;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.app.empire.scene.constant.EnumBufferState;
import com.app.empire.scene.service.warfield.FieldMgr;
import com.app.empire.scene.service.warfield.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.warfield.navi.NavigationManager;
import com.app.empire.scene.service.warfield.navi.exector.NavigationTask;
import com.app.empire.scene.service.warfield.navi.seeker.NavmeshSeeker;
import com.app.empire.scene.service.warfield.navi.seeker.NavmeshSeekerStatuCode;
import com.app.empire.scene.service.warfield.navi.seeker.NavmeshTriangle;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.WorldMgr;

public class ActiveLiving extends Living {

	// / 目标
	protected Vector3 goal;
	// / 路径
	protected List<Vector3> path;
	// / 移动所需要的时间
	protected int moveTime;
	// 移动计数
	protected long moveCounter;
	// / 寻路等待中
	protected boolean navWaiting = false;
	private boolean navFail = false;

	protected List<Snare> snares = new ArrayList<>();

	public boolean isNavFail() {
		return navFail;
	}

	public void setNavFail(boolean navFail) {
		this.navFail = navFail;
	}

	public boolean isNavWaiting() {
		return navWaiting;
	}

	public ActiveLiving(long id) {
		super(id);
	}

	public ActiveLiving(long playerId, long id) {
		super(playerId, id);
	}

	public Vector3 getGoal() {
		return goal;
	}

	/**
	 * 寻路到指定的点
	 * 
	 * @param position
	 */
	public void navigateto(Vector3 target) {
		navWaiting = true;
		NavigationTask task = new NavigationTask(getField().id, id, getPostion(), target);
		NavigationManager.instance.addTask(task);
	}

	/**
	 * 移动到目标点
	 * 
	 * @param goal
	 */
	public void moveto(Vector3 goal) {

		this.goal = goal;
		// @auto living.setSpeed*100
		if (getPostion() == null) {
			Log.error("当前位置为null," + this.getSkin());
			return;
		}
		if (goal == null) {
			Log.error("目标位置为null，" + this.getSkin());
			return;
		}
		this.moveTime = (int) ((Vector3.distance(getPostion(), goal) / (getSpeed() / 100)) * 1000);
		// 记录更新时间
		setTargetPostion(goal);
		setDir(MathUtils.getDirByXZ(getTargetPostion(), getPostion()));
		Set<Long> nearPlayers = getNears(new PlayerSelectorHelper(this));
		for (Long id : nearPlayers) {
			ArmyProxy neararmy = WorldMgr.getArmy(id);
			if (neararmy == null)
				continue;
			PlayerMoveBoardcastMsg.Builder msg = PlayerMoveBoardcastMsg.newBuilder();
			msg.setId(getId());
			msg.setCur(Vector3BuilderHelper.build(getPostion()));
			msg.setTar(Vector3BuilderHelper.build(this.goal));
			msg.setPreArriveTargetServerTime(System.currentTimeMillis());
			PBMessage pkg = MessageUtil.buildMessage(Protocol.U_BC_MOVE, msg);
			neararmy.sendPbMessage(pkg);
		}
	}

	public static void main(String[] args) {
		Vector3 invalid = new Vector3(40.41069F, 1.0F, -119.632835F);
		Vector3 invalid2 = new Vector3(40.163776F, 1.0F, -120.601875F);

		float a = Vector3.distance(invalid, invalid2);
		System.out.println(a);
		for (float i = a * 1000; i > 0; i -= 100) {
			invalid = MathUtils.GetVector3InDistance(invalid, invalid2, 0.1F);
			System.out.println(invalid);
		}
	}

	public void arrial() {
		if (path != null && path.size() > 0) {
			moveto(this.path.remove(0));
		} else {
			stop(false);
		}
	}

	/**
	 * 停止移动
	 */
	public void stop(boolean need2Client) {
		this.moveTime = 0;
		this.goal = Vector3.Invalid;
		this.targetPostion = Vector3.Invalid;
		if (this.path != null)
			this.path.clear();
		if (need2Client) {
			Set<Long> nearPlayers = getNears(new PlayerSelectorHelper(this));
			for (Long id : nearPlayers) {
				ArmyProxy neararmy = WorldMgr.getArmy(id);
				if (neararmy == null)
					continue;
				PlayerStopBoardcastMsg.Builder msg = PlayerStopBoardcastMsg.newBuilder();
				msg.setId(getId());
				msg.setCur(Vector3BuilderHelper.build(getPostion()));
				PBMessage pkg = MessageUtil.buildMessage(Protocol.U_BC_STOP, msg);
				neararmy.sendPbMessage(pkg);
			}
		}
	}

	/**
	 * 到达目的地
	 * 
	 * @return
	 */
	public boolean isArrial() {
		// if (id == 1000000000035L)
		// System.out.println("isArrial this.moveTime = "+id+" " +
		// this.moveTime);
		return this.moveTime <= 0;
	}

	public int getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(int moveTime) {
		// System.err.println("setMoveTime = " + moveTime);
		this.moveTime = moveTime;
	}

	public long getMoveCounter() {
		return moveCounter;
	}

	public void setMoveCounter(long moveCounter) {
		this.moveCounter = moveCounter;
	}

	/**
	 * 寻路到达目的地
	 */
	public void navigateComplete(NavmeshSeekerStatuCode code, List<Vector3> path) {
		System.err.println("code = " + code + " path.size = " + path.size() + " xxxx " + (code == NavmeshSeekerStatuCode.Success));
		navWaiting = false;
		if (code == NavmeshSeekerStatuCode.Success) {
			this.path = path;
			moveto(this.path.remove(0));
			setNavFail(false);
		} else {
			setNavFail(true);
			goal = Vector3.Invalid;
			moveTime = 0;
			if (this.path != null)
				this.path.clear();
		}
	}

	/**
	 * 移动指定的位置
	 * 
	 * @param position
	 */
	@Override
	public void setPostion(Vector3 position) {
		/* 碰撞检测 */
		// 获取需要检测的对象
		// Set<Long> oldNears = this.getNears(new MonsterSelectorHelper(this));
		// Vector3 cur = getPostion();
		// for (Long id : oldNears) {
		// Living living = field.getLiving(id.intValue());
		// if (living == null)
		// continue;
		// Vector3 livingCur = living.getPostion();
		//
		// }
		/* 碰撞检测 */

		if (field != null)
			field.getGrid().moveto(this, position);
		super.setPostion(position);
	}

	/**
	 * 是否为有效的点
	 * 
	 * @param point
	 * @return
	 */
	protected boolean isValidPoint(Vector3 point) {
		if (getField() == null) {
			return false;
		}
		try {
			NavmeshSeeker seeker = FieldMgr.getIns().GetSeekerTemp(getField().getFieldInfo().getResName());
			NavmeshTriangle tri = seeker.getTriangle(point);
			return tri != null;
		} catch (Exception e) {
			//Log.error("isValidPoint ", e);
		}
		return false;
	}

	protected void notityState(EnumBufferState state) {
		super.notityState(state);
		if (state == EnumBufferState.MOVE) {
			stop(true);
		}
	}

	public boolean onDie(Living killer) {
		if (super.onDie(killer)) {
			clearSnare();
			return true;
		}
		return false;
	}

	public void addSnare(Snare snare) {
		synchronized (snares) {
			snares.add(snare);
		}
	}

	public void removeSnare(Snare snare) {
		synchronized (snares) {
			snares.remove(snare);
		}
	}

	public void clearSnare() {
		List<Snare> temp = new ArrayList<>();
		synchronized (snares) {
			temp.addAll(snares);
		}
		for (Snare snare : temp) {
			snare.onDie(this);
		}
		snares.clear();
	}

	public void clearData() {
		super.clearData();
		clearSnare();
	}

}
