package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.AiConfig;
import com.app.db.mysql.entity.MonsterInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.battle.DamageListMsgProtocol.DamageListMsg;
import com.app.empire.protocol.pb.battle.DamageMsgProto.DamageMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.RoleConstants.RoleType;
import com.app.empire.scene.constant.SceneGlobal;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.ai.proxy.MonsterAI;
import com.app.empire.scene.service.ai.proxy.UpdatePosition;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.damage.Damage;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.task.CTBaseCondition;
import com.app.empire.scene.service.role.helper.Hatred;
import com.app.empire.scene.service.role.helper.HatredManager;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.service.warField.spawn.MonsterSpawnNode;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.util.Vector3;
import com.app.thread.exec.DelayAction;

public class Monster extends ActiveLiving {
	protected Logger log = Logger.getLogger(Monster.class);
	private MonsterInfo monsterInfo;
	private AiConfig aiConfig;

	// 怪物攻击的初始技能，固定写死
	private int skillId;
	// 初始位置
	private Vector3 initPosition;
	// 仇恨列表
	private List<Hatred> hatreds = Collections.synchronizedList(new ArrayList<Hatred>());
	// 攻击目标
	private int target;
	// 攻击目标位置
	private Vector3 targetPos;
	// 当前使用的技能id
	private int curSkillID;
	// 攻击者
	private long attacker;
	// 攻击时间
	private long attackTime;
	// 寻找敌人时间
	private long findEnemyTime;
	// 参与者
	private Set<Integer> joiners = new HashSet<>();

	public int getCurSkillID() {
		return curSkillID;
	}

