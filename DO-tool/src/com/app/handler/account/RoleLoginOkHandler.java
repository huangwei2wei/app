package com.app.handler.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.app.dispatch.StatisticsServer;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.data.account.GetRandomName;
import com.app.empire.protocol.data.test.ItemVo;
import com.app.empire.protocol.data.test.ItemVo2;
import com.app.empire.protocol.data.test.Test;
import com.app.empire.protocol.pb.TestMsgProto.TestMsg;
import com.app.empire.protocol.pb.player.PlayerInfoMsgProto.PlayerInfoMsg;
import com.app.net.IConnector;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.handler.IDataHandler;

public class RoleLoginOkHandler implements IDataHandler {
	public AbstractData handle(AbstractData data) throws Exception {
		StatisticsServer.getStatisticsServer().getResNum().getAndIncrement();
		PbAbstractData msg = (PbAbstractData) data;
		PlayerInfoMsg login = PlayerInfoMsg.parseFrom(msg.getBytes());

		IConnector connector = data.getSource();
		System.out.println("角色登录数量:" + StatisticsServer.getStatisticsServer().getPlayerNum().incrementAndGet());
		GetRandomName getRandomName = new GetRandomName();
		// GetRoleList getRoleList = new GetRoleList();
		// connector.send(getRandomName);
		/**
		 * List<Integer> b = new ArrayList<Integer>(); List<Boolean> d = new ArrayList<Boolean>(); List<String> f = new ArrayList<String>(); List<Long> h = new ArrayList<Long>();
		 * List<Byte> j = new ArrayList<Byte>(); List<Short> l = new ArrayList<Short>();
		 * 
		 * for (int i = 0; i < 10; i++) { b.add(i); d.add(true); f.add("abc"); h.add(9999119999L); j.add((byte) i); l.add((short) (i * 10)); }
		 * 
		 * Test test = new Test(); test.setA(1); test.setB(ArrayUtils.toPrimitive(b.toArray(new Integer[b.size()]))); test.setC(false);
		 * test.setD(ArrayUtils.toPrimitive(d.toArray(new Boolean[d.size()]))); test.setE("testttttttt"); test.setF(f.toArray(new String[f.size()])); test.setG(100000);
		 * test.setH(ArrayUtils.toPrimitive(h.toArray(new Long[h.size()]))); test.setI((byte) 125); test.setJ(ArrayUtils.toPrimitive(j.toArray(new Byte[j.size()])));
		 * test.setK((short) 1800); test.setL(ArrayUtils.toPrimitive(l.toArray(new Short[l.size()]))); connector.send(test);
		 */

		// for (int i = 0; i < 10000000; i++) {
		// connector.send(getRoleList);
		// StatisticsServer.getStatisticsServer().getReqNum().getAndIncrement();
		// Thread.sleep(1000);
		// }

		// 心跳
		// for (int i = 0; i < 1000000; i++) {
		// System.out.println("---心---" + i);
		// Heartbeat heartbeat = new Heartbeat();
		// connector.send(heartbeat);
		// Thread.sleep(3000);
		// }

		// StudySkill studySkill = new StudySkill();
		// studySkill.setHeroId(7530);
		// studySkill.setSkillBaseId(101);
		// connector.send(studySkill);

		// GetHeroList getHeroList = new GetHeroList();
		// connector.send(getHeroList);

		//
		// GetEquipList getEquipList = new GetEquipList();
		// int[] heroids = {1};
		// getEquipList.setHeroId(heroids);
		// connector.send(getEquipList);
		//
		// GetMailList getMaillist = new GetMailList();
		// getMaillist.setSkip(0);
		// getMaillist.setLimit(10);
		// connector.send(getMaillist);
		//
		// ReceiveMail receiveMail = new ReceiveMail();
		// receiveMail.setMailId(new int[]{5});
		// connector.send(receiveMail);
		//
		// CreateRoom createRoom = new CreateRoom();
		// createRoom.setRoomType(1);
		// createRoom.setHeroId(1);
		// connector.send(createRoom);
		//
		// GetRoomList getRoomList = new GetRoomList();
		// getRoomList.setRoomType(1);
		// connector.send(getRoomList);

		// IntoRoom intoRoom = new IntoRoom();
		// intoRoom.setHeroId(1);
		// intoRoom.setRoomId(1);
		// intoRoom.setRoomType(1);
		// connector.send(intoRoom);

		// Start start = new Start();
		// start.setRoomType(1);
		// start.setRoomId(1);
		// connector.send(start);
		// System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
		// GetSkillList getSkillList = new GetSkillList();
		// int[] ids = {1};
		// getSkillList.setHeroId(ids);
		// connector.send(getSkillList);

		// GetBackpackList getBackpackList = new GetBackpackList();
		// connector.send(getBackpackList);
		//
		//
		// UseGoods useGoods = new UseGoods();
		// useGoods.setId(3);
		// useGoods.setGoodsNum(1);
		// connector.send(useGoods);
		// 接副本
		// AcessCopyMap acessCopyMap = new AcessCopyMap();
		// acessCopyMap.setCopyMapType((byte) 1);
		// acessCopyMap.setCopyMapId(5);
		// connector.send(acessCopyMap);

		// CompleteCopyMap completeCopyMap = new CompleteCopyMap();
		// completeCopyMap.setCopyMapType((byte) 1);
		// completeCopyMap.setCopyMapId(5);
		// connector.send(completeCopyMap);

		// GetList getList = new GetList();
		// getList.setCopyType(1);
		// getList.setSkip(0);
		// getList.setLimit(10);
		// connector.send(getList);
		// GetShop getShop = new GetShop();
		// getShop.setShopType(1);
		// connector.send(getShop);

		// Buy buy = new Buy();
		// buy.setShopType(1);
		// buy.setId(3);
		// connector.send(buy);

		// private int teamType;// 类型1、主线副本
		// private int heroId;// 英雄id
		// private int[] arms;// 兵种
		// private int[] ArmsNum;// 兵种数量
		// private int animalId;// 神兽

		// SaveTeam saveTeam = new SaveTeam();
		// saveTeam.setTeamType(1);
		// saveTeam.setHeroId(1);
		// saveTeam.setArms(new int[] {1,2});
		// saveTeam.setArmsNum(new int[] {1,2});
		// saveTeam.setAnimalId(1);
		// connector.send(saveTeam);
		//
		// GetTeam getTeam = new GetTeam();
		// getTeam.setTeamType(1);
		// connector.send(getTeam);
		// GetTeam getTeam = new GetTeam();
		// getTeam.setTeamType(1);
		// connector.send(getTeam);

		// GetShop getShop = new GetShop();
		// getShop.setShopType(1);
		// connector.send(getShop);
		//
		// Refresh refresh = new Refresh();
		// refresh.setShopType(1);
		// connector.send(refresh);

		// GetNpc getNpc = new GetNpc();
		// connector.send(getNpc);

		List<Integer> b = new ArrayList<Integer>();
		List<Boolean> d = new ArrayList<Boolean>();
		List<String> f = new ArrayList<String>();
		List<Long> h = new ArrayList<Long>();
		List<Byte> j = new ArrayList<Byte>();
		List<Short> l = new ArrayList<Short>();

		for (int i = 0; i < 10; i++) {
			b.add(i);
			d.add(true);
			f.add("abc");
			h.add(9999119999L);
			j.add((byte) i);
			l.add((short) (i * 10));
		}

		Test test = new Test();
		test.setA(1);
		test.setB(ArrayUtils.toPrimitive(b.toArray(new Integer[b.size()])));
		test.setC(false);
		test.setD(ArrayUtils.toPrimitive(d.toArray(new Boolean[d.size()])));
		test.setE("testttttttt");
		test.setF(f.toArray(new String[f.size()]));
		test.setG(100000);
		test.setH(ArrayUtils.toPrimitive(h.toArray(new Long[h.size()])));
		test.setI((byte) 125);
		test.setJ(ArrayUtils.toPrimitive(j.toArray(new Byte[j.size()])));
		test.setK((short) 1800);
		test.setL(ArrayUtils.toPrimitive(l.toArray(new Short[l.size()])));

		ItemVo2 itemVo2 = new ItemVo2();
		itemVo2.setA(1);
		itemVo2.setB(ArrayUtils.toPrimitive(b.toArray(new Integer[b.size()])));
		itemVo2.setC(false);
		itemVo2.setD(ArrayUtils.toPrimitive(d.toArray(new Boolean[d.size()])));
		itemVo2.setE("---itemVo2itemVo2itemVo2itemVo2");
		itemVo2.setF(f.toArray(new String[f.size()]));
		itemVo2.setG(100000);
		itemVo2.setH(ArrayUtils.toPrimitive(h.toArray(new Long[h.size()])));
		itemVo2.setI((byte) 125);
		itemVo2.setJ(ArrayUtils.toPrimitive(j.toArray(new Byte[j.size()])));
		itemVo2.setK((short) 1800);
		itemVo2.setL(ArrayUtils.toPrimitive(l.toArray(new Short[l.size()])));

		List<ItemVo> list = new ArrayList<ItemVo>();
		for (int i = 0; i < 10; i++) {
			ItemVo itemVo = new ItemVo();
			itemVo.setA(1);
			itemVo.setB(ArrayUtils.toPrimitive(b.toArray(new Integer[b.size()])));
			itemVo.setC(false);
			itemVo.setD(ArrayUtils.toPrimitive(d.toArray(new Boolean[d.size()])));
			itemVo.setE("testttttttt");
			itemVo.setF(f.toArray(new String[f.size()]));
			itemVo.setG(100000);
			itemVo.setH(ArrayUtils.toPrimitive(h.toArray(new Long[h.size()])));
			itemVo.setI((byte) 125);
			itemVo.setJ(ArrayUtils.toPrimitive(j.toArray(new Byte[j.size()])));
			itemVo.setK((short) 1800);
			itemVo.setL(ArrayUtils.toPrimitive(l.toArray(new Short[l.size()])));
			itemVo.setVo2(itemVo2);
			list.add(itemVo);
		}
		test.setM(list);

		ItemVo itemVo = new ItemVo();
		itemVo.setA(1);
		itemVo.setB(ArrayUtils.toPrimitive(b.toArray(new Integer[b.size()])));
		itemVo.setC(false);
		itemVo.setD(ArrayUtils.toPrimitive(d.toArray(new Boolean[d.size()])));
		itemVo.setE("---testttttttt22");
		itemVo.setF(f.toArray(new String[f.size()]));
		itemVo.setG(100000);
		itemVo.setH(ArrayUtils.toPrimitive(h.toArray(new Long[h.size()])));
		itemVo.setI((byte) 125);
		itemVo.setJ(ArrayUtils.toPrimitive(j.toArray(new Byte[j.size()])));
		itemVo.setK((short) 1800);
		itemVo.setL(ArrayUtils.toPrimitive(l.toArray(new Short[l.size()])));
		itemVo.setVo2(itemVo2);
		test.setItemVo(itemVo);

		// connector.send(test);

		// Test2 test2 = new Test2();
		TestMsg.Builder testMsg = TestMsg.newBuilder();
		testMsg.setVipInterimTimeLimit(111112);
		testMsg.setVipTimeLimit(100);
		// test2.setBytes(testMsg.build().toByteArray());
		// connector.send(test2);
		connector.send(Protocol.MAIN_TEST, Protocol.TEST_Test2, testMsg.build(), EnumTarget.WORLDSERVER.getValue());
		return null;
	}
}
