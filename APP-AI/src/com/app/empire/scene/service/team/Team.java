package com.app.empire.scene.service.team;

import java.util.ArrayList;
import java.util.List;

public class Team {
	private int				teamid;
	private long			leader;

	private int				campaignId;
	private ArrayList<Long>	members	= new ArrayList<>();

	public Team(int teamid) {
		this.teamid = teamid;
	}

	public long getLeader() {
		return leader;
	}

	public void setLeader(long leader) {
		this.leader = leader;
	}

	public void addMember(long playerId) {
		synchronized (members) {
			members.add(playerId);
			TeamMgr.getPlayerTeamMap().put(playerId, this.teamid);
		}
	}

	public void removeMember(long playerId) {
		synchronized (members) {
			int index = members.indexOf(playerId);
			if (index != -1) {
				members.remove(index);
			}
			TeamMgr.getPlayerTeamMap().remove(playerId);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Long> getMembers() {
		synchronized (members) {
			return (List<Long>) members.clone();
		}
	}

	public List<Long> getMembers(long playerId) {
		List<Long> list = new ArrayList<>();
		synchronized (members) {
			for (Long long1 : members) {
				if (long1 != playerId) {
					list.add(long1);
				}
			}
		}
		return list;
	}

	public boolean inTeam(long playerId) {
		return members.contains(playerId);
	}

	public int getTeamid() {
		return teamid;
	}

	public int getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}

	public void clear() {
		List<Long> list = getMembers();
		for (Long id : list) {
			TeamMgr.getPlayerTeamMap().remove(id);
		}
		list.clear();

	}

}