	public void setCurSkillID(int curSkillID) {
		this.curSkillID = curSkillID;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public List<Hatred> getHatreds() {
		return hatreds;
	}

	public Vector3 getInitPosition() {
		return initPosition;
	}

	public Monster(MonsterSpawnNode node, MonsterInfo monsterInfo) {
		super(IDMakerHelper.nextID());
		setType(RoleType.monster);
		this.node = node;
		this.monsterInfo = monsterInfo;
		this.ai = new MonsterAI(this);
		this.updatePosition = new UpdatePosition(this, new PlayerSelectorHelper(this)); // new UpdatePosition(this, playerSelector);
	}

	public boolean onDie(Living killer) {
		if (super.onDie(killer)) {
			if (node != null) {
				MonsterSpawnNode mnode = (MonsterSpawnNode) node;
				mnode.lvingDie(this);
			}
			DieAction die = new DieAction(this, killer, 1000);
			// die.getActionQueue().enDelayQueue(die);
			die.execute();

			// // 执行死亡脚本
			// if (getAiConfig() != null) {
			// IScript iScript = ScriptManager.getScriptById(getAiConfig().getScript());
			// if (iScript != null) {
			// IMonsterDie script = (IMonsterDie) iScript;
			// if (script != null) {
			// try {
			// script.action(killer.getArmyId(), getMonsterInfo().getMonsterId());
			// } catch (Exception e) {
			// // TODO: handle exception
			// long playerId = killer.getArmyId();
			// if (killer.getArmyId() > 0) {
			// ChatManager.sendChatMsg2Center(Channel.SYSTEM, "script error: " + script.getScriptId() + "..." + e.toString(), playerId);
			// }
			// }
			// }
			// } else {
			// Log.error("-------------getAiConfig().getScript()-------------" + getAiConfig().getScript());
			// }
			// }

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
		Living deather;
		Living killer;

		public DieAction(Living deather, Living killer, int delay) {
			super(killer, delay);
			this.deather = deather;
			this.killer = killer;
		}

		@Override
		public void execute() {
			if (killer != null && killer.getArmyId() > 0) {
				calculationProfit(getSkin(), killer);
			}
		}

	}

	@Override
	public void enterField(Field f) {
		super.enterField(f);
		initPosition = getPostion().clone();
	}

	/**
	 * 计算收益
	 */
	protected void calculationProfit(int tempId, Living killer) {
		// 无论什么类型，击杀者必定掉落
		if (killer != null && this.getField() != null) {
			// DropManager.dropFromMonster(this.getSkin(), killer.getArmyId(), this.getId(), this.getField().id, this.getPostion());
		}

		// PlayerKillMonsterListMsg.Builder list = PlayerKillMonsterListMsg.newBuilder();
		// for (Long id : joiners) {
		// if (id == killer.getId()) {
		// continue;
		// }
		// if (getField() == null) {
		// log.error("--------------击杀者没有地图信息-------------");
		// continue;
		// }
		// Living l = getField().getLiving(id);
		// if (l != null) {
		// PlayerKillMonsterMsg.Builder msg = PlayerKillMonsterMsg.newBuilder();
		// msg.setBeKillId(tempId);
		// msg.setPlayerId(l.getArmyId());
		// msg.setType(RoleType.monster);
		// msg.setJoinType(BattleSettlementConstant.JOINER);// 参与者
		// list.addKillInfos(msg);
		// }
		// }
		// PlayerKillMonsterMsg.Builder msg = PlayerKillMonsterMsg.newBuilder();
		// msg.setBeKillId(tempId);
		// msg.setPlayerId(killer.getArmyId());
		// msg.setType(RoleType.monster);
		// msg.setJoinType(BattleSettlementConstant.KILLER);// 击杀者
		// list.addKillInfos(msg);
		// PBMessage pkg = MessageUtil.buildMessage(Protocol.C_PLAYER_KILL_MONSTER, list);
		// GatewayLinkedSet.send2Server(pkg);
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public long getAttacker() {
		return attacker;
	}

	public void setAttacker(long attacker) {
		this.attacker = attacker;
	}

	public long getAttackTime() {
		return attackTime;
	}

	public void setAttackTime(long attackTime) {
		this.attackTime = attackTime;
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
			}
			if (getAiConfig() != null && getAiConfig().getRunAway()) {
				return;
			}

			Hatred hatred = null;
			List<Hatred> hatreds = getHatreds();
			synchronized (this.hatreds) {
				for (int i = 0; i < hatreds.size(); i++) {
					if (hatreds.get(i).getTarget() != 0 && hatreds.get(i).getTarget() == damage.getSource().id) {
						hatred = hatreds.remove(i);
						break;
					}
				}
				if (hatred == null) {
					hatred = HatredManager.getInstance().getHatred();
					hatred.setTarget(damage.getSource().id);
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
			HatredManager.getInstance().removeHatred(hatred);
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
				HatredManager.getInstance().removeHatred(hatred);
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
	public int getAttackTarget() {
		return getDefaultAttackTarget();
	}

	/**
	 * 获取默认攻击目标
	 * 
	 * @return
	 */
	public int getDefaultAttackTarget() {
		if (this.getHatreds().size() > 0) {
			synchronized (this.getHatreds()) {
				Iterator<Hatred> iter = this.getHatreds().iterator();
				while (iter.hasNext()) {
					Hatred hatred = (Hatred) iter.next();
					if (hatred == null || hatred.getTarget() == 0) {
						continue;
					}
					Living target = getField().getLiving(hatred.getTarget());
					if (target == null) {
						continue;
					}
					if (target.isDie()) {
						continue;
					}
					return hatred.getTarget();
				}
			}
		}
		return 0;
	}

	/**
	 * 移除一个仇恨
	 * 
	 * @param id
	 */
	public void removeHatred(Integer id) {
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
					if (hatred != null && hatred.getTarget() == 0) {
						HatredManager.getInstance().removeHatred(hatred);
						iter.remove();
						continue;
					}
					Living target = getField().getLiving(hatred.getTarget());
					if (target == null) {
						HatredManager.getInstance().removeHatred(hatred);
						iter.remove();
						continue;
					}
					if (target.isDie()) {
						HatredManager.getInstance().removeHatred(hatred);
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
							HatredManager.getInstance().removeHatred(hatred);
							iter.remove();
						}
					}
				}
			}

			if (lastOverHatred != null && this.getHatreds().size() > 1) {
				// 移出仇恨列表
				HatredManager.getInstance().removeHatred(lastOverHatred);
				this.getHatreds().remove(lastOverHatred);
			}
			setTarget(getDefaultAttackTarget());
		}
	}

	public boolean isClear() {
		return System.currentTimeMillis() >= dieTime + 2 * 1000;
	}

	public MonsterInfo getMonsterInfo() {
		return monsterInfo;
	}

	public void setMonsterInfo(MonsterInfo monsterInfo) {
		this.monsterInfo = monsterInfo;
		int aiId = monsterInfo.getAiId();
		this.aiConfig = ServiceManager.getManager().getGameConfigService().getAiConfigs().get(aiId);
	}

	public Vector3 getTargetPos() {
		return targetPos;
	}

	public void setTargetPos(Vector3 targetPos) {
		this.targetPos = targetPos;
	}

	public AiConfig getAiConfig() {
		return aiConfig;
	}

	public long getFindEnemyTime() {
		return findEnemyTime;
	}

	public void setFindEnemyTime(long findEnemyTime) {
		this.findEnemyTime = findEnemyTime;
	}

	/** 受伤 */
	public int takeDamage(Damage damage) {
		Living source = damage.getSource();

		if (source.getArmyId() != 0) {
			joiners.add(source.getArmyId());
		}
		return super.takeDamage(damage);
	}

	/**
	 * 恢复初始满状态
	 */
	public boolean fullState() {
		List<Damage> damages = new ArrayList<>();
		Damage curSoul = new Damage(this, this);
		curSoul.setDamageType(EnumAttr.CUR_SOUL.getValue());
		curSoul.setDamageValue(Math.min(getCurSoul() - getMaxSoul(), 0));
		damages.add(curSoul);
		takeDamage(curSoul);

		Damage curBlood = new Damage(this, this);
		curBlood.setDamageType(EnumAttr.CUR_BLOOD.getValue());
		curBlood.setDamageValue(Math.min(getCurBlood() - getMaxBlood(), 0));
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
			Set<Integer> players = getNears(new PlayerSelectorHelper(this));
			// players.add(getArmyId());
			for (Integer armyId : players) {
				ArmyProxy army = ServiceManager.getManager().getPlayerService().getArmy(armyId);
				if (army != null) {
					army.sendPbMessage(Protocol.MAIN_BATTLE, Protocol.BATTLE_DAMAGE, damagesPb.build());
				}
			}
			// BroadcastUtil.sendBroadcastPacket(players,
			// Protocol.U_G_DAMAGE,damagesPb.build());
		}

		clearWorkBuffer();
		return true;
	}

	public void moveto(Vector3 goal) {
		super.moveto(goal);
		removeInvincibleBuffer();
	}

	public void stop(boolean need2Client) {
		super.stop(need2Client);
		removeInvincibleBuffer();
	}

	public void addInvincibleBuffer(Buffer invincibleBuffer) {
		if (this.invincibleBuffer != null) {
			log.error("触发了两次速归");
		}
		isRunBack = true;
		this.addBuffer(invincibleBuffer);
		this.invincibleBuffer = invincibleBuffer;
	}

	public boolean isRunBack() {
		return isRunBack;
	}

}
