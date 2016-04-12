package com.app.empire.world.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.app.empire.protocol.data.mail.NewMail;
import com.app.empire.world.dao.mongo.impl.PlayerMailDao;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerMail;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;

/**
 * 邮件服务
 */
@Service
public class PlayerMailService {
	@Autowired
	private PlayerMailDao playerMailDao;

	/**
	 * 给玩家发送邮件
	 * 
	 * @param playerId 接受者玩家id
	 * @param sendPlayerId 发送者玩家id
	 * @param title 标题
	 * @param msg 内容
	 * @param goods 物品（附件）json　格式如[{"goods_ext_id":10,"num":1},{"gold":100}]
	 * @param isPush 是否推送通知玩家
	 */
	public void sendMail(WorldPlayer worldPlayer, int sendPlayerId, String title, String msg, String goods) {
		try {
			int playerId = worldPlayer.getPlayer().getId();
			PlayerMail playerMail = new PlayerMail();
			playerMail.setPlayerId(playerId);
			playerMail.setSendPlayerId(sendPlayerId);
			playerMail.setCreateTime(new Date());
			playerMail.setTitle(title);
			playerMail.setMsg(msg);
			playerMail.setGoods(goods);
			playerMail.setStatus((byte) 1);
			playerMail.setIsDel((byte) 0);
			playerMail = (PlayerMail) playerMailDao.insert(playerMail);
			// 提醒收件玩家
			NewMail newMail = new NewMail();
			newMail.setId(newMail.getId());
			newMail.setTitle(title);
			newMail.setMsg(msg);
			newMail.setGoods(goods);
			worldPlayer.sendData(newMail);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给玩家发送邮件
	 * 
	 * @param playerId 接受者玩家id
	 * @param sendPlayerId 发送者玩家id
	 * @param title 标题
	 * @param msg 内容
	 * @param goods 物品（附件）json　格式如[{"goods_ext_id":10,"num":1},{"gold":100}]
	 * @param isPush 是否推送通知玩家
	 */
	public void sendMail(int playerId, int sendPlayerId, String title, String msg, String goods) {
		try {
			PlayerMail playerMail = new PlayerMail();
			playerMail.setPlayerId(playerId);
			playerMail.setSendPlayerId(sendPlayerId);
			playerMail.setCreateTime(new Date());
			playerMail.setTitle(title);
			playerMail.setMsg(msg);
			playerMail.setGoods(goods);
			playerMail.setStatus((byte) 1);
			playerMail.setIsDel((byte) 0);
			playerMail = (PlayerMail) playerMailDao.insert(playerMail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取玩家邮件列表
	 * 
	 * @param playerId 玩家id
	 * @param skip 记录开始位置
	 * @param limit 记录条数
	 * @return
	 */
	public List<PlayerMail> getMailListByPlayer(WorldPlayer worldPlayer, int skip, int limit) {
		return playerMailDao.getMailListByPlayerId(worldPlayer.getPlayer().getId(), skip, limit);
	}

	/**
	 * 获取玩家单封邮件
	 * 
	 * @param playerId
	 * @param mailId
	 * @return
	 */
	public PlayerMail getMailById(WorldPlayer worldPlayer, int mailId) {
		return playerMailDao.getMailById(worldPlayer.getPlayer().getId(), mailId);
	}

	/**
	 * 设置邮件状态
	 * 
	 * @param worldPlayer
	 * @param mailId
	 * @param status 1未读2已读3已领取
	 * @return
	 */
	public PlayerMail setMail(WorldPlayer worldPlayer, int mailId, byte status) {
		PlayerMail playerMail = playerMailDao.getMailById(worldPlayer.getPlayer().getId(), mailId);
		if (playerMail.getStatus() >= status)
			return playerMail;
		playerMail.setStatus(status);
		return playerMailDao.save(playerMail);
	}

	/**
	 * 邮件领取
	 * 
	 * @param playerId
	 * @param mailId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<Integer, List> receiveMail(WorldPlayer worldPlayer, Integer[] mailId) throws PlayerDataException {
		List<PlayerMail> playerMailList = playerMailDao.getMailListById(worldPlayer.getPlayer().getId(), mailId);
		Map<Integer, List> getGoods = new HashMap<Integer, List>();
		for (PlayerMail playerMail : playerMailList) {
			if (playerMail.getStatus() == 3)
				continue;
			String strGoods = playerMail.getGoods();
			List<Map> addGoods = JSON.parseArray(strGoods, Map.class);
			getGoods.put(playerMail.getId(), addGoods);
			ServiceManager.getManager().getPlayerGoodsService().addGoods(worldPlayer, addGoods);
			playerMail.setStatus((byte) 3); // 修改邮件状态
			playerMailDao.save(playerMail);
		}
		playerMailDao.playerUpdateMail(worldPlayer.getPlayer().getId(), mailId, (byte) 3);
		return getGoods;
	}
	/**
	 * 玩家删除邮件
	 * 
	 * @param worldPlayer
	 * @param mailId
	 */
	public void playerDelMail(WorldPlayer worldPlayer, Integer[] mailId) {
		playerMailDao.playerDelMail(worldPlayer.getPlayer().getId(), mailId);
	}

	/**
	 * 删除过期邮件
	 * 
	 * @param dateTime
	 */
	public void delMail(Date dateTime) {
		playerMailDao.delMail(dateTime);
	}

	/**
	 * GM 发送邮件
	 * 
	 * @param user 帐号名，或者ID，或者昵称多个用户用 “,” 英文逗号隔开
	 * @param userType 0玩家ID，1玩家账号，2玩家昵称
	 * @param title
	 * @param msg
	 * @param goods 物品（附件）json　格式如[{"goods_ext_id":10,"num":1},{"gold":100}]
	 */
	public void sendMailForGM(String user, int userType, String title, String msg, String goods) {
		List<String> players = Arrays.asList(user.split(","));
		List<Player> playerService = ServiceManager.getManager().getPlayerService().getPlayerList(players, userType);
		for (Player player : playerService) {
			WorldPlayer worldPlayer = ServiceManager.getManager().getPlayerService().getPlayer(player.getId());
			if (worldPlayer == null) {
				sendMail(player.getId(), 0, title, msg, goods);
			} else {
				sendMail(worldPlayer, 0, title, msg, goods);
			}
		}
	}

}