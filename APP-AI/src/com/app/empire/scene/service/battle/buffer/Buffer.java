package com.app.empire.scene.service.battle.buffer;

import java.util.LinkedList;

import com.chuangyou.common.protobuf.pb.battle.BufferMsgProto.BufferMsg;
import com.chuangyou.common.util.Log;
import  com.app.empire.scene.util.ThreadSafeRandom;
import com.app.empire.scene.service.battle.AttackOrder;
import com.app.empire.scene.service.battle.buffer.BufferType.FromType;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.battle.damage.effect.DamageEffecterType;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.battle.skill.FuseSkillVo;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.soul.SoulFuseSkillConfig;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.helper.RoleConstants.RoleType;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.role.objects.Player;

public abstract class Buffer {

	protected static final ThreadSafeRandom RND = new ThreadSafeRandom();
	private long bufferId;
	private int templateId;
	/** buffer使用者 */
	protected Living source;
	/** buffer 被作用者 */
	protected Living target;
	/** buffer 造成的伤害数据 */
	protected LinkedList<Damage> damages;
	/** 剩下次数 */
	protected int leftCount;
	/** 是否是常驻Buffer 默认：非常驻 */
	protected boolean isPermanent = false;
	/** 状态 */
	protected int state;
	/** 1-行动时,2-受到伤害时,3-造成伤害时 */
	protected int exeWay;
	/** 是否执行过 */
	protected boolean executed;
	/** 叠加数量 */
	protected int pressedNum;
	/** 技能Buffer模板数据 */
	protected SkillBufferTemplateInfo bufferInfo;
	/** 最后一次执行时间 */
	protected long lastExecTime;

	/** 存活时间 */
	protected long aliveTime;

	protected Buffer(Living source, Living target, SkillBufferTemplateInfo bufferInfo) {
		this.bufferId = IDMakerHelper.bufferId();
		this.source = source;
		this.target = target;
		this.bufferInfo = bufferInfo;
		this.exeWay = bufferInfo.getExeWay();
		init(); // 初始化
	}

	/**
	 * <pre>
	 * 执行buffer
	 * </pre>
	 * 
	 * @return
	 */
	public final boolean execute(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2, int execWay) {
		try {
			// buffer已经失效
			if (state != BufferState.VALID && bufferInfo.getExeWay() != ExecWayType.REMOVE) {
				return false;
			}
			if (execWay != bufferInfo.getExeWay()) {
				return false;
			}
			if (System.currentTimeMillis() - this.lastExecTime < bufferInfo.getCooldown() * 1000) {
				return false;
			}
			if (!exeCost()) {
				return false;
			}
			// Buffer作用冷却CD
			setLastExecTime(System.currentTimeMillis());
			exec(attackOrder, beDamage1, beDamage2); // 执行BUFFER效果
			setExecuted(true);
			damages.add(beDamage1);
			damages.add(beDamage2);
			if (isTimesBuffer()) {// 按次数计算效果
				decrease();
				target.upBuffer(this);
			}
			return true;
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer("[BUFFER]");
			sb.append("attackOrder=").append(attackOrder);
			sb.append("|beDamage1=").append(beDamage1).append("|");
			sb.append("|beDamage2=").append(beDamage2).append("|");
			sb.append("BufferName=").append(bufferInfo.getBufferName());
			sb.append("BufferId=").append(bufferInfo.getTemplateId());
			Log.error(sb.toString(), e);
		}
		return false;
	}

	// 设置buffer失效
	public void stop() {
		state = BufferState.INVALID;
	}

	/**
	 * 判断BUFFER是否有效
	 * 
	 * @return
	 */
	public boolean checkValid() {
		if (state == BufferState.INVALID) {
			return false;
		}

		if (!isPermanent) {
			int durableType = bufferInfo.getDurableType();
			if (durableType == DurableType.TIME || durableType == DurableType.TIME_AND_COUNT) {
				// buffer已经结束
				if (this.aliveTime <= System.currentTimeMillis()) {
					dispose();
					return false;
				}
			}
			if (durableType == DurableType.COUNT || durableType == DurableType.TIME_AND_COUNT) {
				if (leftCount <= 0) {
					dispose();
					return false;
				}
			}
		}
		return enougthMana();
	}

	/** 扣费 */
	public boolean exeCost() {
		SkillBufferTemplateInfo temp = getBufferInfo();
		if (temp.getFromType() == 1 && temp.getCostCount() > 0) {
			return source.costMana(temp.getCostCount());
		}
		return true;
	}

	/** 费用是否足够执行 */
	public boolean enougthMana() {
		if (getBufferInfo().getFromType() != 1) {
			return true;
		}
		return source.getMana() >= getBufferInfo().getCostCount();
	}

	/**
	 * 减少回合数/作用次数
	 * 
	 * @return
	 */
	public int decrease() {
		leftCount--;
		return leftCount;
	}

	/**
	 * 是否按使用次数作用的BUFFER
	 * 
	 * @return
	 */
	public boolean isTimesBuffer() {
		return bufferInfo.getDurableType() == DurableType.COUNT || bufferInfo.getDurableType() == DurableType.TIME_AND_COUNT;
	}

	protected abstract void exec(AttackOrder attackOrder, Damage beDamage1, Damage beDamage2);

	/** 恢复BUFFER的状态 **/
	public void reset() {
		if (damages != null)
			damages.clear();
	}

	/** 恢复继续使用 */
	public void reuse() {

	}

	/**
	 * <pre>
	 * 添加Buffer后，执行动作
	 * </pre>
	 */
	public void afterAdd() {

	}

	/**
	 * <pre>
	 * Buffer叠加后，先前效果失败，重新添加效果
	 * </pre>
	 */
	public void afterCompress() {

	}

