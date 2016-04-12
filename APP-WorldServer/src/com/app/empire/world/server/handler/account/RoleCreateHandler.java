package com.app.empire.world.server.handler.account;

import java.util.List;

import org.apache.log4j.Logger;

import com.app.empire.protocol.data.account.RoleCreate;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.common.util.ServiceUtils;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.exception.CreatePlayerException;
import com.app.empire.world.model.Client;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.service.impl.PlayerService;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 创建角色协议处理
 * 
 * @author doter
 * @since JDK 1.6
 */
public class RoleCreateHandler implements IDataHandler {
	private Logger log = Logger.getLogger(RoleCreateHandler.class);

	public AbstractData handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		Client client = session.getClient(data.getSessionId());
		RoleCreate createActor = (RoleCreate) data;
		String clientModel = createActor.getClientModel();
		String systemName = createActor.getSystemName();
		String systemVersion = createActor.getSystemVersion();

		if ((client == null) || (client.getStatus() != Client.STATUS.ACCOUNTLOGIN)) {
			return null;
		}
		client.setStatus(Client.STATUS.CREATEPLAYE);
		try {
			PlayerService playerService = ServiceManager.getManager().getPlayerService();
			List<Player> list = playerService.getPlayerList(client.getAccountId());
			if (list.size() > 3) {
				return null;
			}
			Player player = playerService.createPlayer(client.getAccountId(), createActor.getNickname(),client.getName(), createActor.getHeroExtId(),
					client.getChannel(), clientModel, systemName, systemVersion);
			// Mail mail = new Mail();
			// mail.setBlackMail(false);
			// mail.setContent(TipMessages.WELCOME);
			// mail.setIsRead(false);
			// mail.setReceivedId(player.getId());
			// mail.setReceivedName(player.getNickname());
			// mail.setSendId(0);
			// mail.setSendName(TipMessages.SYSNAME_MESSAGE);
			// mail.setSendTime(new Date());
			// mail.setTheme(TipMessages.SYS_MAIL);
			// mail.setType(1);
			// mail.setIsStick(1);
			// ServiceManager.getManager().getMailService().saveMail(mail,
			// null);

			// 取角色列表
			GetRoleListHandler getActorListHandler = new GetRoleListHandler();
			getActorListHandler.handle(data);
			return null;
		} catch (CreatePlayerException ex) {
			ServiceUtils.log(log, -1, data.getTypeString(), "CreateActor [" + createActor.getNickname() + "] failed");
			if (!ex.getMessage().startsWith(Common.ERRORKEY)) {
				this.log.error(ex, ex);
			}
			if (null != ex.getMessage())
				throw new ProtocolException(ex.getMessage().replace(Common.ERRORKEY, ""), data.getSerial(), data.getSessionId(),
						data.getType(), data.getSubType());
		} finally {
			client.setStatus(Client.STATUS.ACCOUNTLOGIN);
		}
		return null;
	}
}