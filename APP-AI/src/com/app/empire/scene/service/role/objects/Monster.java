package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.app.empire.scene.entity.MonsterInfo;
import com.chuangyou.common.protobuf.pb.PlayerKillMonsterProto.PlayerKillMonsterMsg;
import com.chuangyou.common.protobuf.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.chuangyou.common.protobuf.pb.battle.DamageMsgProto.DamageMsg;
import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.ai.proxy.MonsterAI;
import com.chuangyou.xianni.battle.action.MonsterPollingAction;
import com.chuangyou.xianni.battle.buffer.Buffer;
import com.chuangyou.xianni.battle.buffer.BufferFactory;
import com.chuangyou.xianni.battle.damage.Damage;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.campaign.task.CTBaseCondition;
import com.chuangyou.xianni.config.SceneGlobal;
import com.chuangyou.xianni.constant.EnumAttr;
import com.chuangyou.xianni.cooldown.CoolDownTypes;
import com.chuangyou.xianni.drop.manager.DropManager;
import com.chuangyou.xianni.entity.buffer.SkillBufferTemplateInfo;
import com.chuangyou.xianni.entity.spawn.AiConfig;
import com.chuangyou.xianni.exec.DelayAction;
import com.chuangyou.xianni.manager.SceneManagers;
import com.chuangyou.xianni.netty.GatewayLinkedSet;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.action.UpdatePositionAction;
import com.chuangyou.xianni.role.helper.Hatred;
import com.chuangyou.xianni.role.helper.IDMakerHelper;
import com.chuangyou.xianni.role.helper.RoleConstants.RoleType;
import com.chuangyou.xianni.role.script.IMonsterDie;
import com.chuangyou.xianni.role.template.AiConfigTemplateMgr;
import com.chuangyou.xianni.script.IScript;
import com.chuangyou.xianni.script.manager.ScriptManager;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.selectors.PlayerSelectorHelper;
import com.chuangyou.xianni.warfield.spawn.MonsterSpawnNode;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

public class Monster extends ActiveLiving {
	private MonsterInfo			monsterInfo;
	private AiConfig			aiConfig;

	// 怪物攻击的初始技能，固定写死
	private int					skillId;
	// 初始位置
	private Vector3				initPosition;
	// 仇恨列表
	private List<Hatred>		hatreds				= Collections.synchronizedList(new ArrayList<Hatred>());
	// 攻击目标
	private Long				target;
	// 当前使用的技能id
	private int					curSkillID;

	// 攻击者
	private Long				attacker;

	public int getCurSkillID() {
		return curSkillID;
	}


	public void setCurSkillID(int curSkillID) {
		this.curSkillID = curSkillID;
		// 写死一个怪物执行技能
		// Skill test = new Skill(BattleTempMgr.getActionInfo(curSkillID));
		// addSkill(test);
	}

	public Long getTarget() {
		return target;
	}

	public void setTarget(Long target) {
		this.target = target;
	}

	public List<Hatred> getHatreds() {
		return hatreds;
	}

	public Vector3 getInitPosition() {
		return initPosition;
	}

	public Monster(MonsterSpawnNode node) {
		super(IDMakerHelper.nextID());
		setType(RoleType.monster);
		this.node = node;

		// enDelayQueue(new MonsterAI(this));
		// enDelayQueue(new UpdatePositionAction(this));new
		// PlayerSelectorHelper(this.activeLiving);
		enDelayQueue(new MonsterPollingAction(this, new MonsterAI(this), new UpdatePositionAction(this, new PlayerSelectorHelper(this))));
		// setCurSkillID(1001);
	}

