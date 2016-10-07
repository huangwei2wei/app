package com.app.empire.scene.service.campaign.action;

import java.util.List;

import com.app.empire.scene.service.team.Team;
import com.app.empire.scene.service.team.TeamMgr;
import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignFactory;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.campaign.CampaignTempMgr;
import com.chuangyou.xianni.campaign.TeamCampaign;
import com.chuangyou.xianni.constant.CampaignConstant.CampaignType;
import com.chuangyou.xianni.entity.campaign.CampaignTemplateInfo;
import com.chuangyou.xianni.exec.Action;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

public class CampaignCreateAction extends Action {
	private ArmyProxy	army;
	private int			campaignId;
	private int			taskId;

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
			Log.error("user request create campaign but is aleady in campaign. playerId : " + army.getPlayerId());
			return;
		}

		CampaignTemplateInfo temp = CampaignTempMgr.get(campaignId);
		if (temp == null) {
			Log.error("playerId: " + army.getPlayerId() + " create campaign if fail ,campaignId:" + campaignId);
			return;
		}

		Campaign campaign = CampaignFactory.createCampaign(temp, army, taskId);
		CampaignMgr.add(campaign);

		campaign.start();
		campaign.onPlayerEnter(army);
		if (campaign.getTemp().getType() == CampaignType.TEAM) {
			Team team = TeamMgr.getTeam(army.getPlayerId());
			if (team == null) {
				return;
			}
			List<Long> players = team.getMembers();
			if (players == null || players.size() <= 0) {
				return;
			}
			for (Long playerId : players) {
				if (army.getPlayerId() == playerId) {
					continue;
				}
				ArmyProxy teamMember = WorldMgr.getArmy(playerId);
				if (teamMember != null) {
					campaign.onPlayerEnter(teamMember);
				}
			}
			TeamCampaign tc = (TeamCampaign) campaign;
			tc.setTeamId(team.getTeamid());
			team.setCampaignId(tc.getIndexId());
		}
	}

}
