package com.app.empire.protocol.data.scene.world;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

/**
 * 登录场景服
 * 
 * @author doter
 * 
 */
public class ArmyInfoMsg extends AbstractData {
	private PlayerInfoMsg playerInfoMsg;// 角色外观信息
	private HeroInfoMsg heoBattleInfo;// 角色战斗信息
	private PetInfoMsg petBattleInfo; // 宠物战斗属性

	public ArmyInfoMsg(int sessionId, int serial) {
		super(Protocol.MAIN_WORLD, Protocol.WORLD_LoginIn, sessionId, serial);
	}

	public ArmyInfoMsg() {
		super(Protocol.MAIN_WORLD, Protocol.WORLD_LoginIn);
	}

	public PlayerInfoMsg getPlayerInfoMsg() {
		return playerInfoMsg;
	}

	public void setPlayerInfoMsg(PlayerInfoMsg playerInfoMsg) {
		this.playerInfoMsg = playerInfoMsg;
	}

	public HeroInfoMsg getHeoBattleInfo() {
		return heoBattleInfo;
	}

	public void setHeoBattleInfo(HeroInfoMsg heoBattleInfo) {
		this.heoBattleInfo = heoBattleInfo;
	}

	public PetInfoMsg getPetBattleInfo() {
		return petBattleInfo;
	}

	public void setPetBattleInfo(PetInfoMsg petBattleInfo) {
		this.petBattleInfo = petBattleInfo;
	}

}