	public boolean onDie(Living killer) {
		if (super.onDie(killer)) {

			if (node != null) {
				MonsterSpawnNode mnode = (MonsterSpawnNode) node;
				mnode.lvingDie(this);
			}
			if (killer != null && this.getField() != null) {
				DropManager.dropFromMonster(this.getSkin(), killer.getArmyId(), this.getId(), this.getField().id, this.getPostion());
			}

			DieAction die = new DieAction(this, killer, 1000);
			die.getActionQueue().enDelayQueue(die);

			if (getAiConfig() != null) {
				IScript iScript = ScriptManager.getScriptById(getAiConfig().getScript());
				if (iScript != null) {
					IMonsterDie script = (IMonsterDie) iScript;
					script.action(killer.getArmyId(), getMonsterInfo().getMonsterId());
				} else {
					Log.error("-------------getAiConfig().getScript()-------------" + getAiConfig().getScript());
				}
			}
			if (field != null && field.getCampaignId() > 0) {
				Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
				if (campaign != null) {
					campaign.notifyTaskEvent(CTBaseCondition.KILL_MONSTER_KIND, getSkin());
					campaign.notifyTaskEvent(CTBaseCondition.KILL_MONSTER_COUNT, getSkin());
				}
			}
		}
		return true;
	}

	class DieAction extends DelayAction {
		Living	deather;
		Living	killer;

		public DieAction(Living deather, Living killer, int delay) {
			super(killer, delay);
			this.deather = deather;
			this.killer = killer;
		}

		@Override
		public void execute() {
			if (killer != null && killer.getArmyId() > 0) {
				notifyCenter(getSkin(), killer.getArmyId());
			}
		}

	}

	@Override
	public void enterField(Field f) {
		// TODO Auto-generated method stub
		super.enterField(f);
		initPosition = getPostion().clone();
	}