	/** 移除BUFFER */
	public void dispose() {
		if (target.removeBuffer(this) && bufferInfo.getExeWay() == ExecWayType.REMOVE && state != BufferState.DEAD_REMOVE) {
			// 从目标身上移除
			target.execBuffer(this, ExecWayType.REMOVE);
		}
		state = BufferState.INVALID;
	}

	/** 初始化 */
	protected void init() {
		templateId = bufferInfo.getTemplateId();
		damages = new LinkedList<Damage>();
		state = BufferState.VALID;
		pressedNum = 1;
		lastExecTime = System.currentTimeMillis();

		int exeTime = 0;
		int exeCount = 0;
		// 元魂状态享受有害buff时间延长50%
		if (bufferInfo.getIsHelpful() == 0 && target.isSoulState()) {
			exeTime = bufferInfo.getExeTime() + bufferInfo.getExeTime() / 2;
			exeCount = bufferInfo.getExeCount() + bufferInfo.getExeCount() / 2;
		} else {
			exeTime = bufferInfo.getExeTime();
			exeCount = bufferInfo.getExeCount();
		}
		// 转化为毫秒数
		exeTime = exeTime * 1000;

		// 如果buff来自魂幡，享受魂幡加成
		exeTime = calSoullv(exeTime, SoulFuseSkillConfig.TIME);

		aliveTime = System.currentTimeMillis() + exeTime;
		this.leftCount = exeCount;
	}

	public void writeProto(BufferMsg.Builder bufferMsg) {
		bufferMsg.setTemplateId(templateId);
		bufferMsg.setSourceId(source.getId());
		if (source.getType() == RoleType.player && source.getSimpleInfo() != null) {
			bufferMsg.setSourcename(source.getSimpleInfo().getNickName());
		}
		int durableType = bufferInfo.getDurableType();
		if (durableType == DurableType.TIME || durableType == DurableType.TIME_AND_COUNT) {
			bufferMsg.setAliveTime(this.aliveTime - System.currentTimeMillis());
		}
		bufferMsg.setLeftCount(leftCount);
		bufferMsg.setBufferId(bufferId);
		bufferMsg.setTargetId(target.getId());
	}

	public int calSoullv(int param, int effectParam) {
		if (bufferInfo.getFromType() == FromType.FUSE && source.getType() == RoleType.player) {
			Player player = (Player) source;
			FuseSkillVo fv = player.getFuseSkill(getTemplateId());
			if (fv != null) {
				SoulFuseSkillConfig config = BattleTempMgr.getFuseSkillTemp(getTemplateId());
				if (config != null && config.getEffectParam() == SoulFuseSkillConfig.EFFECT) {
					int addPercent = player.getSoulLv() * 10;
					param += param * addPercent / 100;
				} else {
					Log.error("SoulFuseSkillConfig " + getTemplateId());
				}
			}
		}
		return param;
	}

	public int getType() {
		return bufferInfo.getType();
	}

	public long getLastExecTime() {
		return lastExecTime;
	}

	public void setLastExecTime(long lastExecTime) {
		this.lastExecTime = lastExecTime;
	}

	public long getBufferId() {
		return bufferId;
	}

	public int getTemplateId() {
		return templateId;
	}

	public Living getSource() {
		return source;
	}

	public Living getTarget() {
		return target;
	}

	public SkillBufferTemplateInfo getBufferInfo() {
		return bufferInfo;
	}

	public LinkedList<Damage> getDamages() {
		return damages;
	}

	public void setDamages(LinkedList<Damage> damages) {
		this.damages = damages;
	}

	public void addDamage(Damage damage) {
		damages.add(damage);
	}

	public boolean isPermanent() {
		return isPermanent;
	}

	public void setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getExeWay() {
		return exeWay;
	}

	public void setExeWay(int exeWay) {
		this.exeWay = exeWay;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public int getPressedNum() {
		return pressedNum;
	}

	public void addPressedNum(int count) {
		this.pressedNum += count;
	}

	/** 获取伤害计算类型 */
	public int getDamageType() {
		switch (bufferInfo.getType()) {
		case BufferType.COMMON_DAMANGE:
		case BufferType.COMMON_RESTORE:
			return DamageEffecterType.COMMON;
		case BufferType.ONLY_BLOOD:
		case BufferType.ONLY_RESTORE_BLOOD:
			return DamageEffecterType.BLOOD;
		case BufferType.ONLY_SOUL:
		case BufferType.ONLY_RESTORE_SOUL:
			return DamageEffecterType.SOUL;
		default:
			return DamageEffecterType.COMMON;
		}
	}

	public int getOverlayType() {
		return bufferInfo.getOverlayType();
	}

	public int getOverlayWay() {
		return bufferInfo.getOverlayWay();
	}

	public int getLeftCount() {
		return leftCount;
	}

	public void setLeftCount(int leftCount) {
		this.leftCount = leftCount;
	}

	public long getAliveTime() {
		return aliveTime;
	}

	public void setAliveTime(long aliveTime) {
		this.aliveTime = aliveTime;
	}

	public int getStatus() {
		return this.bufferInfo.getStatus();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nBuffer : ").append("\t");
		sb.append("orderId : ").append(getTemplateId()).append("\t");
		sb.append("source id : ").append(source.getId()).append("\n\t");
		// for (Damage damage : damages) {
		// sb.append("target id :
		// ").append(damage.getTarget().getId()).append("\n\t");
		// sb.append("damage : ").append(damage.getDamageValue()).append("\t");
		// sb.append("leftValue : ").append(damage.getLeftValue()).append("\t");
		// sb.append("extraData : ").append(damage.getSkillId()).append("\n\t");
		// }
		return sb.toString();
	}
}