package com.app.empire.scene.service.campaign.action;

import com.app.db.mysql.entity.CampaignInfo;
import com.app.empire.scene.constant.CampaignConstant.CampaignType;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.campaign.Campaign;
import com.app.empire.scene.service.campaign.CampaignFactory;
import com.app.empire.scene.service.campaign.CampaignMgr;
import com.app.empire.scene.service.campaign.state.StartState;
import com.app.empire.scene.service.warField.FieldMgr;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.thread.exec.Action;

public class CampaignCreateAction extends Action {
	private ArmyProxy army;
	private int campaignId;
	private int taskId;

	public CampaignCreateAction(ArmyProxy army, int campaignId, int taskId) {
		super(army);
		this.army = army;
		this.campaignId = campaignId;
		this.taskId = taskId;
	}

	@Override
	public void execute() {
		Field curField = FieldMgr.getIns().getField(army.getFieldId());
		// 不允许在副本中创建副本
		if (curField != null && curField.getCampaignId() > 0) {
			log.error("user request create campaign but is aleady in campaign. playerId : " + army.getPlayerId());
			return;
		}

		CampaignInfo temp = ServiceManager.getManager().getGameConfigService().getCampaignTemps().get(campaignId);
		if (temp == null) {
			log.error("playerId: " + army.getPlayerId() + " create campaign if fail ,campaignId:" + campaignId);
			return;
		}

		if (temp.getType() == CampaignType.THE_PLANE && army.getPlayer().getField().id != campaignId % (Field.MAX_ID)) {
			log.error("playerId: " + army.getPlayerId() + " create plane  fail ,campaignId:" + campaignId);
			return;
		}

		Campaign campaign = CampaignFactory.createCampaign(temp, army, taskId);
		if (campaign == null) {
			log.error("创建副本失败---------------------------------------------campaignId :" + campaignId);
			return;
		}
		CampaignMgr.add(campaign);
		campaign.stateTransition(new StartState(campaign));
		campaign.onPlayerEnter(army);
		if (campaign.getTemp().getType() == CampaignType.TEAM) {
			// Team team = TeamMgr.getTeam(army.getPlayerId());
			// if (team == null) {
			// return;
			// }
			// List<Long> players = team.getMembers();
			// if (players == null || players.size() <= 0) {
			// return;
			// }
			// for (Long playerId : players) {
			// if (army.getPlayerId() == playerId) {
			// continue;
			// }
			// ArmyProxy teamMember = WorldMgr.getArmy(playerId);
			// if (teamMember != null) {
			// campaign.onPlayerEnter(teamMember);
			// }
			// }
			// TeamCampaign tc = (TeamCampaign) campaign;
			// tc.setTeamId(team.getTeamid());
			// team.setCampaignId(tc.getIndexId());
		}
	}

}
