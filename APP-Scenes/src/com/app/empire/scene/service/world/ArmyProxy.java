package com.app.empire.scene.service.world;

import java.util.Set;

import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.FieldInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.army.ArmyInfoReloadMsgProto.ArmyInfoReloadMsg;
import com.app.empire.protocol.pb.army.PetInfoProto.PetInfoMsg;
import com.app.empire.protocol.pb.army.PlayerPositionInfoProto.PlayerPositionInfoMsg;
import com.app.empire.protocol.pb.army.PropertyMsgProto.PropertyMsg;
import com.app.empire.protocol.pb.player.PlayerAttUpdateProto.PlayerAttUpdateMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.scene.constant.EnterMapResult;
import com.app.empire.scene.constant.EnumAttr;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.Pet;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.warField.helper.NotifyNearHelper;
import com.app.empire.scene.service.warField.helper.selectors.PlayerSelectorHelper;
import com.app.empire.scene.session.ConnectSession;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.util.Vector3BuilderHelper;
import com.app.empire.scene.util.exec.AbstractActionQueue;
import com.app.empire.scene.util.exec.ThreadManager;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.google.protobuf.Message;

/**
 * 部队代理
 */
public class ArmyProxy extends AbstractActionQueue {
	private static final EnumAttr[] reloadAttrs = { EnumAttr.CUR_SOUL, EnumAttr.CUR_BLOOD, EnumAttr.MANA };
	private Integer sessionId;
	private ConnectSession connSession = null; // 当前所对应的Session（网关
	// private CmdTaskQueue cmdTaskQueue;
	private int playerId; // 用户ID
	private String site; // 站点（跨服用）
	// private Channel channel; // 连接器
	private int fieldId; // 地图ID，用户退出，回写到center
	// private Vector3 position; // 主角位置，用户退出，回写到center

	private Player player; // 英雄
	private Pet pet; // 宠物
	private ArmyPositionRecord posRecord; // 玩家位置信息

	public ArmyProxy(int playerId, Integer sessionId, ConnectSession connSession, String site, SimplePlayerInfo simplePlayerInfo, Player hero, Pet pet) {
		super(ThreadManager.actionExecutor);
		this.playerId = playerId;
		this.sessionId = sessionId;
		this.connSession = connSession;
		this.site = site;
		this.player = hero;
		this.pet = pet;
		// this.cmdTaskQueue = new AbstractCmdTaskQueue(ThreadManager.cmdExecutor);
		this.posRecord = new ArmyPositionRecord();
	}