	/**
	 * 通知
	 * 
	 * @param tempId
	 * @param playerId
	 */
	protected void notifyCenter(int tempId, long playerId) {
		PlayerKillMonsterMsg.Builder msg = PlayerKillMonsterMsg.newBuilder();
		msg.setMonsterTemplateId(tempId);
		msg.setPlayerId(playerId);
		msg.setType(1);
		PBMessage pkg = MessageUtil.buildMessage(Protocol.C_PLAYER_KILL_MONSTER, msg);
		GatewayLinkedSet.send2Server(pkg);
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public Long getAttacker() {
		return attacker;
	}

	public void setAttacker(Long attacker) {
		this.attacker = attacker;
	}

	@Override
	protected void addHatred(Damage damage) {
		super.addHatred(damage);
		this.attacker = damage.getSource().getId();
		// 排除自己给自己的伤害包
		if (damage.getSource().id != id) {
			if (damage.getDamageValue() < 0)
				return; // 移除增益效果
			if (damage.getFromId() > 0 && damage.getTipType() != Damage.MISS) {
				// 非Buff伤害，更新受击时间搓
				// SkillActionTemplateInfo actionInfo =
				// BattleTempMgr.getActionInfo(damage.getSkillId());
				// if (actionInfo != null) {
				// SkillActionMoveTempleteInfo moveInfo =
				// BattleTempMgr.getActionMoveInfo(actionInfo.getMove());
				// if (moveInfo != null) {
				// if (moveInfo.getMove_hitbackstep() > 0) {
				// float hitbackstep = moveInfo.getMove_hitbackstep() / 10.0f;
				// // System.out.println("hitbackstep = " + hitbackstep
				// // + "getPostion() = " + getPostion() + "
				// // damage.getSource().getPostion() = " +
				// // damage.getSource().getPostion());
				// Vector3 dir = Vector3.sub(getPostion(),
				// damage.getSource().getPostion());
				// dir.y = getPostion().y;
				// Vector3 hitBackPoint =
				// MathUtils.GetVector3ByDir(getPostion(), dir.getNormalize(),
				// hitbackstep);
				// // System.out.println("id = " + id + "hitBackPoint =
				// // " + hitBackPoint);
				// if (isValidPoint(hitBackPoint))
				// setPostion(hitBackPoint);
				// }
				// }
				// }

				addCooldown(CoolDownTypes.BE_ATTACK, null, SceneGlobal.AI_BEATTACK_TIME);
			}
			if (getAiConfig() != null && getAiConfig().isRunAway()) {
				return;
			}

			Hatred hatred = null;
			List<Hatred> hatreds = getHatreds();
			synchronized (this.hatreds) {
				for (int i = 0; i < hatreds.size(); i++) {
					if (hatreds.get(i).getTarget() != null && hatreds.get(i).getTarget() == damage.getSource().id) {
						hatred = hatreds.remove(i);
						break;
					}
				}
				if (hatred == null) {
					hatred = SceneManagers.hatredManager.getHatred();
					hatred.setTarget(damage.getSource().id);
					hatred.setFirstAttack(System.currentTimeMillis());
				}
				// 增加仇恨值
				hatred.setHatred(hatred.getHatred() + damage.getDamageValue());
				hatred.setLastAttack(System.currentTimeMillis());
				// 插入仇恨列表（按仇恨值大小排列）
				for (int i = 0; i < hatreds.size(); i++) {
					if (hatreds.get(i).getHatred() < hatred.getHatred()) {
						hatreds.add(i, hatred);
						return;
					}
				}
				hatreds.add(hatred);
			}
		}
	}

	/**
	 * 清除仇恨
	 */
	public void cleanHatreds() {
		Iterator<Hatred> iter = this.getHatreds().iterator();
		while (iter.hasNext()) {
			Hatred hatred = iter.next();
			SceneManagers.hatredManager.removeHatred(hatred);
			iter.remove();
		}
	}

	/**
	 * 清除过期仇恨
	 */
	public void cleanExpiredHatreds() {
		Iterator<Hatred> iter = this.getHatreds().iterator();
		while (iter.hasNext()) {
			Hatred hatred = iter.next();
			long lastAttack = hatred.getLastAttack();
			if (System.currentTimeMillis() - lastAttack > 60000) {
				SceneManagers.hatredManager.removeHatred(hatred);
				iter.remove();
			}
		}
	}

	/**
	 * 最大仇恨
	 * 
	 * @return
	 */
	public Hatred getMaxHatred() {
		if (getHatreds() != null && getHatreds().size() > 0) {
			Hatred max = getHatreds().get(0);
			return max;
		}
		return null;
	}

	/**
	 * 获得攻击目标
	 * 
	 * @return
	 */
	public Long getAttackTarget() {
		return getDefaultAttackTarget();
	}

	// public void clearWorkBuffer() {
	// List<Buffer> allbuffer = new ArrayList<>();
	// synchronized (workBuffers) {
	// for (Entry<Integer, List<Buffer>> entry : workBuffers.entrySet()) {
	// List<Buffer> wayBufs = entry.getValue();
	// allbuffer.addAll(wayBufs);
	// wayBufs.clear();
	// }
	// workBuffers.clear();
	// }
	//
	// for (Buffer buff : allbuffer) {
	// buff.stop();
	// BufferMsg.Builder bmsg = BufferMsg.newBuilder();
	// bmsg.setBufferId(buff.getBufferId());
	// bmsg.setOption(4);// 4 删除
	// bmsg.setSourceId(buff.getSource().getId());
	// bmsg.setTargetId(buff.getTarget().getId());
	// sendBufferChange(bmsg.build());
	// }
	// }

	/**
	 * 获取默认攻击目标
	 * 
	 * @return
	 */
	public Long getDefaultAttackTarget() {
		if (this.getHatreds().size() > 0) {
			synchronized (this.getHatreds()) {
				Iterator<Hatred> iter = this.getHatreds().iterator();
				while (iter.hasNext()) {
					Hatred hatred = (Hatred) iter.next();
					if (hatred == null || hatred.getTarget() == null)
						continue;
					Living target = getField().getLiving(hatred.getTarget());
					if (target == null)
						continue;
					if (target.isDie())
						continue;
					return hatred.getTarget();
				}
			}
		}
		return -1l;
	}

	/**
	 * 移除一个仇恨
	 * 
	 * @param id
	 */
	public void removeHatred(Long id) {
		// 遍历仇恨列表
		Iterator<Hatred> iter = this.getHatreds().iterator();
		while (iter.hasNext()) {
			Hatred hatred = (Hatred) iter.next();
			if (hatred.getTarget() == id) {
				iter.remove();
				break;
			}
		}
	}

	/**
	 * 重新计算仇恨列表
	 */
	public void countHatreds() {
		// 仇恨过时，但在警戒范围内仇恨度最高的目标
		Hatred lastOverHatred = null;
		if (this.getHatreds().size() > 0) {
			long valid = System.currentTimeMillis() - SceneGlobal.AI_MONSTER_OVERDUE;
			// 遍历仇恨列表
			synchronized (hatreds) {
				Iterator<Hatred> iter = this.getHatreds().iterator();
				while (iter.hasNext()) {
					Hatred hatred = (Hatred) iter.next();
					if (hatred != null && hatred.getTarget() == null) {
						SceneManagers.hatredManager.removeHatred(hatred);
						iter.remove();
						continue;
					}
					Living target = getField().getLiving(hatred.getTarget());
					if (target == null) {
						SceneManagers.hatredManager.removeHatred(hatred);
						iter.remove();
						continue;
					}
					if (target.isDie()) {
						SceneManagers.hatredManager.removeHatred(hatred);
						iter.remove();
						continue;
					}
					// 攻击过期，移出仇恨列表
					if (hatred.getLastAttack() < valid) {
						// 仇恨过时，但在警戒范围内仇恨度最高的目标，暂时保留在仇恨列表中
						if (lastOverHatred == null)
							lastOverHatred = hatred;
						else {
							// 移出仇恨列表
							SceneManagers.hatredManager.removeHatred(hatred);
							iter.remove();
						}
					}
				}
			}

			if (lastOverHatred != null && this.getHatreds().size() > 1) {
				// 移出仇恨列表
				SceneManagers.hatredManager.removeHatred(lastOverHatred);
				this.getHatreds().remove(lastOverHatred);
			}
		}
	}

	public boolean isClear() {
		return System.currentTimeMillis() >= dieTime + 5 * 1000;
	}

	public MonsterInfo getMonsterInfo() {
		return monsterInfo;
	}

	public void setMonsterInfo(MonsterInfo monsterInfo) {
		this.monsterInfo = monsterInfo;
		int aiId = monsterInfo.getAiId();
		this.aiConfig = AiConfigTemplateMgr.get(aiId);
	}

	public AiConfig getAiConfig() {
		return aiConfig;
	}

	/**
	 * 恢复初始满状态
	 */
	public boolean fullState() {
		List<Damage> damages = new ArrayList<>();
		Damage curSoul = new Damage(this, this);
		curSoul.setDamageType(EnumAttr.CUR_SOUL.getValue());
		curSoul.setDamageValue(getCurSoul() - getInitSoul());
		damages.add(curSoul);
		takeDamage(curSoul);

		Damage curBlood = new Damage(this, this);
		curBlood.setDamageType(EnumAttr.CUR_BLOOD.getValue());
		curBlood.setDamageValue(getCurSoul() - getInitBlood());
		damages.add(curBlood);
		takeDamage(curBlood);

		if (damages.size() > 0) {
			DamageListMsg.Builder damagesPb = DamageListMsg.newBuilder();
			damagesPb.setAttackId(-1);
			for (Damage d : damages) {
				DamageMsg.Builder dmsg = DamageMsg.newBuilder();
				d.writeProto(dmsg);
				damagesPb.addDamages(dmsg);
			}
			Set<Long> players = getNears(new PlayerSelectorHelper(this));
			// players.add(getArmyId());
			for (Long armyId : players) {
				ArmyProxy army = WorldMgr.getArmy(armyId);
				PBMessage message = MessageUtil.buildMessage(Protocol.U_G_DAMAGE, damagesPb.build());
				if (army != null) {
					army.sendPbMessage(message);
				}
			}
			// BroadcastUtil.sendBroadcastPacket(players,
			// Protocol.U_G_DAMAGE,damagesPb.build());
		}

		clearWorkBuffer();
		return true;
	}
}
