package com.app.empire.scene.service.role.objects;

import java.util.ArrayList;
import java.util.List;

import com.app.empire.protocol.pb.army.PetInfoProto.PetInfoMsg;
import com.app.empire.protocol.pb.army.PropertyMsgProto.PropertyMsg;
import com.app.empire.protocol.pb.battle.BattleLivingInfoMsgProto.BattleLivingInfoMsg;
import com.app.empire.protocol.pb.battle.BattleLivingInfoMsgProto.BattleLivingInfoMsg.Builder;
import com.app.empire.protocol.pb.battle.BufferMsgProto.BufferMsg;
import com.app.empire.protocol.pb.player.PlayerAttSnapProto.PlayerAttSnapMsg;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.constant.RoleConstants;
import com.app.empire.scene.service.battle.buffer.Buffer;
import com.app.empire.scene.service.battle.skill.Skill;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;

public class Pet extends ActiveLiving {

	private int petSoul;
	private int petPhysique;
	private int petQuality;

	public Pet(int playerId, int id) {
		super(playerId, id);
		this.setType(RoleConstants.RoleType.pet);
	}

	public void readPetInfo(PetInfoMsg pet) {
		setSkin(pet.getPetTempId());
		setPetSoul(pet.getPetSoul());
		setPetPhysique(pet.getPetPhysique());
		setPetQuality(pet.getPetQuality());
		List<PropertyMsg> attList = new ArrayList<>(pet.getPetPropertyList());
		readProperty(attList);
	}

	/**
	 * 获取详情
	 */
	@Override
	public Builder getBattlePlayerInfoMsg() {
		// TODO Auto-generated method stub
		cachBattleInfoPacket = BattleLivingInfoMsg.newBuilder();
		cachBattleInfoPacket.setLivingId(getId());
		cachBattleInfoPacket.setPlayerId(getArmyId());
		cachBattleInfoPacket.setType(getType());
		cachBattleInfoPacket.setSkinId(getSkin());

		if (getPostion() != null) {
			cachBattleInfoPacket.setPostion(Vector3BuilderHelper.build(getPostion()));
		} else {
			cachBattleInfoPacket.setPostion(Vector3BuilderHelper.build(Vector3.Invalid));
		}
		cachBattleInfoPacket.setTarget(Vector3BuilderHelper.build(getTargetPostion()));

		List<Buffer> toalBuffer = new ArrayList<>();
		for (List<Buffer> pBuffers : permanentBuffer.values()) {
			toalBuffer.addAll(pBuffers);
		}
		for (List<Buffer> wBuffers : workBuffers.values()) {
			toalBuffer.addAll(wBuffers);
		}
		for (Buffer buffer : toalBuffer) {
			BufferMsg.Builder bufferMsg = BufferMsg.newBuilder();
			buffer.writeProto(bufferMsg);
			cachBattleInfoPacket.addBufferList(bufferMsg);
		}

		for (Skill skill : drivingSkills.values()) {
			System.out.println(skill.getSkillId() + "---------------------skillid");
			cachBattleInfoPacket.addSkills(skill.getSkillId());
		}

		PropertyMsg.Builder pmsg = PropertyMsg.newBuilder();
		pmsg.setType(EnumAttr.PetSoul.getValue());
		pmsg.setTotalPoint(getPetSoul());
		cachBattleInfoPacket.addPropertis(pmsg);

		pmsg = PropertyMsg.newBuilder();
		pmsg.setType(EnumAttr.PetPhysique.getValue());
		pmsg.setTotalPoint(getPetPhysique());
		cachBattleInfoPacket.addPropertis(pmsg);

		pmsg = PropertyMsg.newBuilder();
		pmsg.setType(EnumAttr.PetQuality.getValue());
		pmsg.setTotalPoint(getPetQuality());
		cachBattleInfoPacket.addPropertis(pmsg);

		return cachBattleInfoPacket;
	}

	/**
	 * 获取场景对象的快照信息
	 * 
	 * @return
	 */
	@Override
	public PlayerAttSnapMsg.Builder getAttSnapMsg() {
		if (cacheAttSnapPacker == null)
			cacheAttSnapPacker = PlayerAttSnapMsg.newBuilder();
		cacheAttSnapPacker.setPlayerId(id);
		cacheAttSnapPacker.setType(getType());
		cacheAttSnapPacker.setSkinId(getSkin());
		cacheAttSnapPacker.setPostion(Vector3BuilderHelper.build(getPostion()));
		cacheAttSnapPacker.setTarget(Vector3BuilderHelper.build(getTargetPostion()));
		cacheAttSnapPacker.setOwnerId(getArmyId());
		return cacheAttSnapPacker;
	}

	public int getPetSoul() {
		return petSoul;
	}

	public void setPetSoul(int petSoul) {
		this.petSoul = petSoul;
	}

	public int getPetPhysique() {
		return petPhysique;
	}

	public void setPetPhysique(int petPhysique) {
		this.petPhysique = petPhysique;
	}

	public int getPetQuality() {
		return petQuality;
	}

	public void setPetQuality(int petQuality) {
		this.petQuality = petQuality;
	}
}
