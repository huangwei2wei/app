package com.app.empire.world.server.handler.hero;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.app.empire.protocol.data.hero.GetHeroList;
import com.app.empire.protocol.data.hero.GetHeroListOK;
import com.app.empire.world.entity.mongo.PlayerHero;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
/**
 * 获取英雄列表
 * 
 * @author doter
 *
 */

public class GetHeroListHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		GetHeroList getHeroList = (GetHeroList) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());
		List<PlayerHero> playerHeroList = ServiceManager.getManager().getPlayerHeroService().getHeroListByPlayerId(worldPlayer);
		List<Integer> heroId = new ArrayList<Integer>();// 英雄流水id
		List<Integer> heroExtId = new ArrayList<Integer>();// 英雄扩展id
		List<Integer> experience = new ArrayList<Integer>();// 经验
		List<Integer> lv = new ArrayList<Integer>();// 等级
		List<Integer> fighting = new ArrayList<Integer>();// 战斗力
		List<String> property = new ArrayList<String>();// 属性
		List<Integer> talent = new ArrayList<Integer>();// 天赋数
		List<Integer> useTalent = new ArrayList<Integer>();// 使用了多少天赋值
		for (PlayerHero hero : playerHeroList) {
			heroId.add(hero.getId());
			heroExtId.add(hero.getHeroExtId());
			experience.add(hero.getExperience());
			lv.add(hero.getLv());
			fighting.add(hero.getFight());
			property.add(hero.getProperty());
			talent.add(hero.getTalent());
			useTalent.add(hero.getUseTalent());
		}
		GetHeroListOK getHeroListOK = new GetHeroListOK(getHeroList.getSessionId(), getHeroList.getSerial());
		getHeroListOK.setHeroId((ArrayUtils.toPrimitive(heroId.toArray(new Integer[heroId.size()]))));
		getHeroListOK.setHeroExtId((ArrayUtils.toPrimitive(heroExtId.toArray(new Integer[heroExtId.size()]))));
		getHeroListOK.setExperience((ArrayUtils.toPrimitive(experience.toArray(new Integer[experience.size()]))));
		getHeroListOK.setLv((ArrayUtils.toPrimitive(lv.toArray(new Integer[lv.size()]))));
		getHeroListOK.setFighting((ArrayUtils.toPrimitive(fighting.toArray(new Integer[fighting.size()]))));
		getHeroListOK.setProperty((property.toArray(new String[property.size()])));
		getHeroListOK.setTalent((ArrayUtils.toPrimitive(talent.toArray(new Integer[talent.size()]))));
		getHeroListOK.setUseTalent((ArrayUtils.toPrimitive(useTalent.toArray(new Integer[useTalent.size()]))));
		return getHeroListOK;
	}
}
