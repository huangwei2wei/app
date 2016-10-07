package com.app.empire.scene.service.role.action;

import com.chuangyou.common.util.Vector3;
import com.chuangyou.xianni.entity.spawn.MonsterInfo;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.role.objects.PrivateMonster;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.spawn.MonsterSpawnNode;
import com.chuangyou.xianni.world.ArmyProxy;

public class CreatePrivateMonsterAction extends Action {
	private ArmyProxy	army;
	private Field		field;
	private Vector3		v3;
	private MonsterInfo	monsterInfo;
	/** 怪物过期时间  */
	private int leaveTime = 1*60*1000;

	public CreatePrivateMonsterAction(ArmyProxy army, MonsterInfo monsterInfo, Field field, Vector3 v3) {
		super(army);
		this.army = army;
		this.field = field;
		this.v3 = v3;
		this.monsterInfo = monsterInfo;
	}
	

	public CreatePrivateMonsterAction(ArmyProxy army,MonsterInfo monsterInfo,Vector3 v3,int leaveTime,int mapId) {
		super(army);
		this.army = army;
		this.v3 = v3;
		this.field = FieldMgr.getIns().getField(mapId);
		this.monsterInfo = monsterInfo;
		this.leaveTime = leaveTime;
	}




	public CreatePrivateMonsterAction(ArmyProxy army, MonsterInfo monsterInfo,int leaveTime) {
		super(army);
		this.army = army;
		this.field = army.getPlayer().getField();
		this.v3 = army.getPlayer().getPostion();
		this.monsterInfo = monsterInfo;
		this.leaveTime = leaveTime;
	}

	@Override
	public void execute() {
		PrivateMonster monster = new PrivateMonster(army.getPlayerId(),leaveTime);
		monster.setPostion(v3);
		MonsterSpawnNode.instill(monster, monsterInfo);
		field.enterField(monster);
	}

}
