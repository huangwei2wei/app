package com.app.empire.world.server.handler.mail;

import java.util.List;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.mail.GetMailList;
import com.app.empire.protocol.data.mail.GetMailListOk;
import com.app.empire.world.entity.mongo.PlayerMail;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.handler.IDataHandler;
/**
 * 获取邮件列表
 * 
 * @author doter
 * 
 */
public class GetMailListHandler implements IDataHandler {
	private Logger log = Logger.getLogger(GetMailListHandler.class);
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		GetMailList getMailList = (GetMailList) data;
		WorldPlayer worldPlayer = session.getPlayer(data.getSessionId());

		int skip = getMailList.getSkip();// 起始记录下标，0开始
		int limit = getMailList.getLimit();// 返回记录条数
		List<PlayerMail> playerMail = ServiceManager.getManager().getPlayerMailService().getMailListByPlayer(worldPlayer, skip, limit);
		GetMailListOk getMailListOk = new GetMailListOk(data.getSessionId(), data.getSerial());
		int size = playerMail.size();
		int[] id = new int[size];// 邮件流水id
		int[] sendPlayerId = new int[size];// 发送者玩家id
		int[] sendPlayerNickname = new int[size];// 发送者昵称
		int[] createTime = new int[size];// 创建时间
		String[] title = new String[size];// 标题
		String[] msg = new String[size];// 内容
		String[] goods = new String[size];// 物品（附件）json 格式[{"goods_ext_id":10,"num":1},{"gold":100}]
		int[] status = new int[size];// 1未读２已读３已领取
		int i = 0;
		for (PlayerMail mail : playerMail) {
			id[i] = mail.getId();
			sendPlayerId[i] = mail.getSendPlayerId();
			sendPlayerNickname[i] = mail.getSendPlayerNickname();
			createTime[i] = (int) (mail.getCreateTime().getTime() / 1000);
			title[i] = mail.getTitle();
			msg[i] = mail.getMsg();
			goods[i] = mail.getGoods();
			status[i] = mail.getStatus();
			i++;
		}

		getMailListOk.setId(id);
		getMailListOk.setSendPlayerId(sendPlayerId);
		getMailListOk.setSendPlayerNickname(sendPlayerNickname);
		getMailListOk.setCreateTime(createTime);
		getMailListOk.setTitle(title);
		getMailListOk.setMsg(msg);
		getMailListOk.setGoods(goods);
		getMailListOk.setStatus(status);

		session.write(  getMailListOk);
	}
}