	/**
	 * 像客户端或world服发送数据包
	 * 
	 * @param packet
	 */
	public void sendPbMessage(short type, short subType, Message msg, byte target) {
		try {
			if (msg == null) {
				return;
			}
			if (connSession == null) {
				return;
			}
			// PbAbstractData pbMsg = new PbAbstractData(type, subType, target);
			// pbMsg.setSessionId(getSessionId());
			// pbMsg.setBytes(msg.toByteArray());
			// connSession.write(pbMsg);
			connSession.write(subType, subType, getSessionId(), 0, msg, target);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 像客户端发送数据包
	 * 
	 * @param packet
	 */
	public void sendPbMessage(short type, short subType, Message msg) {
		sendPbMessage(type, subType, msg, EnumTarget.CLIENT.getValue());
	}

	/** 切换地图 */
	public void changeField(Field field, Vector3 postion) {
		leave();
		join(field, postion);
	}

	public void leave() {
		Field field = FieldMgr.getIns().getField(getFieldId());
		if (field != null) {
			field.leaveField(player);

			FieldInfo fieldInfo = field.getFieldInfo();
			float x = fieldInfo.getX();
			float y = fieldInfo.getY();
			float z = fieldInfo.getZ();
			Vector3 v3 = new Vector3(x/ Vector3.Accuracy, y/ Vector3.Accuracy, z/ Vector3.Accuracy);
			player.setPostion(v3);
			if (pet != null) {
				field.leaveField(pet);
			}
			setFieldId(0);
		}
	}

	public void join(Field field, Vector3 postion) {
		player.setPostion(postion);
		// 接受进场保护
		player.setProtection(true);
		setFieldId(field.id);
		field.enterField(player);
		if (pet != null) {
			pet.setPostion(postion);
			pet.setProtection(true);
			field.enterField(pet);
		}
	}

	/**
	 * 更新宠物信息
	 * 
	 * @param petInfo
	 */
	public void updatePet(PetInfoMsg petInfo) {

		if (pet == null) {
			pet = new Pet(playerId, IDMakerHelper.nextID());
			pet.readPetInfo(petInfo);
			pet.setPostion(player.getPostion());

			player.getField().enterField(pet);

			// PBMessage selfMsg =
			// MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP,
			// pet.getAttSnapMsg());
			// this.sendPbMessage(selfMsg);
		} else {
			if (pet.getSkin() != petInfo.getPetTempId()) {
				pet.getField().leaveField(pet);
				pet.destory();
				pet = new Pet(playerId, IDMakerHelper.nextID());
				pet.readPetInfo(petInfo);
				pet.setPostion(player.getPostion());

				player.getField().enterField(pet);

				// PBMessage selfMsg =
				// MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP,
				// pet.getAttSnapMsg());
				// this.sendPbMessage(selfMsg);
			} else {
				PlayerAttUpdateMsg.Builder attUpdateMsg = PlayerAttUpdateMsg.newBuilder();
				if (petInfo.getPetSoul() != pet.getPetSoul()) {
					PropertyMsg.Builder proMsg = PropertyMsg.newBuilder();
					proMsg.setType(EnumAttr.PetSoul.getValue());
					proMsg.setTotalPoint(petInfo.getPetSoul());
					attUpdateMsg.addAtt(proMsg);
					pet.setPetSoul(petInfo.getPetSoul());
				}
				if (petInfo.getPetPhysique() != pet.getPetPhysique()) {
					PropertyMsg.Builder proMsg = PropertyMsg.newBuilder();
					proMsg.setType(EnumAttr.PetPhysique.getValue());
					proMsg.setTotalPoint(petInfo.getPetPhysique());
					attUpdateMsg.addAtt(proMsg);
					pet.setPetPhysique(petInfo.getPetPhysique());
				}
				if (petInfo.getPetQuality() != pet.getPetQuality()) {
					PropertyMsg.Builder proMsg = PropertyMsg.newBuilder();
					proMsg.setType(EnumAttr.PetQuality.getValue());
					proMsg.setTotalPoint(petInfo.getPetQuality());
					attUpdateMsg.addAtt(proMsg);
					pet.setPetQuality(petInfo.getPetQuality());
				}
				if (attUpdateMsg.getAttList() != null && attUpdateMsg.getAttList().size() > 0) {
					attUpdateMsg.setPlayerId(pet.getId());

					// PBMessage selfMsg =
					// MessageUtil.buildMessage(Protocol.U_RESP_PLAYER_ATT_UPDATE,
					// attUpdateMsg);
					// this.sendPbMessage(selfMsg);

					Set<Integer> nears = pet.getNears(new PlayerSelectorHelper(pet));
					NotifyNearHelper.notifyAttrChange(nears, attUpdateMsg.build());
				}
			}
		}

	}

	public void unload() {
		Field field = FieldMgr.getIns().getField(getFieldId());
		try {
			ArmyInfoReloadMsg.Builder armyReload = ArmyInfoReloadMsg.newBuilder();
			for (EnumAttr attr : reloadAttrs) {
				PropertyMsg.Builder pbulider = PropertyMsg.newBuilder();
				pbulider.setType(attr.getValue());
				pbulider.setTotalPoint(player.getProperty(attr.getValue()));
				armyReload.addPropertys(pbulider.build());
			}

			if (field != null && player.getPostion() != null) {
				PostionMsg.Builder postion = PostionMsg.newBuilder();
				postion.setMapId(this.fieldId);
				postion.setMapKey(field.getMapKey());

				Vector3 curPos = player.getPostion();
				PBVector3.Builder pbPos = Vector3BuilderHelper.build(curPos);
				postion.setPostion(pbPos.build());
				armyReload.setPostion(postion.build());

				Campaign campaign = CampaignMgr.getCampagin(field.getCampaignId());
				if (campaign != null) {
					campaign.onPlayerLeave(this, true);
				}
				// 离开地图
				field.leaveField(player);
				player.clearData();
				if (pet != null) {
					field.leaveField(pet);
					pet.clearData();
				}
			}

			// 回写数据
			sendPbMessage(Protocol.MAIN_PLAYER, Protocol.PLAYER_PLAYERINFO, armyReload.build(), EnumTarget.WORLDSERVER.getValue());
		} catch (Exception e) {
			log.error("unload player error,playerId :" + getPlayerId());
		} finally {
			player.destory();
			if (pet != null) {
				pet.destory();
			}
		}
	}

	public void updataPostion(int tempId, int mapId, Vector3 pos) {
		FieldInfo mapTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(posRecord.getMapTempId());
		if (mapTemp != null && mapTemp.getType() == 1) {
			posRecord.setPreMapId(posRecord.getMapTempId());
			posRecord.setPreMapTempId(posRecord.getMapTempId());
			posRecord.setPrePos(posRecord.getPos());
		}

		if (mapId != 0 && tempId != 0) {
			posRecord.setMapId(mapId);
			posRecord.setMapTempId(tempId);
			posRecord.setPos(pos);
		}
	}

	/** 重载位置信息 */
	public void readPositionMsg(PlayerPositionInfoMsg msg) {
		posRecord.setPreMapId(msg.getPrePos().getMapId());
		posRecord.setPreMapTempId(msg.getPrePos().getMapKey());
		Vector3 preV3 = Vector3BuilderHelper.get(msg.getPrePos().getPostion());
		posRecord.setPrePos(preV3);

		posRecord.setMapId(msg.getCurPos().getMapId());
		posRecord.setMapTempId(msg.getCurPos().getMapKey());
		Vector3 V3 = Vector3BuilderHelper.get(msg.getCurPos().getPostion());
		posRecord.setPos(V3);
	}

	/**
	 * 返回出生点
	 */
	public void returnBornMap() {
		// Field field = FieldMgr.getIns().getField(SystemConfigTemplateMgr.getInitBorn());
		// FieldInfo fieldTemp = FieldTemplateMgr.getFieldTemp(SystemConfigTemplateMgr.getInitBorn());
		// Vector3 vector3 = fieldTemp.getPosition();
		// changeField(field, vector3);
		//
		// ChangeMapResultMsg.Builder cmbuilder = ChangeMapResultMsg.newBuilder();
		// cmbuilder.setResult(EnterMapResult.SUCCESS);// 进入成功
		// PostionMsg.Builder postionMsg = PostionMsg.newBuilder();
		// postionMsg.setMapId(field.id);
		// postionMsg.setMapKey(field.getMapKey());
		// postionMsg.setPostion(Vector3BuilderHelper.build(vector3));
		// cmbuilder.setPostion(postionMsg);
		// sendPbMessage(MessageUtil.buildMessage(Protocol.C_ENTER_SENCE_MAP_RESULT, cmbuilder));
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Pet getPet() {
		return pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public ConnectSession getConnSession() {
		return connSession;
	}

	public void setConnSession(ConnectSession connSession) {
		this.connSession = connSession;
	}

	public ArmyPositionRecord getPosRecord() {
		return posRecord;
	}

}
