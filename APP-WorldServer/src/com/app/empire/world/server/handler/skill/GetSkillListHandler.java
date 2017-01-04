package com.app.empire.world.server.handler.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

import com.app.empire.protocol.data.hero.GetSkillList;
import com.app.empire.protocol.data.hero.GetSkillListOK;
import com.app.empire.world.entity.mongo.HeroSkill;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取英雄技能
 * 
 * @author doter
 *
 */
public class GetSkillListHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		GetSkillList getHeroList = (GetSkillList) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		Integer[] heroIds = ArrayUtils.toObject(getHeroList.getHeroId());
		List<Integer> heroIdList = Arrays.asList(heroIds);
		Map<Integer, List<HeroSkill>> getHeroSkill = ServiceManager.getManager().getPlayerSkillService().getHeroSkill(worldPlayer, heroIdList);

		List<Integer> heroId = new ArrayList<Integer>();// 英雄id
		List<Integer> skillBaseId = new ArrayList<Integer>();// 技能基表id
		List<Integer> skillExtId = new ArrayList<Integer>();// 技能扩展id
		List<Integer> lv = new ArrayList<Integer>();// 等级
		List<String> property = new ArrayList<String>();// 属性
		for (Entry<Integer, List<HeroSkill>> entry : getHeroSkill.entrySet()) {
			int playerHeroId = entry.getKey();
			List<HeroSkill> skills = entry.getValue();
			for (HeroSkill skill : skills) {
				heroId.add(playerHeroId);
				skillBaseId.add(skill.getSkillBaseId());
				skillExtId.add(skill.getSkillExtId());
				lv.add(skill.getLv());
				property.add(skill.getProperty());
			}
		}
		
		GetSkillListOK getSkillListOK = new GetSkillListOK(getHeroList.getSessionId(), getHeroList.getSerial());
		getSkillListOK.setHeroId((ArrayUtils.toPrimitive(heroId.toArray(new Integer[heroId.size()]))));
		getSkillListOK.setSkillBaseId((ArrayUtils.toPrimitive(skillBaseId.toArray(new Integer[skillBaseId.size()]))));
		getSkillListOK.setSkillExtId((ArrayUtils.toPrimitive(skillExtId.toArray(new Integer[skillExtId.size()]))));
		getSkillListOK.setLv((ArrayUtils.toPrimitive(lv.toArray(new Integer[lv.size()]))));
		getSkillListOK.setProperty(property.toArray(new String[property.size()]));
		return getSkillListOK;
	}
}
